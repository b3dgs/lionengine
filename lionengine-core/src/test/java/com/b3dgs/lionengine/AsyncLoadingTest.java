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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test {@link AsyncLoading} and its handles with a fake worker pool, an inbox spy and real worker threads
 * (REQ-RT-004, REQ-RT-010, ADR-0019, ADR-0031, ADR-0040).
 */
final class AsyncLoadingTest
{
    /** Milliseconds to wait for a worker thread. */
    private static final long TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(10L);

    /**
     * Pool running each task on a new thread.
     */
    private static final class ThreadPool implements WorkerPool
    {
        /** The thread of the last task. */
        private Thread thread;

        @Override
        public void submit(WorkerTask task)
        {
            thread = new Thread(task::execute, "worker");
            thread.start();
        }

        @Override
        public int getWorkerCount()
        {
            return 1;
        }
    }

    /**
     * Inbox of one place, already taken, that tells when it refused a post.
     */
    private static final class FullInbox implements Inbox
    {
        /** The inbox holding the events. */
        private final InboxSpy spy = new InboxSpy(1);
        /** Counted down at the first refused post. */
        private final CountDownLatch refused = new CountDownLatch(1);

        /**
         * Creates the inbox, with its only place taken.
         */
        FullInbox()
        {
            spy.post(() ->
            {
                // Occupies the only place of the inbox.
            });
        }

        @Override
        public boolean post(InboxEvent event)
        {
            final boolean accepted = spy.post(event);
            if (!accepted)
            {
                refused.countDown();
            }
            return accepted;
        }

        @Override
        public void deliverAll()
        {
            spy.deliverAll();
        }

        @Override
        public long getDroppedCount()
        {
            return spy.getDroppedCount();
        }

        /**
         * Waits for a refused post.
         *
         * @throws InterruptedException If interrupted.
         */
        void awaitRefused() throws InterruptedException
        {
            assertTrue(refused.await(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS), "No post refused in time");
        }
    }

    /** Number of completions run. */
    private final AtomicInteger completions = new AtomicInteger();
    /** Pool with one queued worker. */
    private FakeWorkerPool workers;
    /** Inbox of the results. */
    private InboxSpy inbox;

    /**
     * Creates the pool and the inbox of each test.
     */
    @BeforeEach
    void prepare()
    {
        workers = new FakeWorkerPool(1);
        inbox = new InboxSpy();
    }

    /**
     * Submits a request whose work returns its text and whose completion counts and marks it.
     *
     * @param pool The pool.
     * @param text The result of the work.
     * @return The handle.
     */
    private AsyncHandle<String> submit(WorkerPool pool, String text)
    {
        return AsyncLoading.submit(pool, inbox, () -> text, result ->
        {
            completions.incrementAndGet();
            return "loaded " + result;
        });
    }

    /**
     * A request is pending until the inbox delivers its result, even once its work ran; then it is ready with the
     * value of its completion and a progress of 1.
     */
    @Test
    void testReady()
    {
        final AsyncHandle<String> handle = submit(workers, "hero");

        assertEquals(AsyncState.PENDING, handle.getState());
        assertEquals(0.0, handle.getProgress());
        assertThrows(IllegalStateException.class, handle::get);
        assertNull(handle.getFailure());

        workers.runAll();
        assertEquals(AsyncState.PENDING, handle.getState());
        assertEquals(1, inbox.getPendingCount());

        inbox.deliverAll();
        assertEquals(AsyncState.READY, handle.getState());
        assertEquals("loaded hero", handle.get());
        assertEquals(1.0, handle.getProgress());
        assertNull(handle.getFailure());
        assertEquals(1, completions.get());
    }

    /**
     * Without worker the work runs during the submission, but the state still changes at inbox delivery only.
     */
    @Test
    void testSynchronousPool()
    {
        final AsyncHandle<String> handle = submit(new FakeWorkerPool(0), "hero");

        assertEquals(AsyncState.PENDING, handle.getState());
        assertEquals(1, inbox.getPendingCount());
        inbox.deliverAll();
        assertEquals("loaded hero", handle.get());
    }

    /**
     * A failure of the work fails the request at delivery, with that exception; the completion does not run.
     */
    @Test
    void testWorkFailure()
    {
        final IllegalArgumentException failure = new IllegalArgumentException("Invalid content");
        final AsyncHandle<String> handle = AsyncLoading.<String, String>submit(workers, inbox, () ->
        {
            throw failure;
        }, result ->
        {
            completions.incrementAndGet();
            return result;
        });

        workers.runAll();
        assertEquals(AsyncState.PENDING, handle.getState());
        inbox.deliverAll();

        assertEquals(AsyncState.FAILED, handle.getState());
        assertSame(failure, handle.getFailure());
        assertEquals(1.0, handle.getProgress());
        assertThrows(IllegalStateException.class, handle::get);
        assertEquals(0, completions.get());
    }

    /**
     * A failure of the completion fails the request with that exception, and does not stop the delivery.
     */
    @Test
    void testCompletionFailure()
    {
        final IllegalStateException failure = new IllegalStateException("Cache limit exceeded");
        final AsyncHandle<String> handle = AsyncLoading.submit(workers, inbox, () -> "hero", result ->
        {
            throw failure;
        });
        final AsyncHandle<String> other = submit(workers, "slime");

        workers.runAll();
        inbox.deliverAll();

        assertSame(failure, handle.getFailure());
        assertEquals(AsyncState.FAILED, handle.getState());
        assertEquals("loaded slime", other.get());
    }

