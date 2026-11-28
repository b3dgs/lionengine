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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;
import com.sun.management.ThreadMXBean;

/**
 * Allocation measure of the steady step of {@link JumpSystem} in the standard pipeline: objects on a tile floor press,
 * hold and release the jump with the coyote time, the jump buffer and the cut, landing and leaving the ground; nothing
 * is allocated per step (REQ-STD-004, REQ-ECS-009, ADR-0030, ADR-0084).
 *
 * <p>
 * Measured with the per-thread allocation counter of the JVM over 1,000 steps of 1,000 objects, after a warm-up of
 * 1,000 steps that grows the capacities and the tile buffer, with the 1 KiB tolerance of the other allocation tests.
 * </p>
 */
final class JumpAllocationTest
{
    /** Objects. */
    private static final int OBJECTS = 1000;
    /** Measured steps. */
    private static final int STEPS = 1000;
    /** Allocation tolerance, in bytes, over every measured step. */
    private static final long ALLOCATION_TOLERANCE = 1024L;
    /** The step, in seconds. */
    private static final double STEP = 1.0 / 60.0;
    /** Floor width, in tiles. */
    private static final int WIDTH = 1000;
    /** The slot of the game system holding the jumps. */
    private static final int INPUT_SLOT = 500;
    /** Steps of one press and release cycle. */
    private static final int CYCLE = 20;

    /** Steps played. */
    private int steps;
    /** Jumps seen. */
    private long jumps;

    /**
     * Steps of 1,000 jumping objects, after a warm-up, allocate nothing.
     */
    @Test
    void testSteadyStateAllocatesNothing()
    {
        final World world = new World();
        final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
        final TraitStorage<Movable> movables = world.register(Movable.TYPE);
        final TraitStorage<Gravity> gravities = world.register(Gravity.TYPE);
        final TraitStorage<Collidable> collidables = world.register(Collidable.TYPE);
        final TraitStorage<Jumpable> jumpables = world.register(Jumpable.TYPE);
        final JumpSystem jump = new JumpSystem(world, movables, jumpables);
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(stepSeconds -> hold(jump, jumpables), INPUT_SLOT);
        world.addSystem(jump, JumpSystem.SLOT);
        world.addSystem(stepSeconds -> count(jump, movables), JumpSystem.SLOT + 1);
        world.addSystem(new GravitySystem(world, movables, gravities), GravitySystem.SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        world.addSystem(new CollisionSystem(world,
                                            transformables,
                                            collidables,
                                            movables,
                                            jumpables,
                                            new TileGrid(WIDTH, 8).fill(0, 0, WIDTH, 1)),
                        CollisionSystem.SLOT);
        for (int i = 0; i < OBJECTS; i++)
        {
            final long id = world.create();
            final Transformable transformable = transformables.add(id);
            transformable.setPosition(i + 0.5, 1.25 + i % 3);
            transformable.setPreviousPosition(i + 0.5, 1.25 + i % 3);
            transformable.setSize(0.5, 0.5);
            movables.add(id);
            final Gravity gravity = gravities.add(id);
            gravity.setAcceleration(-9.81);
            gravity.setTerminalVelocity(10.0);
            collidables.add(id).setSize(0.5, 0.5);
            final Jumpable jumpable = jumpables.add(id);
            jumpable.setImpulse(5.0);
            jumpable.setVariableHeight(i % 2 == 0);
        }
        world.flush();
        for (int step = 0; step < STEPS; step++)
        {
            world.update(STEP);
        }
        final long warmJumps = jumps;

        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int step = 0; step < STEPS; step++)
        {
            world.update(STEP);
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertTrue(jumps > warmJumps, "No jump in the measured steps");
        assertEquals(OBJECTS, jump.getQuery().getCount());
    }

    /**
     * Holds the jump of every object for half of a cycle, shifted by object, so that presses and releases happen at
     * every step.
     *
     * @param jump The jump system.
     * @param jumpables The jumpables.
     */
    private void hold(JumpSystem jump, TraitStorage<Jumpable> jumpables)
    {
        steps++;
        final int count = jump.getQuery().getCount();
        for (int i = 0; i < count; i++)
        {
            final long id = jump.getQuery().getId(i);
            jumpables.get(id).setJumpHeld((steps + i) % CYCLE < CYCLE / 2);
        }
    }

    /**
     * Counts the objects that just jumped: their vertical velocity is the impulse.
     *
     * @param jump The jump system.
     * @param movables The movables.
     */
    private void count(JumpSystem jump, TraitStorage<Movable> movables)
    {
        final int count = jump.getQuery().getCount();
        for (int i = 0; i < count; i++)
        {
            if (Double.compare(movables.get(jump.getQuery().getId(i)).getVelocityY(), 5.0) == 0)
            {
                jumps++;
            }
        }
    }
}
