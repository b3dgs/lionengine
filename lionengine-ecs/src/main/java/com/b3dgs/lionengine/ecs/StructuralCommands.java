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
 * The structural changes recorded since the last flush, in recorded order (REQ-ECS-006, REQ-ECS-012, ADR-0030,
 * ADR-0074).
 *
 * <p>
 * Parallel primitive arrays of a command kind and an object ID. The arrays start with the object capacity of the world
 * and double when a command does not fit, a warm-up cost; they never shrink, so a step that records no more commands
 * than an earlier one allocates nothing. The trait commands of the next batch are new kinds of the same log.
 * </p>
 *
 * <p>
 * Not thread-safe: owned by the runtime thread with its world.
 * </p>
 */
final class StructuralCommands
{
    /** Kind of the creation of an object. */
    static final byte CREATE = 0;
    /** Kind of the removal of an object. */
    static final byte REMOVE = 1;

    /** Kind per command. */
    private byte[] kinds;
    /** Object ID per command. */
    private long[] ids;
    /** Number of commands. */
    private int count;

    /**
     * Creates an empty log.
     *
     * @param capacity The initial capacity, positive.
     */
    StructuralCommands(int capacity)
    {
        kinds = new byte[capacity];
        ids = new long[capacity];
    }

    /**
     * Records a command after the others.
     *
     * @param kind The kind.
     * @param id The object ID.
     * @throws IllegalStateException If {@link WorldConfig#MAX_OBJECT_CAPACITY} commands are already recorded.
     */
    void add(byte kind, long id)
    {
        if (count == ids.length)
        {
            if (count == WorldConfig.MAX_OBJECT_CAPACITY)
            {
                throw new IllegalStateException("Too many commands before the flush: 2^30");
            }
            final int capacity = Math.min(count * 2, WorldConfig.MAX_OBJECT_CAPACITY);
            final byte[] newKinds = new byte[capacity];
            final long[] newIds = new long[capacity];
            System.arraycopy(kinds, 0, newKinds, 0, count);
            System.arraycopy(ids, 0, newIds, 0, count);
            kinds = newKinds;
            ids = newIds;
        }
        kinds[count] = kind;
        ids[count] = id;
        count++;
    }

    /**
     * Returns the number of commands.
     *
     * @return The number recorded since the last {@link #clear()}.
     */
    int size()
    {
        return count;
    }

    /**
     * Returns the kind of a command.
     *
     * @param command The command, in [0, {@link #size()}).
     * @return Its kind.
     */
    byte getKind(int command)
    {
        return kinds[command];
    }

    /**
     * Returns the object ID of a command.
     *
     * @param command The command, in [0, {@link #size()}).
     * @return Its object ID.
     */
    long getId(int command)
    {
        return ids[command];
    }

    /**
     * Returns the capacity.
     *
     * @return The number of commands held without growing.
     */
    int getCapacity()
    {
        return ids.length;
    }

    /**
     * Forgets every command, keeping the capacity.
     */
    void clear()
    {
        count = 0;
    }
}
