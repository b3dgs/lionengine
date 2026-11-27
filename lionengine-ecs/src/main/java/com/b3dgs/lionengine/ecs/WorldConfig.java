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
 * Configuration of a {@link World} (REQ-ECS-013, ADR-0034, ADR-0074).
 *
 * <p>
 * The object capacity is the number of objects the world holds without growing: its object table is allocated at
 * construction with this size, and doubles at a flush that needs more (ADR-0030, decision 4). {@link #DEFAULT} holds
 * the documented default. The trait capacity, the collision grid cell size and the random seed of the feature page are
 * added by the batches that use them (traits, collision, random source).
 * </p>
 *
 * <p>
 * Immutable and thread-safe.
 * </p>
 *
 * @param objectCapacity The initial number of objects, in [1, {@link #MAX_OBJECT_CAPACITY}].
 * @since 11.0.0
 */
public record WorldConfig(int objectCapacity)
{
    /** Default object capacity: 1,024 objects (ADR-0034). */
    public static final int DEFAULT_OBJECT_CAPACITY = 1024;
    /** Highest object capacity: 2<sup>30</sup> objects, the largest power of two an array index holds. */
    public static final int MAX_OBJECT_CAPACITY = 1 << 30;
    /** Configuration with every default value. */
    public static final WorldConfig DEFAULT = new WorldConfig(DEFAULT_OBJECT_CAPACITY);

    /**
     * Creates a configuration.
     *
     * @param objectCapacity The initial number of objects, in [1, {@link #MAX_OBJECT_CAPACITY}].
     * @throws IllegalArgumentException If the capacity is outside its range.
     */
    public WorldConfig
    {
        if (objectCapacity < 1 || objectCapacity > MAX_OBJECT_CAPACITY)
        {
            throw new IllegalArgumentException("Object capacity outside [1, 2^30]: " + objectCapacity);
        }
    }
}
