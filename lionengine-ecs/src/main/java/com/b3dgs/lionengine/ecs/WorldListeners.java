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
 * The listeners of a world and the delivery of the flush events to them (REQ-ECS-007, ADR-0018, ADR-0034, ADR-0077).
 *
 * <p>
 * Each list is an array replaced on every registration and removal (copy on write), and each delivery iterates the
 * array current at its start: a listener added or removed during a delivery takes effect from the next event, at the
 * end of that delivery (ADR-0018), and a delivery allocates nothing. Registrations allocate, outside the steady paths.
 * </p>
 */
final class WorldListeners
{
    /** No object listener. */
    private static final ObjectListener[] NO_OBJECT_LISTENERS = new ObjectListener[0];
    /** No trait listener. */
    private static final TraitListener[] NO_TRAIT_LISTENERS = new TraitListener[0];

    /** The object listeners, in registration order. */
    private ObjectListener[] objectListeners = NO_OBJECT_LISTENERS;
    /** The trait listeners, in registration order. */
    private TraitListener[] traitListeners = NO_TRAIT_LISTENERS;

    /**
     * Creates empty lists.
     */
    WorldListeners()
    {
        super();
    }

    /**
     * Adds an object listener at the end of its list.
     *
     * @param listener The listener.
     * @throws IllegalArgumentException If the listener is {@code null} or already registered.
     */
    void addObjectListener(ObjectListener listener)
    {
        if (listener == null || indexOf(objectListeners, listener) >= 0)
        {
            throw new IllegalArgumentException("Object listener is null or already registered");
        }
        final ObjectListener[] grown = new ObjectListener[objectListeners.length + 1];
        System.arraycopy(objectListeners, 0, grown, 0, objectListeners.length);
        grown[objectListeners.length] = listener;
        objectListeners = grown;
    }

    /**
     * Removes an object listener.
     *
     * @param listener The listener.
     * @throws IllegalArgumentException If the listener is not registered.
     */
    void removeObjectListener(ObjectListener listener)
    {
        final int index = indexOf(objectListeners, listener);
        if (index < 0)
        {
            throw new IllegalArgumentException("Object listener not registered");
        }
        final ObjectListener[] shrunk = new ObjectListener[objectListeners.length - 1];
        System.arraycopy(objectListeners, 0, shrunk, 0, index);
        System.arraycopy(objectListeners, index + 1, shrunk, index, shrunk.length - index);
        objectListeners = shrunk;
    }

    /**
     * Adds a trait listener at the end of its list.
     *
     * @param listener The listener.
     * @throws IllegalArgumentException If the listener is {@code null} or already registered.
     */
    void addTraitListener(TraitListener listener)
    {
        if (listener == null || indexOf(traitListeners, listener) >= 0)
        {
            throw new IllegalArgumentException("Trait listener is null or already registered");
        }
        final TraitListener[] grown = new TraitListener[traitListeners.length + 1];
        System.arraycopy(traitListeners, 0, grown, 0, traitListeners.length);
        grown[traitListeners.length] = listener;
        traitListeners = grown;
    }

    /**
     * Removes a trait listener.
     *
     * @param listener The listener.
     * @throws IllegalArgumentException If the listener is not registered.
     */
    void removeTraitListener(TraitListener listener)
    {
        final int index = indexOf(traitListeners, listener);
        if (index < 0)
        {
            throw new IllegalArgumentException("Trait listener not registered");
        }
        final TraitListener[] shrunk = new TraitListener[traitListeners.length - 1];
        System.arraycopy(traitListeners, 0, shrunk, 0, index);
        System.arraycopy(traitListeners, index + 1, shrunk, index, shrunk.length - index);
        traitListeners = shrunk;
    }

    /**
     * Delivers a creation.
     *
     * @param objectId The object ID.
     */
    void created(long objectId)
    {
        for (final ObjectListener listener : objectListeners)
        {
            listener.onCreated(objectId);
        }
    }

    /**
     * Delivers a destruction.
     *
     * @param objectId The object ID.
     */
    void destroyed(long objectId)
    {
        for (final ObjectListener listener : objectListeners)
        {
            listener.onDestroyed(objectId);
        }
    }

    /**
     * Delivers a trait addition.
     *
     * @param objectId The object ID.
     * @param typeId The trait type ID.
     */
    void traitAdded(long objectId, int typeId)
    {
        for (final TraitListener listener : traitListeners)
        {
            listener.onTraitAdded(objectId, typeId);
        }
    }

    /**
     * Delivers a trait removal.
     *
     * @param objectId The object ID.
     * @param typeId The trait type ID.
     */
    void traitRemoved(long objectId, int typeId)
    {
        for (final TraitListener listener : traitListeners)
        {
            listener.onTraitRemoved(objectId, typeId);
        }
    }

    /**
     * Returns the position of a listener, by identity.
     *
     * @param listeners The listeners.
     * @param listener The listener looked for, possibly {@code null}.
     * @return The position, {@code -1} if absent.
     */
    private static int indexOf(Object[] listeners, Object listener)
    {
        for (int i = 0; i < listeners.length; i++)
        {
            if (listeners[i] == listener)
            {
                return i;
            }
        }
        return -1;
    }
}
