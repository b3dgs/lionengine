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
 * Tests of {@link MovementSystem}: integration of the velocity with the step and the speed clamp, at several step
 * durations (REQ-STD-002, REQ-STD-008, REQ-STD-011, ADR-0017, ADR-0034, ADR-0080).
 */
final class MovementSystemTest
{
    /** Tolerance on positions accumulated over many steps, in meters. */
    private static final double EPSILON = 1e-9;

    /** The world. */
    private final World world = new World();
    /** The transformables. */
    private final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
    /** The movables. */
    private final TraitStorage<Movable> movables = world.register(Movable.TYPE);

    /**
     * One second of steps moves an object by its velocity in meters, whatever the step; the previous position is one
     * step behind.
     *
     * @param updatesPerSecond The updates per second: steps of 1/30, 1/60 and 1/120 s.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testIntegration(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        addSystems();
        final long id = create(1.0, 2.0);
        movables.add(id).setVelocity(2.0, -3.0);
        world.flush();

        for (int i = 0; i < updatesPerSecond; i++)
        {
            world.update(step);
        }

        final Transformable transformable = transformables.get(id);
        assertEquals(3.0, transformable.getX(), EPSILON);
        assertEquals(-1.0, transformable.getY(), EPSILON);
        assertEquals(3.0 - 2.0 * step, transformable.getPreviousX(), EPSILON);
        assertEquals(-1.0 + 3.0 * step, transformable.getPreviousY(), EPSILON);
        assertEquals(2.0, movables.get(id).getVelocityX());
        assertEquals(-3.0, movables.get(id).getVelocityY());
    }

    /**
     * A velocity above the maximum speed is scaled down to it, direction kept, before the integration, and stays
     * clamped; one second of steps then covers the maximum speed in meters along the velocity direction.
     *
     * @param updatesPerSecond The updates per second: steps of 1/30, 1/60 and 1/120 s.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testClamp(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        addSystems();
        final long id = create(0.0, 0.0);
        final Movable movable = movables.add(id);
        movable.setVelocity(-30.0, 40.0);
        movable.setMaxSpeed(10.0);
        final long diagonal = create(0.0, 0.0);
        final Movable second = movables.add(diagonal);
        second.setVelocity(8.0, 8.0);
        second.setMaxSpeed(10.0);
        world.flush();

        world.update(step);
        assertEquals(-6.0, movables.get(id).getVelocityX(), EPSILON);
        assertEquals(8.0, movables.get(id).getVelocityY(), EPSILON);
        assertEquals(-6.0 * step, transformables.get(id).getX(), EPSILON);
        assertEquals(8.0 * step, transformables.get(id).getY(), EPSILON);
        final double component = 10.0 / Math.sqrt(2.0);
        assertEquals(component * step, transformables.get(diagonal).getX(), EPSILON);
        assertEquals(component * step, transformables.get(diagonal).getY(), EPSILON);

        for (int i = 1; i < updatesPerSecond; i++)
        {
            world.update(step);
        }
        assertEquals(-6.0, transformables.get(id).getX(), EPSILON);
        assertEquals(8.0, transformables.get(id).getY(), EPSILON);
        assertEquals(component, transformables.get(diagonal).getX(), EPSILON);
        assertEquals(component, transformables.get(diagonal).getY(), EPSILON);
    }

    /**
     * A velocity raised by an earlier system (slot 2000, like {@code Gravity}) is clamped in the same step; a zero
     * maximum speed stops the object.
     */
    @Test
    void testClampAfterEarlierSystem()
    {
        final double step = 1.0 / 60.0;
        addSystems();
        final long id = create(0.0, 0.0);
        movables.add(id).setMaxSpeed(5.0);
        final long stopped = create(1.0, 1.0);
        final Movable blocked = movables.add(stopped);
        blocked.setVelocity(3.0, 3.0);
        blocked.setMaxSpeed(0.0);
        world.flush();
        world.addSystem(s ->
        {
            final Movable movable = movables.get(id);
            movable.setVelocity(movable.getVelocityX(), movable.getVelocityY() - 100.0);
        }, 2000);

        world.update(step);

        assertEquals(-5.0, movables.get(id).getVelocityY(), EPSILON);
        assertEquals(-5.0 * step, transformables.get(id).getY(), EPSILON);
        assertEquals(1.0, transformables.get(stopped).getX());
        assertEquals(1.0, transformables.get(stopped).getY());
    }

    /**
     * Only objects with both traits and no excluded trait move (REQ-STD-011).
     */
    @Test
    void testQuery()
    {
        final TraitStorage<Object> proxies = world.register(new MarkerType());
        final MovementSystem movement = new MovementSystem(world, transformables, movables, new TraitStorage<?>[]
        {
            proxies
        });
        world.addSystem(movement, MovementSystem.SLOT);
        final long moving = create(0.0, 0.0);
        movables.add(moving).setVelocity(60.0, 0.0);
        final long still = create(0.0, 0.0);
        final long proxy = create(0.0, 0.0);
        movables.add(proxy).setVelocity(60.0, 0.0);
        proxies.add(proxy);
        movables.add(world.create()).setVelocity(60.0, 0.0);
        world.flush();

        world.update(1.0 / 60.0);

        assertEquals(1, movement.getQuery().getCount());
        assertEquals(1.0, transformables.get(moving).getX(), EPSILON);
        assertEquals(0.0, transformables.get(still).getX());
        assertEquals(0.0, transformables.get(proxy).getX());
    }

    /**
     * Invalid arguments are refused.
     */
    @Test
    void testInvalidArguments()
    {
        final TraitStorage<?>[] none = new TraitStorage<?>[0];
        final World other = new World();
        final TraitStorage<Movable> foreign = other.register(Movable.TYPE);

        assertThrows(IllegalArgumentException.class, () -> new MovementSystem(null, transformables, movables));
        assertThrows(IllegalArgumentException.class, () -> new MovementSystem(world, null, movables));
        assertThrows(IllegalArgumentException.class, () -> new MovementSystem(world, transformables, null));
        assertThrows(IllegalArgumentException.class, () -> new MovementSystem(world, transformables, movables, null));
        assertThrows(IllegalArgumentException.class, () -> new MovementSystem(world, transformables, foreign, none));
        assertThrows(IllegalArgumentException.class,
                     () -> new MovementSystem(world, transformables, movables, new TraitStorage<?>[]
                     {
                         movables
                     }));
    }

    /**
     * Adds the capture and the movement at their slots.
     */
    private void addSystems()
    {
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
    }

    /**
     * Requests an object with a transformable of size 1 m whose previous position is its position.
     *
     * @param x The horizontal position, in meters.
     * @param y The vertical position, in meters.
     * @return The object ID, pending until the flush.
     */
    private long create(double x, double y)
    {
        final long id = world.create();
        final Transformable transformable = transformables.add(id);
        transformable.setPosition(x, y);
        transformable.setPreviousPosition(x, y);
        transformable.setSize(1.0, 1.0);
        return id;
    }
}
