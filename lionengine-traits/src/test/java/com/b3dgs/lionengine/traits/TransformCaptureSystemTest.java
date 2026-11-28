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

import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * Tests of {@link TransformCaptureSystem}: the previous position is the position at the start of the step
 * (REQ-STD-001, REQ-STD-011, ADR-0030, decision 8, ADR-0080).
 */
final class TransformCaptureSystemTest
{
    /** The step, in seconds. */
    private static final double STEP = 1.0 / 60.0;
    /** Slot of the game system of the tests, in the input and intent range (ADR-0034). */
    private static final int GAME_SLOT = 500;

    /** The world. */
    private final World world = new World();
    /** The transformables. */
    private final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
    /** The movables. */
    private final TraitStorage<Movable> movables = world.register(Movable.TYPE);

    /**
     * The capture runs before every other system: a game system moving an object at slot 500 and the movement at
     * slot 3000 both leave the previous position at the value of the step start, even when the capture is added last.
     */
    @Test
    void testCapturedAtStepStart()
    {
        final long walker = create(1.0, 2.0);
        final long runner = create(0.0, 0.0);
        movables.add(runner).setVelocity(60.0, -30.0);
        world.flush();
        final double[] teleport =
        {
            5.0, 6.0
        };
        world.addSystem(step -> transformables.get(walker).setPosition(teleport[0], teleport[1]), GAME_SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);

        world.update(STEP);
        TransformableTest.assertState(transformables.get(walker), 5.0, 6.0, 1.0, 1.0, 1.0, 2.0);
        TransformableTest.assertState(transformables.get(runner), 1.0, -0.5, 1.0, 1.0, 0.0, 0.0);

        teleport[0] = 7.0;
        world.update(STEP);
        TransformableTest.assertState(transformables.get(walker), 7.0, 6.0, 1.0, 1.0, 5.0, 6.0);
        TransformableTest.assertState(transformables.get(runner), 2.0, -1.0, 1.0, 1.0, 1.0, -0.5);
    }

    /**
     * An object created during a step keeps the previous position the game gave it until the next step start.
     */
    @Test
    void testCreatedDuringStep()
    {
        final long[] created = new long[1];
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(step ->
        {
            if (created[0] == World.NO_OBJECT)
            {
                created[0] = world.create();
                final Transformable transformable = transformables.add(created[0]);
                transformable.setPosition(3.0, 4.0);
                transformable.setPreviousPosition(3.0, 4.0);
            }
            else
            {
                transformables.get(created[0]).setPosition(8.0, 9.0);
            }
        }, GAME_SLOT);

        world.update(STEP);
        TransformableTest.assertState(transformables.get(created[0]), 3.0, 4.0, 0.0, 0.0, 3.0, 4.0);
        world.update(STEP);
        TransformableTest.assertState(transformables.get(created[0]), 8.0, 9.0, 0.0, 0.0, 3.0, 4.0);
    }

    /**
     * An object carrying an excluded trait type is not captured; the others are (REQ-STD-011).
     */
    @Test
    void testExcluded()
    {
        final TraitStorage<Object> proxies = world.register(new MarkerType());
        final TransformCaptureSystem capture = new TransformCaptureSystem(world, transformables, new TraitStorage<?>[]
        {
            proxies
        });
        world.addSystem(capture, TransformCaptureSystem.SLOT);
        final long kept = create(1.0, 1.0);
        final long proxy = create(2.0, 2.0);
        proxies.add(proxy);
        world.flush();
        transformables.get(kept).setPosition(4.0, 4.0);
        transformables.get(proxy).setPosition(5.0, 5.0);

        world.update(STEP);

        assertEquals(1, capture.getQuery().getCount());
        TransformableTest.assertState(transformables.get(kept), 4.0, 4.0, 1.0, 1.0, 4.0, 4.0);
        TransformableTest.assertState(transformables.get(proxy), 5.0, 5.0, 1.0, 1.0, 2.0, 2.0);
    }

    /**
     * Invalid arguments are refused.
     */
    @Test
    void testInvalidArguments()
    {
        final TraitStorage<?>[] none = new TraitStorage<?>[0];
        final World other = new World();
        final TraitStorage<Transformable> foreign = other.register(Transformable.TYPE);

        assertThrows(IllegalArgumentException.class, () -> new TransformCaptureSystem(null, transformables));
        assertThrows(IllegalArgumentException.class, () -> new TransformCaptureSystem(world, null));
        assertThrows(IllegalArgumentException.class, () -> new TransformCaptureSystem(world, transformables, null));
        assertThrows(IllegalArgumentException.class, () -> new TransformCaptureSystem(world, foreign, none));
        assertThrows(IllegalArgumentException.class,
                     () -> new TransformCaptureSystem(world, transformables, new TraitStorage<?>[]
                     {
                         transformables
                     }));
        assertThrows(IllegalArgumentException.class,
                     () -> new TransformCaptureSystem(world, transformables, new TraitStorage<?>[]
                     {
                         null
                     }));
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
