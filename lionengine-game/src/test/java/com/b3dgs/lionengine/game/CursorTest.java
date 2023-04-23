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
package com.b3dgs.lionengine.game;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertThrowsNpe;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.ContextMock;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.game.feature.Services;
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

    private final Services services = new Services();

    /**
     * Prepare test.
     */
    @BeforeEach
    public void before()
    {
        services.add(new ContextMock());
    }

    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        final Cursor cursor = new Cursor(services);

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
        final Cursor cursor = new Cursor(services);
        cursor.setGrid(10, 20);

        assertEquals(10, cursor.getWidth());
        assertEquals(20, cursor.getHeight());
    }

    /**
     * Test add existing ID.
     */
    @Test
    void testAddExistingId()
    {
        final Cursor cursor = new Cursor(services);
        cursor.addImage(1, Medias.create("cursor.png"));
        cursor.addImage(1, Medias.create("cursor.png"));
    }

    /**
     * Test set surface ID.
     */
    @Test
    void testSetSurfaceId()
    {
        final Cursor cursor = new Cursor(services);
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
        final Cursor cursor = new Cursor(services);

        assertThrows(() -> cursor.setSurfaceId(1), CursorRenderer.ERROR_SURFACE_ID + 1);
    }

    /**
     * Test load surface.
     */
    @Test
    void testLoadSurface()
    {
        final Cursor cursor = new Cursor(services);
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
        final Cursor cursor = new Cursor(services);
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
        final Cursor cursor = new Cursor(services);
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
        final Cursor cursor = new Cursor(services);
        cursor.update(1.0);

        final MouseMock mouse = new MouseMock();
        cursor.setSync(mouse);

        assertEquals(0.0, cursor.getX());
        assertEquals(0.0, cursor.getY());
        assertEquals(0.0, cursor.getScreenX());
        assertEquals(0.0, cursor.getScreenY());
        assertNull(cursor.getPushed());
        assertFalse(cursor.isPushed(Integer.valueOf(1)));
        assertFalse(cursor.isPushedOnce(Integer.valueOf(1)));

        mouse.setClick(Integer.valueOf(1));
        mouse.move(2, 3);
        cursor.update(1.0);

        assertEquals(2.0, cursor.getX());
        assertEquals(3.0, cursor.getY());
        assertEquals(2.0, cursor.getScreenX());
        assertEquals(3.0, cursor.getScreenY());
        assertEquals(Integer.valueOf(1), cursor.getPushed());
        assertTrue(cursor.isPushed(Integer.valueOf(1)));
        assertTrue(cursor.isPushedOnce(Integer.valueOf(1)));
    }

    /**
     * Test set area.
     */
    @Test
    void testSetArea()
    {
        final Cursor cursor = new Cursor(services);
        final MouseMock mouse = new MouseMock();
        cursor.setSync(mouse);
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
        final Cursor cursor = new Cursor(services);
        final MouseMock mouse = new MouseMock();
        cursor.setSync(mouse);
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
        final Cursor cursor = new Cursor(services);
        final MouseMock mouse = new MouseMock();
        mouse.move(1, 2);
        cursor.setSync(mouse);

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
        final Cursor cursor = new Cursor(services);
        cursor.setVisible(false);
        cursor.render(g);

        cursor.setVisible(true);

        assertThrowsNpe(() -> cursor.render(g));
    }

    /**
     * Test render
     */
    @Test
    void testRender()
    {
        final Cursor cursor = new Cursor(services);
        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.load();

        final Graphic g = Graphics.createGraphic();
        cursor.render(g);
    }
}
