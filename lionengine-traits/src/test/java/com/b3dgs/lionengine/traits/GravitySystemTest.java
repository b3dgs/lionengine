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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * Tests of {@link GravitySystem}: acceleration of the velocity with the step and the terminal velocity, at several
 * step durations (REQ-STD-003, REQ-STD-008, REQ-STD-011, ADR-0017, ADR-0030, ADR-0034, ADR-0081).
 */
final class GravitySystemTest
{
    /** Tolerance on velocities and positions accumulated over many steps. */
    private static final double EPSILON = 1e-9;
    /** The acceleration of the tests, in meters per second squared. */
    private static final double ACCELERATION = -6.0;

    /** The world. */
    private final World world = new World();
    /** The transformables. */
    private final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
    /** The movables. */
    private final TraitStorage<Movable> movables = world.register(Movable.TYPE);
    /** The gravities. */
    private final TraitStorage<Gravity> gravities = world.register(Gravity.TYPE);

    /**
     * One second of steps adds the acceleration to the vertical velocity, whatever the step; the horizontal velocity
     * is unchanged; the movement then integrates it (explicit Euler: {@code a * (1 + dt) / 2} after one second).
     *
     * @param updatesPerSecond The updates per second: steps of 1/30, 1/60 and 1/120 s.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testAcceleration(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        addSystems();
        final long id = create(ACCELERATION, Double.POSITIVE_INFINITY, 2.0, 0.0);
        world.flush();

        for (int i = 1; i <= updatesPerSecond; i++)
        {
            world.update(step);
            assertEquals(ACCELERATION * i * step, movables.get(id).getVelocityY(), EPSILON, "Step " + i);
        }

        assertEquals(ACCELERATION, movables.get(id).getVelocityY(), EPSILON);
        assertEquals(2.0, movables.get(id).getVelocityX());
        assertEquals(2.0, transformables.get(id).getX(), EPSILON);
        assertEquals(ACCELERATION * (1.0 + step) / 2.0, transformables.get(id).getY(), EPSILON);
    }

    /**
     * The fall speeds up until the terminal velocity, then stays at it exactly; an upward pull stops at its own limit;
     * a velocity beyond the limit is kept; an upward velocity is slowed down normally.
     *
     * @param updatesPerSecond The updates per second: steps of 1/30, 1/60 and 1/120 s.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testTerminalVelocity(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        addSystems();
        final long falling = create(ACCELERATION, 3.0, 0.0, 0.0);
        final long rising = create(4.0, 2.0, 0.0, 0.0);
        final long diving = create(ACCELERATION, 3.0, 0.0, -10.0);
        final long thrown = create(ACCELERATION, 3.0, 0.0, 3.0);
        world.flush();

        for (int i = 1; i <= updatesPerSecond; i++)
        {
            world.update(step);
            assertEquals(Math.max(ACCELERATION * i * step, -3.0),
                         movables.get(falling).getVelocityY(),
                         EPSILON,
                         "Step " + i);
            assertEquals(Math.min(4.0 * i * step, 2.0), movables.get(rising).getVelocityY(), EPSILON, "Step " + i);
            assertEquals(Math.max(3.0 + ACCELERATION * i * step, -3.0),
                         movables.get(thrown).getVelocityY(),
                         EPSILON,
                         "Step " + i);
        }

        assertEquals(-3.0, movables.get(falling).getVelocityY());
        assertEquals(2.0, movables.get(rising).getVelocityY());
        assertEquals(-10.0, movables.get(diving).getVelocityY());
        assertEquals(-3.0, movables.get(thrown).getVelocityY(), EPSILON);
    }

    /**
     * The maximum speed of {@link Movable} still clamps the whole speed after the gravity, in the movement.
     */
    @Test
    void testMaxSpeedAfterGravity()
    {
        addSystems();
        final long id = create(-120.0, Double.POSITIVE_INFINITY, 0.0, 0.0);
        world.flush();
        movables.get(id).setMaxSpeed(1.0);

        world.update(1.0 / 60.0);

        assertEquals(-1.0, movables.get(id).getVelocityY(), EPSILON);
        assertEquals(-1.0 / 60.0, transformables.get(id).getY(), EPSILON);
    }

    /**
     * Only objects with a {@link Movable}, a {@link Gravity} and no excluded trait are accelerated; a
     * {@link Transformable} is not needed (REQ-STD-011).
     */
    @Test
    void testQuery()
    {
        final TraitStorage<Object> proxies = world.register(new MarkerType());
        final GravitySystem gravity = new GravitySystem(world, movables, gravities, new TraitStorage<?>[]
        {
            proxies
        });
        world.addSystem(gravity, GravitySystem.SLOT);
        final long pulled = world.create();
        movables.add(pulled);
        gravities.add(pulled).setAcceleration(ACCELERATION);
        final long weightless = world.create();
        gravities.add(weightless).setAcceleration(ACCELERATION);
        final long still = world.create();
        movables.add(still);
        final long proxy = world.create();
        movables.add(proxy);
        gravities.add(proxy).setAcceleration(ACCELERATION);
        proxies.add(proxy);
        world.flush();

        world.update(0.5);

        assertEquals(1, gravity.getQuery().getCount());
        assertEquals(-3.0, movables.get(pulled).getVelocityY());
        assertEquals(0.0, movables.get(still).getVelocityY());
        assertEquals(0.0, movables.get(proxy).getVelocityY());
    }

    /**
     * Invalid arguments are refused.
     */
    @Test
    void testInvalidArguments()
    {
        final TraitStorage<?>[] none = new TraitStorage<?>[0];
        final World other = new World();
        final TraitStorage<Gravity> foreign = other.register(Gravity.TYPE);

        assertThrows(IllegalArgumentException.class, () -> new GravitySystem(null, movables, gravities));
        assertThrows(IllegalArgumentException.class, () -> new GravitySystem(world, null, gravities));
        assertThrows(IllegalArgumentException.class, () -> new GravitySystem(world, movables, null));
        assertThrows(IllegalArgumentException.class, () -> new GravitySystem(world, movables, gravities, null));
        assertThrows(IllegalArgumentException.class, () -> new GravitySystem(world, movables, foreign, none));
        assertThrows(IllegalArgumentException.class,
                     () -> new GravitySystem(world, movables, gravities, new TraitStorage<?>[]
                     {
                         gravities
                     }));
    }

    /**
     * Adds the gravity and the movement at their slots.
     */
    private void addSystems()
    {
        world.addSystem(new GravitySystem(world, movables, gravities), GravitySystem.SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
    }

    /**
     * Requests an object at the origin with a velocity and a gravity.
     *
     * @param acceleration The vertical acceleration, in meters per second squared.
     * @param terminalVelocity The terminal velocity, in meters per second.
     * @param velocityX The horizontal velocity, in meters per second.
     * @param velocityY The vertical velocity, in meters per second.
     * @return The object ID, pending until the flush.
     */
    private long create(double acceleration, double terminalVelocity, double velocityX, double velocityY)
    {
        final long id = world.create();
        transformables.add(id);
        movables.add(id).setVelocity(velocityX, velocityY);
        final Gravity gravity = gravities.add(id);
        gravity.setAcceleration(acceleration);
        gravity.setTerminalVelocity(terminalVelocity);
        return id;
    }
}
