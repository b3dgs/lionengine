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
package com.b3dgs.lionengine.game.map.feature.transition;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the transition class.
 */
public class TransitionTest
{
    /**
     * Test the getters.
     */
    @Test
    public void testGetters()
    {
        final Transition transition = new Transition(TransitionType.CENTER, "a", "b");

        Assert.assertEquals(TransitionType.CENTER, transition.getType());
        Assert.assertEquals("a", transition.getIn());
        Assert.assertEquals("b", transition.getOut());
    }

    /**
     * Test the group transition hash code.
     */
    @Test
    public void testHashcode()
    {
        final TransitionType type = TransitionType.UP;
        final Transition transition = new Transition(type, "a", "a");

        Assert.assertEquals(transition, transition);
        Assert.assertEquals(new Transition(type, "a", "a").hashCode(), new Transition(type, "a", "a").hashCode());
        Assert.assertEquals(new Transition(type, "a", "b").hashCode(), new Transition(type, "a", "b").hashCode());

        Assert.assertNotEquals(new Transition(TransitionType.CENTER, "a", "b").hashCode(),
                               new Transition(TransitionType.CENTER.getSymetric(), "b", "a").hashCode());
        Assert.assertNotEquals(new Transition(type, "a", "b").hashCode(),
                               new Transition(type.getSymetric(), "b", "a").hashCode());

        Assert.assertNotEquals(new Transition(type, "a", "a").hashCode(), new Object().hashCode());
        Assert.assertNotEquals(new Transition(type, "a", "b").hashCode(),
                               new Transition(TransitionType.CENTER, "b", "a").hashCode());
        Assert.assertNotEquals(new Transition(type, "a", "b").hashCode(),
                               new Transition(TransitionType.CENTER, "a", "b").hashCode());
        Assert.assertNotEquals(new Transition(type, "a", "b").hashCode(), new Transition(type, "a", "a").hashCode());
        Assert.assertNotEquals(new Transition(type, "a", "b").hashCode(), new Transition(type, "b", "b").hashCode());
        Assert.assertNotEquals(new Transition(type, "a", "a").hashCode(), new Transition(type, "b", "a").hashCode());
        Assert.assertNotEquals(new Transition(type, "a", "a").hashCode(), new Transition(type, "b", "b").hashCode());
        Assert.assertNotEquals(new Transition(type, "a", "a").hashCode(),
                               new Transition(type.getSymetric(), "b", "b").hashCode());
    }

    /**
     * Test the group transition equality.
     */
    @Test
    public void testEquals()
    {
        final TransitionType type = TransitionType.UP;
        final Transition transition = new Transition(type, "a", "a");

        Assert.assertEquals(transition, transition);
        Assert.assertEquals(new Transition(type, "a", "a"), new Transition(type, "a", "a"));
        Assert.assertEquals(new Transition(type, "a", "b"), new Transition(type, "a", "b"));
        Assert.assertEquals(new Transition(TransitionType.CENTER, "a", "b"),
                            new Transition(TransitionType.CENTER.getSymetric(), "b", "a"));
        Assert.assertEquals(new Transition(type, "a", "b"), new Transition(type.getSymetric(), "b", "a"));

        Assert.assertNotEquals(new Transition(type, "a", "a"), new Object());
        Assert.assertNotEquals(new Transition(type, "a", "b"), new Transition(type, "b", "a"));
        Assert.assertNotEquals(new Transition(type, "a", "b"), new Transition(TransitionType.CENTER, "a", "b"));
        Assert.assertNotEquals(new Transition(type, "a", "b"), new Transition(type, "a", "a"));
        Assert.assertNotEquals(new Transition(type, "a", "b"), new Transition(type, "b", "b"));
        Assert.assertNotEquals(new Transition(type, "a", "a"), new Transition(type, "b", "a"));
        Assert.assertNotEquals(new Transition(type, "a", "a"), new Transition(type, "b", "b"));
        Assert.assertNotEquals(new Transition(type, "a", "a"), new Transition(type.getSymetric(), "b", "b"));
    }

    /**
     * Test the group to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals(TransitionType.CENTER.name()
                            + " a -> b",
                            new Transition(TransitionType.CENTER, "a", "b").toString());
    }
}
