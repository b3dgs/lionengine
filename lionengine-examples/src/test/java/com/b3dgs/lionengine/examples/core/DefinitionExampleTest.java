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
package com.b3dgs.lionengine.examples.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.DefinitionException;
import com.b3dgs.lionengine.Registry;

/**
 * Runs {@link DefinitionExample} (REQ-DOC-001, REQ-DEF-001 to REQ-DEF-003, REQ-RT-007).
 */
final class DefinitionExampleTest
{
    /**
     * Loads a definition with the example registry.
     *
     * @param json The definition.
     * @return The weapons.
     */
    private static List<DefinitionExample.Weapon> load(String json)
    {
        final Registry<DefinitionExample.Weapon> registry = DefinitionExample.createRegistry();
        return DefinitionExample.load(registry, json.getBytes(StandardCharsets.UTF_8), "arsenal.json");
    }

    /**
     * The example file creates its two weapons.
     */
    @Test
    void testLoad()
    {
        assertEquals(List.of(new DefinitionExample.Weapon("sword", 12, 1.5), new DefinitionExample.Weapon("bow", 7, 30.0)),
                     load(DefinitionExample.ARSENAL));
    }

    /**
     * The example runs.
     */
    @Test
    void testMain()
    {
        assertDoesNotThrow(() -> DefinitionExample.main(new String[0]));
    }

    /**
     * A typo in a parameter name fails with the file position, the id and the parameter.
     */
    @Test
    void testTypo()
    {
        final String json = DefinitionExample.ARSENAL.replace("\"rangeMeters\"", "\"rangeMetres\"");
        final DefinitionException error = assertThrows(DefinitionException.class, () -> load(json));

        assertEquals("Missing value (arsenal.json:7:43, $.weapons[1].parameters, example:bow, rangeMeters)",
                     error.getMessage());
    }

    /**
     * A factory check fails with the file position, the id and the parameter.
     */
    @Test
    void testInvalidDamage()
    {
        final String json = DefinitionExample.ARSENAL.replace("\"damage\": 12", "\"damage\": 0");
        final DefinitionException error = assertThrows(DefinitionException.class, () -> load(json));

        assertEquals("Damage must be positive (arsenal.json:6:56, $.weapons[0].parameters.damage, example:sword, damage)",
                     error.getMessage());
    }

    /**
     * A file of another kind is rejected.
     */
    @Test
    void testWrongFormat()
    {
        final String json = DefinitionExample.ARSENAL.replace("example.arsenal", "example.bestiary");

        assertThrows(DefinitionException.class, () -> load(json));
    }
}
