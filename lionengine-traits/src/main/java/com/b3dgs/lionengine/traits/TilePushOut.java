/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.traits;

import com.b3dgs.lionengine.ecs.Query;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.physics.Box;
import com.b3dgs.lionengine.physics.ShapeQuery;

/**
 * The tile push-out of {@link CollisionSystem}: moves the objects out of the solid shapes of a {@link ShapeQuery}, one
 * axis after the other, and writes their grounded flag (REQ-STD-010, REQ-MAP-008, ADR-0028, ADR-0030, decisions 6 and
 * 7, ADR-0083).
 *
 * <p>
 * For each non-trigger object of its query (a {@link Transformable}, a {@link Collidable} and a {@link Movable}, none
 * of the excluded trait types), with its displacement of the step measured from its previous position:
 * </p>
 * <ol>
 * <li>X pass: the box at {@code (x, previousY)}; if the object moved right, every solid shape overlapping it puts the
 * box at the left of the leftmost one, if it moved left at the right of the rightmost one; no X displacement, no X
 * pass;</li>
 * <li>Y pass: the box at the resolved X and {@code y}; moving down, above the highest overlapping shape (the object is
 * then grounded), moving up, under the lowest one; no Y displacement, no Y pass;</li>
 * <li>a velocity component pointing into a shape that stopped the object is set to {@code 0};</li>
 * <li>the {@link Jumpable} of the object, if any, is grounded if and only if the Y pass pushed it up.</li>
 * </ol>
 *
 * <p>
 * A pushed box ends exactly touching the shape, never overlapping it (the position is moved by the last ulps if the
 * {@code double} rounding would leave an overlap), so a resting object does not catch on the seams between floor tiles
 * in the next X pass. The shapes are found by the strict overlap of {@link Box#overlaps(Box)}: a box resting on a floor
 * touches it and does not overlap it, so a grounded object is the one gravity moved down into the floor during the
 * step.
 * </p>
 *
 * <p>
 * Allocates nothing per step: the result buffer of the queries starts empty and grows to
 * {@code max(count, 2 * length)} boxes when a query returns more shapes than it holds (a warm-up cost), then the query
 * is repeated.
 * </p>
 */
final class TilePushOut
{
    /** No box. */
    private static final Box[] NO_BOXES = new Box[0];
    /** Half of a size. */
    private static final double HALF = 0.5;

    /** The transformables. */
    private final TraitStorage<Transformable> transformables;
    /** The collidables. */
    private final TraitStorage<Collidable> collidables;
    /** The movables. */
    private final TraitStorage<Movable> movables;
    /** The jumpables. */
    private final TraitStorage<Jumpable> jumpables;
    /** The pushed objects. */
    private final Query pushed;
    /** The queried box. */
    private final Box box = new Box();
    /** The solid shapes found. */
    private Box[] solids = NO_BOXES;
    /** The solid shapes, {@code null} for none. */
    private ShapeQuery tiles;

    /**
     * Creates the push-out.
     *
     * @param world The world.
     * @param transformables The storage of {@link Transformable#TYPE} in the world.
     * @param collidables The storage of {@link Collidable#TYPE} in the world.
     * @param movables The storage of {@link Movable#TYPE} in the world.
     * @param jumpables The storage of {@link Jumpable#TYPE} in the world.
     * @param excluded The storages of the excluded trait types in the world, not kept.
     * @throws IllegalArgumentException If an argument is {@code null}, the excluded set contains {@code null} or an
     *             included storage, or a storage belongs to another world.
     */
    TilePushOut(World world,
                TraitStorage<Transformable> transformables,
                TraitStorage<Collidable> collidables,
                TraitStorage<Movable> movables,
                TraitStorage<Jumpable> jumpables,
                TraitStorage<?>[] excluded)
    {
        MovementSystem.check(world, movables, "Movable");
        MovementSystem.check(world, jumpables, "Jumpable");
        this.transformables = transformables;
        this.collidables = collidables;
        this.movables = movables;
        this.jumpables = jumpables;
        pushed = world.createQuery(new TraitStorage<?>[]
        {
            transformables, collidables, movables
        }, excluded);
    }

    /**
     * Returns the pushed objects.
     *
     * @return The query of the objects having a {@link Transformable}, a {@link Collidable}, a {@link Movable} and no
     *         excluded trait; the triggers among them are skipped.
     */
    Query getQuery()
    {
        return pushed;
    }

    /**
     * Returns the solid shapes.
     *
     * @return The solid shapes, {@code null} for none.
     */
    ShapeQuery getShapeQuery()
    {
        return tiles;
    }

    /**
     * Sets the solid shapes.
     *
     * @param tiles The solid shapes, {@code null} for none.
     */
    void setShapeQuery(ShapeQuery tiles)
    {
        this.tiles = tiles;
    }

    /**
     * Pushes every non-trigger object out of the solid shapes and writes its grounded flag; does nothing without
     * shapes.
     *
     * @throws IllegalArgumentException If the position of a pushed object is not finite.
     */
    void update()
    {
        if (tiles == null)
        {
            return;
        }
        final int count = pushed.getCount();
        for (int i = 0; i < count; i++)
        {
            final long id = pushed.getId(i);
            final Collidable collidable = collidables.get(id);
            if (!collidable.isTrigger())
            {
                final Transformable transformable = transformables.get(id);
                final Movable movable = movables.get(id);
                pushX(transformable, collidable, movable);
                final boolean grounded = pushY(transformable, collidable, movable);
                if (jumpables.has(id))
                {
                    jumpables.get(id).setGrounded(grounded);
                }
            }
        }
    }

