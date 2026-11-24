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
package com.b3dgs.lionengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Test {@link DefinitionException}.
 */
final class DefinitionExceptionTest
{
    /**
     * A document error keeps its location and reports it in the message.
     */
    @Test
    void testDocumentError()
    {
        final DefinitionException error = new DefinitionException("Duplicate key", "sprites/hero.json", 3, 7, "$.frames");

        assertEquals("sprites/hero.json", error.getSource());
        assertEquals(3, error.getLine());
        assertEquals(7, error.getColumn());
        assertEquals("$.frames", error.getPath());
        assertNull(error.getId());
        assertNull(error.getParameter());
        assertEquals("Duplicate key (sprites/hero.json:3:7, $.frames)", error.getMessage());
    }

    /**
     * A document error without path omits it from the message.
     */
    @Test
    void testDocumentErrorWithoutPath()
    {
        final DefinitionException error = new DefinitionException("Unexpected end", "level.json",
                                                                   DefinitionException.UNKNOWN,
                                                                   DefinitionException.UNKNOWN,
                                                                   null);

        assertEquals("Unexpected end (level.json:0:0)", error.getMessage());
    }

    /**
     * A registry error keeps its id and parameter and reports them in the message.
     */
    @Test
    void testRegistryError()
    {
        final DefinitionException error = new DefinitionException("Missing value", "lionengine:movable", "maxSpeed");

        assertEquals("lionengine:movable", error.getId());
        assertEquals("maxSpeed", error.getParameter());
        assertNull(error.getSource());
        assertEquals(DefinitionException.UNKNOWN, error.getLine());
        assertEquals(DefinitionException.UNKNOWN, error.getColumn());
        assertNull(error.getPath());
        assertEquals("Missing value (lionengine:movable, maxSpeed)", error.getMessage());
    }

    /**
     * A registry error without parameter omits it from the message.
     */
    @Test
    void testRegistryErrorWithoutParameter()
    {
        final DefinitionException error = new DefinitionException("Unknown id", "game:enemy", null);

        assertEquals("Unknown id (game:enemy)", error.getMessage());
    }
}
