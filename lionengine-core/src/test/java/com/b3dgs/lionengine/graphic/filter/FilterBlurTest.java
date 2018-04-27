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
package com.b3dgs.lionengine.graphic.filter;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Test {@link FilterBlur}.
 */
public final class FilterBlurTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(FilterBlurTest.class);
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
     * Test blur filter
     */
    @Test
    public void testBlur()
    {
        final Media media = Medias.create("image.png");
        final ImageBuffer image = Graphics.getImageBuffer(media);
        int i = 0;
        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                i++;
                if (y < 10)
                {
                    image.setRgb(x, y, i % 2);
                }
                else if (y < 20)
                {
                    image.setRgb(x, y, i % 3);
                }
                else if (y < 32)
                {
                    image.setRgb(x, y, i % 5);
                }
            }
        }
        final FilterBlur blur = new FilterBlur();
        final ImageBuffer filtered = blur.filter(image);

        assertNotEquals(image, filtered);
        assertNotNull(blur.getTransform(1.0, 1.0));
        assertEquals(image.getWidth(), filtered.getWidth());
        assertEquals(image.getHeight(), filtered.getHeight());

        blur.setAlpha(false);
        assertNotNull(blur.filter(image));

        blur.setEdgeMode(FilterBlur.WRAP_EDGES);
        assertNotNull(blur.filter(image));

        blur.setRadius(1.0f);
        assertNotNull(blur.filter(image));

        image.dispose();
        filtered.dispose();
    }

    /**
     * Test without pixel.
     */
    @Test
    public void testNoPixel()
    {
        final FilterBlur blur = new FilterBlur();
        final ImageBuffer image = Graphics.createImageBuffer(1, 1);
        final ImageBuffer filtered = blur.filter(image);

        assertNotNull(filtered);
        assertEquals(image.getWidth(), filtered.getWidth());
        assertEquals(image.getHeight(), filtered.getHeight());

        image.dispose();
        filtered.dispose();
    }

    /**
     * Test with single pixel.
     */
    @Test
    public void testSinglePixel()
    {
        final FilterBlur blur = new FilterBlur();
        final ImageBuffer image = Graphics.createImageBuffer(1, 1);
        final ImageBuffer filtered = blur.filter(image);

        assertEquals(image, filtered);

        image.dispose();
        filtered.dispose();
    }

    /**
     * Test with low width.
     */
    @Test
    public void testLowWidth()
    {
        final FilterBlur blur = new FilterBlur();
        final ImageBuffer image = Graphics.createImageBuffer(1, 3);
        final ImageBuffer filtered = blur.filter(image);

        assertEquals(image, filtered);

        image.dispose();
        filtered.dispose();
    }

    /**
     * Test with low height.
     */
    @Test
    public void testLowHeight()
    {
        final FilterBlur blur = new FilterBlur();
        final ImageBuffer image = Graphics.createImageBuffer(3, 1);
        final ImageBuffer filtered = blur.filter(image);

        assertEquals(image, filtered);

        image.dispose();
        filtered.dispose();
    }
}
