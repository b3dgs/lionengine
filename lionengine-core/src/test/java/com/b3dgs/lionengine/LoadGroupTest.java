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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.management.ThreadMXBean;

/**
 * Test {@link LoadGroup}: mean progress and state of its members (REQ-RT-010, ADR-0031).
 */
final class LoadGroupTest
{
    /** Reads per allocation measurement. */
    private static final int ITERATIONS = 100_000;
    /** Bytes tolerated for the measurement itself: far below one allocation per call. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /** Pool with one queued worker. */
    private FakeWorkerPool workers;
    /** Inbox of the results. */
    private InboxSpy inbox;
    /** Group under test. */
    private LoadGroup group;

    /**
     * Creates the pool, the inbox and the group of each test.
     */
    @BeforeEach
    void prepare()
    {
        workers = new FakeWorkerPool(1);
        inbox = new InboxSpy();
        group = new LoadGroup();
    }

    /**
     * Submits a request returning its text.
     *
     * @param text The text.
     * @return The handle.
     */
    private AsyncHandle<String> pending(String text)
    {
        return AsyncLoading.submit(workers, inbox, () -> text, result -> result);
    }

    /**
     * An empty group is ready, with a progress of 1.
     */
    @Test
    void testEmpty()
    {
        assertEquals(AsyncState.READY, group.getState());
        assertEquals(1.0, group.getProgress());
        assertNull(group.getFailure());
        assertEquals(0, group.size());
        assertEquals(0, group.cancel());
    }

    /**
     * The progress is the mean of the members; the group is pending until every member is ready.
     */
    @Test
    void testProgress()
    {
        group.add(AsyncLoading.ready("hero"));
        group.add(pending("tiles"));

        assertEquals(2, group.size());
        assertEquals(AsyncState.PENDING, group.getState());
        assertEquals(0.5, group.getProgress());

        workers.runAll();
        inbox.deliverAll();
        assertEquals(AsyncState.READY, group.getState());
        assertEquals(1.0, group.getProgress());
    }

    /**
     * One failed member fails the group at once, with the failure of the first failed member added, whatever the
     * other members are.
     */
    @Test
    void testFailed()
    {
        final IllegalStateException first = new IllegalStateException("First");
        final AsyncHandle<String> cancelled = pending("slime");
        group.add(pending("hero"));
        group.add(cancelled);
        group.add(AsyncLoading.failed(first));
        group.add(AsyncLoading.failed(new IllegalStateException("Second")));
        cancelled.cancel();

        assertEquals(AsyncState.FAILED, group.getState());
        assertSame(first, group.getFailure());
    }

    /**
     * A cancelled member without failure makes the group cancelled; cancelling the group cancels its pending members
     * only.
     */
    @Test
    void testCancel()
    {
        final AsyncHandle<String> ready = AsyncLoading.ready("hero");
        final AsyncHandle<String> first = pending("slime");
        final AsyncHandle<String> second = pending("tiles");
        group.add(ready);
        group.add(first);
        group.add(second);

        assertEquals(2, group.cancel());
        assertEquals(0, group.cancel());

        assertEquals(AsyncState.CANCELLED, group.getState());
        assertEquals(AsyncState.CANCELLED, first.getState());
        assertEquals(AsyncState.READY, ready.getState());
        assertNull(group.getFailure());
    }

    /**
     * A {@code null} handle and a handle added twice are refused.
     */
    @Test
    void testInvalid()
    {
        final AsyncHandle<String> handle = AsyncLoading.ready("hero");
        group.add(handle);

        assertThrows(IllegalArgumentException.class, () -> group.add(null));
        assertThrows(IllegalArgumentException.class, () -> group.add(handle));
        assertEquals(1, group.size());
    }

    /**
     * Reading the state, the progress and the failure allocates nothing (REQ-RT-006): a loading screen reads them at
     * every frame.
     */
    @Test
    void testReadsAllocateNothing()
    {
        group.add(AsyncLoading.ready("hero"));
        group.add(pending("tiles"));
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        double sum = 0.0;

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int i = 0; i < ITERATIONS; i++)
        {
            sum += group.getProgress();
            if (group.getState() != AsyncState.PENDING || group.getFailure() != null)
            {
                sum = -1.0;
            }
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, "Allocated " + allocated + " bytes");
        assertEquals(ITERATIONS / 2.0, sum);
    }
}
