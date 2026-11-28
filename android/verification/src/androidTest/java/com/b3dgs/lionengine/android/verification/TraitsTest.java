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
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.TraitType;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.traits.Movable;
import com.b3dgs.lionengine.traits.MovementSystem;
import com.b3dgs.lionengine.traits.TransformCaptureSystem;
import com.b3dgs.lionengine.traits.Transformable;

/**
 * Runs {@code traits} on Android API 21 (ADR-0044): the reset states of {@code Transformable} and {@code Movable}, the
 * capture of the previous position at the step start, the integration of the velocity at steps of 1/30, 1/60 and
 * 1/120 s, the speed clamp on the magnitude and the excluded trait types. Expected values are the ones of the JVM tests
 * of {@code lionengine-traits} (TransformableTest, MovableTest, TransformCaptureSystemTest, MovementSystemTest).
 */
@RunWith(AndroidJUnit4.class)
public final class TraitsTest
{
    /** Tolerance on positions accumulated over many steps, in meters (MovementSystemTest). */
    private static final double EPSILON = 1e-9;
    /** A trait type with no data, excluded from the systems. */
    private static final TraitType<Object> MARKER = new TraitType<>()
    {
        @Override
        public Object create()
        {
            return new Object();
        }

        @Override
        public void reset(Object trait)
        {
            // No data.
        }
    };

    /**
     * Creates the test.
     */
    public TraitsTest()
    {
        super();
    }

    /**
     * A new or reset trait has every field at zero and no speed limit; a negative maximum speed is refused.
     */
    @Test
    public void testResetStates()
    {
        final Transformable transformable = Transformable.TYPE.create();
        transformable.setPosition(1.0, 2.0);
        transformable.setSize(3.0, 4.0);
        transformable.setPreviousPosition(5.0, 6.0);
        Transformable.TYPE.reset(transformable);
        assertEquals(0.0, transformable.getX(), 0.0);
        assertEquals(0.0, transformable.getHeight(), 0.0);
        assertEquals(0.0, transformable.getPreviousY(), 0.0);

        final Movable movable = Movable.TYPE.create();
        assertEquals(Double.POSITIVE_INFINITY, movable.getMaxSpeed(), 0.0);
        movable.setVelocity(3.0, -4.0);
        movable.setMaxSpeed(6.0);
        Movable.TYPE.reset(movable);
        assertEquals(0.0, movable.getVelocityX(), 0.0);
        assertEquals(Double.POSITIVE_INFINITY, movable.getMaxSpeed(), 0.0);
        assertThrows(IllegalArgumentException.class, () -> movable.setMaxSpeed(-0.5));
        assertThrows(IllegalArgumentException.class, () -> movable.setMaxSpeed(Double.NaN));
    }

    /**
     * One second of steps moves an object by its velocity whatever the step; the previous position is one step
     * behind; a speed above the limit is scaled to it, direction kept.
     */
    @Test
    public void testIntegrationAndClamp()
    {
        final int[] rates = {30, 60, 120};
        for (final int rate : rates)
        {
            final double step = 1.0 / rate;
            final World world = new World();
            final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
            final TraitStorage<Movable> movables = world.register(Movable.TYPE);
            world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
            world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
            final long free = world.create();
            transformables.add(free).setPosition(1.0, 2.0);
            movables.add(free).setVelocity(2.0, -3.0);
            final long clamped = world.create();
            transformables.add(clamped);
            final Movable movable = movables.add(clamped);
            movable.setVelocity(-30.0, 40.0);
            movable.setMaxSpeed(10.0);
            world.flush();

            for (int i = 0; i < rate; i++)
            {
                world.update(step);
            }

            assertEquals(3.0, transformables.get(free).getX(), EPSILON);
            assertEquals(-1.0, transformables.get(free).getY(), EPSILON);
            assertEquals(3.0 - 2.0 * step, transformables.get(free).getPreviousX(), EPSILON);
            assertEquals(-6.0, movables.get(clamped).getVelocityX(), EPSILON);
            assertEquals(8.0, movables.get(clamped).getVelocityY(), EPSILON);
            assertEquals(-6.0, transformables.get(clamped).getX(), EPSILON);
            assertEquals(8.0, transformables.get(clamped).getY(), EPSILON);
        }
    }

    /**
     * The capture runs before a game system moving an object without a velocity; an object carrying an excluded trait
     * is neither captured nor moved.
     */
    @Test
    public void testCaptureAndExcluded()
    {
        final World world = new World();
        final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
        final TraitStorage<Movable> movables = world.register(Movable.TYPE);
        final TraitStorage<Object> proxies = world.register(MARKER);
        final TraitStorage<?>[] excluded = {proxies};
        final long walker = world.create();
        transformables.add(walker).setPosition(1.0, 2.0);
        final long proxy = world.create();
        transformables.add(proxy).setPosition(2.0, 2.0);
        movables.add(proxy).setVelocity(60.0, 0.0);
        proxies.add(proxy);
        world.flush();
        world.addSystem(step -> transformables.get(walker).setPosition(5.0, 6.0), 500);
        world.addSystem(new MovementSystem(world, transformables, movables, excluded), MovementSystem.SLOT);
        world.addSystem(new TransformCaptureSystem(world, transformables, excluded), TransformCaptureSystem.SLOT);

        world.update(1.0 / 60.0);

        assertEquals(5.0, transformables.get(walker).getX(), 0.0);
        assertEquals(1.0, transformables.get(walker).getPreviousX(), 0.0);
        assertEquals(2.0, transformables.get(walker).getPreviousY(), 0.0);
        assertEquals(2.0, transformables.get(proxy).getX(), 0.0);
        assertEquals(0.0, transformables.get(proxy).getPreviousX(), 0.0);
    }
}
