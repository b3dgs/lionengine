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
 * The systems of a world, sorted by slot, and their enabled flags (REQ-ECS-008, REQ-ECS-013, ADR-0030, ADR-0034,
 * ADR-0077).
 *
 * <p>
 * Parallel arrays sorted by ascending slot, systems of equal slots in registration order: an addition is inserted after
 * the last system whose slot is lower or equal. Each system has a requested flag, changed at any time, and an applied
 * flag, copied from the requested one at the start of each step, so a change made during a step applies at the next.
 * The arrays double when full and never shrink; running the systems allocates nothing.
 * </p>
 */
final class SystemSchedule
{
    /** Initial length of the arrays. */
    private static final int INITIAL_SYSTEMS = 8;

    /** The systems, by ascending slot. */
    private GameSystem[] systems = new GameSystem[INITIAL_SYSTEMS];
    /** The slots, ascending. */
    private int[] slots = new int[INITIAL_SYSTEMS];
    /** The requested enabled flags. */
    private boolean[] requested = new boolean[INITIAL_SYSTEMS];
    /** The enabled flags of the current step. */
    private boolean[] applied = new boolean[INITIAL_SYSTEMS];
    /** The number of systems. */
    private int count;

    /**
     * Creates an empty schedule.
     */
    SystemSchedule()
    {
        super();
    }

    /**
     * Adds an enabled system after the systems of lower or equal slots.
     *
     * @param system The system.
     * @param slot The slot.
     * @throws IllegalArgumentException If the system is {@code null} or already registered.
     */
    void add(GameSystem system, int slot)
    {
        if (system == null || indexOf(system) >= 0)
        {
            throw new IllegalArgumentException("System is null or already registered");
        }
        if (count == systems.length)
        {
            grow(count * 2);
        }
        int position = count;
        while (position > 0 && slots[position - 1] > slot)
        {
            position--;
        }
        final int moved = count - position;
        System.arraycopy(systems, position, systems, position + 1, moved);
        System.arraycopy(slots, position, slots, position + 1, moved);
        System.arraycopy(requested, position, requested, position + 1, moved);
        System.arraycopy(applied, position, applied, position + 1, moved);
        systems[position] = system;
        slots[position] = slot;
        requested[position] = true;
        applied[position] = true;
        count++;
    }

    /**
     * Removes a system, keeping the order of the others.
     *
     * @param system The system.
     * @throws IllegalArgumentException If the system is not registered.
     */
    void remove(GameSystem system)
    {
        final int position = checkIndex(system);
        final int moved = count - position - 1;
        System.arraycopy(systems, position + 1, systems, position, moved);
        System.arraycopy(slots, position + 1, slots, position, moved);
        System.arraycopy(requested, position + 1, requested, position, moved);
        System.arraycopy(applied, position + 1, applied, position, moved);
        count--;
        systems[count] = null;
    }

    /**
     * Requests the enabled state of a system, applied at the start of the next step.
     *
     * @param system The system.
     * @param enabled {@code true} to run it, {@code false} to skip it.
     * @throws IllegalArgumentException If the system is not registered.
     */
    void setEnabled(GameSystem system, boolean enabled)
    {
        requested[checkIndex(system)] = enabled;
    }

    /**
     * Returns the requested enabled state of a system.
     *
     * @param system The system.
     * @return The state the next step applies.
     * @throws IllegalArgumentException If the system is not registered.
     */
    boolean isEnabled(GameSystem system)
    {
        return requested[checkIndex(system)];
    }

    /**
     * Returns the number of systems.
     *
     * @return The registered systems.
     */
    int size()
    {
        return count;
    }

    /**
     * Applies the requested flags, then runs the enabled systems in order.
     *
     * @param stepSeconds The step, in seconds.
     */
    void run(double stepSeconds)
    {
        System.arraycopy(requested, 0, applied, 0, count);
        for (int i = 0; i < count; i++)
        {
            if (applied[i])
            {
                systems[i].update(stepSeconds);
            }
        }
    }

    /**
     * Returns the position of a registered system.
     *
     * @param system The system.
     * @return Its position.
     * @throws IllegalArgumentException If the system is not registered.
     */
    private int checkIndex(GameSystem system)
    {
        final int position = indexOf(system);
        if (position < 0)
        {
            throw new IllegalArgumentException("System not registered");
        }
        return position;
    }

    /**
     * Returns the position of a system, by identity.
     *
     * @param system The system, possibly {@code null}.
     * @return Its position, {@code -1} if absent.
     */
    private int indexOf(GameSystem system)
    {
        for (int i = 0; i < count; i++)
        {
            if (systems[i] == system)
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Grows the arrays.
     *
     * @param capacity The new length.
     */
    private void grow(int capacity)
    {
        final GameSystem[] newSystems = new GameSystem[capacity];
        final int[] newSlots = new int[capacity];
        final boolean[] newRequested = new boolean[capacity];
        final boolean[] newApplied = new boolean[capacity];
        System.arraycopy(systems, 0, newSystems, 0, count);
        System.arraycopy(slots, 0, newSlots, 0, count);
        System.arraycopy(requested, 0, newRequested, 0, count);
        System.arraycopy(applied, 0, newApplied, 0, count);
        systems = newSystems;
        slots = newSlots;
        requested = newRequested;
        applied = newApplied;
    }
}
