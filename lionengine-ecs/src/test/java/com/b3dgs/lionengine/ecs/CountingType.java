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
 * A trait type counting its pool creations and resets, for the benchmark scene (ADR-0079): the scaling check compares
 * these counts between two population sizes, and a steady step creates no instance.
 *
 * @param <T> The trait class.
 */
abstract class CountingType<T> implements TraitType<T>
{
    /** Instances created. */
    private long created;
    /** Resets. */
    private long resets;

    /**
     * Creates the type.
     */
    CountingType()
    {
        super();
    }

    /**
     * Creates an instance in its reset state.
     *
     * @return The new instance.
     */
    abstract T newInstance();

    /**
     * Resets an instance.
     *
     * @param trait The instance.
     */
    abstract void clear(T trait);

    @Override
    public final T create()
    {
        created++;
        return newInstance();
    }

    @Override
    public final void reset(T trait)
    {
        resets++;
        clear(trait);
    }

    /**
     * Returns the instances created.
     *
     * @return The number of {@link #create()} calls.
     */
    final long getCreated()
    {
        return created;
    }

    /**
     * Returns the resets.
     *
     * @return The number of {@link #reset(Object)} calls.
     */
    final long getResets()
    {
        return resets;
    }
}
