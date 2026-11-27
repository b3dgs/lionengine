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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests of {@link WorldConfig} (REQ-ECS-013, REQ-DET-003, ADR-0034, ADR-0075, ADR-0078).
 */
final class WorldConfigTest
{
    /**
     * The default configuration holds 1,024 objects and 256 traits per trait type, and the random seed
     * {@code 0x4C494F4E}.
     */
    @Test
    void testDefault()
    {
        assertEquals(1024, WorldConfig.DEFAULT.objectCapacity());
        assertEquals(256, WorldConfig.DEFAULT.traitCapacity());
        assertEquals(0x4C49_4F4EL, WorldConfig.DEFAULT.randomSeed());
        assertEquals(WorldConfig.DEFAULT_RANDOM_SEED, WorldConfig.DEFAULT.randomSeed());
        assertEquals(1 << 30, new WorldConfig(WorldConfig.MAX_OBJECT_CAPACITY, 1).objectCapacity());
        assertEquals(1 << 30, new WorldConfig(1, WorldConfig.MAX_TRAIT_CAPACITY).traitCapacity());
    }

    /**
     * The bounds of the capacity are accepted.
     *
     * @param capacity The capacity.
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 16, WorldConfig.MAX_OBJECT_CAPACITY})
    void testValidCapacity(int capacity)
    {
        assertEquals(capacity, new WorldConfig(capacity, WorldConfig.DEFAULT_TRAIT_CAPACITY).objectCapacity());
        assertEquals(capacity, new WorldConfig(WorldConfig.DEFAULT_OBJECT_CAPACITY, capacity).traitCapacity());
    }

    /**
     * A capacity outside its range is refused.
     *
     * @param capacity The capacity.
     */
    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, WorldConfig.MAX_OBJECT_CAPACITY + 1, Integer.MAX_VALUE})
    void testInvalidCapacity(int capacity)
    {
        assertThrows(IllegalArgumentException.class,
                     () -> new WorldConfig(capacity, WorldConfig.DEFAULT_TRAIT_CAPACITY));
    }

    /**
     * A trait capacity outside its range is refused.
     *
     * @param capacity The capacity.
     */
    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, WorldConfig.MAX_TRAIT_CAPACITY + 1, Integer.MAX_VALUE})
    void testInvalidTraitCapacity(int capacity)
    {
        assertThrows(IllegalArgumentException.class,
                     () -> new WorldConfig(WorldConfig.DEFAULT_OBJECT_CAPACITY, capacity));
    }

    /**
     * The two-capacity constructor uses the default seed; any seed is accepted, the capacities still checked.
     *
     * @param seed The seed.
     */
    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -1L, 0L, 1L, 0x4C49_4F4EL, Long.MAX_VALUE})
    void testRandomSeed(long seed)
    {
        assertEquals(new WorldConfig(8, 4, WorldConfig.DEFAULT_RANDOM_SEED), new WorldConfig(8, 4));
        final WorldConfig config = new WorldConfig(8, 4, seed);
        assertEquals(seed, config.randomSeed());
        assertEquals(8, config.objectCapacity());
        assertEquals(4, config.traitCapacity());
        assertThrows(IllegalArgumentException.class, () -> new WorldConfig(0, 4, seed));
        assertThrows(IllegalArgumentException.class, () -> new WorldConfig(8, 0, seed));
        final int objects = WorldConfig.MAX_OBJECT_CAPACITY + 1;
        final int traits = WorldConfig.MAX_TRAIT_CAPACITY + 1;
        assertThrows(IllegalArgumentException.class, () -> new WorldConfig(objects, 4, seed));
        assertThrows(IllegalArgumentException.class, () -> new WorldConfig(8, traits, seed));
    }
}
