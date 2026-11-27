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
 * The effective changes of a flush, one call per change, in the order the flush applies them (REQ-ECS-012, ADR-0034,
 * ADR-0075, ADR-0076).
 *
 * <p>
 * The seam of the world listeners of a later batch: the flush calls it only for changes that happen, never for a
 * command that changes nothing (a removal of an object or a trait already gone, an addition to an object removed by an
 * earlier command). A replaced trait is reported as removed, then added. The removal of an object reports the removal
 * of each of its traits, by ascending type ID, then the destruction of the object.
 * </p>
 *
 * <p>
 * The queries are not maintained through this hook: the world updates them itself, right after each change of a trait
 * mask, so that during a call the queries agree with the masks. When a trait is added, the queries already list the
 * object; when a trait is removed, the call comes before the mask changes, so the queries still list it (ADR-0076).
 * </p>
 *
 * <p>
 * Every method does nothing by default. Called on the runtime thread, during the flush; an implementation must not
 * change the world.
 * </p>
 */
interface ChangeHook
{
    /**
     * Called when an object becomes alive.
     *
     * @param objectId The object ID.
     */
    default void onCreated(long objectId)
    {
        // Nothing by default: the world without listeners ignores its changes.
    }

    /**
     * Called when an object is removed, after the removal of its traits.
     *
     * @param objectId The object ID, stale after the flush.
     */
    default void onDestroyed(long objectId)
    {
        // Nothing by default: the world without listeners ignores its changes.
    }

    /**
     * Called when a trait is attached to an object.
     *
     * @param objectId The object ID.
     * @param traitTypeId The trait type ID.
     */
    default void onTraitAdded(long objectId, int traitTypeId)
    {
        // Nothing by default: the world without listeners ignores its changes.
    }

    /**
     * Called when a trait is detached from an object, before its instance is reset.
     *
     * @param objectId The object ID.
     * @param traitTypeId The trait type ID.
     */
    default void onTraitRemoved(long objectId, int traitTypeId)
    {
        // Nothing by default: the world without listeners ignores its changes.
    }
}
