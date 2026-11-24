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

/**
 * Bounded first-in first-out queue of {@link InboxEvent}s, the only way for another thread to reach the runtime thread
 * (ADR-0018, ADR-0031, ADR-0040, REQ-RT-001, REQ-RT-002).
 *
 * <p>
 * The game loop owns the inbox (its capacity comes from the loop configuration, 1,024 by default) and delivers it at
 * the start of each iteration, before the update steps. Pause, resume, stop and time-scale requests are not inbox
 * entries: they go through {@link LoopControl}.
 * </p>
 *
 * <p>
 * Threads: {@link #post(InboxEvent)} may be called from any thread; {@link #deliverAll()} and
 * {@link #getDroppedCount()} only from the runtime thread.
 * </p>
 *
 * @since 11.0.0
 */
public interface Inbox
{
    /**
     * Posts an event for delivery on the runtime thread.
     *
     * <p>
     * Never blocks beyond a short lock and allocates nothing. When the inbox is full the event is rejected and counted,
     * and the drop is logged once at the next delivery.
     * </p>
     *
     * @param event The event to deliver (never {@code null}).
     * @return {@code true} if the event was accepted, {@code false} if it was rejected because the inbox is full.
     * @throws IllegalArgumentException If {@code event} is {@code null}.
     */
    boolean post(InboxEvent event);

    /**
     * Delivers every accepted event, in arrival order, by calling {@link InboxEvent#deliver()}.
     *
     * <p>
     * Runtime thread only. An event posted during the delivery is delivered at the next one; a listener registered
     * during a delivery takes effect after it.
     * </p>
     */
    void deliverAll();

    /**
     * Returns the number of events rejected since the inbox was created.
     *
     * @return The number of rejected events (zero or more).
     */
    long getDroppedCount();
}
