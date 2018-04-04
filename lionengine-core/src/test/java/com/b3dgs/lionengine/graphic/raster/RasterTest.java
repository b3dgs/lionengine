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

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;

/**
 * Test {@link Raster}.
 */
public final class RasterTest
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
     * Test field.
     */
    @Test
    public void testField()
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
     * Test color.
     */
    @Test
    public void testColor()
    {
        final RasterData raster = new RasterData(0, 0, 0, 0, 0, 0);
        final RasterColor color = RasterColor.load(raster, 0, 0, false);

        Assert.assertEquals(0, color.getStart());
        Assert.assertEquals(0, color.getEnd());

        final RasterData raster2 = new RasterData(200, 10, 10, 100, 10, 0);
        final RasterColor color2 = RasterColor.load(raster2, 0, 90, false);

        Assert.assertEquals(130, color2.getStart());
        Assert.assertEquals(130, color2.getEnd());
    }

    /**
     * Test color smoothed.
     */
    @Test
    public void testColorSmooth()
    {
        final RasterData raster = new RasterData(200, 10, 10, 100, 10, 0);
        final RasterColor color = RasterColor.load(raster, 0, 90, true);

        Assert.assertEquals(130, color.getStart());
        Assert.assertEquals(120, color.getEnd());

        final RasterData raster2 = new RasterData(200, 10, 10, 100, 10, 0);
        final RasterColor color2 = RasterColor.load(raster2, 1, 90, true);

        Assert.assertEquals(160, color2.getStart());
        Assert.assertEquals(170, color2.getEnd());
    }

    /**
     * Test load.
     */
    @Test
    public void testLoad()
    {
        Assert.assertNotNull(Raster.load(mediaRaster));
    }

    /**
     * Test load failure.
     */
    @Test(expected = LionEngineException.class)
    public void testLoadFailure()
    {
        Assert.assertNotNull(Raster.load(mediaRasterError));
    }
}
