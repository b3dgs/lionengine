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
import com.b3dgs.lionengine.traits.Collidable;
import com.b3dgs.lionengine.traits.CollisionListener;
import com.b3dgs.lionengine.traits.CollisionSystem;
import com.b3dgs.lionengine.traits.Movable;
import com.b3dgs.lionengine.traits.MovementSystem;
import com.b3dgs.lionengine.traits.Transformable;

/**
 * Runs {@code physics} and the collision of {@code traits} on Android API 21 (ADR-0044): the strict box overlap, the
 * broad phase against a brute-force comparison on seeded random boxes, the reset state of {@code Collidable}, the
 * layer and mask rule both ways, and the enter and exit events of two objects meeting at steps of 1/30, 1/60 and
 * 1/120 s, exits before enters, with an exit when an object is removed. Expected values are the ones of the JVM tests
 * of {@code lionengine-physics} and {@code lionengine-traits} (BoxTest, BroadPhaseTest, CollidableTest,
 * CollisionSystemTest).
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
}
