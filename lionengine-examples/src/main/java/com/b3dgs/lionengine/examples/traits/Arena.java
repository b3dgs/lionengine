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

package com.b3dgs.lionengine.examples.traits;

import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.ecs.WorldConfig;
import com.b3dgs.lionengine.traits.Collidable;
import com.b3dgs.lionengine.traits.CollisionListener;
import com.b3dgs.lionengine.traits.CollisionSystem;
import com.b3dgs.lionengine.traits.Movable;
import com.b3dgs.lionengine.traits.MovementSystem;
import com.b3dgs.lionengine.traits.TransformCaptureSystem;
import com.b3dgs.lionengine.traits.Transformable;

/**
 * The world of {@link CollisionExample}: two balls rolling toward each other through a trigger zone, and a ghost in the
 * zone that nothing detects (REQ-STD-005, REQ-STD-008, REQ-STD-010, ADR-0030, ADR-0034, ADR-0082).
 *
 * <p>
 * The game registers {@link Transformable}, {@link Movable} and {@link Collidable} with their {@code TYPE} constants,
 * creates the standard systems with the storages the registration returned and adds them at their slots:
 * {@link TransformCaptureSystem} first, {@link MovementSystem} at {@value MovementSystem#SLOT} and
 * {@link CollisionSystem} at {@value CollisionSystem#SLOT}, with the default cell size of 2 m; the listener of the game
 * receives the enter and exit events at the end of each collision step. Positions are in meters with Y up, velocities
 * in meters per second.
 * </p>
 *
 * <p>
 * Layers: the balls are on {@link #BALL_LAYER} and collide with every layer; the zone is a trigger on
 * {@link #ZONE_LAYER} that collides with the balls only; the ghost is on {@link #GHOST_LAYER} and collides with ghosts
 * only. A pair is detected only if each mask includes the other's layer: the balls detect each other and the zone, the
 * ghost is detected by nobody although it overlaps the zone and the balls cross it. Detection only: the balls pass
 * through each other and through the zone, nothing is moved by the collision.
 * </p>
 *
 * @since 11.0.0
 */
public final class Arena
{
    /** The constant step, in seconds. */
    public static final double STEP_SECONDS = 1.0 / 60.0;
    /** Distance of each ball from the center at the start, in meters: left at {@code -START}, right at the opposite. */
    public static final double START = 3.0;
    /** Ball speed, in meters per second, toward the other ball. */
    public static final double SPEED = 1.5;
    /** Ball box side, in meters. */
    public static final double BALL_SIZE = 1.0;
    /** Zone box width, in meters, centered on the origin. */
    public static final double ZONE_WIDTH = 2.0;
    /** Zone box height, in meters. */
    public static final double ZONE_HEIGHT = 4.0;
    /** Ghost box side, in meters, centered on the origin. */
    public static final double GHOST_SIZE = 0.5;
    /** The layer of the balls: bit 0. */
    public static final int BALL_LAYER = 1;
    /** The layer of the zone: bit 1. */
    public static final int ZONE_LAYER = 2;
    /** The layer of the ghost: bit 2. */
    public static final int GHOST_LAYER = 4;

    /** The world. */
    private final World world = new World(new WorldConfig(4, 4));
    /** The transformables. */
    private final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
    /** The movables. */
    private final TraitStorage<Movable> movables = world.register(Movable.TYPE);
    /** The collidables. */
    private final TraitStorage<Collidable> collidables = world.register(Collidable.TYPE);
    /** The collision system. */
    private final CollisionSystem collision = new CollisionSystem(world, transformables, collidables);
    /** The left ball. */
    private final long left = world.create();
    /** The right ball. */
    private final long right = world.create();
    /** The trigger zone. */
    private final long zone = world.create();
    /** The ghost. */
    private final long ghost = world.create();

    /**
     * Creates the world, the objects and the systems, registers the listener, and flushes.
     *
     * @param listener The listener of the collision events.
     */
    public Arena(CollisionListener listener)
    {
        ball(left, -START, SPEED);
        ball(right, START, -SPEED);
        final Collidable trigger = place(zone, ZONE_WIDTH, ZONE_HEIGHT);
        trigger.setLayer(ZONE_LAYER);
        trigger.setMask(BALL_LAYER);
        trigger.setTrigger(true);
        final Collidable hidden = place(ghost, GHOST_SIZE, GHOST_SIZE);
        hidden.setLayer(GHOST_LAYER);
        hidden.setMask(GHOST_LAYER);
        collision.addListener(listener);
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        world.addSystem(collision, CollisionSystem.SLOT);
        world.flush();
    }

    /**
     * Plays one step: capture, movement, collision and its events, flush.
     */
    public void step()
    {
        world.update(STEP_SECONDS);
    }

    /**
     * Returns the left ball.
     *
     * @return The left ball ID, the lowest of the world.
     */
    public long getLeft()
    {
        return left;
    }

    /**
     * Returns the right ball.
     *
     * @return The right ball ID.
     */
    public long getRight()
    {
        return right;
    }

    /**
     * Returns the trigger zone.
     *
     * @return The zone ID.
     */
    public long getZone()
    {
        return zone;
    }

    /**
     * Returns the ghost.
     *
     * @return The ghost ID, the highest of the world.
     */
    public long getGhost()
    {
        return ghost;
    }

    /**
     * Returns the position of an object.
     *
     * @param id The object.
     * @return Its transformable, as of the last step.
     */
    public Transformable getTransformable(long id)
    {
        return transformables.get(id);
    }

    /**
     * Returns the number of pairs overlapping now.
     *
     * @return The pairs of the last collision step.
     */
    public int getPairCount()
    {
        return collision.getPairCount();
    }

    /**
     * Adds a ball rolling on the X axis.
     *
     * @param id The ball.
     * @param x The start position, in meters.
     * @param velocity The horizontal velocity, in meters per second.
     */
    private void ball(long id, double x, double velocity)
    {
        final Transformable transformable = transformables.add(id);
        transformable.setPosition(x, 0.0);
        transformable.setPreviousPosition(x, 0.0);
        transformable.setSize(BALL_SIZE, BALL_SIZE);
        movables.add(id).setVelocity(velocity, 0.0);
        final Collidable collidable = collidables.add(id);
        collidable.setSize(BALL_SIZE, BALL_SIZE);
        collidable.setLayer(BALL_LAYER);
    }

    /**
     * Adds a still object at the origin.
     *
     * @param id The object.
     * @param width The box width, in meters.
     * @param height The box height, in meters.
     * @return The collidable of the object, to set its layers.
     */
    private Collidable place(long id, double width, double height)
    {
        transformables.add(id).setSize(width, height);
        final Collidable collidable = collidables.add(id);
        collidable.setSize(width, height);
        return collidable;
    }
}
