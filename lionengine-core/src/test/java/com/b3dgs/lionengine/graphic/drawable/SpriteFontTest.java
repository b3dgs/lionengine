/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.FactoryMediaDefault;
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
import com.b3dgs.lionengine.graphic.filter.FilterBilinear;

/**
 * Test {@link SpriteFont}.
 */
public final class SpriteFontTest
{
    /** Image media. */
    private static Media media;
    /** Font media. */
    private static Media font;

    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Medias.setLoadFromJar(SpriteFontTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());

        media = Medias.create("image.png");
        font = Medias.create("fontdata.xml");
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
        assertThrows(() -> new SpriteFontImpl((Media) null, font, 1, 1), "Unexpected null argument !");
    }

    /**
     * Test constructor with <code>null</code> font.
     */
    @Test
    public void testConstructorFontNull()
    {
        assertThrows(() -> new SpriteFontImpl(media, (Media) null, 1, 1), "Unexpected null argument !");
    }

    /**
     * Test constructor with invalid letter width.
     */
    @Test
    public void testConstructorInvalidLetterWidth()
    {
        assertThrows(() -> new SpriteFontImpl(media, font, 0, 1).load(),
                     "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test constructor with invalid letter height.
     */
    @Test
    public void testConstructorInvalidLetterHeight()
    {
        assertThrows(() -> new SpriteFontImpl(media, font, 1, 0).load(),
                     "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test constructor with media.
     */
    @Test
    public void testConstructorMedia()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);

        assertFalse(sprite.isLoaded());
        assertNull(sprite.getSurface());

        sprite.load();
        sprite.prepare();

        assertTrue(sprite.isLoaded());
        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());

        sprite.dispose();
    }

    /**
     * Test load with media.
     */
    @Test
    public void testLoadMedia()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
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
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();

        assertThrows(() -> sprite.load(), "[" + media + "] " + SpriteImpl.ERROR_ALREADY_LOADED);

        sprite.dispose();
    }

    /**
     * Test load with surface.
     */
    @Test
    public void testLoadSurfaceAlready()
    {
        final SpriteFont sprite = new SpriteFontImpl(Graphics.createImageBuffer(32, 16), font, 6, 7);

        assertThrows(() -> sprite.load(), SpriteImpl.ERROR_ALREADY_LOADED);

        sprite.dispose();
    }

    /**
     * Test stretch sprite.
     */
    @Test
    public void testStretch()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.stretch(100.0, 100.0);

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());

        sprite.stretch(200.0, 100.0);

        assertEquals(130, sprite.getWidth());
        assertEquals(32, sprite.getHeight());

        sprite.stretch(100.0, 200.0);

        assertEquals(130, sprite.getWidth());
        assertEquals(64, sprite.getHeight());

        sprite.stretch(200.0, 200.0);

        assertEquals(260, sprite.getWidth());
        assertEquals(128, sprite.getHeight());

        sprite.dispose();
    }

    /**
     * Test stretch sprite with invalid width.
     */
    @Test
    public void testStretchInvalidWidth()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.prepare();

        assertThrows(() -> sprite.stretch(0.0, 100.0), "Invalid argument: 0.0 is not strictly superior to 0.0");

        sprite.dispose();
    }

    /**
     * Test stretch sprite with invalid height.
     */
    @Test
    public void testStretchInvalidHeight()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.prepare();

        assertThrows(() -> sprite.stretch(100, 0.0), "Invalid argument: 0.0 is not strictly superior to 0.0");

        sprite.dispose();
    }

    /**
     * Test rotate sprite.
     */
    @Test
    public void testRotate()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        for (int angle = -720; angle < 720; angle++)
        {
            sprite.rotate(angle);

            assertTrue(sprite.getWidth() >= 64, angle + Constant.SPACE + sprite.getWidth());
            assertTrue(sprite.getHeight() >= 32, angle + Constant.SPACE + sprite.getHeight());
        }
        sprite.dispose();
    }

    /**
     * Test set location.
     */
    @Test
    public void testSetLocation()
    {
        final SpriteFontImpl sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.prepare();

        assertEquals(0.0, sprite.getX());
        assertEquals(0.0, sprite.getY());

        sprite.setLocation(1.5, 2.5);

        assertEquals(1.5, sprite.getX());
        assertEquals(2.5, sprite.getY());

        sprite.dispose();
    }

    /**
     * Test set location with viewer.
     */
    @Test
    public void testSetLocationViewer()
    {
        final SpriteFontImpl sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();

        final ViewerMock viewer = new ViewerMock();
        sprite.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        assertEquals(0.0, sprite.getX());
        assertEquals(0.0, sprite.getY());

        viewer.set(10, 20);
        sprite.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        assertEquals(0.0, sprite.getX());
        assertEquals(0.0, sprite.getY());

        sprite.dispose();
    }

    /**
     * Test set alpha.
     */
    @Test
    public void testSetAlpha()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        for (int alpha = 0; alpha < 256; alpha++)
        {
            sprite.setAlpha(alpha);

            assertEquals(64, sprite.getWidth());
            assertEquals(32, sprite.getHeight());
        }
        sprite.dispose();
    }

    /**
     * Test set alpha too low.
     */
    @Test
    public void testSetAlphaLow()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.prepare();

        assertThrows(() -> sprite.setAlpha(-1), "Invalid argument: -1 is not superior or equal to 0");

        sprite.dispose();
    }

    /**
     * Test set alpha too high.
     */
    @Test
    public void testSetAlphaHigh()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.prepare();

        assertThrows(() -> sprite.setAlpha(256), "Invalid argument: 256 is not inferior or equal to 255");

        sprite.dispose();
    }

    /**
     * Test set transparency.
     */
    @Test
    public void testSetTransparency()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.setTransparency(ColorRgba.BLACK);

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());

        sprite.dispose();
    }

    /**
     * Test set fade.
     */
    @Test
    public void testSetFade()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.setFade(128, 128);
        sprite.setFade(128, 128);

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());

        sprite.dispose();
    }

    /**
     * Test filter bilinear.
     */
    @Test
    public void testFilterBilinear()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.filter(new FilterBilinear());

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());

        sprite.dispose();
    }

    /**
     * Test filter <code>null</code>.
     */
    @Test
    public void testFilterNull()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.prepare();

        assertThrows(() -> sprite.filter(null), "Unexpected null argument !");

        sprite.dispose();
    }

    /**
     * Test mirror.
     */
    @Test
    public void testMirror()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.prepare();

        assertEquals(Mirror.NONE, sprite.getMirror());

        sprite.setMirror(Mirror.HORIZONTAL);

        assertEquals(Mirror.HORIZONTAL, sprite.getMirror());

        sprite.dispose();
    }

    /**
     * Test mirror <code>null</code>.
     */
    @Test
    public void testMirrorNull()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.prepare();

        assertThrows(() -> sprite.setMirror(null), "Unexpected null argument !");

        sprite.dispose();
    }

    /**
     * Test rendering point.
     */
    @Test
    public void testRenderingPoint()
    {
        final SpriteFontImpl sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.prepare();
        sprite.setLocation(5.0, 10.0);
        sprite.setOrigin(Origin.TOP_LEFT);
        sprite.setOrigin(Origin.MIDDLE);

        sprite.dispose();
    }

    /**
     * Test origin <code>null</code>.
     */
    @Test
    public void testSetOriginNull()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.prepare();

        assertThrows(() -> sprite.setOrigin(null), "Unexpected null argument !");

        sprite.dispose();
    }

    /**
     * Test render.
     */
    @Test
    public void testRender()
    {
        final Graphic g = Graphics.createImageBuffer(100, 100).createGraphic();
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.prepare();
        sprite.render(g);

        sprite.setMirror(Mirror.HORIZONTAL);
        sprite.setAlign(Align.RIGHT);
        sprite.setText("az%");
        sprite.setLocation(1.5, 2.5);
        sprite.render(g);

        sprite.setMirror(Mirror.VERTICAL);
        sprite.setLineHeight(5);
        sprite.render(g);

        g.dispose();
        sprite.dispose();
    }

    /**
     * Test draw.
     */
    @Test
    public void testDraw()
    {
        final Graphic g = Graphics.createImageBuffer(100, 100).createGraphic();
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();
        sprite.draw(g, 1, 2, Align.CENTER, "az%");

        g.dispose();
        sprite.dispose();
    }

    /**
     * Test get text height.
     */
    @Test
    public void testGetTextHeight()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();

        assertEquals(7, sprite.getTextHeight("az"));
        assertEquals(14, sprite.getTextHeight("az%az"));

        sprite.dispose();
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();

        assertEquals(sprite, sprite);

        assertNotEquals(sprite, new SpriteFontImpl(media, font, 6, 7));
        assertNotEquals(sprite, null);
        assertNotEquals(sprite, new Object());
        assertNotEquals(sprite, new SpriteFontImpl(media, font, 1, 7));
        assertNotEquals(sprite, new SpriteFontImpl(media, font, 6, 1));
        assertNotEquals(sprite, new SpriteFontImpl(media, font, 1, 1));

        sprite.dispose();
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final SpriteFont sprite = new SpriteFontImpl(media, font, 6, 7);
        sprite.load();

        assertHashNotEquals(sprite, new SpriteFontImpl(media, font, 6, 7));
        assertHashNotEquals(sprite, new Object());
        assertHashNotEquals(sprite, new SpriteFontImpl(media, Medias.create("fontdata2.xml"), 1, 7));
        assertHashNotEquals(sprite, new SpriteFontImpl(media, font, 1, 7));
        assertHashNotEquals(sprite, new SpriteFontImpl(media, font, 6, 1));
        assertHashNotEquals(sprite, new SpriteFontImpl(media, font, 1, 1));

        sprite.dispose();
    }
}
