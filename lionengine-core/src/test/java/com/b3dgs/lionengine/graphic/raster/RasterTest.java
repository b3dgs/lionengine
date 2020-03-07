/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.raster;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.FactoryMediaDefault;
import com.b3dgs.lionengine.Medias;

/**
 * Test {@link Raster}.
 */
public final class RasterTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Medias.setLoadFromJar(RasterTest.class);
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test field.
     */
    @Test
    public void testField()
    {
        final RasterData red = new RasterData(0, 0, 0, 0);
        final RasterData green = new RasterData(0, 0, 0, 0);
        final RasterData blue = new RasterData(0, 0, 0, 0);
        final Raster raster = new Raster(red, green, blue);

        assertEquals(red, raster.getRed());
        assertEquals(green, raster.getGreen());
        assertEquals(blue, raster.getBlue());
    }

    /**
     * Test load.
     */
    @Test
    public void testLoad()
    {
        assertNotNull(Raster.load(Medias.create("raster.xml")));
    }

    /**
     * Test load failure.
     */
    @Test
    public void testLoadFailure()
    {
        assertThrows(() -> Raster.load(Medias.create("raster_error.xml")), "Node not found: Red");
    }
}
