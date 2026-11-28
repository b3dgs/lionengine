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

import com.b3dgs.lionengine.ecs.Query;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.ecs.WorldConfig;
import com.b3dgs.lionengine.traits.Collidable;
import com.b3dgs.lionengine.traits.CollisionSystem;
import com.b3dgs.lionengine.traits.Gravity;
import com.b3dgs.lionengine.traits.GravitySystem;
import com.b3dgs.lionengine.traits.Jumpable;
import com.b3dgs.lionengine.traits.Movable;
import com.b3dgs.lionengine.traits.MovementSystem;
import com.b3dgs.lionengine.traits.TransformCaptureSystem;
import com.b3dgs.lionengine.traits.Transformable;

/**
 * The world of {@link LandingExample}: a hero falls onto the solid floor of {@link Ground}, stops on it, grounded, then
 * walks into the wall and stays against it (REQ-STD-008, REQ-STD-010, REQ-MAP-008, ADR-0030, ADR-0034, ADR-0083).
 *
 * <p>
 * The game registers the standard trait types, {@code Jumpable} included for its grounded flag, and adds the systems
 * at their slots: {@link TransformCaptureSystem} first, its own walk system at {@value #WALK_SLOT} (between 1000 and
 * 2000, where decisions set velocities, ADR-0034), {@link GravitySystem} at {@value GravitySystem#SLOT},
 * {@link MovementSystem} at {@value MovementSystem#SLOT} and {@link CollisionSystem} at {@value CollisionSystem#SLOT},
 * created with the storages of {@code Movable} and {@code Jumpable} and the {@link Ground}: it pushes the hero out of
 * the solid tiles, X then Y, zeroes the velocity that points into them and sets the grounded flag when the hero is
 * pushed up out of the floor. The walk system reads that flag, written by the previous step: once grounded, the hero
 * walks right. Meters, Y up, meters per second.
 * </p>
 *
 * @since 11.0.0
 */
public final class Landing
{
    /** The constant step, in seconds. */
    public static final double STEP_SECONDS = 1.0 / 60.0;
    /** The slot of the walk system. */
    public static final int WALK_SLOT = 1500;
    /** Hero starting center X, in meters. */
    public static final double START_X = 1.5;
    /** Hero starting center Y, in meters. */
    public static final double START_Y = 3.0;
    /** Hero box size, in meters. */
    public static final double SIZE = 0.5;
    /** Gravity, in meters per second squared, negative pulls down. */
    public static final double ACCELERATION = -10.0;
    /** Terminal velocity, in meters per second: 1/6 m per step, under half of the box. */
    public static final double TERMINAL_VELOCITY = 10.0;
    /** Walking speed, in meters per second. */
    public static final double WALK_SPEED = 2.0;

    /** The world. */
    private final World world = new World(new WorldConfig(1, 1));
    /** The transformables. */
    private final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
    /** The movables. */
    private final TraitStorage<Movable> movables = world.register(Movable.TYPE);
    /** The gravities. */
    private final TraitStorage<Gravity> gravities = world.register(Gravity.TYPE);
    /** The collidables. */
    private final TraitStorage<Collidable> collidables = world.register(Collidable.TYPE);
    /** The jumpables. */
    private final TraitStorage<Jumpable> jumpables = world.register(Jumpable.TYPE);
    /** The hero. */
    private final long hero = world.create();

    /**
     * Creates the world, the hero, the ground and the systems, and flushes.
     */
    public Landing()
    {
        final Transformable transformable = transformables.add(hero);
        transformable.setPosition(START_X, START_Y);
        transformable.setPreviousPosition(START_X, START_Y);
        transformable.setSize(SIZE, SIZE);
        movables.add(hero);
        final Gravity gravity = gravities.add(hero);
        gravity.setAcceleration(ACCELERATION);
        gravity.setTerminalVelocity(TERMINAL_VELOCITY);
        collidables.add(hero).setSize(SIZE, SIZE);
        jumpables.add(hero);
        final CollisionSystem collision = new CollisionSystem(world,
                                                              transformables,
                                                              collidables,
                                                              movables,
                                                              jumpables,
                                                              new Ground());
        final Query walkers = collision.getTileQuery();
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(stepSeconds -> walk(walkers), WALK_SLOT);
        world.addSystem(new GravitySystem(world, movables, gravities), GravitySystem.SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        world.addSystem(collision, CollisionSystem.SLOT);
        world.flush();
    }

    /**
     * Plays one step: capture, walk, gravity, movement, collision with the ground, flush.
     */
    public void step()
    {
        world.update(STEP_SECONDS);
    }

    /**
     * Returns the hero position.
     *
     * @return The hero transformable, as of the last step.
     */
    public Transformable getHero()
    {
        return transformables.get(hero);
    }

    /**
     * Returns the hero velocity.
     *
     * @return The hero movable, as of the last step.
     */
    public Movable getHeroMovable()
    {
        return movables.get(hero);
    }

    /**
     * Tells whether the hero stands on the floor.
     *
     * @return The grounded flag written by the collision of the last step.
     */
    public boolean isGrounded()
    {
        return jumpables.get(hero).isGrounded();
    }

    /**
     * Makes the grounded objects walk right.
     *
     * @param walkers The objects pushed out of the ground.
     */
    private void walk(Query walkers)
    {
        final int count = walkers.getCount();
        for (int i = 0; i < count; i++)
        {
            final long id = walkers.getId(i);
            if (jumpables.has(id) && jumpables.get(id).isGrounded())
            {
                final Movable movable = movables.get(id);
                movable.setVelocity(WALK_SPEED, movable.getVelocityY());
            }
        }
    }
}
