/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Hq2x;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.test.mock.FactoryGraphicMock;

/**
 * Test the Hq2x filter.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Hq2xTest
{
    /** Image media. */
    private static final Media MEDIA = Medias.create("image.png");

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(Hq2xTest.class);
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
     * Test the Hq2x filter
     */
    @Test
    public void testHq2x()
    {
        final ImageBuffer image = Graphics.getImageBuffer(MEDIA);
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
        final Hq2x hq2x = new Hq2x(image);
        final ImageBuffer filtered = hq2x.getScaledImage();

        Assert.assertNotEquals(image, filtered);
        Assert.assertEquals(image.getWidth() * 2, filtered.getWidth());
        Assert.assertEquals(image.getHeight() * 2, filtered.getHeight());
    }
}