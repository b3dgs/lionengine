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
 * A trait type counting the pool creations and resets of another type, for the benchmark scene (ADR-0079, ADR-0081):
 * the scaling check compares these counts between two population sizes, and a steady step creates no instance.
 *
 * <p>
 * Public in the {@code ecs} test JAR (the fixture placement of ADR-0045, ADR-0079 decision 2) for the benchmark of the
 * standard traits, which wraps their {@code TYPE} constants. Runtime thread only.
 * </p>
 *
 * @param <T> The trait class.
 */
public final class CountingType<T> implements TraitType<T>
{
    /** The counted type. */
    private final TraitType<T> type;
    /** Instances created. */
    private long created;
    /** Resets. */
    private long resets;

    /**
     * Creates the type.
     *
     * @param type The counted type, which creates and resets the instances.
     */
    public CountingType(TraitType<T> type)
    {
        super();
        this.type = type;
    }

    @Override
    public T create()
    {
        created++;
        return type.create();
    }

    @Override
    public void reset(T trait)
    {
        resets++;
        type.reset(trait);
    }

    /**
     * Returns the counted type.
     *
     * @return The type creating and resetting the instances.
     */
    public TraitType<T> getType()
    {
        return type;
    }

    /**
     * Returns the instances created.
     *
     * @return The number of {@link #create()} calls.
     */
    public long getCreated()
    {
        return created;
    }

    /**
     * Returns the resets.
     *
     * @return The number of {@link #reset(Object)} calls.
     */
    public long getResets()
    {
        return resets;
    }
}
