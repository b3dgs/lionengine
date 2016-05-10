/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.map.feature.circuit;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the circuit class.
 */
public class CircuitTest
{
    /**
     * Test the getters.
     */
    @Test
    public void testGetters()
    {
        final Circuit circuit = new Circuit(CircuitType.MIDDLE, "a", "b");

        Assert.assertEquals(CircuitType.MIDDLE, circuit.getType());
        Assert.assertEquals("a", circuit.getIn());
        Assert.assertEquals("b", circuit.getOut());
    }

    /**
     * Test the group circuit hash code.
     */
    @Test
    public void testHashcode()
    {
        final CircuitType type = CircuitType.TOP;
        final Circuit circuit = new Circuit(type, "a", "a");

        Assert.assertEquals(circuit, circuit);
        Assert.assertEquals(new Circuit(type, "a", "a").hashCode(), new Circuit(type, "a", "a").hashCode());
        Assert.assertEquals(new Circuit(type, "a", "b").hashCode(), new Circuit(type, "a", "b").hashCode());

        Assert.assertNotEquals(new Circuit(CircuitType.MIDDLE, "a", "b").hashCode(),
                               new Circuit(CircuitType.MIDDLE.getSymetric(), "b", "a").hashCode());
        Assert.assertNotEquals(new Circuit(type, "a", "b").hashCode(),
                               new Circuit(type.getSymetric(), "b", "a").hashCode());

        Assert.assertNotEquals(new Circuit(type, "a", "a").hashCode(), new Object().hashCode());
        Assert.assertNotEquals(new Circuit(type, "a", "b").hashCode(),
                               new Circuit(CircuitType.MIDDLE, "b", "a").hashCode());
        Assert.assertNotEquals(new Circuit(type, "a", "b").hashCode(),
                               new Circuit(CircuitType.MIDDLE, "a", "b").hashCode());
        Assert.assertNotEquals(new Circuit(type, "a", "b").hashCode(), new Circuit(type, "a", "a").hashCode());
        Assert.assertNotEquals(new Circuit(type, "a", "b").hashCode(), new Circuit(type, "b", "b").hashCode());
        Assert.assertNotEquals(new Circuit(type, "a", "a").hashCode(), new Circuit(type, "b", "a").hashCode());
        Assert.assertNotEquals(new Circuit(type, "a", "a").hashCode(), new Circuit(type, "b", "b").hashCode());
        Assert.assertNotEquals(new Circuit(type, "a", "a").hashCode(),
                               new Circuit(type.getSymetric(), "b", "b").hashCode());
    }

    /**
     * Test the group circuit equality.
     */
    @Test
    public void testEquals()
    {
        final CircuitType type = CircuitType.TOP;
        final Circuit circuit = new Circuit(type, "a", "a");

        Assert.assertEquals(circuit, circuit);
        Assert.assertEquals(new Circuit(type, "a", "a"), new Circuit(type, "a", "a"));
        Assert.assertEquals(new Circuit(type, "a", "b"), new Circuit(type, "a", "b"));
        Assert.assertEquals(new Circuit(CircuitType.MIDDLE, "a", "b"),
                            new Circuit(CircuitType.MIDDLE.getSymetric(), "b", "a"));
        Assert.assertEquals(new Circuit(type, "a", "b"), new Circuit(type.getSymetric(), "b", "a"));

        Assert.assertNotEquals(new Circuit(type, "a", "a"), new Object());
        Assert.assertNotEquals(new Circuit(type, "a", "b"), new Circuit(type, "b", "a"));
        Assert.assertNotEquals(new Circuit(type, "a", "b"), new Circuit(CircuitType.MIDDLE, "a", "b"));
        Assert.assertNotEquals(new Circuit(type, "a", "b"), new Circuit(type, "a", "a"));
        Assert.assertNotEquals(new Circuit(type, "a", "b"), new Circuit(type, "b", "b"));
        Assert.assertNotEquals(new Circuit(type, "a", "a"), new Circuit(type, "b", "a"));
        Assert.assertNotEquals(new Circuit(type, "a", "a"), new Circuit(type, "b", "b"));
        Assert.assertNotEquals(new Circuit(type, "a", "a"), new Circuit(type.getSymetric(), "b", "b"));
    }

    /**
     * Test the group to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals(CircuitType.MIDDLE.name()
                            + " a -> b",
                            new Circuit(CircuitType.MIDDLE, "a", "b").toString());
    }
}
