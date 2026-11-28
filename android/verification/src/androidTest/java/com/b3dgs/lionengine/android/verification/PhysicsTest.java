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

package com.b3dgs.lionengine.android.verification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b3dgs.lionengine.RandomSource;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.physics.Box;
import com.b3dgs.lionengine.physics.BroadPhase;
import com.b3dgs.lionengine.physics.ShapeQuery;
import com.b3dgs.lionengine.traits.Collidable;
import com.b3dgs.lionengine.traits.CollisionListener;
import com.b3dgs.lionengine.traits.CollisionSystem;
import com.b3dgs.lionengine.traits.Gravity;
import com.b3dgs.lionengine.traits.GravitySystem;
import com.b3dgs.lionengine.traits.JumpSystem;
import com.b3dgs.lionengine.traits.Jumpable;
import com.b3dgs.lionengine.traits.Movable;
import com.b3dgs.lionengine.traits.MovementSystem;
import com.b3dgs.lionengine.traits.TransformCaptureSystem;
import com.b3dgs.lionengine.traits.Transformable;

/**
 * Runs {@code physics} and the collision of {@code traits} on Android API 21 (ADR-0044): the strict box overlap, the
 * broad phase against a brute-force comparison on seeded random boxes, the reset state of {@code Collidable}, the
 * layer and mask rule both ways, and the enter and exit events of two objects meeting at steps of 1/30, 1/60 and
 * 1/120 s, exits before enters, with an exit when an object is removed; the tile push-out on a floor, a wall and a
 * ceiling with the grounded flag of {@code Jumpable} at the same step durations, and the reset state of
 * {@code Jumpable}; the {@code Jump} system on a tile floor (the jump, the cut on an early release) and with the
 * grounded flag set by the game (the coyote window and the jump buffer, accepted just inside and refused just
 * outside, no double jump) at the same step durations. Expected values are the ones of the JVM tests of
 * {@code lionengine-physics} and {@code lionengine-traits} (BoxTest, BroadPhaseTest, CollidableTest,
 * CollisionSystemTest, TileCollisionTest, JumpableTest, JumpSystemTest).
 */
@RunWith(AndroidJUnit4.class)
public final class PhysicsTest
{
    /**
     * Creates the test.
     */
    public PhysicsTest()
    {
        super();
    }

