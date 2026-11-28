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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * Tests of {@link JumpSystem} with the standard pipeline (capture, {@code Jump}, {@code Gravity}, {@code Movement} and
 * the tile push-out of {@code Collision}): the jump when grounded, the coyote window and the jump buffer accepted just
 * inside and refused just outside, the early release with the cut on and off, no double jump in the air, excluded
 * types and objects without {@link Movable}, at steps of 1/30, 1/60 and 1/120 s (REQ-STD-004, REQ-STD-008,
 * REQ-STD-011, ADR-0030, ADR-0034, ADR-0084).
 */
final class JumpSystemTest
{
    /** Gravity, in meters per second squared. */
    private static final double GRAVITY = -9.81;
    /** Jump impulse, in meters per second. */
    private static final double IMPULSE = 8.0;
    /** Walking speed, in meters per second. */
    private static final double WALK = 2.0;
    /** Size of the object boxes, in meters. */
    private static final double SIZE = 0.5;
    /** Half of {@link #SIZE}. */
    private static final double HALF = SIZE / 2.0;
    /** Top of the floor, in meters. */
    private static final double FLOOR = 1.0;
    /** Steps of the coyote and buffer windows, in steps of the system; each window adds one half step. */
    private static final int WINDOW_STEPS = 3;

    /** The world. */
    private final World world = new World();
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
    /** The grid: a floor of 6 tiles on row 0, a ledge at {@code x = 6}, nothing beyond. */
    private final TileGrid grid = new TileGrid(16, 64).fill(0, 0, 6, 1);

