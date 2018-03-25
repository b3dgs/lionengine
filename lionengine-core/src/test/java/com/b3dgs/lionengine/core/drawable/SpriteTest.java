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

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.filter.FilterBilinear;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Sprite;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link Sprite}.
 */
public final class SpriteTest
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
        Medias.setLoadFromJar(SpriteTest.class);

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
     * Test constructor with media.
     */
    @Test
    public void testConstructorMedia()
    {
        final Sprite sprite = new SpriteImpl(media);

        Assert.assertFalse(sprite.isLoaded());
        Assert.assertNull(sprite.getSurface());
        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
    }

    /**
     * Test constructor with surface.
     */
    @Test
    public void testConstructorSurface()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final Sprite sprite = new SpriteImpl(surface);

        Assert.assertTrue(sprite.isLoaded());
        Assert.assertEquals(surface, sprite.getSurface());
        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
    }

    /**
     * Test load with media.
     */
    @Test
    public void testLoadMedia()
    {
        final Sprite sprite = new SpriteImpl(media);
        sprite.load();

        Assert.assertNotNull(sprite.getSurface());

        sprite.prepare();
        sprite.dispose();
    }

    /**
     * Test load with media already loaded.
     */
    @Test(expected = LionEngineException.class)
    public void testLoadMediaAlready()
    {
        final Sprite sprite = new SpriteImpl(media);
        sprite.load();
        sprite.load();
    }

    /**
     * Test load with surface.
     */
    @Test(expected = LionEngineException.class)
    public void testLoadSurface()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.load();
    }

    /**
     * Test stretch sprite.
     */
    @Test
    public void testStretch()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.stretch(100.0, 100.0);

        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());

        sprite.stretch(200.0, 100.0);

        Assert.assertEquals(128, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());

        sprite.stretch(100.0, 200.0);

        Assert.assertEquals(128, sprite.getWidth());
        Assert.assertEquals(64, sprite.getHeight());

        sprite.stretch(200.0, 200.0);

        Assert.assertEquals(256, sprite.getWidth());
        Assert.assertEquals(128, sprite.getHeight());
    }

    /**
     * Test stretch sprite with invalid width.
     */
    @Test(expected = LionEngineException.class)
    public void testStretchInvalidWidth()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.stretch(0.0, 100.0);
    }

    /**
     * Test stretch sprite with invalid height.
     */
    @Test(expected = LionEngineException.class)
    public void testStretchInvalidHeight()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.stretch(100, 0.0);
    }

    /**
     * Test rotate sprite.
     */
    @Test
    public void testRotate()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        for (int angle = -720; angle < 720; angle++)
        {
            sprite.rotate(angle);
            Assert.assertTrue(angle + Constant.SPACE + sprite.getWidth(), sprite.getWidth() >= 64);
            Assert.assertTrue(angle + Constant.SPACE + sprite.getHeight(), sprite.getHeight() >= 32);
        }
    }

    /**
     * Test set location.
     */
    @Test
    public void testSetLocation()
    {
        final SpriteImpl sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));

        Assert.assertEquals(0.0, sprite.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, sprite.getY(), UtilTests.PRECISION);
        Assert.assertEquals(0, sprite.getRenderX());
        Assert.assertEquals(0, sprite.getRenderY());

        sprite.setLocation(1.5, 2.5);

        Assert.assertEquals(1.5, sprite.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.5, sprite.getY(), UtilTests.PRECISION);
        Assert.assertEquals(1, sprite.getRenderX());
        Assert.assertEquals(2, sprite.getRenderY());
    }

    /**
     * Test set location with viewer.
     */
    @Test
    public void testSetLocationViewer()
    {
        final SpriteImpl sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        final ViewerMock viewer = new ViewerMock();
        sprite.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        Assert.assertEquals(1.5, sprite.getX(), UtilTests.PRECISION);
        Assert.assertEquals(237.5, sprite.getY(), UtilTests.PRECISION);
        Assert.assertEquals(1, sprite.getRenderX());
        Assert.assertEquals(237, sprite.getRenderY());

        viewer.set(10, 20);
        sprite.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        Assert.assertEquals(-8.5, sprite.getX(), UtilTests.PRECISION);
        Assert.assertEquals(257.5, sprite.getY(), UtilTests.PRECISION);
        Assert.assertEquals(-9, sprite.getRenderX());
        Assert.assertEquals(257, sprite.getRenderY());
    }

    /**
     * Test set alpha.
     */
    @Test
    public void testSetAlpha()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        for (int alpha = 0; alpha < 256; alpha++)
        {
            sprite.setAlpha(alpha);

            Assert.assertEquals(64, sprite.getWidth());
            Assert.assertEquals(32, sprite.getHeight());
        }
    }

    /**
     * Test set alpha too low.
     */
    @Test(expected = LionEngineException.class)
    public void testSetAlphaLow()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.setAlpha(-1);
    }

    /**
     * Test set alpha too high.
     */
    @Test(expected = LionEngineException.class)
    public void testSetAlphaHigh()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.setAlpha(256);
    }

    /**
     * Test set transparency.
     */
    @Test
    public void testSetTransparency()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.setTransparency(ColorRgba.BLACK);

        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
    }

    /**
     * Test set fade.
     */
    @Test
    public void testSetFade()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.setFade(128, 128);
        sprite.setFade(128, 128);

        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
    }

    /**
     * Test filter bilinear.
     */
    @Test
    public void testFilterBilinear()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.filter(new FilterBilinear());

        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
    }

    /**
     * Test filter <code>null</code>.
     */
    @Test(expected = LionEngineException.class)
    public void testFilterNull()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.filter(null);
    }

    /**
     * Test the mirror.
     */
    @Test
    public void testMirror()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));

        Assert.assertEquals(Mirror.NONE, sprite.getMirror());

        sprite.setMirror(Mirror.HORIZONTAL);

        Assert.assertEquals(Mirror.HORIZONTAL, sprite.getMirror());
    }

    /**
     * Test mirror <code>null</code>.
     */
    @Test(expected = LionEngineException.class)
    public void testMirrorNull()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.setMirror(null);
    }

    /**
     * Test rendering point.
     */
    @Test
    public void testRenderingPoint()
    {
        final SpriteImpl sprite = new SpriteImpl(Graphics.createImageBuffer(10, 20));
        sprite.setLocation(5.0, 10.0);
        sprite.setOrigin(Origin.TOP_LEFT);

        Assert.assertEquals(5, sprite.getRenderX());
        Assert.assertEquals(10, sprite.getRenderY());

        sprite.setOrigin(Origin.MIDDLE);

        Assert.assertEquals(0, sprite.getRenderX());
        Assert.assertEquals(0, sprite.getRenderY());
    }

    /**
     * Test origin <code>null</code>.
     */
    @Test(expected = LionEngineException.class)
    public void testSetOriginNull()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.setOrigin(null);
    }

    /**
     * Test render.
     */
    @Test
    public void testRender()
    {
        final Graphic g = Graphics.createImageBuffer(100, 100).createGraphic();
        try
        {
            final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
            sprite.render(g);

            sprite.setMirror(Mirror.HORIZONTAL);
            sprite.render(g);

            sprite.setMirror(Mirror.VERTICAL);
            sprite.render(g);
        }
        finally
        {
            g.dispose();
        }
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final Sprite sprite = new SpriteImpl(surface);
        final Sprite spriteMedia = new SpriteImpl(media);
        spriteMedia.load();

        Assert.assertEquals(sprite, sprite);
        Assert.assertEquals(sprite, new SpriteImpl(surface));
        Assert.assertEquals(spriteMedia, spriteMedia);

        Assert.assertNotEquals(sprite, null);
        Assert.assertNotEquals(sprite, new Object());
        Assert.assertNotEquals(sprite, new SpriteImpl(media));
        Assert.assertNotEquals(spriteMedia, new SpriteImpl(media));
        Assert.assertNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(64, 32)));
        Assert.assertNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(32, 32)));
        Assert.assertNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(64, 64)));
        Assert.assertNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(32, 64)));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final int sprite = new SpriteImpl(surface).hashCode();
        final Sprite spriteMedia = new SpriteImpl(media);
        spriteMedia.load();

        Assert.assertEquals(sprite, new SpriteImpl(surface).hashCode());

        Assert.assertNotEquals(sprite, new Object().hashCode());
        Assert.assertNotEquals(spriteMedia.hashCode(), new SpriteImpl(media).hashCode());
        Assert.assertNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(64, 32)).hashCode());
        Assert.assertNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(32, 32)).hashCode());
        Assert.assertNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(64, 64)).hashCode());
        Assert.assertNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(32, 64)).hashCode());
    }
}
