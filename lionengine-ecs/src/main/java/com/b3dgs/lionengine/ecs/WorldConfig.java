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

import com.b3dgs.lionengine.RandomSource;

/**
 * Configuration of a {@link World} (REQ-ECS-013, REQ-WORLD-001, REQ-DET-001, REQ-DET-003, ADR-0034, ADR-0074, ADR-0075,
 * ADR-0078).
 *
 * <p>
 * The object capacity is the number of objects the world holds without growing: its object table is allocated at
 * construction with this size, and doubles at a flush that needs more (ADR-0030, decision 4). The trait capacity is the
 * number of traits of each registered type held without growing: each {@link TraitStorage} creates that many pooled
 * instances at registration, and its dense array doubles at a flush that needs more; {@link TraitStorage#reserve(int)}
 * raises it for one type. The random seed starts the world's {@link RandomSource}: every value
 * is valid, {@code 0} included, and two worlds with the same seed and the same operations draw the same numbers
 * (ADR-0078). {@link #DEFAULT} holds the documented defaults; the two-capacity constructor uses
 * {@link #DEFAULT_RANDOM_SEED}. The collision grid cell size of the feature page is added by the batch that uses it
 * (collision).
 * </p>
 *
 * <p>
 * Immutable and thread-safe.
 * </p>
 *
 * @param objectCapacity The initial number of objects, in [1, {@link #MAX_OBJECT_CAPACITY}].
 * @param traitCapacity The initial number of traits per trait type, in [1, {@link #MAX_TRAIT_CAPACITY}].
 * @param randomSeed The seed of the world's random source, any value.
 * @since 11.0.0
 */
public record WorldConfig(int objectCapacity, int traitCapacity, long randomSeed)
{
    /** Default object capacity: 1,024 objects (ADR-0034). */
    public static final int DEFAULT_OBJECT_CAPACITY = 1024;
    /** Highest object capacity: 2<sup>30</sup> objects, the largest power of two an array index holds. */
    public static final int MAX_OBJECT_CAPACITY = 1 << 30;
    /** Default trait capacity: 256 instances per trait type (ADR-0034). */
    public static final int DEFAULT_TRAIT_CAPACITY = 256;
    /** Highest trait capacity: 2<sup>30</sup> instances per trait type, as many as objects. */
    public static final int MAX_TRAIT_CAPACITY = MAX_OBJECT_CAPACITY;
    /**
     * Default random seed: {@code 0x4C494F4E}, the ASCII of "LION", the constant of the camera shake seed of ADR-0034
     * reused on purpose by the owner's choice (ADR-0078). The world's generator and a camera's noise stay independent:
     * they are different algorithms with their own state.
     */
    public static final long DEFAULT_RANDOM_SEED = 0x4C49_4F4EL;
    /** Configuration with every default value. */
    public static final WorldConfig DEFAULT = new WorldConfig(DEFAULT_OBJECT_CAPACITY,
                                                              DEFAULT_TRAIT_CAPACITY,
                                                              DEFAULT_RANDOM_SEED);

    /**
     * Creates a configuration.
     *
     * @param objectCapacity The initial number of objects, in [1, {@link #MAX_OBJECT_CAPACITY}].
     * @param traitCapacity The initial number of traits per trait type, in [1, {@link #MAX_TRAIT_CAPACITY}].
     * @param randomSeed The seed of the world's random source, any value.
     * @throws IllegalArgumentException If a capacity is outside its range.
     */
    public WorldConfig
    {
        if (objectCapacity < 1 || objectCapacity > MAX_OBJECT_CAPACITY)
        {
            throw new IllegalArgumentException("Object capacity outside [1, 2^30]: " + objectCapacity);
        }
        checkTraitCapacity(traitCapacity);
    }

    /**
     * Creates a configuration with the default random seed, {@link #DEFAULT_RANDOM_SEED}.
     *
     * @param objectCapacity The initial number of objects, in [1, {@link #MAX_OBJECT_CAPACITY}].
     * @param traitCapacity The initial number of traits per trait type, in [1, {@link #MAX_TRAIT_CAPACITY}].
     * @throws IllegalArgumentException If a capacity is outside its range.
     */
    public WorldConfig(int objectCapacity, int traitCapacity)
    {
        this(objectCapacity, traitCapacity, DEFAULT_RANDOM_SEED);
    }

    /**
     * Creates the random source of a world from the seed: a new generator for each world, never shared.
     *
     * @return The generator, started at {@link #randomSeed()}.
     */
    RandomSource createRandomSource()
    {
        return new RandomSource(randomSeed);
    }

    /**
     * Checks a trait capacity.
     *
     * @param traitCapacity The capacity.
     * @throws IllegalArgumentException If the capacity is outside [1, {@link #MAX_TRAIT_CAPACITY}].
     */
    static void checkTraitCapacity(int traitCapacity)
    {
        if (traitCapacity < 1 || traitCapacity > MAX_TRAIT_CAPACITY)
        {
            throw new IllegalArgumentException("Trait capacity outside [1, 2^30]: " + traitCapacity);
        }
    }
}
