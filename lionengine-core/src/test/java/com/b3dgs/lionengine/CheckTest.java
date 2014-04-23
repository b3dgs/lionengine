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
package com.b3dgs.lionengine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the check class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CheckTest
{
    /**
     * Test check constructor.
     * 
     * @throws SecurityException If error.
     * @throws NoSuchMethodException If error.
     * @throws IllegalArgumentException If error.
     * @throws IllegalAccessException If error.
     * @throws InstantiationException If error.
     */
    @Test
    public void testCheckConstructor() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException
    {
        final Constructor<Check> constructor = Check.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try
        {
            final Check check = constructor.newInstance();
            Assert.assertNotNull(check);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }
    }

    /**
     * Test the check argument function.
     */
    @Test
    public void testCheckArgument()
    {
        try
        {
            Check.argument(true);
        }
        catch (final LionEngineException exception)
        {
            Assert.fail();
        }

        try
        {
            Check.argument(false);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the check not null function.
     */
    @Test
    public void testCheckNotNull()
    {
        try
        {
            Check.notNull(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            Check.notNull(new Object());
        }
        catch (final LionEngineException exception)
        {
            Assert.fail();
        }
    }
}
