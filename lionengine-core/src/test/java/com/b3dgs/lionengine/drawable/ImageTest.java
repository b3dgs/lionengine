/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.drawable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.FactoryGraphicProvider;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.MediaMock;

/**
 * Test the image class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ImageTest
{
    /** Image media. */
    private static final Media MEDIA = new MediaMock("image.png");
    /** Graphic test output. */
    private static Graphic g;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(new FactoryGraphicMock());
        g = Core.GRAPHIC.createImageBuffer(100, 100, Transparency.OPAQUE).createGraphic();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(null);
    }

    /**
     * Test function around the image failure.
     */
    @Test
    public void testImageFailure()
    {
        try
        {
            Core.GRAPHIC.createImageBuffer(-16, 16, Transparency.OPAQUE);
            Assert.fail();
        }
        catch (final NegativeArraySizeException exception)
        {
            // Success
        }
        try
        {
            Core.GRAPHIC.createImageBuffer(16, -16, Transparency.OPAQUE);
            Assert.fail();
        }
        catch (final NegativeArraySizeException exception)
        {
            // Success
        }
        // Load unexisting image
        try
        {
            Drawable.loadImage(new MediaMock("void", true));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test function around the image.
     */
    @Test
    public void testImage()
    {
        final int width = 16;
        final int height = 16;
        final ImageBuffer surface = Core.GRAPHIC.createImageBuffer(width, height, Transparency.OPAQUE);
        final Image imageA = Drawable.loadImage(surface);

        Assert.assertNotNull(imageA);
        Assert.assertEquals(width, imageA.getWidth());
        Assert.assertEquals(height, imageA.getHeight());
        Assert.assertEquals(surface, imageA.getSurface());

        // Share correctly the surface
        final Image imageB = Drawable.loadImage(imageA.getSurface());
        Assert.assertEquals(imageA, imageB);
        Assert.assertEquals(imageB, Drawable.loadImage(imageB.getSurface()));

        // Load from file
        final Image imageC = Drawable.loadImage(MEDIA);
        imageC.load(false);
        DrawableTestTool.assertImageInfoCorrect(MEDIA, imageC);
        Assert.assertNotNull(imageC.getSurface());
        DrawableTestTool.testImageRender(g, imageC);
        Assert.assertFalse(imageC.equals(Drawable.loadImage(MEDIA)));

        // Equals
        final ImageBuffer surfaceA = Core.GRAPHIC.createImageBuffer(16, 16, Transparency.OPAQUE);
        final Image imageD = Drawable.loadImage(surfaceA);
        final ImageBuffer surfaceB = Core.GRAPHIC.createImageBuffer(16, 20, Transparency.OPAQUE);
        final Image imageE = Drawable.loadImage(surfaceB);
        final ImageBuffer surfaceC = Core.GRAPHIC.createImageBuffer(20, 16, Transparency.OPAQUE);
        final Image imageF = Drawable.loadImage(surfaceC);
        Assert.assertTrue(imageD.equals(imageD));
        Assert.assertFalse(imageD.equals(imageE));
        Assert.assertFalse(imageD.equals(imageF));
    }
}
