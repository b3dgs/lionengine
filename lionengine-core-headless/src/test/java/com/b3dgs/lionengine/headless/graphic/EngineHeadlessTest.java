/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.headless.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Version;

/**
 * Test {@link EngineHeadless}.
 */
final class EngineHeadlessTest
{
    /**
     * Clean test.
     */
    @AfterEach
    public void cleanUp()
    {
        Engine.terminate();
    }

    /**
     * Test start default already.
     */
    @Test
    void testDefaultAlready()
    {
        EngineHeadless.start(EngineHeadlessTest.class.getName(), new Version(1, 0, 0));

        assertThrows(() -> EngineHeadless.start(EngineHeadlessTest.class.getName(), new Version(1, 0, 0)),
                     "The engine has already been started !");
    }

    /**
     * Test start without resources.
     */
    @Test
    void testNullResources()
    {
        assertThrows(() -> EngineHeadless.start(EngineHeadlessTest.class.getName(),
                                                new Version(1, 0, 0),
                                                (String) null),
                     "Unexpected null argument !");
    }

    /**
     * Test start with resources already started.
     */
    @Test
    void testResourcesAlready()
    {
        EngineHeadless.start(EngineHeadlessTest.class.getName(), new Version(1, 0, 0), Constant.EMPTY_STRING);

        assertTrue(Engine.isStarted());
        assertThrows(() -> EngineHeadless.start(EngineHeadlessTest.class.getName(),
                                                new Version(1, 0, 0),
                                                Constant.EMPTY_STRING),
                     "The engine has already been started !");
    }

    /**
     * Test start with class already started.
     */
    @Test
    void testClass()
    {
        EngineHeadless.start(EngineHeadlessTest.class.getName(), new Version(1, 0, 0), EngineHeadlessTest.class);

        assertTrue(Engine.isStarted());
        assertThrows(() -> EngineHeadless.start(EngineHeadlessTest.class.getName(),
                                                new Version(1, 0, 0),
                                                EngineHeadlessTest.class),
                     "The engine has already been started !");
    }
}