    /**
     * Pushes an object out of the shapes on X, its box at its previous Y.
     *
     * @param transformable The position.
     * @param collidable The box.
     * @param movable The velocity.
     */
    private void pushX(Transformable transformable, Collidable collidable, Movable movable)
    {
        final double x = transformable.getX();
        final double previousX = transformable.getPreviousX();
        box.set(x + collidable.getOffsetX(),
                transformable.getPreviousY() + collidable.getOffsetY(),
                collidable.getWidth(),
                collidable.getHeight());
        final int count = find(x, previousX);
        if (count > 0)
        {
            final double offsetX = collidable.getOffsetX();
            final double half = collidable.getWidth() * HALF;
            if (x > previousX)
            {
                transformable.setPosition(below(lowest(count, false), offsetX, half), transformable.getY());
                movable.setVelocity(Math.min(movable.getVelocityX(), 0.0), movable.getVelocityY());
            }
            else
            {
                transformable.setPosition(above(highest(count, false), offsetX, half), transformable.getY());
                movable.setVelocity(Math.max(movable.getVelocityX(), 0.0), movable.getVelocityY());
            }
        }
    }

    /**
     * Pushes an object out of the shapes on Y, its box at its resolved X.
     *
     * @param transformable The position.
     * @param collidable The box.
     * @param movable The velocity.
     * @return {@code true} if the object was pushed up (a contact below).
     */
    private boolean pushY(Transformable transformable, Collidable collidable, Movable movable)
    {
        final double y = transformable.getY();
        final double previousY = transformable.getPreviousY();
        box.set(transformable.getX() + collidable.getOffsetX(),
                y + collidable.getOffsetY(),
                collidable.getWidth(),
                collidable.getHeight());
        final int count = find(y, previousY);
        boolean grounded = false;
        if (count > 0)
        {
            final double offsetY = collidable.getOffsetY();
            final double half = collidable.getHeight() * HALF;
            if (y < previousY)
            {
                transformable.setPosition(transformable.getX(), above(highest(count, true), offsetY, half));
                movable.setVelocity(movable.getVelocityX(), Math.max(movable.getVelocityY(), 0.0));
                grounded = true;
            }
            else
            {
                transformable.setPosition(transformable.getX(), below(lowest(count, true), offsetY, half));
                movable.setVelocity(movable.getVelocityX(), Math.min(movable.getVelocityY(), 0.0));
            }
        }
        return grounded;
    }

    /**
     * Returns the lowest lower bound of the found shapes on an axis.
     *
     * @param count The found shapes, at least one.
     * @param vertical {@code true} for Y, {@code false} for X.
     * @return The lowest lower bound, in meters.
     */
    private double lowest(int count, boolean vertical)
    {
        double limit = Double.POSITIVE_INFINITY;
        for (int i = 0; i < count; i++)
        {
            limit = Math.min(limit, vertical ? solids[i].getMinY() : solids[i].getMinX());
        }
        return limit;
    }

    /**
     * Returns the highest upper bound of the found shapes on an axis.
     *
     * @param count The found shapes, at least one.
     * @param vertical {@code true} for Y, {@code false} for X.
     * @return The highest upper bound, in meters.
     */
    private double highest(int count, boolean vertical)
    {
        double limit = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < count; i++)
        {
            limit = Math.max(limit, vertical ? solids[i].getMaxY() : solids[i].getMaxX());
        }
        return limit;
    }

    /**
     * Finds the shapes overlapping the box when the object moved on the axis.
     *
     * @param position The position on the axis, in meters.
     * @param previous The previous position on the axis, in meters.
     * @return The number of shapes found, all in the buffer; {@code 0} without displacement.
     */
    private int find(double position, double previous)
    {
        if (Double.compare(position, previous) != 0)
        {
            return find();
        }
        return 0;
    }

    /**
     * Finds the shapes overlapping the box, growing the buffer when they do not fit.
     *
     * @return The number of shapes found, all in the buffer.
     */
    private int find()
    {
        int count = tiles.query(box, solids);
        if (count > solids.length)
        {
            final Box[] grown = new Box[Math.max(count, solids.length * 2)];
            System.arraycopy(solids, 0, grown, 0, solids.length);
            for (int i = solids.length; i < grown.length; i++)
            {
                grown[i] = new Box();
            }
            solids = grown;
            count = tiles.query(box, solids);
        }
        return count;
    }

    /**
     * Returns the position putting the upper bound of a box at or under a limit, as close as the {@code double} values
     * allow.
     *
     * @param limit The limit, in meters.
     * @param offset The offset of the box center from the position, in meters.
     * @param half The half size of the box, in meters.
     * @return The position, whose box bound {@code (position + offset) + half} is not greater than the limit.
     */
    private static double below(double limit, double offset, double half)
    {
        double position = limit - half - offset;
        while (position + offset + half > limit)
        {
            position = Math.nextAfter(position, Double.NEGATIVE_INFINITY);
        }
        return position;
    }

    /**
     * Returns the position putting the lower bound of a box at or over a limit, as close as the {@code double} values
     * allow.
     *
     * @param limit The limit, in meters.
     * @param offset The offset of the box center from the position, in meters.
     * @param half The half size of the box, in meters.
     * @return The position, whose box bound {@code (position + offset) - half} is not lower than the limit.
     */
    private static double above(double limit, double offset, double half)
    {
        double position = limit + half - offset;
        while (position + offset - half < limit)
        {
            position = Math.nextUp(position);
        }
        return position;
    }
}
