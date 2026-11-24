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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Inbox that records what is posted, for the tests of event producers (ADR-0040, ADR-0045).
 *
 * <p>
 * Follows the {@link Inbox} contract (bounded, first-in first-out, events posted during a delivery wait for the next
 * one) and keeps every accepted event for assertions. Published in the {@code core} test JAR. Posting is thread-safe.
 * </p>
 */
public final class InboxSpy implements Inbox
{
    /** Guards the lists and the counter. */
    private final Object lock = new Object();
    /** Capacity. */
    private final int capacity;
    /** Accepted events not delivered yet. */
    private final List<InboxEvent> pending = new ArrayList<>();
    /** Every accepted event, in arrival order. */
    private final List<InboxEvent> posted = new ArrayList<>();
    /** Number of rejected events. */
    private long dropped;

    /**
     * Creates an inbox with the default capacity of the game loop, 1,024 events.
     */
    public InboxSpy()
    {
        this(1024);
    }

    /**
     * Creates an inbox.
     *
     * @param capacity The number of pending events it holds, positive.
     * @throws IllegalArgumentException If the capacity is not positive.
     */
    public InboxSpy(int capacity)
    {
        if (capacity < 1)
        {
            throw new IllegalArgumentException("Capacity not positive: " + capacity);
        }
        this.capacity = capacity;
    }

    @Override
    public boolean post(InboxEvent event)
    {
        if (event == null)
        {
            throw new IllegalArgumentException("Event must not be null");
        }
        synchronized (lock)
        {
            final boolean accepted = pending.size() < capacity;
            if (accepted)
            {
                pending.add(event);
                posted.add(event);
            }
            else
            {
                dropped++;
            }
            return accepted;
        }
    }

    @Override
    public void deliverAll()
    {
        final List<InboxEvent> events;
        synchronized (lock)
        {
            events = new ArrayList<>(pending);
            pending.clear();
        }
        for (int i = 0; i < events.size(); i++)
        {
            events.get(i).deliver();
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

    /**
     * Returns every accepted event, delivered or not.
     *
     * @return The events in arrival order (unmodifiable copy).
     */
    public List<InboxEvent> getPosted()
    {
        synchronized (lock)
        {
            return Collections.unmodifiableList(new ArrayList<>(posted));
        }
    }

    /**
     * Returns the number of accepted events not delivered yet.
     *
     * @return The number of pending events.
     */
    public int getPendingCount()
    {
        synchronized (lock)
        {
            return pending.size();
        }
    }
}