    /**
     * A work or a completion returning {@code null} fails the request.
     */
    @Test
    void testNullResult()
    {
        final AsyncHandle<String> nullWork = AsyncLoading.submit(workers, inbox, () -> null, result -> "never");
        final AsyncHandle<String> nullValue = AsyncLoading.submit(workers, inbox, () -> "hero", result -> null);

        workers.runAll();
        inbox.deliverAll();

        assertInstanceOf(IllegalStateException.class, nullWork.getFailure());
        assertInstanceOf(IllegalStateException.class, nullValue.getFailure());
    }

    /**
     * A pending request can be cancelled once; its result, before or after its work ran, is discarded without
     * completion; a final request is not cancelled.
     */
    @Test
    void testCancel()
    {
        final AsyncHandle<String> before = submit(workers, "hero");
        final AsyncHandle<String> after = submit(workers, "slime");
        final AsyncHandle<String> ready = submit(workers, "tiles");

        assertTrue(before.cancel());
        assertFalse(before.cancel());
        workers.runAll();
        assertTrue(after.cancel());
        inbox.deliverAll();

        assertEquals(AsyncState.CANCELLED, before.getState());
        assertEquals(AsyncState.CANCELLED, after.getState());
        assertEquals(0.0, after.getProgress());
        assertNull(after.getFailure());
        assertThrows(IllegalStateException.class, after::get);
        assertEquals(1, completions.get());
        assertFalse(ready.cancel());
        assertEquals(AsyncState.READY, ready.getState());
    }

    /**
     * Without worker, a full inbox fails the request before the submission returns, since nobody else could deliver
     * it; the completion does not run.
     */
    @Test
    void testSynchronousInboxFull()
    {
        inbox = new InboxSpy(1);
        inbox.post(() ->
        {
            // Occupies the only place of the inbox.
        });

        final AsyncHandle<String> handle = submit(new FakeWorkerPool(0), "hero");

        assertEquals(AsyncState.FAILED, handle.getState());
        assertTrue(handle.getFailure().getMessage().startsWith("Inbox full"));
        inbox.deliverAll();
        assertEquals(AsyncState.FAILED, handle.getState());
        assertEquals(0, completions.get());
    }

    /**
     * On a worker, a full inbox delays the result until the inbox accepts it: nothing is lost.
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    void testWorkerInboxFull() throws InterruptedException
    {
        final FullInbox full = new FullInbox();
        final ThreadPool pool = new ThreadPool();

        final AsyncHandle<String> handle = AsyncLoading.submit(pool, full, () -> "hero", result -> "loaded " + result);
        full.awaitRefused();
        assertEquals(AsyncState.PENDING, handle.getState());

        full.deliverAll();
        pool.thread.join(TIMEOUT_MILLIS);
        assertFalse(pool.thread.isAlive());
        full.deliverAll();

        assertEquals("loaded hero", handle.get());
    }

    /**
     * A worker interrupted while the inbox is full gives up: the request stays pending and the interrupt is kept.
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    void testInterruptedWorker() throws InterruptedException
    {
        final FullInbox full = new FullInbox();
        final ThreadPool pool = new ThreadPool();

        final AsyncHandle<String> handle = AsyncLoading.submit(pool, full, () -> "hero", result -> "loaded " + result);
        full.awaitRefused();
        pool.thread.interrupt();
        pool.thread.join(TIMEOUT_MILLIS);

        assertFalse(pool.thread.isAlive());
        full.deliverAll();
        assertEquals(AsyncState.PENDING, handle.getState());
        full.deliverAll();
        assertEquals(AsyncState.PENDING, handle.getState());
    }

    /**
     * The final handles are ready or failed at once, with a progress of 1.
     */
    @Test
    void testFinalHandles()
    {
        final IllegalArgumentException failure = new IllegalArgumentException("No decoder");
        final AsyncHandle<String> ready = AsyncLoading.ready("hero");
        final AsyncHandle<String> failed = AsyncLoading.failed(failure);

        assertEquals("hero", ready.get());
        assertEquals(1.0, ready.getProgress());
        assertFalse(ready.cancel());
        assertSame(failure, failed.getFailure());
        assertEquals(AsyncState.FAILED, failed.getState());
        assertEquals(1.0, failed.getProgress());
        assertFalse(failed.cancel());
    }

    /**
     * {@code null} arguments and a closed pool are refused.
     */
    @Test
    void testInvalidArguments()
    {
        final AsyncWork<String> work = () -> "hero";
        final AsyncCompletion<String, String> completion = result -> result;
        final FakeWorkerPool closed = new FakeWorkerPool(1);
        closed.close();

        assertThrows(IllegalArgumentException.class, () -> AsyncLoading.submit(null, inbox, work, completion));
        assertThrows(IllegalArgumentException.class, () -> AsyncLoading.submit(workers, null, work, completion));
        assertThrows(IllegalArgumentException.class, () -> AsyncLoading.submit(workers, inbox, null, completion));
        assertThrows(IllegalArgumentException.class, () -> AsyncLoading.submit(workers, inbox, work, null));
        assertThrows(IllegalStateException.class, () -> AsyncLoading.submit(closed, inbox, work, completion));
        assertThrows(IllegalArgumentException.class, () -> AsyncLoading.ready(null));
        assertThrows(IllegalArgumentException.class, () -> AsyncLoading.failed(null));
        assertEquals(0, workers.getPendingCount());
    }
}