    /**
     * Adds the standard pipeline: capture, jump, gravity, movement and collision with the tiles.
     *
     * @return The jump system.
     */
    private JumpSystem pipeline()
    {
        final JumpSystem jump = new JumpSystem(world, movables, jumpables);
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(jump, JumpSystem.SLOT);
        world.addSystem(new GravitySystem(world, movables, gravities), GravitySystem.SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        world.addSystem(new CollisionSystem(world, transformables, collidables, movables, jumpables, grid),
                        CollisionSystem.SLOT);
        return jump;
    }

    /**
     * Creates a jumper of {@link #SIZE} with the gravity and the impulse, flushed.
     *
     * @param x The center X, in meters.
     * @param y The center Y, in meters.
     * @param velocityX The horizontal velocity, in meters per second.
     * @return The object.
     */
    private long jumper(double x, double y, double velocityX)
    {
        final long id = world.create();
        final Transformable transformable = transformables.add(id);
        transformable.setPosition(x, y);
        transformable.setPreviousPosition(x, y);
        transformable.setSize(SIZE, SIZE);
        movables.add(id).setVelocity(velocityX, 0.0);
        gravities.add(id).setAcceleration(GRAVITY);
        collidables.add(id).setSize(SIZE, SIZE);
        jumpables.add(id).setImpulse(IMPULSE);
        world.flush();
        return id;
    }

    /**
     * Runs steps.
     *
     * @param steps The steps.
     * @param stepSeconds The step, in seconds.
     */
    private void run(int steps, double stepSeconds)
    {
        for (int i = 0; i < steps; i++)
        {
            world.update(stepSeconds);
        }
    }

    /**
     * Returns the vertical velocity of an object.
     *
     * @param id The object.
     * @return The vertical velocity, in meters per second.
     */
    private double velocityY(long id)
    {
        return movables.get(id).getVelocityY();
    }

    /**
     * Holds or releases the jump of an object, then plays one step.
     *
     * @param id The object.
     * @param held {@code true} to hold the jump.
     * @param stepSeconds The step, in seconds.
     */
    private void step(long id, boolean held, double stepSeconds)
    {
        jumpables.get(id).setJumpHeld(held);
        world.update(stepSeconds);
    }

    /**
     * A grounded object jumps on a press: its vertical velocity is replaced by the impulse, then accelerated by the
     * gravity of the step; it leaves the ground, rises to about {@code impulse^2 / (2 g)} and lands; holding the jump
     * does not jump again, a new press does.
     *
     * @param updatesPerSecond The step rate.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testJumpWhenGrounded(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        final JumpSystem jump = pipeline();
        final long id = jumper(2.5, FLOOR + HALF, 0.0);
        run(updatesPerSecond / 2, step);
        assertTrue(jumpables.get(id).isGrounded());
        assertEquals(1, jump.getQuery().getCount());

        step(id, true, step);

        assertEquals(IMPULSE + GRAVITY * step, velocityY(id));
        assertFalse(jumpables.get(id).isGrounded());
        assertEquals(0.0, jumpables.get(id).getCoyoteRemaining());
        assertEquals(0.0, jumpables.get(id).getBufferRemaining());
        assertEquals(FLOOR + HALF + (IMPULSE + GRAVITY * step) * step, transformables.get(id).getY(), 1e-12);
        assertEquals(2.5, transformables.get(id).getX());

        double top = 0.0;
        while (!jumpables.get(id).isGrounded())
        {
            step(id, true, step);
            top = Math.max(top, transformables.get(id).getY());
        }
        final double height = IMPULSE * IMPULSE / (-2.0 * GRAVITY);
        assertEquals(height, top - (FLOOR + HALF), IMPULSE * step);
        assertEquals(FLOOR + HALF, transformables.get(id).getY());

        for (int i = 0; i < updatesPerSecond; i++)
        {
            step(id, true, step);
            assertEquals(0.0, velocityY(id), "Held on landing: no new jump");
            assertTrue(jumpables.get(id).isGrounded());
        }
        step(id, false, step);
        step(id, true, step);
        assertEquals(IMPULSE + GRAVITY * step, velocityY(id));
    }

    /**
     * Walks an object off the ledge and returns it at the first step whose collision did not ground it: the next step
     * of the jump system is the first one in the air.
     *
     * @param coyoteSteps The coyote time, in steps, plus one half step.
     * @param step The step, in seconds.
     * @return The object.
     */
    private long walkOff(int coyoteSteps, double step)
    {
        pipeline();
        final long id = jumper(4.5, FLOOR + HALF, WALK);
        final Jumpable jumpable = jumpables.get(id);
        jumpable.setCoyoteTime((coyoteSteps + 0.5) * step);
        jumpable.setJumpBuffer(0.0);
        world.update(step);
        while (jumpable.isGrounded())
        {
            world.update(step);
            assertEquals(jumpable.getCoyoteTime(), jumpable.getCoyoteRemaining());
        }
        return id;
    }

    /**
     * Walking off a ledge, a press at the last step of the coyote time jumps.
     *
     * @param updatesPerSecond The step rate.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testCoyoteInside(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        final long id = walkOff(WINDOW_STEPS, step);
        run(WINDOW_STEPS - 1, step);
        assertTrue(velocityY(id) < 0.0);
        assertEquals(0.5 * step + step, jumpables.get(id).getCoyoteRemaining(), 1e-12);

        step(id, true, step);

        assertEquals(IMPULSE + GRAVITY * step, velocityY(id));
        assertEquals(WALK, movables.get(id).getVelocityX());
        assertEquals(0.0, jumpables.get(id).getCoyoteRemaining());
    }

    /**
     * Walking off a ledge, a press one step after the coyote time does not jump, and is not kept without a jump buffer.
     *
     * @param updatesPerSecond The step rate.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testCoyoteOutside(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        final long id = walkOff(WINDOW_STEPS, step);
        run(WINDOW_STEPS, step);
        assertEquals(0.5 * step, jumpables.get(id).getCoyoteRemaining(), 1e-12);
        final double before = velocityY(id);

        step(id, true, step);

        assertEquals(before + GRAVITY * step, velocityY(id), 1e-12);
        assertEquals(0.0, jumpables.get(id).getCoyoteRemaining());
        for (int i = 0; i < updatesPerSecond; i++)
        {
            final double falling = velocityY(id);
            step(id, true, step);
            assertTrue(velocityY(id) < falling);
        }
    }

    /**
     * Drops an object from 3 m, counting the steps of its fall: the returned count is the step whose collision
     * grounded it, so the jump system sees it grounded at the next step.
     *
     * @param step The step, in seconds.
     * @return The steps until the landing.
     */
    private static int landingSteps(double step)
    {
        final JumpSystemTest reference = new JumpSystemTest();
        reference.pipeline();
        final long id = reference.jumper(2.5, 3.0, 0.0);
        int steps = 0;
        while (!reference.jumpables.get(id).isGrounded())
        {
            reference.world.update(step);
            steps++;
        }
        return steps;
    }

    /**
     * Drops an object from 3 m with a jump buffer of {@link #WINDOW_STEPS} steps and a half, no coyote time, and
     * presses the jump a given number of steps before the first step that sees it grounded, then holds it.
     *
     * @param ahead The steps from the press to the first step that sees the object grounded.
     * @param step The step, in seconds.
     * @return The object, after the step whose collision landed it.
     */
    private long buffered(int ahead, double step)
    {
        final int landing = landingSteps(step);
        pipeline();
        final long id = jumper(2.5, 3.0, 0.0);
        final Jumpable jumpable = jumpables.get(id);
        jumpable.setCoyoteTime(0.0);
        jumpable.setJumpBuffer((WINDOW_STEPS + 0.5) * step);
        run(landing - ahead, step);
        step(id, true, step);
        assertFalse(jumpable.isGrounded(), "The press is in the air");
        assertEquals(jumpable.getJumpBuffer(), jumpable.getBufferRemaining());
        for (int i = 1; i < ahead; i++)
        {
            step(id, true, step);
        }
        return id;
    }

    /**
     * A press in the air within the jump buffer before landing jumps at the first step that sees the object
     * grounded, the step after its collision landed it.
     *
     * @param updatesPerSecond The step rate.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testBufferInside(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        final long id = buffered(WINDOW_STEPS, step);
        assertTrue(jumpables.get(id).isGrounded());
        assertEquals(0.0, velocityY(id));
        assertEquals(1.5 * step, jumpables.get(id).getBufferRemaining(), 1e-12);

        step(id, true, step);

        assertEquals(IMPULSE + GRAVITY * step, velocityY(id));
        assertFalse(jumpables.get(id).isGrounded());
        assertEquals(0.0, jumpables.get(id).getBufferRemaining());
    }

    /**
     * A press in the air one step earlier than the jump buffer allows does not jump on landing.
     *
     * @param updatesPerSecond The step rate.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testBufferOutside(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        final long id = buffered(WINDOW_STEPS + 1, step);
        assertTrue(jumpables.get(id).isGrounded());

        for (int i = 0; i < updatesPerSecond; i++)
        {
            step(id, true, step);
            assertEquals(0.0, velocityY(id));
            assertTrue(jumpables.get(id).isGrounded());
            assertEquals(0.0, jumpables.get(id).getBufferRemaining());
        }
    }

    /**
     * Jumps from the floor and holds the jump for two more steps.
     *
     * @param variableHeight The variable height switch.
     * @param step The step, in seconds.
     * @return The object, ascending.
     */
    private long jumpAndHold(boolean variableHeight, double step)
    {
        pipeline();
        final long id = jumper(2.5, FLOOR + HALF, 0.0);
        jumpables.get(id).setVariableHeight(variableHeight);
        run(2, step);
        assertTrue(jumpables.get(id).isGrounded());
        step(id, true, step);
        step(id, true, step);
        step(id, true, step);
        assertTrue(velocityY(id) > 0.0);
        return id;
    }

    /**
     * With the variable height on, an early release while ascending multiplies the upward velocity by the cut factor,
     * once per jump: a second press and release in the air cuts nothing, and the jump lands lower.
     *
     * @param updatesPerSecond The step rate.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testCutOn(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        final long id = jumpAndHold(true, step);
        final double ascending = velocityY(id);

        step(id, false, step);

        assertEquals(ascending * Jumpable.DEFAULT_CUT_FACTOR + GRAVITY * step, velocityY(id));
        step(id, true, step);
        final double again = velocityY(id);
        assertTrue(again > 0.0);
        step(id, false, step);
        assertEquals(again + GRAVITY * step, velocityY(id), 1e-12, "Cut once per jump");
    }

    /**
     * With the variable height off, an early release while ascending changes nothing.
     *
     * @param updatesPerSecond The step rate.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testCutOff(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        final long id = jumpAndHold(false, step);
        final double ascending = velocityY(id);

        step(id, false, step);

        assertEquals(ascending + GRAVITY * step, velocityY(id), 1e-12);
    }

    /**
     * With the variable height on, a release after the apex, while descending, cuts nothing.
     */
    @Test
    void testReleaseDescending()
    {
        final double step = 1.0 / 60.0;
        final long id = jumpAndHold(true, step);
        while (velocityY(id) > 0.0)
        {
            step(id, true, step);
        }
        final double descending = velocityY(id);

        step(id, false, step);

        assertEquals(descending + GRAVITY * step, velocityY(id), 1e-12);
    }

    /**
     * A press in the air after a jump does not jump again; kept by the jump buffer, it expires before the landing.
     *
     * @param updatesPerSecond The step rate.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testNoDoubleJump(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        final long id = jumpAndHold(false, step);
        step(id, false, step);
        final double ascending = velocityY(id);

        step(id, true, step);

        assertEquals(ascending + GRAVITY * step, velocityY(id), 1e-12);
        while (!jumpables.get(id).isGrounded())
        {
            final double before = velocityY(id);
            step(id, true, step);
            assertTrue(velocityY(id) < before || jumpables.get(id).isGrounded());
        }
        step(id, true, step);
        assertEquals(0.0, velocityY(id));
        assertTrue(jumpables.get(id).isGrounded());
    }

    /**
     * Without collision, the grounded flag set by the game allows the jump, which clears it and keeps the horizontal
     * velocity; a press with no jump buffer still jumps when grounded; an object without {@link Movable} is not
     * processed.
     */
    @Test
    void testGroundedByGame()
    {
        final JumpSystem jump = new JumpSystem(world, movables, jumpables);
        world.addSystem(jump, JumpSystem.SLOT);
        final long id = world.create();
        movables.add(id).setVelocity(1.5, -2.0);
        final Jumpable jumpable = jumpables.add(id);
        jumpable.setImpulse(IMPULSE);
        jumpable.setJumpBuffer(0.0);
        final long still = world.create();
        jumpables.add(still).setGrounded(true);
        world.flush();

        jumpable.setGrounded(true);
        world.update(0.1);
        assertEquals(Jumpable.DEFAULT_COYOTE_TIME, jumpable.getCoyoteRemaining());
        step(id, true, 0.1);
        step(still, true, 0.1);

        assertEquals(IMPULSE, velocityY(id));
        assertEquals(1.5, movables.get(id).getVelocityX());
        assertFalse(jumpable.isGrounded());
        assertEquals(1, jump.getQuery().getCount());
        assertTrue(jumpables.get(still).isGrounded());
        assertEquals(0.0, jumpables.get(still).getBufferRemaining());
        assertEquals(0.0, jumpables.get(still).getCoyoteRemaining());
    }

    /**
     * An object carrying an excluded trait type does not jump; once the type is removed it does.
     */
    @Test
    void testExcluded()
    {
        final TraitStorage<Object> proxies = world.register(new MarkerType());
        final JumpSystem jump = new JumpSystem(world, movables, jumpables, new TraitStorage<?>[]
        {
            proxies
        });
        world.addSystem(jump, JumpSystem.SLOT);
        final long id = world.create();
        movables.add(id);
        final Jumpable jumpable = jumpables.add(id);
        jumpable.setImpulse(IMPULSE);
        jumpable.setGrounded(true);
        proxies.add(id);
        world.flush();

        step(id, true, 0.1);
        assertEquals(0.0, velocityY(id));
        assertEquals(0, jump.getQuery().getCount());

        proxies.remove(id);
        world.flush();
        step(id, false, 0.1);
        step(id, true, 0.1);
        assertEquals(IMPULSE, velocityY(id));
    }

    /**
     * Invalid arguments are refused.
     */
    @Test
    void testInvalidArguments()
    {
        final TraitStorage<?>[] none = {};
        assertThrows(IllegalArgumentException.class, () -> new JumpSystem(null, movables, jumpables));
        assertThrows(IllegalArgumentException.class, () -> new JumpSystem(world, null, jumpables));
        assertThrows(IllegalArgumentException.class, () -> new JumpSystem(world, movables, null));
        assertThrows(IllegalArgumentException.class, () -> new JumpSystem(world, movables, jumpables, null));
        assertThrows(IllegalArgumentException.class, () -> new JumpSystem(world, null, jumpables, none));
    }
}
