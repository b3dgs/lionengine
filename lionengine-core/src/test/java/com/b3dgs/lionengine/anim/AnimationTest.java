/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.anim;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Test the animation class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AnimationTest
{
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

        final Animation animation = Anim.createAnimation(first, last, speed, reverse, repeat);

        Assert.assertEquals(first, animation.getFirst());
        Assert.assertEquals(last, animation.getLast());
        Assert.assertEquals(speed, animation.getSpeed(), 0.01);
        Assert.assertTrue(reverse == animation.getReverse());
        Assert.assertTrue(repeat == animation.getRepeat());
    }

    /**
     * Test the animation failure minimum frame.
     */
    @Test(expected = LionEngineException.class)
    public void testFailureMinimumFrame()
    {
        Assert.assertNotNull(Anim.createAnimation(Animation.MINIMUM_FRAME - 1, 0, 0.0, false, false));
    }

    /**
     * Test the animation failure maximum frame.
     */
    @Test(expected = LionEngineException.class)
    public void testFailureMaximumFrame()
    {
        Assert.assertNotNull(Anim.createAnimation(Animation.MINIMUM_FRAME, Animation.MINIMUM_FRAME - 1, 0.0, false,
                false));
    }

    /**
     * Test the animation failure speed.
     */
    @Test(expected = LionEngineException.class)
    public void testFailureSpeed()
    {
        Assert.assertNotNull(Anim.createAnimation(Animation.MINIMUM_FRAME, Animation.MINIMUM_FRAME + 1, -1.0, false,
                false));
    }
}
