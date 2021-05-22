/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.EngineMock;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link Setup}.
 */
final class SetupTest
{
    /**
     * Start engine.
     */
    @BeforeAll
    static void beforeAll()
    {
        Engine.start(new EngineMock(SetupTest.class.getSimpleName(), Version.DEFAULT));

        Medias.setLoadFromJar(SetupTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Terminate engine.
     */
    @AfterAll
    static void afterAll()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);

        Engine.terminate();
    }

    /**
     * Test the setup config.
     */
    @Test
    void testConfig()
    {
        final Media config = Medias.create("Object.xml");
        final Setup setup = new Setup(config);

        assertEquals(config, setup.getMedia());
        assertNotNull(setup);
    }

    /**
     * Test the setup config inside folder.
     */
    @Test
    void testConfigFolder()
    {
        final Media config = Medias.create("setup", "Object.xml");
        final Setup setup = new Setup(config);

        assertEquals(config, setup.getMedia());
        assertNotNull(setup);
    }

    /**
     * Test the setup config.
     */
    @Test
    void testConfigNoIcon()
    {
        final Media config = Medias.create("ObjectNoIcon.xml");
        final Setup setup = new Setup(config);

        assertEquals(config, setup.getMedia());
        assertThrows(() -> setup.getIconFile(), Setup.ERROR_ICON_FILE);
        assertNotNull(setup);
    }

    /**
     * Test the setup class.
     */
    @Test
    void testClass()
    {
        final Media config = Medias.create("Object.xml");
        final Setup setup = new Setup(config);

        assertEquals(FeaturableModel.class, setup.getConfigClass(ClassLoader.getSystemClassLoader()));
        assertEquals(FeaturableModel.class, setup.getConfigClass(ClassLoader.getSystemClassLoader()));
    }

    /**
     * Test the setup with no class.
     */
    @Test
    void testNoClass()
    {
        final Setup setup = new Setup(Medias.create("ObjectNoClass.xml"));

        assertCause(() -> setup.getConfigClass(ClassLoader.getSystemClassLoader()), ClassNotFoundException.class);
    }

    /**
     * Test the setup surface config.
     */
    @Test
    void testSurface()
    {
        final Media config = Medias.create("Object.xml");
        final Setup setup = new Setup(config);

        assertEquals(Medias.create("surface.png"), setup.getSurfaceFile());
        assertEquals(Medias.create("icon.png"), setup.getIconFile());
        assertEquals(7, setup.getSurface().getWidth());
        assertEquals(11, setup.getSurface().getHeight());
        assertEquals(7, setup.getIcon().getWidth());
        assertEquals(11, setup.getIcon().getHeight());
    }

    /**
     * Test the setup with no surface.
     */
    @Test
    void testNoSurface()
    {
        final Setup setup = new Setup(Medias.create("ObjectNoConstructor.xml"));

        assertThrows(() -> setup.getSurfaceFile(), Setup.ERROR_SURFACE_FILE);
        assertThrows(() -> setup.getSurface(), Setup.ERROR_SURFACE);
    }

    /**
     * Test the setup with no surface.
     */
    @Test
    void testNoIcon()
    {
        final Setup setup = new Setup(Medias.create("ObjectNoConstructor.xml"));

        assertThrows(() -> setup.getIconFile(), Setup.ERROR_ICON_FILE);
    }
}
