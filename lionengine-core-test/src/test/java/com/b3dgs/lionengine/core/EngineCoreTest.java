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
package com.b3dgs.lionengine.core;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.test.EngineMock;
import com.b3dgs.lionengine.test.FactoryGraphicMock;
import com.b3dgs.lionengine.test.FactoryMediaMock;
import com.b3dgs.lionengine.test.SecurityManagerMock;

/**
 * Test the engine core class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EngineCoreTest
{
    /** Test name. */
    private static final String NAME = EngineCoreTest.class.getName();

    /**
     * Reestablish the engine start state.
     */
    @After
    public void afterTest()
    {
        if (EngineCore.isStarted())
        {
            EngineCore.terminate();
        }
        System.setSecurityManager(null);
    }

    /**
     * Test the engine mock.
     */
    @Test
    public void testMock()
    {
        Assert.assertNotNull(new EngineMock());
    }

    /**
     * Test the engine not started on get program name.
     */
    @Test(expected = LionEngineException.class)
    public void testNotStartedGetProgramName()
    {
        Assert.assertNull(EngineCore.getProgramName());
    }

    /**
     * Test the engine not started on get program version.
     */
    @Test(expected = LionEngineException.class)
    public void testNotStartedGetProgramVersion()
    {
        Assert.assertNull(EngineCore.getProgramVersion());
    }

    /**
     * Test the engine already started.
     */
    @Test(expected = LionEngineException.class)
    public void testAlreadyStarted()
    {
        EngineCore.start(NAME, Version.DEFAULT, new FactoryGraphicMock(), new FactoryMediaMock());
        EngineCore.start(NAME, Version.DEFAULT, new FactoryGraphicMock(), new FactoryMediaMock());
        EngineCore.terminate();
    }

    /**
     * Test the engine terminate.
     */
    @Test(expected = LionEngineException.class)
    public void testTerminate()
    {
        EngineCore.terminate();
    }

    /**
     * Test the engine factory graphic error.
     */
    @Test(expected = LionEngineException.class)
    public void testFactoryGraphicError()
    {
        EngineCore.start(NAME, Version.create(0, 1, 0), null, new FactoryMediaMock());
    }

    /**
     * Test the engine factory media error.
     */
    @Test(expected = LionEngineException.class)
    public void testFactoryMediaError()
    {
        EngineCore.start(NAME, Version.create(0, 1, 0), new FactoryGraphicMock(), null);
    }

    /**
     * Test the engine started flag.
     */
    @Test
    public void testStarted()
    {
        Assert.assertFalse(EngineCore.isStarted());
        EngineCore.start(NAME, Version.create(0, 1, 0), new FactoryGraphicMock(), new FactoryMediaMock());
        Assert.assertTrue(EngineCore.isStarted());
        EngineCore.terminate();
        Assert.assertFalse(EngineCore.isStarted());
    }

    /**
     * Test the engine getter.
     */
    @Test
    public void testGetter()
    {
        EngineCore.start(NAME, Version.create(1, 2, 3), new FactoryGraphicMock(), new FactoryMediaMock());
        Assert.assertEquals(NAME, EngineCore.getProgramName());
        Assert.assertEquals("1.2.3", EngineCore.getProgramVersion().toString());
        EngineCore.terminate();
    }

    /**
     * Test the engine system property.
     */
    @Test
    public void testSystemProperty()
    {
        Assert.assertEquals(null, EngineCore.getSystemProperty("null", null));
        System.setSecurityManager(new SecurityManagerMock(false));

        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        Assert.assertNull(Constant.EMPTY_STRING, EngineCore.getSystemProperty("security", null));
        Verbose.info("****************************************************************************************");
    }
}
