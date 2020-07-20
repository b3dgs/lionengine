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
package com.b3dgs.lionengine.game;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link Cursor}.
 */
final class CursorTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(CursorTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Graphics.setFactoryGraphic(null);
        Medias.setLoadFromJar(null);
    }

    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        final Cursor cursor = new Cursor();

        assertNull(cursor.getSurfaceId());
        assertEquals(0.0, cursor.getX());
        assertEquals(0.0, cursor.getY());
        assertEquals(1, cursor.getWidth());
        assertEquals(1, cursor.getHeight());
        assertEquals(1.0, cursor.getSensibilityHorizontal());
        assertEquals(1.0, cursor.getSensibilityVertical());
        assertEquals(0.0, cursor.getScreenX());
        assertEquals(0.0, cursor.getScreenY());
    }

    /**
     * Test set grid.
     */
    @Test
    void testSetGrid()
    {
        final Cursor cursor = new Cursor();
        cursor.setGrid(10, 20);

        assertEquals(10, cursor.getWidth());
        assertEquals(20, cursor.getHeight());
    }

    /**
     * Test no input.
     */
    @Test
    void testNoInput()
    {
        final Cursor cursor = new Cursor();

        assertThrows(NullPointerException.class, () -> cursor.getClick(), null);
    }

    /**
     * Test add existing ID.
     */
    @Test
    void testAddExistingId()
    {
        final Cursor cursor = new Cursor();
        cursor.addImage(1, Medias.create("cursor.png"));
        cursor.addImage(1, Medias.create("cursor.png"));
    }

    /**
     * Test set surface ID.
     */
    @Test
    void testSetSurfaceId()
    {
        final Cursor cursor = new Cursor();
        cursor.addImage(1, Medias.create("cursor.png"));
        cursor.setSurfaceId(1);
        cursor.update(1.0);

        assertEquals(Integer.valueOf(1), cursor.getSurfaceId());
        assertFalse(cursor.isLoaded());

        cursor.load();

        assertTrue(cursor.isLoaded());

        cursor.dispose();

        assertNull(cursor.getSurfaceId());
    }

    /**
     * Test set surface unknown ID.
     */
    @Test
    void testSetSurfaceIdUnknown()
    {
        final Cursor cursor = new Cursor();

        assertThrows(() -> cursor.setSurfaceId(1), CursorRenderer.ERROR_SURFACE_ID + 1);
    }

    /**
     * Test load surface.
     */
    @Test
    void testLoadSurface()
    {
        final Cursor cursor = new Cursor();
        cursor.addImage(1, Medias.create("cursor.png"));
        cursor.update(1.0);

        assertFalse(cursor.isLoaded());

        cursor.load();

        assertTrue(cursor.isLoaded());

        cursor.dispose();

        assertNull(cursor.getSurfaceId());
    }

    /**
     * Test sensibility.
     */
    @Test
    void testSensibility()
    {
        final Cursor cursor = new Cursor();
        cursor.setSensibility(1.0, 2.0);

        assertEquals(1.0, cursor.getSensibilityHorizontal());
        assertEquals(2.0, cursor.getSensibilityVertical());
    }

    /**
     * Test set location.
     */
    @Test
    void testSetLocation()
    {
        final Cursor cursor = new Cursor();
        cursor.setLocation(1, 2);

        assertEquals(0.0, cursor.getX());
        assertEquals(0.0, cursor.getY());
        assertEquals(1.0, cursor.getScreenX());
        assertEquals(2.0, cursor.getScreenY());

        cursor.update(1.0);

        assertEquals(1.0, cursor.getX());
        assertEquals(2.0, cursor.getY());
        assertEquals(1.0, cursor.getScreenX());
        assertEquals(2.0, cursor.getScreenY());
    }

    /**
     * Test input device.
     */
    @Test
    void testInput()
    {
        final Cursor cursor = new Cursor();
        cursor.update(1.0);

        final MouseMock mouse = new MouseMock();
        cursor.setInputDevice(mouse);

        assertEquals(0.0, cursor.getX());
        assertEquals(0.0, cursor.getY());
        assertEquals(0.0, cursor.getScreenX());
        assertEquals(0.0, cursor.getScreenY());
        assertEquals(0, cursor.getClick());
        assertFalse(cursor.hasClicked(1));
        assertFalse(cursor.hasClickedOnce(1));

        mouse.setClick(1);
        mouse.move(2, 3);
        cursor.update(1.0);

        assertEquals(2.0, cursor.getX());
        assertEquals(3.0, cursor.getY());
        assertEquals(2.0, cursor.getScreenX());
        assertEquals(3.0, cursor.getScreenY());
        assertEquals(1, cursor.getClick());
        assertTrue(cursor.hasClicked(1));
        assertTrue(cursor.hasClickedOnce(1));
    }

    /**
     * Test sync mode disabled.
     */
    @Test
    void testSyncModeDisabled()
    {
        final Cursor cursor = new Cursor();
        final MouseMock mouse = new MouseMock();
        cursor.setInputDevice(mouse);

        assertTrue(cursor.isSynchronized());

        cursor.setSyncMode(false);

        assertFalse(cursor.isSynchronized());

        cursor.setSensibility(0.5, 0.5);

        mouse.move(2, 4);
        cursor.update(1.0);

        assertEquals(1.0, cursor.getX());
        assertEquals(2.0, cursor.getY());
        assertEquals(1.0, cursor.getScreenX());
        assertEquals(2.0, cursor.getScreenY());
    }

    /**
     * Test set area.
     */
    @Test
    void testSetArea()
    {
        final Cursor cursor = new Cursor();
        final MouseMock mouse = new MouseMock();
        cursor.setInputDevice(mouse);
        cursor.setArea(5, 6, 10, 20);
        cursor.update(1.0);

        assertEquals(5.0, cursor.getX());
        assertEquals(6.0, cursor.getY());
        assertEquals(5.0, cursor.getScreenX());
        assertEquals(6.0, cursor.getScreenY());

        mouse.move(30, 40);
        cursor.update(1.0);

        assertEquals(10.0, cursor.getX());
        assertEquals(20.0, cursor.getY());
        assertEquals(10.0, cursor.getScreenX());
        assertEquals(20.0, cursor.getScreenY());
    }

    /**
     * Test set offset.
     */
    @Test
    void testSetOffset()
    {
        final Cursor cursor = new Cursor();
        final MouseMock mouse = new MouseMock();
        cursor.setInputDevice(mouse);
        cursor.setRenderingOffset(10, 20);

        mouse.move(1, 2);
        cursor.update(1.0);

        assertEquals(1.0, cursor.getX());
        assertEquals(2.0, cursor.getY());
        assertEquals(1.0, cursor.getScreenX());
        assertEquals(2.0, cursor.getScreenY());
    }

    /**
     * Test set viewer.
     */
    @Test
    void testSetViewer()
    {
        final Cursor cursor = new Cursor();
        final MouseMock mouse = new MouseMock();
        mouse.move(1, 2);
        cursor.setInputDevice(mouse);

        final ViewerMock viewer = new ViewerMock();
        cursor.setViewer(viewer);
        viewer.set(100, 200);

        cursor.update(1.0);

        assertEquals(101.0, cursor.getX());
        assertEquals(438.0, cursor.getY());
        assertEquals(1.0, cursor.getScreenX());
        assertEquals(2.0, cursor.getScreenY());
    }

    /**
     * Test render no surface
     */
    @Test
    void testRenderNoSurface()
    {
        final Graphic g = Graphics.createGraphic();
        final Cursor cursor = new Cursor();
        cursor.setVisible(false);
        cursor.render(g);

        cursor.setVisible(true);

        assertThrows(NullPointerException.class, () -> cursor.render(g), null);
    }

    /**
     * Test render
     */
    @Test
    void testRender()
    {
        final Cursor cursor = new Cursor();
        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.load();

        final Graphic g = Graphics.createGraphic();
        cursor.render(g);
    }
}
