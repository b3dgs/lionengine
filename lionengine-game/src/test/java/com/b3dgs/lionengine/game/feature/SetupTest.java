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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link Setup}.
 */
public final class SetupTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(SetupTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test the setup config.
     */
    @Test
    public void testConfig()
    {
        final Media config = Medias.create("object.xml");
        final Setup setup = new Setup(config);

        assertEquals(config, setup.getMedia());
        assertNotNull(setup);
    }

    /**
     * Test the setup config.
     */
    @Test
    public void testConfigNoIcon()
    {
        final Media config = Medias.create("object_no_icon.xml");
        final Setup setup = new Setup(config);

        assertEquals(config, setup.getMedia());
        assertThrows(() -> setup.getIconFile(), Setup.ERROR_ICON_FILE);
        assertNotNull(setup);
    }

    /**
     * Test the setup class.
     */
    @Test
    public void testClass()
    {
        final Media config = Medias.create("object.xml");
        final Setup setup = new Setup(config);

        assertEquals(FeaturableModel.class, setup.getConfigClass(ClassLoader.getSystemClassLoader()));
        assertEquals(FeaturableModel.class, setup.getConfigClass(ClassLoader.getSystemClassLoader()));
    }

    /**
     * Test the setup with no class.
     */
    @Test()
    public void testNoClass()
    {
        final Setup setup = new Setup(Medias.create("no_class.xml"));

        assertCause(() -> setup.getConfigClass(ClassLoader.getSystemClassLoader()), ClassNotFoundException.class);
    }

    /**
     * Test the setup surface config.
     */
    @Test
    public void testSurface()
    {
        final Media config = Medias.create("object.xml");
        final Setup setup = new Setup(config);

        assertEquals(Medias.create("surface.png"), setup.getSurfaceFile());
        assertEquals(Medias.create("icon.png"), setup.getIconFile());
        assertEquals(7, setup.getSurface().getWidth());
        assertEquals(11, setup.getSurface().getHeight());
    }

    /**
     * Test the setup with no surface.
     */
    @Test()
    public void testNoSurface()
    {
        final Setup setup = new Setup(Medias.create("no_constructor.xml"));

        assertThrows(() -> setup.getSurfaceFile(), Setup.ERROR_SURFACE_FILE);
        assertThrows(() -> setup.getSurface(), Setup.ERROR_SURFACE);
    }

    /**
     * Test the setup with no surface.
     */
    @Test()
    public void testNoIcon()
    {
        final Setup setup = new Setup(Medias.create("no_constructor.xml"));

        assertThrows(() -> setup.getIconFile(), Setup.ERROR_ICON_FILE);
    }
}
