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
package com.b3dgs.lionengine.core;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.mock.EngineMock;

/**
 * Test the engine core class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EngineCoreTest
{
    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        EngineCore.terminate();
    }

    /**
     * Test the engine.
     */
    @Test
    public void testEngine()
    {
        Assert.assertNotNull(new EngineMock());
        Assert.assertEquals(null, EngineCore.getProgramName());
        Assert.assertEquals(null, EngineCore.getProgramVersion());

        EngineCore.start("EngineCoreTest", Version.create(0, 0, 0), Verbose.NONE, null, null);
        EngineCore.start("EngineCoreTest", Version.create(0, 1, 0), Verbose.NONE, null, null);
        Assert.assertEquals("EngineCoreTest", EngineCore.getProgramName());
        Assert.assertEquals("0.0.0", EngineCore.getProgramVersion());
        EngineCore.terminate();

        EngineCore.start("EngineCoreTest", Version.create(0, 1, 0), Verbose.NONE, null, null);
        Assert.assertTrue(EngineCore.isStarted());

        Assert.assertNull(EngineCore.getSystemProperty("null"));
    }
}
