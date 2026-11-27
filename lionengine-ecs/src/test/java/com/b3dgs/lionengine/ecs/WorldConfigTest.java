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
 * Tests of {@link WorldConfig} (REQ-ECS-013, ADR-0034).
 */
final class WorldConfigTest
{
    /**
     * The default configuration holds 1,024 objects.
     */
    @Test
    void testDefault()
    {
        assertEquals(1024, WorldConfig.DEFAULT.objectCapacity());
        assertEquals(1 << 30, new WorldConfig(WorldConfig.MAX_OBJECT_CAPACITY).objectCapacity());
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
        assertEquals(capacity, new WorldConfig(capacity).objectCapacity());
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
        assertThrows(IllegalArgumentException.class, () -> new WorldConfig(capacity));
    }
}
