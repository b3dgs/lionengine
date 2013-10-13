/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Test the animation package.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AnimTest
{
    /**
     * Test fail case of the factory.
     * 
     * @param first The first frame.
     * @param last The last frame.
     * @param speed The animation speed.
     */
    private static void testFactoryFail(int first, int last, double speed)
    {
        try
        {
            Anim.createAnimation(first, last, speed, false, false);
            Assert.fail();
        }
        catch (LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test success case of the factory.
     * 
     * @param first The first frame.
     * @param last The last frame.
     * @param speed The animation speed.
     * @param reverse The reverse flag.
     * @param repeat The repeat flag.
     */
    private static void testFactorySuccess(int first, int last, double speed, boolean reverse, boolean repeat)
    {
        try
        {
            Anim.createAnimation(first, last, speed, false, false);
        }
        catch (LionEngineException exception)
        {
            Assert.fail();
        }
    }

    /**
     * Test the factory.
     * 
     * @throws SecurityException If error.
     * @throws NoSuchMethodException If error.
     * @throws IllegalArgumentException If error.
     * @throws IllegalAccessException If error.
     * @throws InstantiationException If error.
     */
    @Test
    public void testFactory() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException
    {
        final Constructor<Anim> constructor = Anim.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try
        {
            final Anim anim = constructor.newInstance();
            Assert.assertNotNull(anim);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }

        testFactoryFail(0, 0, 0.0);
        testFactoryFail(-1, 0, 0.0);
        testFactoryFail(1, 0, 0.0);
        testFactoryFail(2, 1, 0.0);
        testFactoryFail(1, 1, -1.0);

        testFactorySuccess(1, 1, 0.0, false, false);
        testFactorySuccess(1, 2, 0.0, true, false);
        testFactorySuccess(1, 1, 1.0, false, true);
        testFactorySuccess(1, 2, 1.0, true, true);

        final Animator animator = Anim.createAnimator();
        Assert.assertNotNull(animator);
    }
}
