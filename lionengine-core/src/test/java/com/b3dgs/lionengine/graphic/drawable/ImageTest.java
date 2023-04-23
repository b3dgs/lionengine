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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Test {@link Image}.
 */
final class ImageTest
{
    /** Image media. */
    private static Media media;

    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(ImageTest.class);

        media = Medias.create("image.png");
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
    void testConstructorMediaNull()
    {
        assertThrows(() -> new ImageImpl((Media) null), "Unexpected null argument !");
    }

    /**
     * Test constructor with <code>null</code> surface.
     */
    @Test
    void testConstructorSurfaceNull()
    {
        assertThrows(() -> new ImageImpl((ImageBuffer) null), "Unexpected null argument !");
    }

    /**
     * Test constructor with media.
     */
    @Test
    void testConstructorMedia()
    {
        final Image image = new ImageImpl(media);

        assertFalse(image.isLoaded());
        assertNull(image.getSurface());
        assertEquals(64, image.getWidth());
        assertEquals(32, image.getHeight());

        image.dispose();
    }

    /**
     * Test constructor with surface.
     */
    @Test
    void testConstructorSurface()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final Image image = new ImageImpl(surface);

        assertTrue(image.isLoaded());
        assertEquals(surface, image.getSurface());
        assertEquals(64, image.getWidth());
        assertEquals(32, image.getHeight());

        image.dispose();
    }

    /**
     * Test load with media.
     */
    @Test
    void testLoadMedia()
    {
        final Image image = new ImageImpl(media);
        image.load();

        assertNotNull(image.getSurface());

        image.prepare();
        image.dispose();
    }

    /**
     * Test load with media already loaded.
     */
    @Test
    void testLoadMediaAlready()
    {
        final Image image = new ImageImpl(media);
        image.load();

        assertThrows(() -> image.load(), "[" + media + "] " + ImageImpl.ERROR_ALREADY_LOADED);

        image.dispose();
    }

    /**
     * Test load with surface.
     */
    @Test
    void testLoadSurface()
    {
        final Image image = new ImageImpl(Graphics.createImageBuffer(64, 32));

        assertThrows(() -> image.load(), ImageImpl.ERROR_ALREADY_LOADED);

        image.dispose();
    }

    /**
     * Test set location.
     */
    @Test
    void testSetLocation()
    {
        final ImageImpl image = new ImageImpl(Graphics.createImageBuffer(64, 32));

        assertEquals(0.0, image.getX());
        assertEquals(0.0, image.getY());
        assertEquals(0, image.getRenderX());
        assertEquals(0, image.getRenderY());

        image.setLocation(1.5, 2.5);

        assertEquals(1.5, image.getX());
        assertEquals(2.5, image.getY());
        assertEquals(2, image.getRenderX());
        assertEquals(3, image.getRenderY());

        image.dispose();
    }

    /**
     * Test set location with viewer.
     */
    @Test
    void testSetLocationViewer()
    {
        final ImageImpl image = new ImageImpl(Graphics.createImageBuffer(64, 32));
        final ViewerMock viewer = new ViewerMock();
        image.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        assertEquals(1.5, image.getX());
        assertEquals(237.5, image.getY());
        assertEquals(2, image.getRenderX());
        assertEquals(238, image.getRenderY());

        viewer.set(10, 20);
        image.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        assertEquals(-8.5, image.getX());
        assertEquals(257.5, image.getY());
        assertEquals(-8, image.getRenderX());
        assertEquals(258, image.getRenderY());

        image.dispose();
    }

    /**
     * Test rendering point.
     */
    @Test
    void testRenderingPoint()
    {
        final ImageImpl image = new ImageImpl(Graphics.createImageBuffer(10, 20));
        image.setLocation(5.0, 10.0);
        image.setOrigin(Origin.TOP_LEFT);

        assertEquals(5, image.getRenderX());
        assertEquals(10, image.getRenderY());

        image.setOrigin(Origin.MIDDLE);

        assertEquals(0, image.getRenderX());
        assertEquals(0, image.getRenderY());

        image.dispose();
    }

    /**
     * Test origin <code>null</code>.
     */
    @Test
    void testSetOriginNull()
    {
        final Image image = new ImageImpl(Graphics.createImageBuffer(64, 32));

        assertThrows(() -> image.setOrigin(null), "Unexpected null argument !");

        image.dispose();
    }

    /**
     * Test the render.
     */
    @Test
    void testRender()
    {
        final Graphic g = Graphics.createImageBuffer(100, 100).createGraphic();
        final Image image = new ImageImpl(Graphics.createImageBuffer(64, 32));
        image.render(g);

        g.dispose();
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final Image image = new ImageImpl(surface);
        final Image imageMedia = new ImageImpl(media);
        imageMedia.load();

        assertEquals(image, image);
        assertEquals(image, new ImageImpl(surface));
        assertEquals(imageMedia, imageMedia);

        assertNotEquals(image, null);
        assertNotEquals(image, new Object());
        assertNotEquals(image, new ImageImpl(media));
        assertNotEquals(imageMedia, new ImageImpl(media));
        assertNotEquals(image, new ImageImpl(Graphics.createImageBuffer(64, 32)));
        assertNotEquals(image, new ImageImpl(Graphics.createImageBuffer(32, 32)));
        assertNotEquals(image, new ImageImpl(Graphics.createImageBuffer(64, 64)));
        assertNotEquals(image, new ImageImpl(Graphics.createImageBuffer(32, 64)));

        image.dispose();
        imageMedia.dispose();
        surface.dispose();
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final Image image = new ImageImpl(surface);
        final Image imageMedia = new ImageImpl(media);
        imageMedia.load();

        assertHashEquals(image, new ImageImpl(surface));

        assertHashNotEquals(image, new Object());
        assertHashNotEquals(imageMedia, new ImageImpl(media));
        assertHashNotEquals(image, new ImageImpl(Graphics.createImageBuffer(64, 32)));
        assertHashNotEquals(image, new ImageImpl(Graphics.createImageBuffer(32, 32)));
        assertHashNotEquals(image, new ImageImpl(Graphics.createImageBuffer(64, 64)));
        assertHashNotEquals(image, new ImageImpl(Graphics.createImageBuffer(32, 64)));

        image.dispose();
        imageMedia.dispose();
        surface.dispose();
    }
}
