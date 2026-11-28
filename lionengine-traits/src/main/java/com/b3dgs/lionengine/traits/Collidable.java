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

import com.b3dgs.lionengine.ecs.TraitType;
import com.b3dgs.lionengine.physics.Box;

/**
 * Standard trait: the collision box of an object, its layers and the layers it collides with (REQ-STD-005,
 * REQ-STD-010, ADR-0011, ADR-0013, ADR-0014, ADR-0030, ADR-0034, ADR-0082).
 *
 * <p>
 * Data only, no logic. Fields:
 * </p>
 * <ul>
 * <li>the box size ({@link #getWidth()}, {@link #getHeight()}), the full width and height of an axis-aligned box, in
 * meters, positive or zero, finite, {@code double};</li>
 * <li>the offset of the box center from the position of the {@link Transformable} ({@link #getOffsetX()},
 * {@link #getOffsetY()}), in meters, Y up, finite, {@code double}: the box center is
 * {@code (x + offsetX, y + offsetY)};</li>
 * <li>the layer ({@link #getLayer()}), an {@code int} bit set: the layers the object is in, one bit per layer, 32
 * layers;</li>
 * <li>the mask ({@link #getMask()}), an {@code int} bit set: the layers the object collides with;</li>
 * <li>the trigger flag ({@link #isTrigger()}): a trigger is an area that reports what enters and leaves it and never
 * blocks.</li>
 * </ul>
 *
 * <p>
 * Two objects collide only if each mask includes the other's layer: {@code (maskA & layerB) != 0} and
 * {@code (maskB & layerA) != 0} (ADR-0030). A layer of {@code 0} collides with nothing.
 * </p>
 *
 * <p>
 * A new or reset instance has a box of size {@code 0} centered on the position (offset {@code (0, 0)}, ADR-0034), the
 * layer {@link #DEFAULT_LAYER} (bit 0), the mask {@link #ALL_LAYERS} and is not a trigger: the game sets the size it
 * wants. A box of size {@code 0} overlaps only a box that strictly contains its center.
 * </p>
 *
 * <p>
 * Interactions: {@link CollisionSystem} (slot {@value CollisionSystem#SLOT}) finds the overlapping pairs of the objects
 * having a {@link Collidable} and a {@link Transformable}, after {@link MovementSystem} has moved them, and reports
 * the pairs that start and stop overlapping to its {@link CollisionListener}s. Object versus object is detection only:
 * neither object is moved, trigger or not. The trigger flag matters to the tile push-out of a later batch, which never
 * blocks a trigger. Registered in a world with {@link #TYPE}; instances are pooled by the world and must not be kept
 * after their removal. Runtime thread only.
 * </p>
 *
 * <p>
 * Memory: 56 bytes per instance on the desktop JVM (64-bit HotSpot, compressed class pointers), measured with the
 * per-thread allocation counter by {@code TraitMemoryTest} (ADR-0034); the pool also holds one reference per instance.
 * </p>
 *
 * @since 11.0.0
 */
public final class Collidable
{
    /** The layer of a new collidable: bit 0, the first of the 32 layers. */
    public static final int DEFAULT_LAYER = 1;
    /** Every layer: the mask of a new collidable, which collides with every layer. */
    public static final int ALL_LAYERS = -1;

    /** Separator of two values in a message. */
    private static final String SEPARATOR = ", ";

    /**
     * The trait type to register in a world: creates instances in the reset state, and resets them to it on removal.
     * Shared by every world, which each register it once.
     */
    public static final TraitType<Collidable> TYPE = new TraitType<>()
    {
        @Override
        public Collidable create()
        {
            return new Collidable();
        }

        @Override
        public void reset(Collidable trait)
        {
            trait.width = 0.0;
            trait.height = 0.0;
            trait.offsetX = 0.0;
            trait.offsetY = 0.0;
            trait.layer = DEFAULT_LAYER;
            trait.mask = ALL_LAYERS;
            trait.trigger = false;
        }
    };

    /** Full box width, in meters. */
    private double width;
    /** Full box height, in meters. */
    private double height;
    /** Horizontal offset of the box center from the position, in meters. */
    private double offsetX;
    /** Vertical offset of the box center from the position, in meters, Y up. */
    private double offsetY;
    /** Layers of the object, one bit per layer. */
    private int layer = DEFAULT_LAYER;
    /** Layers the object collides with, one bit per layer. */
    private int mask = ALL_LAYERS;
    /** Trigger flag. */
    private boolean trigger;

