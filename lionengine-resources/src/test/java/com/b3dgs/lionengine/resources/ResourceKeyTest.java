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
package com.b3dgs.lionengine.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test {@link ResourceKey} (REQ-RES-005, ADR-0007).
 */
final class ResourceKeyTest
{
    /**
     * Valid paths are kept as given.
     *
     * @param path The path.
     */
    @ParameterizedTest
    @ValueSource(strings =
    {
        "hero.png", "sprites/hero.png", "a/b/c.d", "Sprites/Hero.PNG", ".hidden", "a/..b", "a/b..", "a/.../b"
    })
    void testValid(String path)
    {
        final ResourceKey key = new ResourceKey(path);

        assertEquals(path, key.path());
        assertEquals(path, key.toString());
    }

    /**
     * Invalid paths are rejected, never converted.
     *
     * @param path The path.
     */
    @ParameterizedTest
    @ValueSource(strings =
    {
        "", "/a", "a/", "a//b", ".", "..", "./a", "a/.", "a/./b", "a/../b", "../a", "a\\b", "/"
    })
    void testInvalid(String path)
    {
        assertThrows(IllegalArgumentException.class, () -> new ResourceKey(path));
    }

    /**
     * A {@code null} path is rejected.
     */
    @Test
    void testNull()
    {
        assertThrows(IllegalArgumentException.class, () -> new ResourceKey(null));
    }

    /**
     * Keys are equal when their paths are equal, and the case matters.
     */
    @Test
    void testEquality()
    {
        assertEquals(new ResourceKey("sprites/hero.png"), new ResourceKey("sprites/hero.png"));
        assertEquals(new ResourceKey("sprites/hero.png").hashCode(), new ResourceKey("sprites/hero.png").hashCode());
        assertNotEquals(new ResourceKey("sprites/hero.png"), new ResourceKey("sprites/Hero.png"));
    }
}
