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

import com.b3dgs.lionengine.Align;
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
import com.b3dgs.lionengine.graphic.SpriteFont;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the sprite class.
 */
public class SpriteFontTest
{
    /** Image media. */
    private static Media media;
    /** Font media. */
    private static Media font;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(SpriteFontTest.class);

        media = Medias.create("image.png");
        font = Medias.create("fontdata.xml");
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
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);

        Assert.assertFalse(sprite.isLoaded());
        Assert.assertNull(sprite.getSurface());
        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
    }

    /**
     * Test the load with media.
     */
    @Test
    public void testLoadMedia()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();

        Assert.assertNotNull(sprite.getSurface());

        sprite.prepare();
        sprite.dispose();
    }

    /**
     * Test the load with media already loaded.
     */
    @Test(expected = LionEngineException.class)
    public void testLoadMediaAlready()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.load();
    }

    /**
     * Test the stretch sprite.
     */
    @Test
    public void testStretch()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.stretch(100.0, 100.0);

        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());

        sprite.stretch(200.0, 100.0);

        Assert.assertEquals(130, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());

        sprite.stretch(100.0, 200.0);

        Assert.assertEquals(130, sprite.getWidth());
        Assert.assertEquals(64, sprite.getHeight());

        sprite.stretch(200.0, 200.0);

        Assert.assertEquals(260, sprite.getWidth());
        Assert.assertEquals(128, sprite.getHeight());
    }

    /**
     * Test the stretch sprite with invalid width.
     */
    @Test(expected = LionEngineException.class)
    public void testStretchInvalidWidth()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.stretch(0.0, 100.0);
    }

    /**
     * Test the stretch sprite with invalid height.
     */
    @Test(expected = LionEngineException.class)
    public void testStretchInvalidHeight()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.stretch(100, 0.0);
    }

    /**
     * Test the rotate sprite.
     */
    @Test
    public void testRotate()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        for (int angle = -720; angle < 720; angle++)
        {
            sprite.rotate(angle);
            Assert.assertTrue(angle + Constant.SPACE + sprite.getWidth(), sprite.getWidth() >= 64);
            Assert.assertTrue(angle + Constant.SPACE + sprite.getHeight(), sprite.getHeight() >= 32);
        }
    }

    /**
     * Test the set location.
     */
    @Test
    public void testSetLocation()
    {
        final SpriteFontImpl sprite = new SpriteFontImpl(media, font, 6, 7);

        Assert.assertEquals(0.0, sprite.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, sprite.getY(), UtilTests.PRECISION);

        sprite.setLocation(1.5, 2.5);

        Assert.assertEquals(1.5, sprite.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.5, sprite.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the set location with viewer.
     */
    @Test
    public void testSetLocationViewer()
    {
        final SpriteFontImpl sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        final ViewerMock viewer = new ViewerMock();
        sprite.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        Assert.assertEquals(0.0, sprite.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, sprite.getY(), UtilTests.PRECISION);

        viewer.set(10, 20);
        sprite.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        Assert.assertEquals(0.0, sprite.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, sprite.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the set alpha.
     */
    @Test
    public void testSetAlpha()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        for (int alpha = 0; alpha < 256; alpha++)
        {
            sprite.setAlpha(alpha);

            Assert.assertEquals(64, sprite.getWidth());
            Assert.assertEquals(32, sprite.getHeight());
        }
    }

    /**
     * Test the set alpha too low.
     */
    @Test(expected = LionEngineException.class)
    public void testSetAlphaLow()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.setAlpha(-1);
    }

    /**
     * Test the set alpha too high.
     */
    @Test(expected = LionEngineException.class)
    public void testSetAlphaHigh()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.setAlpha(256);
    }

    /**
     * Test the set transparency.
     */
    @Test
    public void testSetTransparency()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.setTransparency(ColorRgba.BLACK);

        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
    }

    /**
     * Test the set fade.
     */
    @Test
    public void testSetFade()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.setFade(128, 128);
        sprite.setFade(128, 128);

        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
    }

    /**
     * Test the filter bilinear.
     */
    @Test
    public void testFilter()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.filter(new FilterBilinear());

        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
    }

    /**
     * Test the filter.
     */
    @Test(expected = LionEngineException.class)
    public void testFilterNull()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.filter(null);
    }

    /**
     * Test the mirror.
     */
    @Test
    public void testMirror()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);

        Assert.assertEquals(Mirror.NONE, sprite.getMirror());

        sprite.setMirror(Mirror.HORIZONTAL);

        Assert.assertEquals(Mirror.HORIZONTAL, sprite.getMirror());
    }

    /**
     * Test the mirror <code>null</code>.
     */
    @Test(expected = LionEngineException.class)
    public void testMirrorNull()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.setMirror(null);
    }

    /**
     * Test the origin <code>null</code>.
     */
    @Test
    public void testRenderingPoint()
    {
        final SpriteFontImpl sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.setLocation(5.0, 10.0);
        sprite.setOrigin(Origin.TOP_LEFT);

        sprite.setOrigin(Origin.MIDDLE);
    }

    /**
     * Test the origin <code>null</code>.
     */
    @Test(expected = LionEngineException.class)
    public void testSetOriginNull()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.setOrigin(null);
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
            final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
            sprite.render(g);

            sprite.setMirror(Mirror.HORIZONTAL);
            sprite.setAlign(Align.RIGHT);
            sprite.setText("az%");
            sprite.setLocation(1.5, 2.5);
            sprite.render(g);

            sprite.setMirror(Mirror.VERTICAL);
            sprite.setLineHeight(5);
            sprite.render(g);
        }
        finally
        {
            g.dispose();
        }
    }

    /**
     * Test the draw.
     */
    @Test
    public void testDraw()
    {
        final Graphic g = Graphics.createImageBuffer(100, 100).createGraphic();
        try
        {
            final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
            sprite.load();
            sprite.draw(g, 1, 2, Align.CENTER, "az%");
        }
        finally
        {
            g.dispose();
        }
    }

    /**
     * Test the get text height.
     */
    @Test
    public void testGetTextHeight()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();

        Assert.assertEquals(8, sprite.getTextHeight("az"));
        Assert.assertEquals(16, sprite.getTextHeight("az%az"));
    }

    /**
     * Test the equals.
     */
    @Test
    public void testEquals()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();

        Assert.assertEquals(sprite, sprite);

        Assert.assertNotEquals(sprite, new SpriteFontImpl(media, font, 6, 7));
        Assert.assertNotEquals(sprite, null);
        Assert.assertNotEquals(sprite, new Object());
        Assert.assertNotEquals(sprite, new SpriteFontImpl(media, font, 1, 7));
        Assert.assertNotEquals(sprite, new SpriteFontImpl(media, font, 6, 1));
        Assert.assertNotEquals(sprite, new SpriteFontImpl(media, font, 1, 1));
    }

    /**
     * Test the hash.
     */
    @Test
    public void testHash()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        final int code = sprite.hashCode();

        Assert.assertNotEquals(code, new SpriteFontImpl(media, font, 6, 7).hashCode());
        Assert.assertNotEquals(code, new Object().hashCode());
        Assert.assertNotEquals(code, new SpriteFontImpl(media, Medias.create("fontdata2.xml"), 1, 7).hashCode());
        Assert.assertNotEquals(code, new SpriteFontImpl(media, font, 1, 7).hashCode());
        Assert.assertNotEquals(code, new SpriteFontImpl(media, font, 6, 1).hashCode());
        Assert.assertNotEquals(code, new SpriteFontImpl(media, font, 1, 1).hashCode());
    }
}
