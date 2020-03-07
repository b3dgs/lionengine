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
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
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
 * Test {@link SpriteDigit}.
 */
public final class SpriteDigitTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(SpriteDigitTest.class);
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
        assertThrows(() -> new SpriteDigitImpl((Media) null, 0, 0, 1), "Unexpected null argument !");
    }

    /**
     * Test constructor with <code>null</code> surface.
     */
    @Test
    public void testConstructorSurfaceNull()
    {
        assertThrows(() -> new SpriteDigitImpl((ImageBuffer) null, 0, 0, 1), "Unexpected null argument !");
    }

    /**
     * Test constructor with media.
     */
    @Test
    public void testConstructorMedia()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Medias.create("image.png"), 1, 1, 1);

        assertTrue(sprite.isLoaded());
        assertNotNull(sprite.getSurface());
        assertEquals(1, sprite.getWidth());
        assertEquals(1, sprite.getHeight());
    }

    /**
     * Test constructor with media.
     */
    @Test
    public void testConstructorInvalidDigit()
    {
        assertThrows(() -> new SpriteDigitImpl(Medias.create("image.png"), 1, 1, 0),
                     "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test constructor with surface.
     */
    @Test
    public void testConstructorSurface()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final SpriteDigit sprite = new SpriteDigitImpl(surface, 1, 1, 1);

        assertTrue(sprite.isLoaded());
        assertEquals(surface, sprite.getSurface());
        assertEquals(1, sprite.getWidth());
        assertEquals(1, sprite.getHeight());
    }

    /**
     * Test load with media.
     */
    @Test
    public void testLoadMedia()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Medias.create("image.png"), 1, 1, 1);

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
        final SpriteDigit sprite = new SpriteDigitImpl(media, 1, 1, 1);

        sprite.load();
    }

    /**
     * Test load with surface.
     */
    @Test
    public void testLoadSurface()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);

        sprite.load();
        sprite.setOrigin(Origin.MIDDLE);
    }

    /**
     * Test set value.
     */
    @Test
    public void testSetValueTen()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);
        sprite.setValue(-1);
        sprite.setValue(0);
        sprite.setValue(1);
        sprite.setValue(10);
        sprite.setValue(100);
        sprite.setValue(1000);
    }

    /**
     * Test set value.
     */
    @Test
    public void testSetValueTenMore()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 3);
        sprite.setValue(-1);
        sprite.setValue(0);
        sprite.setValue(1);
        sprite.setValue(10);
        sprite.setValue(100);
        sprite.setValue(1000);
    }

    /**
     * Test stretch sprite.
     */
    @Test
    public void testStretch()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);
        sprite.stretch(100.0, 100.0);

        assertEquals(1, sprite.getWidth());
        assertEquals(1, sprite.getHeight());

        sprite.stretch(200.0, 100.0);

        assertEquals(2, sprite.getWidth());
        assertEquals(1, sprite.getHeight());

        sprite.stretch(100.0, 200.0);

        assertEquals(2, sprite.getWidth());
        assertEquals(2, sprite.getHeight());

        sprite.stretch(200.0, 200.0);

        assertEquals(4, sprite.getWidth());
        assertEquals(4, sprite.getHeight());
    }

    /**
     * Test stretch sprite with invalid width.
     */
    @Test
    public void testStretchInvalidWidth()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);

        assertThrows(() -> sprite.stretch(0.0, 100.0), "Invalid argument: 0.0 is not strictly superior to 0.0");
    }

    /**
     * Test stretch sprite with invalid height.
     */
    @Test
    public void testStretchInvalidHeight()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);

        assertThrows(() -> sprite.stretch(100, 0.0), "Invalid argument: 0.0 is not strictly superior to 0.0");
    }

    /**
     * Test rotate sprite.
     */
    @Test
    public void testRotate()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);
        for (int angle = -720; angle < 720; angle++)
        {
            sprite.rotate(angle);

            assertTrue(sprite.getWidth() >= 1, angle + Constant.SPACE + sprite.getWidth());
            assertTrue(sprite.getHeight() >= 1, angle + Constant.SPACE + sprite.getHeight());
        }
    }

    /**
     * Test set location.
     */
    @Test
    public void testSetLocation()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);

        assertEquals(0.0, sprite.getX());
        assertEquals(0.0, sprite.getY());

        sprite.setLocation(1.5, 2.5);

        assertEquals(1.5, sprite.getX());
        assertEquals(2.5, sprite.getY());
    }

    /**
     * Test set location with viewer.
     */
    @Test
    public void testSetLocationViewer()
    {
        final SpriteDigitImpl sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);
        final ViewerMock viewer = new ViewerMock();
        sprite.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        assertEquals(1.5, sprite.getX());
        assertEquals(237.5, sprite.getY());

        viewer.set(10, 20);
        sprite.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        assertEquals(-8.5, sprite.getX());
        assertEquals(257.5, sprite.getY());
    }

    /**
     * Test set alpha.
     */
    @Test
    public void testSetAlpha()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);
        for (int alpha = 0; alpha < 256; alpha++)
        {
            sprite.setAlpha(alpha);

            assertEquals(1, sprite.getWidth());
            assertEquals(1, sprite.getHeight());
        }
    }

    /**
     * Test set alpha too low.
     */
    @Test
    public void testSetAlphaLow()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);

        assertThrows(() -> sprite.setAlpha(-1), "Invalid argument: -1 is not superior or equal to 0");
    }

    /**
     * Test set alpha too high.
     */
    @Test
    public void testSetAlphaHigh()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);

        assertThrows(() -> sprite.setAlpha(256), "Invalid argument: 256 is not inferior or equal to 255");
    }

    /**
     * Test set transparency.
     */
    @Test
    public void testSetTransparency()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);
        sprite.setTransparency(ColorRgba.BLACK);

        assertEquals(1, sprite.getWidth());
        assertEquals(1, sprite.getHeight());
    }

    /**
     * Test set fade.
     */
    @Test
    public void testSetFade()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);
        sprite.setFade(128, 128);
        sprite.setFade(128, 128);

        assertEquals(1, sprite.getWidth());
        assertEquals(1, sprite.getHeight());
    }

    /**
     * Test filter bilinear.
     */
    @Test
    public void testFilterBilinear()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);
        sprite.filter(new FilterBilinear());

        assertEquals(1, sprite.getWidth());
        assertEquals(1, sprite.getHeight());
    }

    /**
     * Test filter <code>null</code>.
     */
    @Test
    public void testFilterNull()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);

        assertThrows(() -> sprite.filter(null), "Unexpected null argument !");
    }

    /**
     * Test the mirror.
     */
    @Test
    public void testMirror()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);

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
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);

        assertThrows(() -> sprite.setMirror(null), "Unexpected null argument !");
    }

    /**
     * Test origin <code>null</code>.
     */
    @Test
    public void testSetOriginNull()
    {
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);

        assertThrows(() -> sprite.setOrigin(null), "Unexpected null argument !");
    }

    /**
     * Test render.
     */
    @Test
    public void testRender()
    {
        final Graphic g = Graphics.createImageBuffer(100, 100).createGraphic();
        final SpriteDigit sprite = new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1);
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
        final SpriteDigit sprite = new SpriteDigitImpl(surface, 1, 1, 1);
        final Media media = Medias.create("image.png");
        final SpriteDigit spriteMedia = new SpriteDigitImpl(media, 1, 1, 1);

        assertEquals(sprite, sprite);
        assertEquals(sprite, new SpriteDigitImpl(surface, 1, 1, 1));
        assertEquals(spriteMedia, spriteMedia);

        assertNotEquals(sprite, null);
        assertNotEquals(sprite, new Object());
        assertNotEquals(sprite, new SpriteDigitImpl(media, 1, 1, 1));
        assertNotEquals(spriteMedia, new SpriteDigitImpl(media, 1, 1, 1));
        assertNotEquals(sprite, new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1));
        assertNotEquals(sprite, new SpriteDigitImpl(Graphics.createImageBuffer(32, 32), 1, 1, 1));
        assertNotEquals(sprite, new SpriteDigitImpl(Graphics.createImageBuffer(64, 64), 1, 1, 1));
        assertNotEquals(sprite, new SpriteDigitImpl(Graphics.createImageBuffer(32, 64), 1, 1, 1));

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
        final SpriteDigit sprite = new SpriteDigitImpl(surface, 1, 1, 1);
        final Media media = Medias.create("image.png");
        final SpriteDigit spriteMedia = new SpriteDigitImpl(media, 1, 1, 1);

        assertHashEquals(sprite, new SpriteDigitImpl(surface, 1, 1, 1));

        assertHashNotEquals(sprite, new Object());
        assertHashNotEquals(spriteMedia, new SpriteDigitImpl(media, 1, 1, 1));
        assertHashNotEquals(sprite, new SpriteDigitImpl(Graphics.createImageBuffer(64, 32), 1, 1, 1));
        assertHashNotEquals(sprite, new SpriteDigitImpl(Graphics.createImageBuffer(32, 32), 1, 1, 1));
        assertHashNotEquals(sprite, new SpriteDigitImpl(Graphics.createImageBuffer(64, 64), 1, 1, 1));
        assertHashNotEquals(sprite, new SpriteDigitImpl(Graphics.createImageBuffer(32, 64), 1, 1, 1));

        sprite.dispose();
        spriteMedia.dispose();
        surface.dispose();
    }
}
