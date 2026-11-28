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

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.TraitType;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.traits.Gravity;
import com.b3dgs.lionengine.traits.GravitySystem;
import com.b3dgs.lionengine.traits.Lifetime;
import com.b3dgs.lionengine.traits.LifetimeSystem;
import com.b3dgs.lionengine.traits.Movable;
import com.b3dgs.lionengine.traits.MovementSystem;
import com.b3dgs.lionengine.traits.TransformCaptureSystem;
import com.b3dgs.lionengine.traits.Transformable;

/**
 * Runs {@code traits} on Android API 21 (ADR-0044): the reset states of {@code Transformable} and {@code Movable}, the
 * capture of the previous position at the step start, the integration of the velocity at steps of 1/30, 1/60 and
 * 1/120 s, the speed clamp on the magnitude and the excluded trait types; the reset states of {@code Gravity} and
 * {@code Lifetime}, the acceleration up to the terminal velocity and the expiry with its removal at the flush, at the
 * same steps. Expected values are the ones of the JVM tests of {@code lionengine-traits} (TransformableTest,
 * MovableTest, TransformCaptureSystemTest, MovementSystemTest, GravityTest, GravitySystemTest, LifetimeTest,
 * LifetimeSystemTest).
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

    /**
     * A new or reset gravity has no acceleration and no terminal velocity, a new or reset lifetime never expires;
     * negative or not-a-number limits and durations are refused.
     */
    @Test
    public void testGravityAndLifetimeResetStates()
    {
        final Gravity gravity = Gravity.TYPE.create();
        assertEquals(0.0, gravity.getAcceleration(), 0.0);
        assertEquals(Double.POSITIVE_INFINITY, gravity.getTerminalVelocity(), 0.0);
        gravity.setAcceleration(-9.81);
        gravity.setTerminalVelocity(20.0);
        Gravity.TYPE.reset(gravity);
        assertEquals(0.0, gravity.getAcceleration(), 0.0);
        assertEquals(Double.POSITIVE_INFINITY, gravity.getTerminalVelocity(), 0.0);
        assertThrows(IllegalArgumentException.class, () -> gravity.setTerminalVelocity(-0.5));
        assertThrows(IllegalArgumentException.class, () -> gravity.setTerminalVelocity(Double.NaN));

        final Lifetime lifetime = Lifetime.TYPE.create();
        assertEquals(Double.POSITIVE_INFINITY, lifetime.getDuration(), 0.0);
        lifetime.setDuration(1.5);
        Lifetime.TYPE.reset(lifetime);
        assertEquals(Double.POSITIVE_INFINITY, lifetime.getDuration(), 0.0);
        assertThrows(IllegalArgumentException.class, () -> lifetime.setDuration(-0.5));
        assertThrows(IllegalArgumentException.class, () -> lifetime.setDuration(Double.NaN));
    }

    /**
     * One second of steps adds the acceleration to the vertical velocity whatever the step, the movement integrates it
     * ({@code a * (1 + dt) / 2}); a fall stops speeding up at its terminal velocity; a velocity beyond it is kept
     * (GravitySystemTest).
     */
    @Test
    public void testGravity()
    {
        final int[] rates = {30, 60, 120};
        for (final int rate : rates)
        {
            final double step = 1.0 / rate;
            final World world = new World();
            final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
            final TraitStorage<Movable> movables = world.register(Movable.TYPE);
            final TraitStorage<Gravity> gravities = world.register(Gravity.TYPE);
            world.addSystem(new GravitySystem(world, movables, gravities), GravitySystem.SLOT);
            world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
            final long free = world.create();
            transformables.add(free);
            movables.add(free).setVelocity(2.0, 0.0);
            gravities.add(free).setAcceleration(-6.0);
            final long falling = world.create();
            transformables.add(falling);
            movables.add(falling);
            final Gravity limited = gravities.add(falling);
            limited.setAcceleration(-6.0);
            limited.setTerminalVelocity(3.0);
            final long diving = world.create();
            transformables.add(diving);
            movables.add(diving).setVelocity(0.0, -10.0);
            final Gravity dive = gravities.add(diving);
            dive.setAcceleration(-6.0);
            dive.setTerminalVelocity(3.0);
            world.flush();

            for (int i = 0; i < rate; i++)
            {
                world.update(step);
            }

            assertEquals(-6.0, movables.get(free).getVelocityY(), EPSILON);
            assertEquals(2.0, movables.get(free).getVelocityX(), 0.0);
            assertEquals(2.0, transformables.get(free).getX(), EPSILON);
            assertEquals(-6.0 * (1.0 + step) / 2.0, transformables.get(free).getY(), EPSILON);
            assertEquals(-3.0, movables.get(falling).getVelocityY(), 0.0);
            assertEquals(-10.0, movables.get(diving).getVelocityY(), 0.0);
        }
    }

    /**
     * An object of half a second is removed by the flush of the step its count-down reaches zero, once; an excluded
     * object never expires (LifetimeSystemTest).
     */
    @Test
    public void testLifetime()
    {
        final int[] rates = {30, 60, 120};
        for (final int rate : rates)
        {
            final double step = 1.0 / rate;
            final World world = new World();
            final TraitStorage<Lifetime> lifetimes = world.register(Lifetime.TYPE);
            final TraitStorage<Object> proxies = world.register(MARKER);
            final TraitStorage<?>[] excluded = {proxies};
            world.addSystem(new LifetimeSystem(world, lifetimes, excluded), LifetimeSystem.SLOT);
            final long id = world.create();
            lifetimes.add(id).setDuration(0.5);
            final long proxy = world.create();
            lifetimes.add(proxy).setDuration(0.0);
            proxies.add(proxy);
            world.flush();
            double remaining = 0.5;
            int expiry = 0;
            do
            {
                remaining -= step;
                expiry++;
            }
            while (remaining > 0.0);

            for (int i = 1; i < expiry; i++)
            {
                world.update(step);
                assertTrue(world.isAlive(id));
            }
            world.update(step);

            assertFalse(world.isAlive(id));
            assertTrue(world.isAlive(proxy));
            assertEquals(1, world.getObjectCount());
            assertTrue(expiry == rate / 2 || expiry == rate / 2 + 1);
        }
    }
}
