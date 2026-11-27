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
 * ADR-0074, ADR-0075).
 *
 * <p>
 * Parallel primitive arrays of a command kind, an object ID, a trait type ID and an argument (the staged instance of a
 * trait addition). The arrays start with the object capacity of the world and double when a command does not fit, a
 * warm-up cost; they never shrink, so a step that records no more commands than an earlier one allocates nothing.
 * Object and trait commands share the log, so they keep one recorded order (ADR-0074, decision 7).
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
    /** Kind of the addition of a trait. */
    static final byte ADD_TRAIT = 2;
    /** Kind of the removal of a trait. */
    static final byte REMOVE_TRAIT = 3;
    /** Trait type ID of an object command. */
    static final int NO_TYPE = -1;

    /** Kind per command. */
    private byte[] kinds;
    /** Object ID per command. */
    private long[] ids;
    /** Trait type ID per command, {@link #NO_TYPE} for an object command. */
    private int[] types;
    /** Argument per command: the staged slot of a trait addition, otherwise {@code 0}. */
    private int[] args;
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
        types = new int[capacity];
        args = new int[capacity];
    }

    /**
     * Records an object command after the others.
     *
     * @param kind The kind, {@link #CREATE} or {@link #REMOVE}.
     * @param id The object ID.
     * @throws IllegalStateException If {@link WorldConfig#MAX_OBJECT_CAPACITY} commands are already recorded.
     */
    void add(byte kind, long id)
    {
        add(kind, id, NO_TYPE, 0);
    }

    /**
     * Records a command after the others.
     *
     * @param kind The kind.
     * @param id The object ID.
     * @param type The trait type ID, {@link #NO_TYPE} for an object command.
     * @param arg The argument, the staged slot of a trait addition.
     * @throws IllegalStateException If {@link WorldConfig#MAX_OBJECT_CAPACITY} commands are already recorded.
     */
    void add(byte kind, long id, int type, int arg)
    {
        ensureRoom();
        kinds[count] = kind;
        ids[count] = id;
        types[count] = type;
        args[count] = arg;
        count++;
    }

    /**
     * Makes room for one more command, doubling the arrays when they are full.
     *
     * @throws IllegalStateException If {@link WorldConfig#MAX_OBJECT_CAPACITY} commands are already recorded.
     */
    void ensureRoom()
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
            final int[] newTypes = new int[capacity];
            final int[] newArgs = new int[capacity];
            System.arraycopy(kinds, 0, newKinds, 0, count);
            System.arraycopy(ids, 0, newIds, 0, count);
            System.arraycopy(types, 0, newTypes, 0, count);
            System.arraycopy(args, 0, newArgs, 0, count);
            kinds = newKinds;
            ids = newIds;
            types = newTypes;
            args = newArgs;
        }
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
     * Returns the trait type ID of a command.
     *
     * @param command The command, in [0, {@link #size()}).
     * @return Its trait type ID, {@link #NO_TYPE} for an object command.
     */
    int getType(int command)
    {
        return types[command];
    }

    /**
     * Returns the argument of a command.
     *
     * @param command The command, in [0, {@link #size()}).
     * @return Its argument: the staged slot of a trait addition, otherwise {@code 0}.
     */
    int getArg(int command)
    {
        return args[command];
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
