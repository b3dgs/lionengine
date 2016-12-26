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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the group transition class.
 */
public class GroupTransitionTest
{
    /**
     * Test the group getters.
     */
    @Test
    public void testGetters()
    {
        final GroupTransition transition = new GroupTransition("a", "b");
        Assert.assertEquals("a", transition.getIn());
        Assert.assertEquals("b", transition.getOut());
    }

    /**
     * Test the group transition hash code.
     */
    @Test
    public void testHashcode()
    {
        Assert.assertEquals(new GroupTransition("a", "a").hashCode(), new GroupTransition("a", "a").hashCode());
        Assert.assertEquals(new GroupTransition("a", "b").hashCode(), new GroupTransition("a", "b").hashCode());

        Assert.assertNotEquals(new GroupTransition("a", "a").hashCode(), new Object().hashCode());
        Assert.assertNotEquals(new GroupTransition("a", "b").hashCode(), new GroupTransition("b", "a").hashCode());
        Assert.assertNotEquals(new GroupTransition("a", "b").hashCode(), new GroupTransition("a", "a").hashCode());
        Assert.assertNotEquals(new GroupTransition("a", "b").hashCode(), new GroupTransition("b", "b").hashCode());
        Assert.assertNotEquals(new GroupTransition("a", "a").hashCode(), new GroupTransition("b", "a").hashCode());
        Assert.assertNotEquals(new GroupTransition("a", "a").hashCode(), new GroupTransition("b", "b").hashCode());
    }

    /**
     * Test the group transition equality.
     */
    @Test
    public void testEquals()
    {
        final GroupTransition transition = new GroupTransition("a", "a");

        Assert.assertEquals(transition, transition);
        Assert.assertEquals(new GroupTransition("a", "a"), new GroupTransition("a", "a"));
        Assert.assertEquals(new GroupTransition("a", "b"), new GroupTransition("a", "b"));

        Assert.assertNotEquals(new GroupTransition("a", "a"), new Object());
        Assert.assertNotEquals(new GroupTransition("a", "b"), new GroupTransition("b", "a"));
        Assert.assertNotEquals(new GroupTransition("a", "b"), new GroupTransition("a", "a"));
        Assert.assertNotEquals(new GroupTransition("a", "b"), new GroupTransition("b", "b"));
        Assert.assertNotEquals(new GroupTransition("a", "a"), new GroupTransition("b", "a"));
        Assert.assertNotEquals(new GroupTransition("a", "a"), new GroupTransition("b", "b"));
    }

    /**
     * Test the group to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("a -> b", new GroupTransition("a", "b").toString());
    }
}
