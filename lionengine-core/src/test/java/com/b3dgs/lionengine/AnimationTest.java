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
package com.b3dgs.lionengine;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the animation class.
 */
public class AnimationTest
{
    /** Minimum frame value. */
    private static final int MIN = Animation.MINIMUM_FRAME;

    /**
     * Test the animation failure minimum frame.
     */
    @Test(expected = LionEngineException.class)
    public void testFailureMinimumFrame()
    {
        Assert.assertNotNull(new Animation(Animation.DEFAULT_NAME, MIN - 1, 0, 0.0, false, false));
    }

    /**
     * Test the animation failure maximum frame.
     */
    @Test(expected = LionEngineException.class)
    public void testFailureMaximumFrame()
    {
        Assert.assertNotNull(new Animation(Animation.DEFAULT_NAME, MIN, MIN - 1, 0.0, false, false));
    }

    /**
     * Test the animation failure speed.
     */
    @Test(expected = LionEngineException.class)
    public void testFailureSpeed()
    {
        Assert.assertNotNull(new Animation(Animation.DEFAULT_NAME, MIN, MIN + 1, -1.0, false, false));
    }

    /**
     * Test the animation getter.
     */
    @Test
    public void testGetter()
    {
        final int first = 1;
        final int last = first + 1;
        final double speed = 1.5;
        final boolean reverse = true;
        final boolean repeat = false;

        final Animation animation = new Animation("name", first, last, speed, reverse, repeat);

        Assert.assertEquals("name", animation.getName());
        Assert.assertEquals(first, animation.getFirst());
        Assert.assertEquals(last, animation.getLast());
        Assert.assertEquals(speed, animation.getSpeed(), 0.01);
        Assert.assertTrue(reverse == animation.hasReverse());
        Assert.assertTrue(repeat == animation.hasRepeat());
    }

    /**
     * Test the animation equality.
     */
    @Test
    public void testEquals()
    {
        final Animation animation = new Animation("test", 1, 2, 3, false, true);

        Assert.assertEquals(animation, animation);
        Assert.assertEquals(animation, new Animation("test", 1, 2, 3, false, true));
        Assert.assertEquals(animation, new Animation("test", 11, 12, 3, false, true));
        Assert.assertEquals(animation, new Animation("test", 1, 12, 2, false, true));
        Assert.assertEquals(animation, new Animation("test", 1, 2, 13, false, true));
        Assert.assertEquals(animation, new Animation("test", 1, 2, 3, true, true));
        Assert.assertEquals(animation, new Animation("test", 1, 2, 3, false, false));

        Assert.assertNotEquals(animation, null);
        Assert.assertNotEquals(animation, new Object());
        Assert.assertNotEquals(animation, new Animation("test1", 1, 2, 3, false, true));
    }

    /**
     * Test the animation hash code.
     */
    @Test
    public void testHashcode()
    {
        final int animation = new Animation("test", 1, 2, 3, false, true).hashCode();
    
        Assert.assertEquals(animation, new Animation("test", 1, 2, 3, false, true).hashCode());
        Assert.assertEquals(animation, new Animation("test", 10, 12, 3, false, true).hashCode());
        Assert.assertEquals(animation, new Animation("test", 1, 12, 3, false, true).hashCode());
        Assert.assertEquals(animation, new Animation("test", 1, 2, 13, false, true).hashCode());
        Assert.assertEquals(animation, new Animation("test", 1, 2, 3, true, true).hashCode());
        Assert.assertEquals(animation, new Animation("test", 1, 2, 3, false, false).hashCode());
    
        Assert.assertNotEquals(animation, new Object().hashCode());
        Assert.assertNotEquals(animation, new Animation("test1", 1, 2, 3, false, true).hashCode());
    }
}
