/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.awt;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.EngineCore;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the engine class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EngineAwtTest
{
    /**
     * Clean up test.
     */
    @After
    public void cleanUp()
    {
        Engine.terminate();
    }

    /**
     * Test the constructor.
     * 
     * @throws Throwable If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Throwable
    {
        Engine.start("EngineTest", Version.create(0, 0, 0), "resources");
        UtilTests.testPrivateConstructor(Engine.class);
    }

    /**
     * Test the engine.
     */
    @Test(expected = LionEngineException.class)
    public void testEngine()
    {
        Engine.start("EngineTest", Version.create(0, 0, 0), (String) null);
        Assert.assertTrue(EngineCore.isStarted());
        Engine.start("EngineTest", Version.create(0, 1, 0), (String) null);
    }

    /**
     * Test the engine start with class.
     */
    @Test(expected = LionEngineException.class)
    public void testEngineClass()
    {
        Engine.start("EngineTest", Version.create(0, 0, 0), EngineAwtTest.class);
        Assert.assertTrue(EngineCore.isStarted());
        Engine.start("EngineTest", Version.create(0, 1, 0), EngineAwtTest.class);
    }
}
