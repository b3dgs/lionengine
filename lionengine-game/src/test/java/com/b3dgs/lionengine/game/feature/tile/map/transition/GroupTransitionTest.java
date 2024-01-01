/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test {@link GroupTransition}.
 */
final class GroupTransitionTest
{
    /**
     * Test the group getters.
     */
    @Test
    void testGetters()
    {
        final GroupTransition transition = new GroupTransition("a", "b");

        assertEquals("a", transition.getIn());
        assertEquals("b", transition.getOut());
    }

    /**
     * Test the group transition equality.
     */
    @Test
    void testEquals()
    {
        final GroupTransition transition = new GroupTransition("a", "a");

        assertEquals(transition, transition);
        assertEquals(new GroupTransition("a", "a"), new GroupTransition("a", "a"));
        assertEquals(new GroupTransition("a", "b"), new GroupTransition("a", "b"));

        assertNotEquals(new GroupTransition("a", "a"), null);
        assertNotEquals(new GroupTransition("a", "a"), new Object());
        assertNotEquals(new GroupTransition("a", "b"), new GroupTransition("b", "a"));
        assertNotEquals(new GroupTransition("a", "b"), new GroupTransition("a", "a"));
        assertNotEquals(new GroupTransition("a", "b"), new GroupTransition("b", "b"));
        assertNotEquals(new GroupTransition("a", "a"), new GroupTransition("b", "a"));
        assertNotEquals(new GroupTransition("a", "a"), new GroupTransition("b", "b"));
    }

    /**
     * Test the group transition hash code.
     */
    @Test
    void testHashCode()
    {
        assertHashEquals(new GroupTransition("a", "a"), new GroupTransition("a", "a"));
        assertHashEquals(new GroupTransition("a", "b"), new GroupTransition("a", "b"));

        assertHashNotEquals(new GroupTransition("a", "a"), new Object());
        assertHashNotEquals(new GroupTransition("a", "b"), new GroupTransition("b", "a"));
        assertHashNotEquals(new GroupTransition("a", "b"), new GroupTransition("a", "a"));
        assertHashNotEquals(new GroupTransition("a", "b"), new GroupTransition("b", "b"));
        assertHashNotEquals(new GroupTransition("a", "a"), new GroupTransition("b", "a"));
        assertHashNotEquals(new GroupTransition("a", "a"), new GroupTransition("b", "b"));
    }

    /**
     * Test the group to string.
     */
    @Test
    void testToString()
    {
        assertEquals("GroupTransition[groupIn=a, groupOut=b]", new GroupTransition("a", "b").toString());
    }
}
