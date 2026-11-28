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

package com.b3dgs.lionengine.traits;

/**
 * Receives the collision events of a {@link CollisionSystem}: the pairs of objects that start and stop overlapping
 * (REQ-STD-005, REQ-STD-010, ADR-0018, ADR-0030, ADR-0034, ADR-0082).
 *
 * <p>
 * Registered on the collision system of a world with {@link CollisionSystem#addListener(CollisionListener)}, called
 * synchronously on the runtime thread at the end of each step of the system, after the detection (ADR-0034): first
 * every exit, then every enter, each in ascending order of {@code (objectA, objectB)}, with {@code objectA < objectB};
 * the listeners in registration order for each event. Every overlapping pair whose layers and masks accept each other
 * is reported, triggers or not: object versus object is detection only (ADR-0030).
 * </p>
 *
 * <p>
 * An exit is also reported when one object of an overlapping pair is removed, or loses its {@link Collidable} or its
 * {@link Transformable}, or gains an excluded trait: at the first step of the system after the flush that applied the
 * change, the object then being out of the query; a removed object's ID is stale during the call
 * ({@code World.isAlive} returns {@code false}).
 * </p>
 *
 * <p>
 * A listener runs inside the step of the world, like a system at slot {@value CollisionSystem#SLOT}: it may read and
 * change trait data, and record structural changes (create or remove objects, add or remove traits), applied at the
 * flush that ends the step; it must not flush or update the world, nor add or remove a system
 * ({@link IllegalStateException}). A listener added or removed during a delivery takes effect from the next event
 * (ADR-0018). An exception propagates out of {@code World.update}; the world is undefined afterwards (ADR-0017).
 * </p>
 *
 * <p>
 * Both methods do nothing by default, so an implementation overrides only the events it needs. No allocation by the
 * system per event.
 * </p>
 *
 * @since 11.0.0
 */
public interface CollisionListener
{
    /**
     * Called when two objects start overlapping.
     *
     * @param objectA The lower ID of the pair, alive.
     * @param objectB The higher ID of the pair, alive.
     */
    default void onEnter(long objectA, long objectB)
    {
        // Nothing by default: the listener ignores this event.
    }

    /**
     * Called when two objects stop overlapping, or one of them left the collision.
     *
     * @param objectA The lower ID of the pair, stale if its object was removed.
     * @param objectB The higher ID of the pair, stale if its object was removed.
     */
    default void onExit(long objectA, long objectB)
    {
        // Nothing by default: the listener ignores this event.
    }
}
