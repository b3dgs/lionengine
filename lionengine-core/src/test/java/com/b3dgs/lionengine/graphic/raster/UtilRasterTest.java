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
package com.b3dgs.lionengine.graphic.raster;

import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.FactoryMediaDefault;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link UtilRaster}.
 */
public final class UtilRasterTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Medias.setLoadFromJar(UtilRasterTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilRaster.class);
    }

    /**
     * Test raster.
     */
    @Test
    public void testRaster()
    {
        final Media media = Medias.create("raster.xml");
        final Raster raster = Raster.load(media);
        final RasterData rasterData = raster.getRed();

        assertTrue(UtilRaster.getRasterColor(0, rasterData, 2) > 0);

        final RasterData data = new RasterData(rasterData.getStart(),
                                               rasterData.getStep(),
                                               rasterData.getForce(),
                                               rasterData.getAmplitude(),
                                               rasterData.getOffset(),
                                               1);

        assertTrue(UtilRaster.getRasterColor(0, data, 2) < 0);
    }
}
