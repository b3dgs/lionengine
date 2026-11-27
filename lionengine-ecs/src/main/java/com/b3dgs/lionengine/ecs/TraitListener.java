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
 * Listens to the traits attached to and detached from the objects of a world (REQ-ECS-007, REQ-ECS-012, ADR-0011,
 * ADR-0018, ADR-0030, ADR-0034, ADR-0077).
 *
 * <p>
 * Registered with {@link World#addTraitListener(TraitListener)}; one listener receives the events of every trait type,
 * named by the type ID of {@link TraitStorage#getTypeId()}, so a listener interested in one type compares it. The
 * world calls it synchronously, on the runtime thread, during the flush, once per effective change and in the order
 * the flush applies the recorded commands. A replaced trait is reported as removed, then added; an addition then a
 * removal recorded in one step both apply and both fire; the removal of an object reports the removal of each of its
 * traits by ascending type ID, before its destruction; a command that changes nothing (removing a trait the object
 * does not have, adding a trait to an object already removed) fires nothing. Several listeners are called in
 * registration order.
 * </p>
 *
 * <p>
 * State during a call: for an addition the trait is attached and readable with {@link TraitStorage#get(long)}, and
 * the queries already list the object if it matches them; for a removal the trait is still attached and readable,
 * and the queries still list the object; the instance is reset right after the last listener returns. The rules of
 * {@link ObjectListener} apply: reading and changing trait data are allowed, adding or removing listeners takes effect
 * from the next event, and a structural change of the world throws {@link IllegalStateException}. An exception thrown
 * by a listener propagates out of the flush; the world is undefined afterwards (ADR-0017).
 * </p>
 *
 * <p>
 * Both methods do nothing by default, so an implementation overrides only the events it needs. No allocation by the
 * world per event.
 * </p>
 *
 * @since 11.0.0
 */
public interface TraitListener
{
    /**
     * Called when a trait is attached to an object, at the flush that applies its addition.
     *
     * @param objectId The object ID, alive during the call.
     * @param traitTypeId The trait type ID.
     */
    default void onTraitAdded(long objectId, int traitTypeId)
    {
        // Nothing by default: the listener ignores this event.
    }

    /**
     * Called when a trait is detached from an object, at the flush that applies its removal, its replacement or the
     * removal of its object, before its instance is reset.
     *
     * @param objectId The object ID, alive during the call.
     * @param traitTypeId The trait type ID.
     */
    default void onTraitRemoved(long objectId, int traitTypeId)
    {
        // Nothing by default: the listener ignores this event.
    }
}
