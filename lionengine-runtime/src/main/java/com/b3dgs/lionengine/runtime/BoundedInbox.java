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

import java.util.Arrays;

import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.InboxEvent;
import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;

/**
 * Bounded first-in first-out inbox of the game loop (ADR-0018, ADR-0031, ADR-0040, REQ-RT-002, REQ-RT-009).
 *
 * <p>
 * A ring buffer guarded by a private lock held only to add, count or take events, never while an event runs.
 * {@link #deliverAll()} takes the events accepted so far, then runs them outside the lock: an event posted during the
 * delivery waits for the next one. Rejected events are counted and reported once, as a warning, at the next delivery.
 * Allocates nothing.
 * </p>
 */
final class BoundedInbox implements Inbox
{
    /** Warning of rejected events, followed by their number. */
    static final String WARNING_DROPPED = "Inbox full, events dropped:";

    /** Guards the queue and the counters. */
    private final Object lock = new Object();
    /** Accepted events, from {@link #head}, in arrival order. */
    private final InboxEvent[] queue;
    /** Events of the current delivery. */
    private final InboxEvent[] delivering;
    /** Log of the drop reports. */
    private final Log log;
    /** Index of the oldest accepted event. */
    private int head;
    /** Number of accepted events not delivered. */
    private int count;
    /** Number of events rejected since creation. */
    private long dropped;
    /** Number of events rejected since the last report. */
    private long unreported;
    /** Whether a delivery is running, to reject a nested one. */
    private boolean deliveryRunning;

    /**
     * Creates an empty inbox.
     *
     * @param capacity The number of events it holds, positive.
     * @param log The log of the drop reports.
     */
    BoundedInbox(int capacity, Log log)
    {
        queue = new InboxEvent[capacity];
        delivering = new InboxEvent[capacity];
        this.log = log;
    }

    @Override
    public boolean post(InboxEvent event)
    {
        if (event == null)
        {
            throw new IllegalArgumentException("Event must not be null");
        }
        final boolean accepted;
        synchronized (lock)
        {
            accepted = count < queue.length;
            if (accepted)
            {
                queue[(head + count) % queue.length] = event;
                count++;
            }
            else
            {
                dropped++;
                unreported++;
            }
        }
        return accepted;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * An exception thrown by an event stops the delivery: the remaining events of this delivery are discarded and the
     * exception reaches the loop.
     * </p>
     *
     * @throws IllegalStateException If called from an event of the running delivery.
     */
    @Override
    public void deliverAll()
    {
        if (deliveryRunning)
        {
            throw new IllegalStateException("Delivery already running");
        }
        final int total;
        final long drops;
        synchronized (lock)
        {
            total = count;
            for (int i = 0; i < total; i++)
            {
                final int index = (head + i) % queue.length;
                delivering[i] = queue[index];
                queue[index] = null;
            }
            head = 0;
            count = 0;
            drops = unreported;
            unreported = 0L;
        }
        if (drops > 0L)
        {
            log.log(LogLevel.WARNING, WARNING_DROPPED, drops);
        }
        deliveryRunning = true;
        try
        {
            for (int i = 0; i < total; i++)
            {
                delivering[i].deliver();
            }
        }
        finally
        {
            Arrays.fill(delivering, 0, total, null);
            deliveryRunning = false;
        }
    }

    @Override
    public long getDroppedCount()
    {
        synchronized (lock)
        {
            return dropped;
        }
    }
}
