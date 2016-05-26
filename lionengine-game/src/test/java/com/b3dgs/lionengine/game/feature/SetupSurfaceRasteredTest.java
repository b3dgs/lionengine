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
package com.b3dgs.lionengine.game.feature;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.raster.SetupSurfaceRastered;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;

/**
 * Test the setup surface rastered class.
 */
public class SetupSurfaceRasteredTest
{
    /** Object configuration file name. */
    private static final String OBJECT_XML = "object.xml";
    /** Raster configuration file name. */
    private static final String RASTER_XML = "raster.xml";

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(SetupSurfaceRasteredTest.class);
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
     * Test the setup surface rastered config.
     */
    @Test
    public void testConfig()
    {
        final Media raster = Medias.create(RASTER_XML);
        final SetupSurfaceRastered setup = new SetupSurfaceRastered(Medias.create(OBJECT_XML), raster, false);

        Assert.assertEquals(raster, setup.getFile());
        Assert.assertFalse(setup.hasSmooth());
        for (final SpriteAnimated sprite : setup.getRasters())
        {
            Assert.assertEquals(setup.getSurface().getWidth(), sprite.getSurface().getWidth());
            Assert.assertEquals(setup.getSurface().getHeight(), sprite.getSurface().getHeight());
        }
    }

    /**
     * Test the setup surface rastered config.
     */
    @Test
    public void testConfigSmooth()
    {
        final Media raster = Medias.create(RASTER_XML);
        final SetupSurfaceRastered setup = new SetupSurfaceRastered(Medias.create(OBJECT_XML), raster, true);

        Assert.assertEquals(raster, setup.getFile());
        Assert.assertTrue(setup.hasSmooth());
        for (final SpriteAnimated sprite : setup.getRasters())
        {
            Assert.assertEquals(setup.getSurface().getWidth(), sprite.getSurface().getWidth());
            Assert.assertEquals(setup.getSurface().getHeight(), sprite.getSurface().getHeight());
        }
    }

    /**
     * Test the setup surface rastered without raster.
     */
    @Test(expected = LionEngineException.class)
    public void testConfigNoRaster()
    {
        Assert.assertNotNull(new SetupSurfaceRastered(Medias.create(OBJECT_XML), null, false));
    }
}
