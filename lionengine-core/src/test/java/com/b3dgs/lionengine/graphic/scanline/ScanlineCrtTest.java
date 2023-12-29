/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.scanline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Scanline;

/**
 * Test {@link ScanlineCrt}.
 */
final class ScanlineCrtTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(ScanlineCrtTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.create("scanline", "crt", "320_240.png").getFile().delete();
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test filter.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test
    void testScanline() throws ReflectiveOperationException
    {
        final Resolution source = new Resolution(320, 240, 60);
        final Scanline scanline = ScanlineCrt.getInstance(source, 1.0);

        assertEquals(scanline, ScanlineCrt.getInstance(source, 1.0));

        scanline.prepare(new Config(source, 32, true));
        scanline.prepare(new Config(source, 32, true));

        final ImageBuffer image = Graphics.createImageBuffer(320, 240);
        final Graphic g = image.createGraphic();
        scanline.render(g);

        final Field field = ScanlineCrt.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(ScanlineCrtTest.class, null);

        final Scanline scanline2 = ScanlineCrt.getInstance(source, 1.0);
        scanline2.prepare(new Config(source, 32, true));
        scanline2.render(g);

        image.dispose();
        g.dispose();
    }
}
