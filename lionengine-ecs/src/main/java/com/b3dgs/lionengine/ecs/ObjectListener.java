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
package com.b3dgs.lionengine.ecs;

/**
 * Listens to the objects that become alive and that are destroyed in a world (REQ-ECS-007, REQ-ECS-012, ADR-0011,
 * ADR-0018, ADR-0030, ADR-0034, ADR-0077).
 *
 * <p>
 * Registered with {@link World#addObjectListener(ObjectListener)}. The world calls it synchronously, on the runtime
 * thread, during the flush, once per effective change and in the order the flush applies the recorded commands: a
 * command that changes nothing (the removal of an object already removed) fires nothing. Several listeners are called
 * in registration order. The removal of an object first reports the removal of each of its traits to the
 * {@link TraitListener}s, by ascending type ID, then its destruction here.
 * </p>
 *
 * <p>
 * During a call the listener may read the world (liveness, traits, queries) and change the data inside traits, and may
 * add or remove listeners, which takes effect from the next event. It must not change the structure of the world:
 * creating or removing an object, adding or removing a trait, flushing, updating, registering a trait type, creating a
 * query, reserving capacity or adding or removing a system throw {@link IllegalStateException}. A listener that needs
 * a structural change keeps the ID in its own buffer and lets a system record the change in the next step. An
 * exception thrown by a listener propagates out of the flush; the world is undefined afterwards (ADR-0017).
 * </p>
 *
 * <p>
 * Both methods do nothing by default, so an implementation overrides only the events it needs. No allocation by the
 * world per event.
 * </p>
 *
 * @since 11.0.0
 */
public interface ObjectListener
{
    /**
     * Called when an object becomes alive, at the flush that applies its creation.
     *
     * @param objectId The object ID, alive during the call.
     */
    default void onCreated(long objectId)
    {
        // Nothing by default: the listener ignores this event.
    }

    /**
     * Called when an object is destroyed, at the flush that applies its removal, after the removal of its traits.
     *
     * @param objectId The object ID, already stale during the call: {@link World#isAlive(long)} returns {@code false}.
     */
    default void onDestroyed(long objectId)
    {
        // Nothing by default: the listener ignores this event.
    }
}
