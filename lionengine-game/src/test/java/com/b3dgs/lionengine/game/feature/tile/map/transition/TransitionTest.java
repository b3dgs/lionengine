/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Transition}.
 */
public final class TransitionTest
{
    /**
     * Test the getters.
     */
    @Test
    public void testGetters()
    {
        final Transition transition = new Transition(TransitionType.CENTER, "a", "b");

        assertEquals(TransitionType.CENTER, transition.getType());
        assertEquals("a", transition.getIn());
        assertEquals("b", transition.getOut());
    }

    /**
     * Test the group transition equality.
     */
    @Test
    public void testEquals()
    {
        final TransitionType type = TransitionType.UP;
        final Transition transition = new Transition(type, "a", "a");

        assertEquals(transition, transition);
        assertEquals(new Transition(type, "a", "a"), new Transition(type, "a", "a"));
        assertEquals(new Transition(type, "a", "b"), new Transition(type, "a", "b"));
        assertEquals(new Transition(TransitionType.CENTER, "a", "b"),
                     new Transition(TransitionType.CENTER.getSymetric(), "b", "a"));
        assertEquals(new Transition(type, "a", "b"), new Transition(type.getSymetric(), "b", "a"));

        assertNotEquals(new Transition(type, "a", "a"), new Object());
        assertNotEquals(new Transition(type, "a", "b"), new Transition(type, "b", "a"));
        assertNotEquals(new Transition(type, "a", "b"), new Transition(TransitionType.CENTER, "a", "b"));
        assertNotEquals(new Transition(type, "a", "b"), new Transition(type, "a", "a"));
        assertNotEquals(new Transition(type, "a", "b"), new Transition(type, "b", "b"));
        assertNotEquals(new Transition(type, "a", "a"), new Transition(type, "b", "a"));
        assertNotEquals(new Transition(type, "a", "a"), new Transition(type, "b", "b"));
        assertNotEquals(new Transition(type, "a", "a"), new Transition(type.getSymetric(), "b", "b"));
    }

    /**
     * Test the group transition hash code.
     */
    @Test
    public void testHashCode()
    {
        final TransitionType type = TransitionType.UP;
        final Transition transition = new Transition(type, "a", "a");

        assertHashEquals(transition, transition);
        assertHashEquals(new Transition(type, "a", "a"), new Transition(type, "a", "a"));
        assertHashEquals(new Transition(type, "a", "b"), new Transition(type, "a", "b"));

        assertHashNotEquals(new Transition(TransitionType.CENTER, "a", "b"),
                            new Transition(TransitionType.CENTER.getSymetric(), "b", "a"));
        assertHashNotEquals(new Transition(type, "a", "b"), new Transition(type.getSymetric(), "b", "a"));

        assertHashNotEquals(new Transition(type, "a", "a"), new Object());
        assertHashNotEquals(new Transition(type, "a", "b"), new Transition(TransitionType.CENTER, "b", "a"));
        assertHashNotEquals(new Transition(type, "a", "b"), new Transition(TransitionType.CENTER, "a", "b"));
        assertHashNotEquals(new Transition(type, "a", "b"), new Transition(type, "a", "a"));
        assertHashNotEquals(new Transition(type, "a", "b"), new Transition(type, "b", "b"));
        assertHashNotEquals(new Transition(type, "a", "a"), new Transition(type, "b", "a"));
        assertHashNotEquals(new Transition(type, "a", "a"), new Transition(type, "b", "b"));
        assertHashNotEquals(new Transition(type, "a", "a"), new Transition(type.getSymetric(), "b", "b"));
    }

    /**
     * Test the group to string.
     */
    @Test
    public void testToString()
    {
        assertEquals(TransitionType.CENTER.name() + " a -> b",
                     new Transition(TransitionType.CENTER, "a", "b").toString());
    }
}
