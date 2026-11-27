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
package com.b3dgs.lionengine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.sun.management.ThreadMXBean;

/**
 * Test {@link ThreadConfinement} (ADR-0018, ADR-0055, REQ-RT-003).
 */
final class ThreadConfinementTest
{
    /** Checks per allocation measurement. */
    private static final int ITERATIONS = 100_000;
    /** Bytes tolerated for the measurement itself: far below one allocation per call. */
    private static final long ALLOCATION_TOLERANCE = 1024L;
    /** Threads racing for the first check. */
    private static final int RACERS = 8;
    /** Seconds to wait for a thread. */
    private static final long TIMEOUT_SECONDS = 10L;

    /**
     * Runs an action on a new thread and returns what it threw.
     *
     * @param name The name of the thread.
     * @param action The action.
     * @return The exception thrown by the action, {@code null} if none.
     * @throws InterruptedException If interrupted while waiting for the thread.
     */
    static RuntimeException runOn(String name, Runnable action) throws InterruptedException
    {
        final AtomicReference<RuntimeException> failure = new AtomicReference<>();
        final Thread thread = new Thread(() ->
        {
            try
            {
                action.run();
            }
            catch (final IllegalStateException exception)
            {
                failure.set(exception);
            }
        }, name);
        thread.start();
        thread.join(TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS));
        return failure.get();
    }

    /**
     * The thread of the first check owns the object: it passes every later check.
     */
    @Test
    void testFirstCheckOwns()
    {
        final ThreadConfinement confinement = new ThreadConfinement("Cache");

        assertDoesNotThrow(confinement::check);
        assertDoesNotThrow(confinement::check);
    }

    /**
     * Another thread is refused, with a message naming the object, both threads and the rule.
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    void testOtherThreadRefused() throws InterruptedException
    {
        final ThreadConfinement confinement = new ThreadConfinement("Cache");
        confinement.check();

        final RuntimeException failure = runOn("worker", confinement::check);

        assertInstanceOf(IllegalStateException.class, failure);
        assertEquals("Cache used on the thread worker, but it belongs to the thread "
                     + Thread.currentThread().getName()
                     + " (runtime thread only, ADR-0018)",
                     failure.getMessage());
        assertDoesNotThrow(confinement::check);
    }

    /**
     * The owner is the thread of the first check, not the thread that created the object: a cache created while
     * wiring the game belongs to the runtime thread that uses it first.
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    void testOwnerIsFirstUser() throws InterruptedException
    {
        final ThreadConfinement confinement = new ThreadConfinement("Cache");

        assertNull(runOn("lionengine-runtime", confinement::check));
        final IllegalStateException failure = assertThrows(IllegalStateException.class, confinement::check);
        assertTrue(failure.getMessage().contains("belongs to the thread lionengine-runtime"));
    }

    /**
     * When several threads race for the first check, exactly one becomes the owner.
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    void testRaceHasOneOwner() throws InterruptedException
    {
        final ThreadConfinement confinement = new ThreadConfinement("Cache");
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(RACERS);
        final AtomicInteger accepted = new AtomicInteger();
        final AtomicInteger refused = new AtomicInteger();
        for (int i = 0; i < RACERS; i++)
        {
            final Thread thread = new Thread(() ->
            {
                try
                {
                    start.await();
                    confinement.check();
                    accepted.incrementAndGet();
                }
                catch (final InterruptedException exception)
                {
                    Thread.currentThread().interrupt();
                }
                catch (final IllegalStateException exception)
                {
                    refused.incrementAndGet();
                }
                finally
                {
                    done.countDown();
                }
            }, "racer-" + i);
            thread.start();
        }
        start.countDown();

        assertTrue(done.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
        assertEquals(1, accepted.get());
        assertEquals(RACERS - 1, refused.get());
        assertThrows(IllegalStateException.class, confinement::check);
    }

    /**
     * A check by the owner allocates nothing (REQ-RT-006, measured with the per-thread allocation counter of the JVM).
     */
    @Test
    void testCheckAllocatesNothing()
    {
        final ThreadConfinement confinement = new ThreadConfinement("Cache");
        confinement.check();
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int i = 0; i < ITERATIONS; i++)
        {
            confinement.check();
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, "Allocated " + allocated + " bytes");
    }

    /**
     * A {@code null} name is refused.
     */
    @Test
    void testNullName()
    {
        assertThrows(IllegalArgumentException.class, () -> new ThreadConfinement(null));
    }
}
