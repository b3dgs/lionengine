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
package com.b3dgs.lionengine.graphic.drawable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.filter.FilterBilinear;

/**
 * Test {@link Sprite}.
 */
public final class SpriteTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(SpriteTest.class);
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Graphics.setFactoryGraphic(null);
        Medias.setLoadFromJar(null);
    }

    /**
     * Test constructor with <code>null</code> media.
     */
    @Test
    public void testConstructorMediaNull()
    {
        assertThrows(() -> new SpriteImpl((Media) null), "Unexpected null argument !");
    }

    /**
     * Test constructor with <code>null</code> surface.
     */
    @Test
    public void testConstructorSurfaceNull()
    {
        assertThrows(() -> new SpriteImpl((ImageBuffer) null), "Unexpected null argument !");
    }

    /**
     * Test constructor with media.
     */
    @Test
    public void testConstructorMedia()
    {
        final Sprite sprite = new SpriteImpl(Medias.create("image.png"));

        assertFalse(sprite.isLoaded());
        assertNull(sprite.getSurface());
        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
    }

    /**
     * Test constructor with surface.
     */
    @Test
    public void testConstructorSurface()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final Sprite sprite = new SpriteImpl(surface);

        assertTrue(sprite.isLoaded());
        assertEquals(surface, sprite.getSurface());
        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
    }

    /**
     * Test load with media.
     */
    @Test
    public void testLoadMedia()
    {
        final Sprite sprite = new SpriteImpl(Medias.create("image.png"));
        sprite.dispose();
        sprite.load();

        assertNotNull(sprite.getSurface());

        sprite.prepare();
        sprite.dispose();
    }

    /**
     * Test load with media already loaded.
     */
    @Test
    public void testLoadMediaAlready()
    {
        final Media media = Medias.create("image.png");
        final Sprite sprite = new SpriteImpl(media);
        sprite.load();

        assertThrows(() -> sprite.load(), "[" + media + "] " + SpriteImpl.ERROR_ALREADY_LOADED);
    }

    /**
     * Test load with surface.
     */
    @Test
    public void testLoadSurface()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));

        assertThrows(() -> sprite.load(), SpriteImpl.ERROR_ALREADY_LOADED);
    }

    /**
     * Test stretch sprite.
     */
    @Test
    public void testStretch()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.stretch(100.0, 100.0);

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());

        sprite.stretch(200.0, 100.0);

        assertEquals(128, sprite.getWidth());
        assertEquals(32, sprite.getHeight());

        sprite.stretch(100.0, 200.0);

        assertEquals(128, sprite.getWidth());
        assertEquals(64, sprite.getHeight());

        sprite.stretch(200.0, 200.0);

        assertEquals(256, sprite.getWidth());
        assertEquals(128, sprite.getHeight());
    }

    /**
     * Test stretch sprite with invalid width.
     */
    @Test
    public void testStretchInvalidWidth()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));

        assertThrows(() -> sprite.stretch(0.0, 100.0), "Invalid argument: 0.0 is not strictly superior to 0.0");
    }

    /**
     * Test stretch sprite with invalid height.
     */
    @Test
    public void testStretchInvalidHeight()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));

        assertThrows(() -> sprite.stretch(100, 0.0), "Invalid argument: 0.0 is not strictly superior to 0.0");
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

            assertTrue(sprite.getWidth() == 64, angle + Constant.SPACE + sprite.getWidth());
            assertTrue(sprite.getHeight() == 32, angle + Constant.SPACE + sprite.getHeight());
        }
    }

    /**
     * Test set location.
     */
    @Test
    public void testSetLocation()
    {
        final SpriteImpl sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));

        assertEquals(0.0, sprite.getX());
        assertEquals(0.0, sprite.getY());
        assertEquals(0, sprite.getRenderX());
        assertEquals(0, sprite.getRenderY());

        sprite.setLocation(1.5, 2.5);

        assertEquals(1.5, sprite.getX());
        assertEquals(2.5, sprite.getY());
        assertEquals(2, sprite.getRenderX());
        assertEquals(3, sprite.getRenderY());
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

        assertEquals(1.5, sprite.getX());
        assertEquals(237.5, sprite.getY());
        assertEquals(2, sprite.getRenderX());
        assertEquals(238, sprite.getRenderY());

        viewer.set(10, 20);
        sprite.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        assertEquals(-8.5, sprite.getX());
        assertEquals(257.5, sprite.getY());
        assertEquals(-8, sprite.getRenderX());
        assertEquals(258, sprite.getRenderY());
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

            assertEquals(64, sprite.getWidth());
            assertEquals(32, sprite.getHeight());
        }
    }

    /**
     * Test set alpha too low.
     */
    @Test
    public void testSetAlphaLow()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));

        assertThrows(() -> sprite.setAlpha(-1), "Invalid argument: -1 is not superior or equal to 0");
    }

    /**
     * Test set alpha too high.
     */
    @Test
    public void testSetAlphaHigh()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));

        assertThrows(() -> sprite.setAlpha(256), "Invalid argument: 256 is not inferior or equal to 255");
    }

    /**
     * Test set transparency.
     */
    @Test
    public void testSetTransparency()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.setTransparency(ColorRgba.BLACK);

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
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

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
    }

    /**
     * Test set frame offset.
     */
    @Test
    public void testSetFrameOffset()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.setFrameOffsets(1, -1);
    }

    /**
     * Test filter bilinear.
     */
    @Test
    public void testFilterBilinear()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.filter(new FilterBilinear());

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
    }

    /**
     * Test filter <code>null</code>.
     */
    @Test
    public void testFilterNull()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));

        assertThrows(() -> sprite.filter(null), "Unexpected null argument !");
    }

    /**
     * Test the mirror.
     */
    @Test
    public void testMirror()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));

        assertEquals(Mirror.NONE, sprite.getMirror());

        sprite.setMirror(Mirror.HORIZONTAL);

        assertEquals(Mirror.HORIZONTAL, sprite.getMirror());
    }

    /**
     * Test mirror <code>null</code>.
     */
    @Test
    public void testMirrorNull()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));

        assertThrows(() -> sprite.setMirror(null), "Unexpected null argument !");
    }

    /**
     * Test rendering point.
     */
    @Test
    public void testRenderingPoint()
    {
        final SpriteImpl sprite = new SpriteImpl(Graphics.createImageBuffer(10, 20));
        sprite.setOrigin(Origin.TOP_LEFT);
        sprite.setLocation(5.0, 10.0);

        assertEquals(5, sprite.getRenderX());
        assertEquals(10, sprite.getRenderY());

        sprite.setOrigin(Origin.MIDDLE);
        sprite.setLocation(5.0, 10.0);

        assertEquals(0, sprite.getRenderX());
        assertEquals(0, sprite.getRenderY());

        sprite.rotate(1);
        sprite.setMirror(Mirror.HORIZONTAL);
        sprite.setLocation(5.0, 10.0);

        assertEquals(0, sprite.getRenderX());
        assertEquals(0, sprite.getRenderY());

        sprite.rotate(1);
        sprite.setMirror(Mirror.VERTICAL);
        sprite.setLocation(5.0, 10.0);

        assertEquals(0, sprite.getRenderX());
        assertEquals(0, sprite.getRenderY());

        sprite.rotate(1);
        sprite.setMirror(Mirror.NONE);
        sprite.setLocation(5.0, 10.0);

        assertEquals(0, sprite.getRenderX());
        assertEquals(0, sprite.getRenderY());
    }

    /**
     * Test origin <code>null</code>.
     */
    @Test
    public void testSetOriginNull()
    {
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));

        assertThrows(() -> sprite.setOrigin(null), "Unexpected null argument !");
    }

    /**
     * Test render.
     */
    @Test
    public void testRender()
    {
        final Graphic g = Graphics.createImageBuffer(100, 100).createGraphic();
        final Sprite sprite = new SpriteImpl(Graphics.createImageBuffer(64, 32));
        sprite.render(g);

        sprite.setMirror(Mirror.HORIZONTAL);
        sprite.render(g);

        sprite.setMirror(Mirror.VERTICAL);
        sprite.render(g);

        g.dispose();
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final Sprite sprite = new SpriteImpl(surface);
        final Media media = Medias.create("image.png");
        final Sprite spriteMedia = new SpriteImpl(media);
        spriteMedia.load();

        assertEquals(sprite, sprite);
        assertEquals(sprite, new SpriteImpl(surface));
        assertEquals(spriteMedia, spriteMedia);

        assertNotEquals(sprite, null);
        assertNotEquals(sprite, new Object());
        assertNotEquals(sprite, new SpriteImpl(media));
        assertNotEquals(spriteMedia, new SpriteImpl(media));
        assertNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(64, 32)));
        assertNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(32, 32)));
        assertNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(64, 64)));
        assertNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(32, 64)));

        sprite.dispose();
        spriteMedia.dispose();
        surface.dispose();
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final Sprite sprite = new SpriteImpl(surface);
        final Media media = Medias.create("image.png");
        final Sprite spriteMedia = new SpriteImpl(media);
        spriteMedia.load();

        assertHashEquals(sprite, new SpriteImpl(surface));

        assertHashNotEquals(sprite, new Object());
        assertHashNotEquals(spriteMedia, new SpriteImpl(media));
        assertHashNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(64, 32)));
        assertHashNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(32, 32)));
        assertHashNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(64, 64)));
        assertHashNotEquals(sprite, new SpriteImpl(Graphics.createImageBuffer(32, 64)));

        sprite.dispose();
        spriteMedia.dispose();
        surface.dispose();
    }
}
