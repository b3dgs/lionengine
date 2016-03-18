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
package com.b3dgs.lionengine;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.Medias;

/**
 * Test the raster
 */
public class RasterTest
{
    /** Raster. */
    private static Media mediaRaster;
    /** Raster error. */
    private static Media mediaRasterError;

    /**
     * Prepare test.
     * 
     * @throws IOException If error.
     */
    @BeforeClass
    public static void setUp() throws IOException
    {
        Medias.setLoadFromJar(RasterTest.class);
        mediaRaster = Medias.create("raster.xml");
        mediaRasterError = Medias.create("raster_error.xml");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test raster field.
     */
    @Test
    public void testRasterField()
    {
        final RasterData red = new RasterData(0, 0, 0, 0, 0, 0);
        final RasterData green = new RasterData(0, 0, 0, 0, 0, 0);
        final RasterData blue = new RasterData(0, 0, 0, 0, 0, 0);
        final Raster raster = new Raster(red, green, blue);

        Assert.assertEquals(red, raster.getRed());
        Assert.assertEquals(green, raster.getGreen());
        Assert.assertEquals(blue, raster.getBlue());
    }

    /**
     * Test load raster.
     */
    @Test
    public void testLoadRaster()
    {
        Assert.assertNotNull(Raster.load(mediaRaster));
    }

    /**
     * Test load raster failure.
     */
    @Test(expected = LionEngineException.class)
    public void testLoadRasterFailure()
    {
        Assert.assertNotNull(Raster.load(mediaRasterError));
    }
}
