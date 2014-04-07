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
package com.b3dgs.lionengine.game;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Engine;

/**
 * Test the factory object game.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryObjectGameTest
{
    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
    }

    /**
     * Test the game factory.
     */
    @Test
    public void testFactoryObjectGame()
    {
        Engine.start("FactoryObjectGameTest", Version.create(1, 0, 0), "resources");
        final FactoryObject factory = new FactoryObject();

        Assert.assertTrue(Arrays.equals(TypeObject.values(), factory.getTypes()));
        Assert.assertNull(factory.getSetup(TypeObject.TYPE));

        try
        {
            factory.create(TypeObject.TYPE);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        factory.load();

        try
        {
            factory.create(TypeObject.TYPE_PACKAGE);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            factory.create(TypeObject.TYPE_CONSTRUCTOR);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        Assert.assertNotNull(factory.getSetup(TypeObject.TYPE));
        Assert.assertNotNull(factory.create(TypeObject.TYPE));
        Assert.assertTrue(Arrays.equals(TypeObject.values(), factory.getTypes()));
    }
}
