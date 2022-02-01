/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.EngineMock;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Version;

/**
 * Test {@link ComponentUpdatable}.
 */
final class ComponentUpdatableTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Start engine.
     */
    @BeforeAll
    static void beforeAll()
    {
        Engine.start(new EngineMock(ComponentUpdatableTest.class.getSimpleName(), Version.DEFAULT));

        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        Medias.setLoadFromJar(ComponentUpdatableTest.class);
        config = UtilTransformable.createMedia(ComponentUpdatableTest.class);
    }

    /**
     * Terminate engine.
     */
    @AfterAll
    static void afterAll()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
        Medias.setLoadFromJar(null);

        Engine.terminate();
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);

    /**
     * Test the updater.
     */
    @Test
    void testUpdater()
    {
        final ComponentUpdatable updatable = new ComponentUpdatable();
        final Handler handler = new Handler(new Services());
        handler.addComponent(updatable);

        final Updater object = new Updater(services, setup);
        handler.add(object);

        assertFalse(object.isUpdated());

        handler.update(1.0);

        assertTrue(object.isUpdated());

        handler.removeAll();
        handler.update(1.0);
    }
}
