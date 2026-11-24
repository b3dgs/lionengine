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
package com.b3dgs.lionengine.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.InboxEvent;
import com.b3dgs.lionengine.Log;
import com.sun.management.ThreadMXBean;

/**
 * Test the inbox of {@link GameLoop} and the runtime-thread rules (REQ-RT-001, REQ-RT-002, REQ-RT-009), and the
 * allocation of an iteration.
 */
final class GameLoopInboxTest
{
    /** Bytes tolerated for the allocation measurement itself: far below one allocation per iteration. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /**
     * Creates a started loop with an inbox capacity.
     *
     * @param capacity The inbox capacity.
     * @param clock The clock.
     * @param recorder The recorder.
     * @return The loop.
     */
    private static GameLoop start(int capacity, ControllableClock clock, GameLoopTest.Recorder recorder)
    {
        return GameLoopTest.start(new LoopConfig(100, 0.25, 1.0, 0.000_25, capacity), clock, recorder);
    }

    /**
     * Events are delivered in arrival order, at the start of the iteration, before the updates.
     */
    @Test
    void testDeliveryOrder()
    {
        final ControllableClock clock = new ControllableClock();
        final GameLoopTest.Recorder recorder = new GameLoopTest.Recorder();
        final GameLoop loop = start(16, clock, recorder);
        final Inbox inbox = loop.getInbox();
        inbox.post(() -> recorder.calls.append('a'));
        inbox.post(() -> recorder.calls.append('b'));

        GameLoopTest.frame(loop, clock, 20_000_000L);

        assertEquals("RabUUR", recorder.calls.toString());
    }

    /**
     * An event posted during a delivery is delivered at the next iteration.
     */
    @Test
    void testPostDuringDelivery()
    {
        final ControllableClock clock = new ControllableClock();
        final GameLoopTest.Recorder recorder = new GameLoopTest.Recorder();
        final GameLoop loop = start(16, clock, recorder);
        final Inbox inbox = loop.getInbox();
        inbox.post(() -> inbox.post(() -> recorder.calls.append('n')));

        loop.iterate();
        assertEquals("RR", recorder.calls.toString());
        loop.iterate();
        assertEquals("RRnR", recorder.calls.toString());
    }

    /**
     * When full, the inbox rejects and counts events and reports the drop once at the next delivery; control requests
     * still work.
     */
    @Test
    void testOverflow()
    {
        final ControllableClock clock = new ControllableClock();
        final GameLoopTest.Recorder recorder = new GameLoopTest.Recorder();
        final GameLoop loop = start(2, clock, recorder);
        final Inbox inbox = loop.getInbox();
        final InboxEvent event = () -> recorder.calls.append('e');

        assertTrue(inbox.post(event));
        assertTrue(inbox.post(event));
        assertFalse(inbox.post(event));
        assertFalse(inbox.post(event));
        assertEquals(2L, inbox.getDroppedCount());
        loop.pause();

        GameLoopTest.frame(loop, clock, 50_000_000L);
        assertEquals("ReeR", recorder.calls.toString());
        assertSame(LoopState.PAUSED, loop.getState());
        assertEquals(List.of("WARNING " + BoundedInbox.WARNING_DROPPED + " 2"), recorder.logged);

        loop.iterate();
        assertEquals(1, recorder.logged.size());
        assertEquals(2L, inbox.getDroppedCount());
    }

    /**
     * The ring buffer keeps the arrival order across many fill and delivery cycles.
     */
    @Test
    void testWrapAround()
    {
        final StringBuilder delivered = new StringBuilder();
        final BoundedInbox inbox = new BoundedInbox(3, new Log());
        for (int cycle = 0; cycle < 5; cycle++)
        {
            final char first = (char) ('a' + cycle);
            inbox.post(() -> delivered.append(first));
            inbox.post(() -> delivered.append('-'));
            inbox.deliverAll();
        }

        assertEquals("a-b-c-d-e-", delivered.toString());
    }

