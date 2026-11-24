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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test the {@code core} test fixtures {@link InboxSpy} and {@link FakeWorkerPool}.
 */
final class FixturesTest
{
    /**
     * The spy delivers in arrival order, records the accepted events and counts the rejected ones.
     */
    @Test
    void testInboxSpy()
    {
        final List<String> delivered = new ArrayList<>();
        final InboxSpy inbox = new InboxSpy(2);
        final InboxEvent first = () -> delivered.add("first");
        final InboxEvent second = () -> delivered.add("second");

        assertTrue(inbox.post(first));
        assertTrue(inbox.post(second));
        assertFalse(inbox.post(() -> delivered.add("third")));
        assertEquals(2, inbox.getPendingCount());
        assertEquals(1L, inbox.getDroppedCount());

        inbox.deliverAll();
        assertEquals(List.of("first", "second"), delivered);
        assertEquals(List.of(first, second), inbox.getPosted());
        assertEquals(0, inbox.getPendingCount());
    }

    /**
     * An event posted during a delivery waits for the next one.
     */
    @Test
    void testInboxSpyPostDuringDelivery()
    {
        final List<String> delivered = new ArrayList<>();
        final InboxSpy inbox = new InboxSpy();
        inbox.post(() -> inbox.post(() -> delivered.add("later")));

        inbox.deliverAll();
        assertTrue(delivered.isEmpty());
        inbox.deliverAll();
        assertEquals(List.of("later"), delivered);
    }

    /**
     * The spy rejects invalid arguments.
     */
    @Test
    void testInboxSpyArguments()
    {
        final InboxSpy inbox = new InboxSpy();

        assertThrows(IllegalArgumentException.class, () -> inbox.post(null));
        assertThrows(IllegalArgumentException.class, () -> new InboxSpy(0));
    }

    /**
     * Without workers a task runs when submitted.
     */
    @Test
    void testSynchronousPool()
    {
        final List<String> done = new ArrayList<>();
        final FakeWorkerPool pool = new FakeWorkerPool(0);

        pool.submit(() -> done.add("task"));

        assertEquals(0, pool.getWorkerCount());
        assertEquals(List.of("task"), done);
        assertEquals(1, pool.getCompletedCount());
    }

    /**
     * With workers, tasks wait until the test runs them, oldest first.
     */
    @Test
    void testDeferredPool()
    {
        final List<String> done = new ArrayList<>();
        final FakeWorkerPool pool = new FakeWorkerPool(2);
        pool.submit(() -> done.add("first"));
        pool.submit(() -> done.add("second"));
        pool.submit(() -> done.add("third"));

        assertEquals(2, pool.getWorkerCount());
        assertEquals(3, pool.getPendingCount());
        assertTrue(pool.runNext());
        assertEquals(List.of("first"), done);

        pool.runAll();
        assertEquals(List.of("first", "second", "third"), done);
        assertFalse(pool.runNext());
        assertEquals(3, pool.getCompletedCount());
    }

    /**
     * A closed pool rejects submissions; invalid arguments are rejected.
     */
    @Test
    void testPoolArguments()
    {
        final FakeWorkerPool pool = new FakeWorkerPool(1);
        assertThrows(IllegalArgumentException.class, () -> pool.submit(null));
        assertThrows(IllegalArgumentException.class, () -> new FakeWorkerPool(-1));

        pool.close();
        assertThrows(IllegalStateException.class, () -> pool.submit(pool::getPendingCount));
    }
}
