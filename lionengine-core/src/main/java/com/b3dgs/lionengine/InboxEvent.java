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
 * Event handed from any thread to the runtime thread through an {@link Inbox} (ADR-0018, ADR-0040).
 *
 * <p>
 * The producing module implements it and applies its own effect in {@link #deliver()}: input pending state, completion
 * of an asynchronous handle, playlist advance, platform event. The inbox never inspects event types, so {@code core}
 * knows no producer. A producer may reuse event instances to allocate nothing per event (REQ-RT-006).
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface InboxEvent
{
    /**
     * Applies the effect of the event.
     *
     * <p>
     * Called once per accepted post, on the runtime thread, when the inbox delivers its events at the start of an
     * iteration, before the update steps. An exception thrown here stops the loop (fail-fast, ADR-0017).
     * </p>
     */
    void deliver();
}
