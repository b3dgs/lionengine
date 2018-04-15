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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Test {@link Engine}.
 */
public final class EngineTest
{
    /**
     * Reestablish the engine start state.
     */
    @AfterEach
    public void afterTest()
    {
        if (Engine.isStarted())
        {
            Engine.terminate();
        }
    }

    /**
     * Test start.
     */
    @Test
    public void testStart()
    {
        Engine.start(new EngineMock("name", Version.DEFAULT));

        assertThrows(() -> Engine.start(new EngineMock(null, Version.DEFAULT)), Check.ERROR_NULL);
        assertThrows(() -> Engine.start(new EngineMock("name", null)), Check.ERROR_NULL);
    }

    /**
     * Test not started on get program name.
     */
    @Test
    public void testNotStartedGetProgramName()
    {
        assertThrows(() -> Engine.getProgramName(), Engine.ERROR_STARTED_NOT);
    }

    /**
     * Test not started on get program version.
     */
    @Test
    public void testNotStartedGetProgramVersion()
    {
        assertThrows(() -> Engine.getProgramVersion(), Engine.ERROR_STARTED_NOT);
    }

    /**
     * Test already started.
     */
    @Test
    public void testAlreadyStarted()
    {
        Engine.start(new EngineMock("name", Version.DEFAULT));

        assertThrows(() -> Engine.start(new EngineMock("name", Version.DEFAULT)), Engine.ERROR_STARTED_ALREADY);
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
        assertFalse(Engine.isStarted());

        Engine.start(new EngineMock("name", Version.DEFAULT));

        assertTrue(Engine.isStarted());

        Engine.terminate();

        assertFalse(Engine.isStarted());
    }

    /**
     * Test getter.
     */
    @Test
    public void testGetter()
    {
        Engine.start(new EngineMock("name", Version.create(1, 2, 3)));

        assertEquals("name", Engine.getProgramName());
        assertEquals(Version.create(1, 2, 3), Engine.getProgramVersion());
    }
}