    /**
     * Boxes overlap when their interiors share an area; boxes touching by an edge or a corner do not; invalid values
     * are refused.
     */
    @Test
    public void testBoxOverlap()
    {
        final Box unit = new Box(0.0, 0.0, 1.0, 1.0);

        assertTrue(unit.overlaps(new Box(0.5, 0.5, 1.0, 1.0)));
        assertTrue(unit.overlaps(new Box(0.99, 0.0, 1.0, 1.0)));
        assertFalse(unit.overlaps(new Box(1.0, 0.0, 1.0, 1.0)));
        assertFalse(unit.overlaps(new Box(1.0, 1.0, 1.0, 1.0)));
        assertFalse(unit.overlaps(new Box(0.0, -1.0, 1.0, 1.0)));
        assertEquals(-0.5, new Box(1.0, -2.0, 3.0, 0.5).getMinX(), 0.0);
        assertThrows(IllegalArgumentException.class, () -> new Box(Double.NaN, 0.0, 1.0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new Box(0.0, 0.0, -1.0, 1.0));
    }

    /**
     * On seeded random boxes the broad phase finds exactly the pairs of the brute-force comparison, at several cell
     * sizes (BroadPhaseTest).
     */
    @Test
    public void testBroadPhase()
    {
        final double[] cellSizes = {0.25, 2.0, 100.0};
        for (final double cellSize : cellSizes)
        {
            final RandomSource random = new RandomSource(0x4C49_4F4EL);
            final Box[] boxes = new Box[150];
            for (int i = 0; i < boxes.length; i++)
            {
                boxes[i] = new Box(random.nextDouble() * 40.0 - 20.0,
                                   random.nextDouble() * 40.0 - 20.0,
                                   random.nextDouble() * 3.0,
                                   random.nextDouble() * 3.0);
            }
            final BroadPhase broadPhase = new BroadPhase(cellSize, 1);
            for (final Box box : boxes)
            {
                broadPhase.add(box);
            }
            final int pairs = broadPhase.findPairs();
            int expected = 0;
            for (int i = 0; i < boxes.length; i++)
            {
                for (int j = i + 1; j < boxes.length; j++)
                {
                    if (boxes[i].overlaps(boxes[j]))
                    {
                        expected++;
                    }
                }
            }

            assertEquals(expected, pairs);
            for (int p = 0; p < pairs; p++)
            {
                assertTrue(broadPhase.getFirst(p) < broadPhase.getSecond(p));
                assertTrue(boxes[broadPhase.getFirst(p)].overlaps(boxes[broadPhase.getSecond(p)]));
            }
        }
    }

    /**
     * A new or reset collidable has a box of size zero, layer 1, every mask layer and no trigger; invalid sizes are
     * refused (CollidableTest).
     */
    @Test
    public void testCollidableResetState()
    {
        final Collidable collidable = Collidable.TYPE.create();
        collidable.setSize(1.0, 2.0);
        collidable.setOffset(0.5, 0.5);
        collidable.setLayer(4);
        collidable.setMask(2);
        collidable.setTrigger(true);
        Collidable.TYPE.reset(collidable);

        assertEquals(0.0, collidable.getWidth(), 0.0);
        assertEquals(0.0, collidable.getOffsetY(), 0.0);
        assertEquals(1, collidable.getLayer());
        assertEquals(-1, collidable.getMask());
        assertFalse(collidable.isTrigger());
        assertThrows(IllegalArgumentException.class, () -> collidable.setSize(-1.0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> collidable.setOffset(Double.NaN, 0.0));
    }

    /**
     * A pair is detected only if each mask includes the other's layer, in both creation orders (CollisionSystemTest).
     */
    @Test
    public void testLayersAndMasks()
    {
        final int[][] cases = {{1, -1, 1, -1, 1}, {1, 2, 2, 1, 1}, {1, 2, 2, 2, 0}, {2, 2, 1, 2, 0}, {0, -1, 1, -1, 0}};
        for (final int[] values : cases)
        {
            final World world = new World();
            final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
            final TraitStorage<Collidable> collidables = world.register(Collidable.TYPE);
            final CollisionSystem collision = new CollisionSystem(world, transformables, collidables);
            world.addSystem(collision, CollisionSystem.SLOT);
            final long a = world.create();
            transformables.add(a);
            final Collidable first = collidables.add(a);
            first.setSize(1.0, 1.0);
            first.setLayer(values[0]);
            first.setMask(values[1]);
            final long b = world.create();
            transformables.add(b);
            final Collidable second = collidables.add(b);
            second.setSize(1.0, 1.0);
            second.setLayer(values[2]);
            second.setMask(values[3]);
            world.flush();

            world.update(1.0 / 60.0);

            assertEquals(values[4], collision.getPairCount());
        }
    }

    /**
     * Two objects moving toward each other enter at the first step their boxes overlap and exit at the first step they
     * no longer do, at every step duration; a removed object reports an exit at the next step (CollisionSystemTest).
     */
    @Test
    public void testEnterExitAndRemoval()
    {
        final int[] rates = {30, 60, 120};
        for (final int rate : rates)
        {
            final double step = 1.0 / rate;
            final World world = new World();
            final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
            final TraitStorage<Movable> movables = world.register(Movable.TYPE);
            final TraitStorage<Collidable> collidables = world.register(Collidable.TYPE);
            final CollisionSystem collision = new CollisionSystem(world, transformables, collidables);
            world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
            world.addSystem(collision, CollisionSystem.SLOT);
            final List<String> events = new ArrayList<>();
            final int[] current = {0};
            collision.addListener(new CollisionListener()
            {
                @Override
                public void onEnter(long objectA, long objectB)
                {
                    events.add("enter " + current[0]);
                }

                @Override
                public void onExit(long objectA, long objectB)
                {
                    events.add("exit " + current[0]);
                }
            });
            final long left = world.create();
            transformables.add(left).setPosition(-5.0, 0.0);
            movables.add(left).setVelocity(2.0, 0.0);
            collidables.add(left).setSize(1.0, 1.0);
            final long right = world.create();
            transformables.add(right).setPosition(5.0, 0.25);
            movables.add(right).setVelocity(-1.0, 0.0);
            collidables.add(right).setSize(0.5, 0.5);
            world.flush();
            final List<String> expected = new ArrayList<>();
            double xLeft = -5.0;
            double xRight = 5.0;
            boolean overlapping = false;
            for (int i = 1; i <= 6 * rate; i++)
            {
                xLeft += 2.0 * step;
                xRight += -1.0 * step;
                final boolean now = new Box(xLeft, 0.0, 1.0, 1.0).overlaps(new Box(xRight, 0.25, 0.5, 0.5));
                if (now != overlapping)
                {
                    expected.add((now ? "enter " : "exit ") + i);
                }
                overlapping = now;
            }

            for (current[0] = 1; current[0] <= 6 * rate; current[0]++)
            {
                world.update(step);
            }

            assertEquals(2, expected.size());
            assertEquals(expected, events);

            movables.get(left).setVelocity(0.0, 0.0);
            transformables.get(left).setPosition(transformables.get(right).getX(), 0.0);
            world.update(step);
            world.remove(right);
            world.flush();
            world.update(step);

            assertEquals(4, events.size());
            assertTrue(events.get(2).startsWith("enter"));
            assertTrue(events.get(3).startsWith("exit"));
            assertEquals(0, collision.getPairCount());
        }
    }

    /**
     * A new or reset jumpable has no impulse, is not grounded, has a coyote time and a jump buffer of 0.1 s, a cut
     * factor of 0.5 and no variable height; invalid values are refused (JumpableTest).
     */
    @Test
    public void testJumpableResetState()
    {
        final Jumpable jumpable = Jumpable.TYPE.create();
        jumpable.setImpulse(7.5);
        jumpable.setGrounded(true);
        jumpable.setCoyoteTime(0.2);
        jumpable.setCutFactor(1.0);
        jumpable.setVariableHeight(true);
        Jumpable.TYPE.reset(jumpable);

        assertEquals(0.0, jumpable.getImpulse(), 0.0);
        assertFalse(jumpable.isGrounded());
        assertEquals(0.1, jumpable.getCoyoteTime(), 0.0);
        assertEquals(0.1, jumpable.getJumpBuffer(), 0.0);
        assertEquals(0.5, jumpable.getCutFactor(), 0.0);
        assertFalse(jumpable.isVariableHeight());
        assertThrows(IllegalArgumentException.class, () -> jumpable.setImpulse(-1.0));
        assertThrows(IllegalArgumentException.class, () -> jumpable.setCutFactor(1.5));
    }

    /**
     * At every step duration, an object falling under gravity lands exactly on a tile floor, grounded, its vertical
     * velocity zeroed; walking right it is stopped exactly by a wall, its horizontal velocity zeroed; moving up it is
     * stopped exactly under a ceiling, not grounded (TileCollisionTest).
     */
    @Test
    public void testTilePushOut()
    {
        final Box[] tiles = new Box[19];
        for (int i = 0; i < 16; i++)
        {
            tiles[i] = new Box(i + 0.5, 0.5, 1.0, 1.0);
        }
        tiles[16] = new Box(10.5, 1.5, 1.0, 1.0);
        tiles[17] = new Box(10.5, 2.5, 1.0, 1.0);
        tiles[18] = new Box(4.5, 6.5, 4.0, 1.0);
        final ShapeQuery ground = new ShapeQuery()
        {
            @Override
            public int query(Box area, Box[] results)
            {
                int count = 0;
                for (final Box tile : tiles)
                {
                    if (tile.overlaps(area))
                    {
                        if (count < results.length)
                        {
                            results[count].set(tile);
                        }
                        count++;
                    }
                }
                return count;
            }
        };
        final int[] rates = {30, 60, 120};
        for (final int rate : rates)
        {
            final double step = 1.0 / rate;
            final World world = new World();
            final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
            final TraitStorage<Movable> movables = world.register(Movable.TYPE);
            final TraitStorage<Gravity> gravities = world.register(Gravity.TYPE);
            final TraitStorage<Collidable> collidables = world.register(Collidable.TYPE);
            final TraitStorage<Jumpable> jumpables = world.register(Jumpable.TYPE);
            world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
            world.addSystem(new GravitySystem(world, movables, gravities), GravitySystem.SLOT);
            world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
            world.addSystem(new CollisionSystem(world, transformables, collidables, movables, jumpables, ground),
                            CollisionSystem.SLOT);
            final long walker = world.create();
            final Transformable transformable = transformables.add(walker);
            transformable.setPosition(2.5, 3.0);
            transformable.setPreviousPosition(2.5, 3.0);
            movables.add(walker);
            gravities.add(walker).setAcceleration(-9.81);
            collidables.add(walker).setSize(0.5, 0.5);
            jumpables.add(walker);
            final long climber = world.create();
            final Transformable climbing = transformables.add(climber);
            climbing.setPosition(4.5, 5.0);
            climbing.setPreviousPosition(4.5, 5.0);
            movables.add(climber).setVelocity(0.0, 6.0);
            collidables.add(climber).setSize(0.5, 0.5);
            jumpables.add(climber).setGrounded(true);
            world.flush();

            for (int i = 0; i < rate * 2; i++)
            {
                world.update(step);
            }

            assertEquals(1.25, transformable.getY(), 0.0);
            assertEquals(0.0, movables.get(walker).getVelocityY(), 0.0);
            assertTrue(jumpables.get(walker).isGrounded());
            assertEquals(5.75, climbing.getY(), 0.0);
            assertEquals(0.0, movables.get(climber).getVelocityY(), 0.0);
            assertFalse(jumpables.get(climber).isGrounded());

            movables.get(walker).setVelocity(2.0, 0.0);
            for (int i = 0; i < rate * 5; i++)
            {
                world.update(step);
            }

            assertEquals(9.75, transformable.getX(), 0.0);
            assertEquals(1.25, transformable.getY(), 0.0);
            assertEquals(0.0, movables.get(walker).getVelocityX(), 0.0);
            assertTrue(jumpables.get(walker).isGrounded());
        }
    }

    /**
     * At every step duration, an object resting on a tile floor jumps on a press, its vertical velocity replaced by
     * the impulse then accelerated by the gravity of the step, and no longer grounded; with the variable height on, an
     * early release while ascending halves the upward velocity (JumpSystemTest).
     */
    @Test
    public void testJumpAndCut()
    {
        final Box floor = new Box(8.0, 0.5, 16.0, 1.0);
        final ShapeQuery ground = new ShapeQuery()
        {
            @Override
            public int query(Box area, Box[] results)
            {
                if (floor.overlaps(area))
                {
                    if (results.length > 0)
                    {
                        results[0].set(floor);
                    }
                    return 1;
                }
                return 0;
            }
        };
        final int[] rates = {30, 60, 120};
        for (final int rate : rates)
        {
            final double step = 1.0 / rate;
            final World world = new World();
            final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
            final TraitStorage<Movable> movables = world.register(Movable.TYPE);
            final TraitStorage<Gravity> gravities = world.register(Gravity.TYPE);
            final TraitStorage<Collidable> collidables = world.register(Collidable.TYPE);
            final TraitStorage<Jumpable> jumpables = world.register(Jumpable.TYPE);
            world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
            world.addSystem(new JumpSystem(world, movables, jumpables), JumpSystem.SLOT);
            world.addSystem(new GravitySystem(world, movables, gravities), GravitySystem.SLOT);
            world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
            world.addSystem(new CollisionSystem(world, transformables, collidables, movables, jumpables, ground),
                            CollisionSystem.SLOT);
            final long id = world.create();
            final Transformable transformable = transformables.add(id);
            transformable.setPosition(2.5, 1.25);
            transformable.setPreviousPosition(2.5, 1.25);
            transformable.setSize(0.5, 0.5);
            movables.add(id);
            gravities.add(id).setAcceleration(-9.81);
            collidables.add(id).setSize(0.5, 0.5);
            final Jumpable jumpable = jumpables.add(id);
            jumpable.setImpulse(8.0);
            jumpable.setVariableHeight(true);
            world.flush();
            world.update(step);
            world.update(step);
            assertTrue(jumpable.isGrounded());

            jumpable.setJumpHeld(true);
            world.update(step);
            assertEquals(8.0 + -9.81 * step, movables.get(id).getVelocityY(), 0.0);
            assertFalse(jumpable.isGrounded());
            world.update(step);
            world.update(step);
            final double ascending = movables.get(id).getVelocityY();
            jumpable.setJumpHeld(false);
            world.update(step);
            assertEquals(ascending * 0.5 + -9.81 * step, movables.get(id).getVelocityY(), 0.0);

            while (!jumpable.isGrounded())
            {
                world.update(step);
            }
            assertEquals(1.25, transformable.getY(), 0.0);
            world.update(step);
            assertEquals(0.0, movables.get(id).getVelocityY(), 0.0);
        }
    }

    /**
     * At every step duration, with the grounded flag set by the game: a press at the third step in the air jumps with
     * a coyote time of three steps and a half, a press at the fourth does not; a press three steps before the ground
     * jumps on it with a jump buffer of three steps and a half, a press four steps before does not; a press in the air
     * after a jump does not jump again (JumpSystemTest).
     */
    @Test
    public void testCoyoteAndBuffer()
    {
        final int[] rates = {30, 60, 120};
        for (final int rate : rates)
        {
            final double step = 1.0 / rate;
            for (int air = 3; air <= 4; air++)
            {
                assertEquals(air == 3, jumpCase(step, 3.5 * step, 0.0, true, air));
                assertEquals(air == 3, jumpCase(step, 0.0, 3.5 * step, false, air));
            }
        }
    }

    /**
     * Plays one coyote or buffer case with {@code JumpSystem} alone and tells whether it jumped.
     *
     * @param step The step, in seconds.
     * @param coyoteTime The coyote time, in seconds.
     * @param jumpBuffer The jump buffer, in seconds.
     * @param coyote {@code true} for the coyote case: grounded, then in the air and pressing; {@code false} for the
     *            buffer case: pressing in the air, then grounded.
     * @param steps The steps in the air before the press (coyote), or from the press to the ground (buffer).
     * @return {@code true} if the case jumped.
     */
    private static boolean jumpCase(double step, double coyoteTime, double jumpBuffer, boolean coyote, int steps)
    {
        final World world = new World();
        final TraitStorage<Movable> movables = world.register(Movable.TYPE);
        final TraitStorage<Jumpable> jumpables = world.register(Jumpable.TYPE);
        world.addSystem(new JumpSystem(world, movables, jumpables), JumpSystem.SLOT);
        final long id = world.create();
        movables.add(id);
        final Jumpable jumpable = jumpables.add(id);
        jumpable.setImpulse(8.0);
        jumpable.setCoyoteTime(coyoteTime);
        jumpable.setJumpBuffer(jumpBuffer);
        world.flush();
        jumpable.setGrounded(coyote);
        world.update(step);
        jumpable.setGrounded(false);
        if (coyote)
        {
            for (int i = 1; i < steps; i++)
            {
                world.update(step);
            }
            jumpable.setJumpHeld(true);
            world.update(step);
        }
        else
        {
            jumpable.setJumpHeld(true);
            world.update(step);
            for (int i = 1; i < steps; i++)
            {
                world.update(step);
            }
            jumpable.setGrounded(true);
            world.update(step);
        }
        final boolean jumped = Double.compare(movables.get(id).getVelocityY(), 8.0) == 0;
        assertFalse(jumpable.isGrounded() && jumped);
        jumpable.setGrounded(false);
        movables.get(id).setVelocity(0.0, 1.0);
        jumpable.setJumpHeld(false);
        world.update(step);
        jumpable.setJumpHeld(true);
        world.update(step);
        assertEquals("No double jump in the air", 1.0, movables.get(id).getVelocityY(), 0.0);
        return jumped;
    }
}