    /**
     * Creates a collidable in its reset state: a box of size {@code 0} on the position, layer {@link #DEFAULT_LAYER},
     * mask {@link #ALL_LAYERS}, not a trigger.
     */
    public Collidable()
    {
        super();
    }

    /**
     * Returns the box width.
     *
     * @return The full width of the box, in meters, positive or zero.
     */
    public double getWidth()
    {
        return width;
    }

    /**
     * Returns the box height.
     *
     * @return The full height of the box, in meters, positive or zero.
     */
    public double getHeight()
    {
        return height;
    }

    /**
     * Sets the box size.
     *
     * @param width The full width of the box, in meters, positive or zero, finite.
     * @param height The full height of the box, in meters, positive or zero, finite.
     * @throws IllegalArgumentException If a size is negative, infinite or not a number; the size is left unchanged.
     */
    public void setSize(double width, double height)
    {
        if (!isSize(width) || !isSize(height))
        {
            throw new IllegalArgumentException("Collidable size is negative or not finite: "
                                               + width
                                               + SEPARATOR
                                               + height);
        }
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the horizontal offset of the box center.
     *
     * @return The offset from the position, in meters.
     */
    public double getOffsetX()
    {
        return offsetX;
    }

    /**
     * Returns the vertical offset of the box center.
     *
     * @return The offset from the position, in meters, Y up.
     */
    public double getOffsetY()
    {
        return offsetY;
    }

    /**
     * Sets the offset of the box center from the position of the {@link Transformable}.
     *
     * @param offsetX The horizontal offset, in meters, finite.
     * @param offsetY The vertical offset, in meters, Y up, finite.
     * @throws IllegalArgumentException If an offset is infinite or not a number; the offset is left unchanged.
     */
    public void setOffset(double offsetX, double offsetY)
    {
        if (!Double.isFinite(offsetX) || !Double.isFinite(offsetY))
        {
            throw new IllegalArgumentException("Collidable offset is not finite: " + offsetX + SEPARATOR + offsetY);
        }
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * Returns the layers of the object.
     *
     * @return The layer bit set, one bit per layer.
     */
    public int getLayer()
    {
        return layer;
    }

    /**
     * Sets the layers of the object.
     *
     * @param layer The layer bit set, one bit per layer, usually one bit; {@code 0} collides with nothing.
     */
    public void setLayer(int layer)
    {
        this.layer = layer;
    }

    /**
     * Returns the layers the object collides with.
     *
     * @return The mask bit set, one bit per layer.
     */
    public int getMask()
    {
        return mask;
    }

    /**
     * Sets the layers the object collides with.
     *
     * @param mask The mask bit set, one bit per layer, {@link #ALL_LAYERS} for every layer, {@code 0} for none.
     */
    public void setMask(int mask)
    {
        this.mask = mask;
    }

    /**
     * Tells whether the object is a trigger.
     *
     * @return {@code true} for an area that reports what enters and leaves it and never blocks.
     */
    public boolean isTrigger()
    {
        return trigger;
    }

    /**
     * Sets the trigger flag.
     *
     * @param trigger {@code true} for an area that reports what enters and leaves it and never blocks.
     */
    public void setTrigger(boolean trigger)
    {
        this.trigger = trigger;
    }

    /**
     * Tells whether a value is a valid box size.
     *
     * @param size The size.
     * @return {@code true} if positive or zero and finite.
     */
    private static boolean isSize(double size)
    {
        return size >= 0.0 && size < Double.POSITIVE_INFINITY;
    }

    /**
     * Tells whether this object and another one collide by their layers: each mask includes the other's layer.
     *
     * @param other The other collidable.
     * @return {@code true} if {@code (mask & other.layer) != 0} and {@code (other.mask & layer) != 0}.
     */
    boolean accepts(Collidable other)
    {
        return (mask & other.layer) != 0 && (other.mask & layer) != 0;
    }

    /**
     * Writes the world box of the object.
     *
     * @param transformable The position of the object.
     * @param box The box receiving the center {@code (x + offsetX, y + offsetY)} and the size.
     * @throws IllegalArgumentException If the position is not finite.
     */
    void toBox(Transformable transformable, Box box)
    {
        box.set(transformable.getX() + offsetX, transformable.getY() + offsetY, width, height);
    }
}
