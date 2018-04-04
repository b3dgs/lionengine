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

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link Engine}.
 */
public final class EngineTest
{
    /** Test name. */
    private static final String NAME = EngineTest.class.getName();

    /**
     * Reestablish the engine start state.
     */
    @After
    public void afterTest()
    {
        if (Engine.isStarted())
        {
            Engine.terminate();
        }
    }

    /**
     * Test mock.
     */
    @Test
    public void testMock()
    {
        Assert.assertNotNull(new EngineMock(NAME, Version.DEFAULT));
    }

    /**
     * Test not started on get program name.
     */
    @Test(expected = LionEngineException.class)
    public void testNotStartedGetProgramName()
    {
        Assert.assertNull(Engine.getProgramName());
    }

    /**
     * Test not started on get program version.
     */
    @Test(expected = LionEngineException.class)
    public void testNotStartedGetProgramVersion()
    {
        Assert.assertNull(Engine.getProgramVersion());
    }

    /**
     * Test already started.
     */
    @Test(expected = LionEngineException.class)
    public void testAlreadyStarted()
    {
        Engine.start(new EngineMock(NAME, Version.DEFAULT));
        Engine.start(new EngineMock(NAME, Version.DEFAULT));
    }

    /**
     * Test terminate.
     */
    @Test
    public void testTerminate()
    {
        Engine.terminate();
        Engine.terminate();
    }

    /**
     * Test started flag.
     */
    @Test
    public void testStarted()
    {
        Assert.assertFalse(Engine.isStarted());
        Engine.start(new EngineMock(NAME, Version.DEFAULT));
        Assert.assertTrue(Engine.isStarted());
        Engine.terminate();
        Assert.assertFalse(Engine.isStarted());
    }

    /**
     * Test getter.
     */
    @Test
    public void testGetter()
    {
        Engine.start(new EngineMock(NAME, Version.create(1, 2, 3)));
        Assert.assertEquals(NAME, Engine.getProgramName());
        Assert.assertEquals("Version [1.2.3]", Engine.getProgramVersion().toString());
        Engine.terminate();
    }
}
