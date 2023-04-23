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
 * Test {@link SpriteTiled}.
 */
final class SpriteTiledTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(SpriteTiledTest.class);
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
     * Test constructor with invalid tile width.
     */
    @Test
    void testConstructorInvalidTileWidth()
    {
        assertThrows(() -> new SpriteTiledImpl(Medias.create("image.png"), 0, 1),
                     "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test constructor with invalid tile height.
     */
    @Test
    void testConstructorInvalidTileHeight()
    {
        assertThrows(() -> new SpriteTiledImpl(Medias.create("image.png"), 1, 0),
                     "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test constructor with media.
     */
    @Test
    void testConstructorMedia()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Medias.create("image.png"), 16, 8);

        assertFalse(sprite.isLoaded());
        assertNull(sprite.getSurface());
        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
        assertEquals(16, sprite.getTileWidth());
        assertEquals(8, sprite.getTileHeight());
        assertEquals(4, sprite.getTilesHorizontal());
        assertEquals(4, sprite.getTilesVertical());
    }

    /**
     * Test constructor with surface.
     */
    @Test
    void testConstructorSurface()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final SpriteTiled sprite = new SpriteTiledImpl(surface, 16, 8);

        assertTrue(sprite.isLoaded());
        assertEquals(surface, sprite.getSurface());
        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
        assertEquals(16, sprite.getTileWidth());
        assertEquals(8, sprite.getTileHeight());
        assertEquals(4, sprite.getTilesHorizontal());
        assertEquals(4, sprite.getTilesVertical());
    }

    /**
     * Test load with media.
     */
    @Test
    void testLoadMedia()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Medias.create("image.png"), 16, 8);
        sprite.load();

        assertNotNull(sprite.getSurface());

        sprite.prepare();
        sprite.dispose();
    }

    /**
     * Test load with media already loaded.
     */
    @Test
    void testLoadMediaAlready()
    {
        final Media media = Medias.create("image.png");
        final SpriteTiled sprite = new SpriteTiledImpl(media, 16, 8);
        sprite.load();

        assertThrows(() -> sprite.load(), "[" + media + "] " + SpriteImpl.ERROR_ALREADY_LOADED);
    }

    /**
     * Test load with surface.
     */
    @Test
    void testLoadSurface()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.load(), SpriteImpl.ERROR_ALREADY_LOADED);
    }

    /**
     * Test stretch sprite.
     */
    @Test
    void testStretch()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.stretch(100.0, 100.0);

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
        assertEquals(16, sprite.getTileWidth());
        assertEquals(8, sprite.getTileHeight());

        sprite.stretch(200.0, 100.0);

        assertEquals(128, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
        assertEquals(32, sprite.getTileWidth());
        assertEquals(8, sprite.getTileHeight());

        sprite.stretch(100.0, 200.0);

        assertEquals(64, sprite.getWidth());
        assertEquals(64, sprite.getHeight());
        assertEquals(16, sprite.getTileWidth());
        assertEquals(16, sprite.getTileHeight());

        sprite.stretch(200.0, 200.0);

        assertEquals(128, sprite.getWidth());
        assertEquals(64, sprite.getHeight());
        assertEquals(32, sprite.getTileWidth());
        assertEquals(16, sprite.getTileHeight());
    }

    /**
     * Test stretch sprite with invalid width.
     */
    @Test
    void testStretchInvalidWidth()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.stretch(0.0, 100.0), "Invalid argument: 0.0 is not strictly superior to 0.0");
    }

    /**
     * Test stretch sprite with invalid height.
     */
    @Test
    void testStretchInvalidHeight()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.stretch(100, 0.0), "Invalid argument: 0.0 is not strictly superior to 0.0");
    }

    /**
     * Test rotate sprite.
     */
    @Test
    void testRotate()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        for (int angle = -720; angle < 720; angle++)
        {
            sprite.setAngleAnchor(1, 2);
            sprite.rotate(angle);

            assertTrue(sprite.getWidth() == 64, angle + Constant.SPACE + sprite.getWidth());
            assertTrue(sprite.getHeight() == 32, angle + Constant.SPACE + sprite.getHeight());
        }
    }

    /**
     * Test set location.
     */
    @Test
    void testSetLocation()
    {
        final SpriteTiledImpl sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);

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
    void testSetLocationViewer()
    {
        final SpriteTiledImpl sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);
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
    void testSetAlpha()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);
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
    void testSetAlphaLow()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.setAlpha(-1), "Invalid argument: -1 is not superior or equal to 0");
    }

    /**
     * Test set alpha too high.
     */
    @Test
    void testSetAlphaHigh()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.setAlpha(256), "Invalid argument: 256 is not inferior or equal to 255");
    }

    /**
     * Test set transparency.
     */
    @Test
    void testSetTransparency()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.setTransparency(ColorRgba.BLACK);

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
    }

    /**
     * Test set frame offset.
     */
    @Test
    void testSetFrameOffset()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 18);
        sprite.setFrameOffsets(1, -1);
    }

    /**
     * Test filter bilinear.
     */
    @Test
    void testFilterBilinear()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.filter(new FilterBilinear());

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
    }

    /**
     * Test filter <code>null</code>.
     */
    @Test
    void testFilterNull()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.filter(null), "Unexpected null argument !");
    }

    /**
     * Test mirror.
     */
    @Test
    void testMirror()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertEquals(Mirror.NONE, sprite.getMirror());

        sprite.setMirror(Mirror.HORIZONTAL);

        assertEquals(Mirror.HORIZONTAL, sprite.getMirror());
    }

    /**
     * Test mirror <code>null</code>.
     */
    @Test
    void testMirrorNull()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.setMirror(null), "Unexpected null argument !");
    }

    /**
     * Test rendering point.
     */
    @Test
    void testRenderingPoint()
    {
        final SpriteTiledImpl sprite = new SpriteTiledImpl(Graphics.createImageBuffer(10, 20), 10, 20);
        sprite.setLocation(5.0, 10.0);
        sprite.setOrigin(Origin.TOP_LEFT);

        assertEquals(5, sprite.getRenderX());
        assertEquals(10, sprite.getRenderY());

        sprite.setOrigin(Origin.MIDDLE);

        assertEquals(0, sprite.getRenderX());
        assertEquals(0, sprite.getRenderY());
    }

    /**
     * Test origin <code>null</code>.
     */
    @Test
    void testSetOriginNull()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.setOrigin(null), "Unexpected null argument !");
    }

    /**
     * Test set tile with invalid value.
     */
    @Test
    void testSetTileInvalid()
    {
        final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.setTile(-1), "Invalid argument: -1 is not superior or equal to 0");
    }

    /**
     * Test render.
     */
    @Test
    void testRender()
    {
        final Graphic g = Graphics.createImageBuffer(100, 100).createGraphic();
        try
        {
            final SpriteTiled sprite = new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8);
            sprite.render(g);
            sprite.setTile(0);

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
    void testEquals()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final SpriteTiled sprite = new SpriteTiledImpl(surface, 16, 8);
        final Media media = Medias.create("image.png");
        final SpriteTiled spriteMedia = new SpriteTiledImpl(media, 16, 8);
        spriteMedia.load();

        assertEquals(sprite, sprite);
        assertEquals(sprite, new SpriteTiledImpl(surface, 16, 8));
        assertEquals(spriteMedia, spriteMedia);

        assertNotEquals(sprite, null);
        assertNotEquals(sprite, new Object());
        assertNotEquals(sprite, new SpriteTiledImpl(media, 16, 8));
        assertNotEquals(spriteMedia, new SpriteTiledImpl(media, 16, 8));
        assertNotEquals(sprite, new SpriteTiledImpl(surface, 16, 16));
        assertNotEquals(sprite, new SpriteTiledImpl(surface, 8, 16));
        assertNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8));
        assertNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(32, 32), 16, 8));
        assertNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(64, 64), 16, 8));
        assertNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(32, 64), 16, 8));
        assertNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 8, 8));
        assertNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 16));
        assertNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(32, 32), 8, 8));
        assertNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(64, 64), 16, 16));

        sprite.dispose();
        spriteMedia.dispose();
        surface.dispose();
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final SpriteTiled sprite = new SpriteTiledImpl(surface, 16, 8);
        final Media media = Medias.create("image.png");
        final SpriteTiled spriteMedia = new SpriteTiledImpl(media, 16, 8);
        spriteMedia.load();

        assertHashEquals(sprite, new SpriteTiledImpl(surface, 16, 8));

        assertHashNotEquals(sprite, new Object());
        assertHashNotEquals(spriteMedia, new SpriteTiledImpl(media, 16, 8));
        assertHashNotEquals(sprite, new SpriteTiledImpl(surface, 16, 16));
        assertHashNotEquals(sprite, new SpriteTiledImpl(surface, 8, 16));
        assertHashNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 8));
        assertHashNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(32, 32), 16, 8));
        assertHashNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(64, 64), 16, 8));
        assertHashNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(32, 64), 16, 8));
        assertHashNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 8, 8));
        assertHashNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(64, 32), 16, 16));
        assertHashNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(32, 32), 8, 8));
        assertHashNotEquals(sprite, new SpriteTiledImpl(Graphics.createImageBuffer(64, 64), 16, 16));

        sprite.dispose();
        spriteMedia.dispose();
        surface.dispose();
    }
}