    /**
     * An event that throws fails the loop; the other events of that delivery are discarded.
     */
    @Test
    void testEventFailure()
    {
        final ControllableClock clock = new ControllableClock();
        final GameLoopTest.Recorder recorder = new GameLoopTest.Recorder();
        final GameLoop loop = start(16, clock, recorder);
        final Inbox inbox = loop.getInbox();
        inbox.post(() ->
        {
            throw new IllegalStateException("event");
        });
        inbox.post(() -> recorder.calls.append('x'));

        assertThrows(IllegalStateException.class, loop::iterate);
        assertSame(LoopState.FAILED, loop.getState());
        assertEquals("R", recorder.calls.toString());
    }

    /**
     * A delivery cannot start from inside a delivery.
     */
    @Test
    void testNestedDelivery()
    {
        final BoundedInbox inbox = new BoundedInbox(4, new Log());
        inbox.post(inbox::deliverAll);

        assertThrows(IllegalStateException.class, inbox::deliverAll);
        inbox.post(inbox::getDroppedCount);
        inbox.deliverAll();
        assertEquals(0L, inbox.getDroppedCount());
    }

    /**
     * A {@code null} event is rejected.
     */
    @Test
    void testNullEvent()
    {
        final Inbox inbox = new BoundedInbox(4, new Log());

        assertThrows(IllegalArgumentException.class, () -> inbox.post(null));
    }

    /**
     * Several threads post at once: every event is accepted and delivered on the runtime thread.
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    void testConcurrentPosts() throws InterruptedException
    {
        final ControllableClock clock = new ControllableClock();
        final GameLoopTest.Recorder recorder = new GameLoopTest.Recorder();
        final GameLoop loop = start(1024, clock, recorder);
        final Inbox inbox = loop.getInbox();
        final AtomicInteger delivered = new AtomicInteger();
        final Thread runtime = Thread.currentThread();
        final InboxEvent event = () ->
        {
            if (Thread.currentThread() == runtime)
            {
                delivered.incrementAndGet();
            }
        };
        final int threads = 4;
        final int posts = 200;
        final CountDownLatch done = new CountDownLatch(threads);
        for (int t = 0; t < threads; t++)
        {
            new Thread(() ->
            {
                for (int i = 0; i < posts; i++)
                {
                    inbox.post(event);
                }
                done.countDown();
            }).start();
        }
        assertTrue(done.await(10, TimeUnit.SECONDS));

        loop.iterate();

        assertEquals(threads * posts, delivered.get());
        assertEquals(0L, inbox.getDroppedCount());
    }

    /**
     * An iteration allocates nothing in steady state, inbox delivery included (ADR-0019), measured with the
     * per-thread allocation counter of the JVM.
     */
    @Test
    void testIterationAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final ControllableClock clock = new ControllableClock();
        final long[] counter = new long[1];
        final GameLoop loop = new GameLoop(GameLoopTest.CONFIG,
                                           clock,
                                           new Log(),
                                           step -> counter[0]++,
                                           (alpha, delta) -> counter[0]++);
        final InboxEvent event = () -> counter[0]++;
        loop.setLagListener(seconds -> counter[0]++);
        iterate(loop, clock, event);

        final long before = bean.getCurrentThreadAllocatedBytes();
        iterate(loop, clock, event);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertTrue(counter[0] > 0L);
    }

    /**
     * Runs many iterations with irregular frames, a long frame now and then, and one posted event each.
     *
     * @param loop The loop.
     * @param clock The clock.
     * @param event The event posted each iteration.
     */
    private static void iterate(GameLoop loop, ControllableClock clock, InboxEvent event)
    {
        for (int i = 0; i < 20_000; i++)
        {
            loop.getInbox().post(event);
            clock.advance(3_000_000L + i % 7 * 5_000_000L + i % 101 / 100 * 400_000_000L);
            loop.iterate();
        }
    }
}
