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
package com.b3dgs.lionengine.core.drawable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Image;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the image class.
 */
public class ImageTest
{
    /** Image media. */
    private static Media media;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(ImageTest.class);

        media = Medias.create("image.png");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Graphics.setFactoryGraphic(null);
        Medias.setLoadFromJar(null);
    }

    /**
     * Test the constructor with media.
     */
    @Test
    public void testConstructorMedia()
    {
        final Image image = new ImageImpl(media);

        Assert.assertFalse(image.isLoaded());
        Assert.assertNull(image.getSurface());
        Assert.assertEquals(64, image.getWidth());
        Assert.assertEquals(32, image.getHeight());
    }

    /**
     * Test the constructor with surface.
     */
    @Test
    public void testConstructorSurface()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final Image image = new ImageImpl(surface);

        Assert.assertTrue(image.isLoaded());
        Assert.assertEquals(surface, image.getSurface());
        Assert.assertEquals(64, image.getWidth());
        Assert.assertEquals(32, image.getHeight());
    }

    /**
     * Test the load with media.
     */
    @Test
    public void testLoadMedia()
    {
        final Image image = new ImageImpl(media);
        image.load();

        Assert.assertNotNull(image.getSurface());

        image.prepare();
        image.dispose();
    }

    /**
     * Test the load with media already loaded.
     */
    @Test(expected = LionEngineException.class)
    public void testLoadMediaAlready()
    {
        final Image image = new ImageImpl(media);
        image.load();
        image.load();
    }

    /**
     * Test the load with surface.
     */
    @Test(expected = LionEngineException.class)
    public void testLoadSurface()
    {
        final Image image = new ImageImpl(Graphics.createImageBuffer(64, 32));
        image.load();
    }

    /**
     * Test the set location.
     */
    @Test
    public void testSetLocation()
    {
        final ImageImpl image = new ImageImpl(Graphics.createImageBuffer(64, 32));

        Assert.assertEquals(0.0, image.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, image.getY(), UtilTests.PRECISION);
        Assert.assertEquals(0, image.getRenderX());
        Assert.assertEquals(0, image.getRenderY());

        image.setLocation(1.5, 2.5);

        Assert.assertEquals(1.5, image.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.5, image.getY(), UtilTests.PRECISION);
        Assert.assertEquals(1, image.getRenderX());
        Assert.assertEquals(2, image.getRenderY());
    }

    /**
     * Test the set location with viewer.
     */
    @Test
    public void testSetLocationViewer()
    {
        final ImageImpl image = new ImageImpl(Graphics.createImageBuffer(64, 32));
        final ViewerMock viewer = new ViewerMock();
        image.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        Assert.assertEquals(1.5, image.getX(), UtilTests.PRECISION);
        Assert.assertEquals(237.5, image.getY(), UtilTests.PRECISION);
        Assert.assertEquals(1, image.getRenderX());
        Assert.assertEquals(237, image.getRenderY());

        viewer.set(10, 20);
        image.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        Assert.assertEquals(-8.5, image.getX(), UtilTests.PRECISION);
        Assert.assertEquals(257.5, image.getY(), UtilTests.PRECISION);
        Assert.assertEquals(-9, image.getRenderX());
        Assert.assertEquals(257, image.getRenderY());
    }

    /**
     * Test the origin <code>null</code>.
     */
    @Test
    public void testRenderingPoint()
    {
        final ImageImpl image = new ImageImpl(Graphics.createImageBuffer(10, 20));
        image.setLocation(5.0, 10.0);
        image.setOrigin(Origin.TOP_LEFT);

        Assert.assertEquals(5, image.getRenderX());
        Assert.assertEquals(10, image.getRenderY());

        image.setOrigin(Origin.MIDDLE);

        Assert.assertEquals(0, image.getRenderX());
        Assert.assertEquals(0, image.getRenderY());
    }

    /**
     * Test the origin <code>null</code>.
     */
    @Test(expected = LionEngineException.class)
    public void testSetOriginNull()
    {
        final Image image = new ImageImpl(Graphics.createImageBuffer(64, 32));
        image.setOrigin(null);
    }

    /**
     * Test the render.
     */
    @Test
    public void testRender()
    {
        final Graphic g = Graphics.createImageBuffer(100, 100).createGraphic();
        try
        {
            final Image image = new ImageImpl(Graphics.createImageBuffer(64, 32));
            image.render(g);
        }
        finally
        {
            g.dispose();
        }
    }

    /**
     * Test the equals.
     */
    @Test
    public void testEquals()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final Image image = new ImageImpl(surface);
        final Image imageMedia = new ImageImpl(media);
        imageMedia.load();

        Assert.assertEquals(image, image);
        Assert.assertEquals(image, new ImageImpl(surface));
        Assert.assertEquals(imageMedia, imageMedia);

        Assert.assertNotEquals(image, null);
        Assert.assertNotEquals(image, new Object());
        Assert.assertNotEquals(image, new ImageImpl(media));
        Assert.assertNotEquals(imageMedia, new ImageImpl(media));
        Assert.assertNotEquals(image, new ImageImpl(Graphics.createImageBuffer(64, 32)));
        Assert.assertNotEquals(image, new ImageImpl(Graphics.createImageBuffer(32, 32)));
        Assert.assertNotEquals(image, new ImageImpl(Graphics.createImageBuffer(64, 64)));
        Assert.assertNotEquals(image, new ImageImpl(Graphics.createImageBuffer(32, 64)));
    }

    /**
     * Test the hash.
     */
    @Test
    public void testHash()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final int image = new ImageImpl(surface).hashCode();
        final Image imageMedia = new ImageImpl(media);
        imageMedia.load();

        Assert.assertEquals(image, new ImageImpl(surface).hashCode());

        Assert.assertNotEquals(image, new Object().hashCode());
        Assert.assertNotEquals(imageMedia.hashCode(), new ImageImpl(media).hashCode());
        Assert.assertNotEquals(image, new ImageImpl(Graphics.createImageBuffer(64, 32)).hashCode());
        Assert.assertNotEquals(image, new ImageImpl(Graphics.createImageBuffer(32, 32)).hashCode());
        Assert.assertNotEquals(image, new ImageImpl(Graphics.createImageBuffer(64, 64)).hashCode());
        Assert.assertNotEquals(image, new ImageImpl(Graphics.createImageBuffer(32, 64)).hashCode());
    }
}
