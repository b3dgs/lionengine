/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test the setup class.
 */
public class SetupTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(SetupTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
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

        Assert.assertEquals(config, setup.getMedia());
        Assert.assertNotNull(setup);
    }

    /**
     * Test the setup class.
     */
    @Test
    public void testClass()
    {
        final Media config = Medias.create("object.xml");
        final Setup setup = new Setup(config);

        Assert.assertEquals(FeaturableModel.class, setup.getConfigClass(ClassLoader.getSystemClassLoader()));
        Assert.assertEquals(FeaturableModel.class, setup.getConfigClass(ClassLoader.getSystemClassLoader()));
    }

    /**
     * Test the setup with no class.
     */
    @Test()
    public void testNoClass()
    {
        final Setup setup = new Setup(Medias.create("no_class.xml"));
        try
        {
            Assert.assertEquals(FeaturableModel.class, setup.getConfigClass(ClassLoader.getSystemClassLoader()));
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals(ClassNotFoundException.class, exception.getCause().getClass());
        }
    }

    /**
     * Test the setup surface config.
     */
    @Test
    public void testSurface()
    {
        final Media config = Medias.create("object.xml");
        final Setup setup = new Setup(config);

        Assert.assertEquals(Medias.create("surface.png"), setup.getSurfaceFile());
        Assert.assertEquals(7, setup.getSurface().getWidth());
        Assert.assertEquals(11, setup.getSurface().getHeight());
    }
}
