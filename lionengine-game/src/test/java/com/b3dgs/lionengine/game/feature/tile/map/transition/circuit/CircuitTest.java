/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Circuit}.
 */
final class CircuitTest
{
    /**
     * Test the getters.
     */
    @Test
    void testGetters()
    {
        final Circuit circuit = new Circuit(CircuitType.MIDDLE, "a", "b");

        assertEquals(CircuitType.MIDDLE, circuit.getType());
        assertEquals("a", circuit.getIn());
        assertEquals("b", circuit.getOut());
    }

    /**
     * Test the group circuit equality.
     */
    @Test
    void testEquals()
    {
        final CircuitType type = CircuitType.TOP;
        final Circuit circuit = new Circuit(type, "a", "a");

        assertEquals(circuit, circuit);
        assertEquals(new Circuit(type, "a", "a"), new Circuit(type, "a", "a"));
        assertEquals(new Circuit(type, "a", "b"), new Circuit(type, "a", "b"));
        assertEquals(new Circuit(CircuitType.MIDDLE, "a", "b"),
                     new Circuit(CircuitType.MIDDLE.getSymetric(), "b", "a"));
        assertEquals(new Circuit(type, "a", "b"), new Circuit(type.getSymetric(), "b", "a"));

        assertNotEquals(new Circuit(type, "a", "a"), null);
        assertNotEquals(new Circuit(type, "a", "a"), new Object());
        assertNotEquals(new Circuit(type, "a", "b"), new Circuit(type, "b", "a"));
        assertNotEquals(new Circuit(type, "a", "b"), new Circuit(CircuitType.MIDDLE, "a", "b"));
        assertNotEquals(new Circuit(type, "a", "b"), new Circuit(type, "a", "a"));
        assertNotEquals(new Circuit(type, "a", "b"), new Circuit(type, "b", "b"));
        assertNotEquals(new Circuit(type, "a", "a"), new Circuit(type, "b", "a"));
        assertNotEquals(new Circuit(type, "a", "a"), new Circuit(type, "b", "b"));
        assertNotEquals(new Circuit(type, "a", "a"), new Circuit(type.getSymetric(), "b", "b"));
    }

    /**
     * Test the group circuit hash code.
     */
    @Test
    void testHashCode()
    {
        final CircuitType type = CircuitType.TOP;
        final Circuit circuit = new Circuit(type, "a", "a");

        assertHashEquals(circuit, circuit);
        assertHashEquals(new Circuit(type, "a", "a"), new Circuit(type, "a", "a"));
        assertHashEquals(new Circuit(type, "a", "b"), new Circuit(type, "a", "b"));

        assertHashNotEquals(new Circuit(CircuitType.MIDDLE, "a", "b"),
                            new Circuit(CircuitType.MIDDLE.getSymetric(), "b", "a"));
        assertHashNotEquals(new Circuit(type, "a", "b"), new Circuit(type.getSymetric(), "b", "a"));

        assertHashNotEquals(new Circuit(type, "a", "a"), new Object());
        assertHashNotEquals(new Circuit(type, "a", "b"), new Circuit(CircuitType.MIDDLE, "b", "a"));
        assertHashNotEquals(new Circuit(type, "a", "b"), new Circuit(CircuitType.MIDDLE, "a", "b"));
        assertHashNotEquals(new Circuit(type, "a", "b"), new Circuit(type, "a", "a"));
        assertHashNotEquals(new Circuit(type, "a", "b"), new Circuit(type, "b", "b"));
        assertHashNotEquals(new Circuit(type, "a", "a"), new Circuit(type, "b", "a"));
        assertHashNotEquals(new Circuit(type, "a", "a"), new Circuit(type, "b", "b"));
        assertHashNotEquals(new Circuit(type, "a", "a"), new Circuit(type.getSymetric(), "b", "b"));
    }

    /**
     * Test the group to string.
     */
    @Test
    void testToString()
    {
        assertEquals(CircuitType.MIDDLE.name() + " GroupTransition[groupIn=a, groupOut=b]",
                     new Circuit(CircuitType.MIDDLE, "a", "b").toString());
    }
}
