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
package com.b3dgs.lionengine.game;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link Cursor}.
 */
public final class CursorTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(CursorTest.class);
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
     * Test constructor.
     */
    @Test
    public void testConstructor()
    {
        final Cursor cursor = new Cursor();

        Assert.assertNull(cursor.getSurfaceId());
        Assert.assertEquals(0.0, cursor.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, cursor.getY(), UtilTests.PRECISION);
        Assert.assertEquals(1, cursor.getWidth());
        Assert.assertEquals(1, cursor.getHeight());
        Assert.assertEquals(1.0, cursor.getSensibilityHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, cursor.getSensibilityVertical(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, cursor.getScreenX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, cursor.getScreenY(), UtilTests.PRECISION);
    }

    /**
     * Test set grid.
     */
    @Test
    public void testSetGrid()
    {
        final Cursor cursor = new Cursor();
        cursor.setGrid(10, 20);

        Assert.assertEquals(10, cursor.getWidth());
        Assert.assertEquals(20, cursor.getHeight());
    }

    /**
     * Test no input.
     */
    @Test(expected = NullPointerException.class)
    public void testNoInput()
    {
        final Cursor cursor = new Cursor();

        Assert.assertEquals(-1, cursor.getClick());
    }

    /**
     * Test add existing ID.
     */
    @Test
    public void testAddExistingId()
    {
        final Cursor cursor = new Cursor();
        cursor.addImage(1, Medias.create("cursor.png"));
        cursor.addImage(1, Medias.create("cursor.png"));
    }

    /**
     * Test set surface ID.
     */
    @Test
    public void testSetSurfaceId()
    {
        final Cursor cursor = new Cursor();
        cursor.addImage(1, Medias.create("cursor.png"));
        cursor.setSurfaceId(1);
        cursor.update(1.0);

        Assert.assertEquals(Integer.valueOf(1), cursor.getSurfaceId());
        Assert.assertFalse(cursor.isLoaded());

        cursor.load();

        Assert.assertTrue(cursor.isLoaded());

        cursor.dispose();

        Assert.assertNull(cursor.getSurfaceId());
    }

    /**
     * Test set surface unknown ID.
     */
    @Test(expected = LionEngineException.class)
    public void testSetSurfaceIdUnknown()
    {
        final Cursor cursor = new Cursor();
        cursor.setSurfaceId(1);
    }

    /**
     * Test load surface.
     */
    @Test
    public void testLoadSurface()
    {
        final Cursor cursor = new Cursor();
        cursor.addImage(1, Medias.create("cursor.png"));
        cursor.update(1.0);

        Assert.assertFalse(cursor.isLoaded());

        cursor.load();

        Assert.assertTrue(cursor.isLoaded());

        cursor.dispose();

        Assert.assertNull(cursor.getSurfaceId());
    }

    /**
     * Test sensibility.
     */
    @Test
    public void testSensibility()
    {
        final Cursor cursor = new Cursor();
        cursor.setSensibility(1.0, 2.0);

        Assert.assertEquals(1.0, cursor.getSensibilityHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, cursor.getSensibilityVertical(), UtilTests.PRECISION);
    }

    /**
     * Test set location.
     */
    @Test
    public void testSetLocation()
    {
        final Cursor cursor = new Cursor();
        cursor.setLocation(1, 2);

        Assert.assertEquals(0.0, cursor.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, cursor.getY(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, cursor.getScreenX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, cursor.getScreenY(), UtilTests.PRECISION);

        cursor.update(1.0);

        Assert.assertEquals(1.0, cursor.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, cursor.getY(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, cursor.getScreenX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, cursor.getScreenY(), UtilTests.PRECISION);
    }

    /**
     * Test input device.
     */
    @Test
    public void testInput()
    {
        final Cursor cursor = new Cursor();
        cursor.update(1.0);

        final MouseMock mouse = new MouseMock();
        cursor.setInputDevice(mouse);

        Assert.assertEquals(0.0, cursor.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, cursor.getY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, cursor.getScreenX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, cursor.getScreenY(), UtilTests.PRECISION);
        Assert.assertEquals(0, cursor.getClick());
        Assert.assertFalse(cursor.hasClicked(1));
        Assert.assertFalse(cursor.hasClickedOnce(1));

        mouse.setClick(1);
        mouse.move(2, 3);
        cursor.update(1.0);

        Assert.assertEquals(2.0, cursor.getX(), UtilTests.PRECISION);
        Assert.assertEquals(3.0, cursor.getY(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, cursor.getScreenX(), UtilTests.PRECISION);
        Assert.assertEquals(3.0, cursor.getScreenY(), UtilTests.PRECISION);
        Assert.assertEquals(1, cursor.getClick());
        Assert.assertTrue(cursor.hasClicked(1));
        Assert.assertTrue(cursor.hasClickedOnce(1));
    }

    /**
     * Test sync mode disabled.
     */
    @Test
    public void testSyncModeDisabled()
    {
        final Cursor cursor = new Cursor();
        final MouseMock mouse = new MouseMock();
        cursor.setInputDevice(mouse);

        Assert.assertTrue(cursor.isSynchronized());

        cursor.setSyncMode(false);

        Assert.assertFalse(cursor.isSynchronized());

        cursor.setSensibility(0.5, 0.5);

        mouse.move(2, 4);
        cursor.update(1.0);

        Assert.assertEquals(1.0, cursor.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, cursor.getY(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, cursor.getScreenX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, cursor.getScreenY(), UtilTests.PRECISION);
    }

    /**
     * Test set area.
     */
    @Test
    public void testSetArea()
    {
        final Cursor cursor = new Cursor();
        final MouseMock mouse = new MouseMock();
        cursor.setInputDevice(mouse);
        cursor.setArea(5, 6, 10, 20);
        cursor.update(1.0);

        Assert.assertEquals(5.0, cursor.getX(), UtilTests.PRECISION);
        Assert.assertEquals(6.0, cursor.getY(), UtilTests.PRECISION);
        Assert.assertEquals(5.0, cursor.getScreenX(), UtilTests.PRECISION);
        Assert.assertEquals(6.0, cursor.getScreenY(), UtilTests.PRECISION);

        mouse.move(30, 40);
        cursor.update(1.0);

        Assert.assertEquals(10.0, cursor.getX(), UtilTests.PRECISION);
        Assert.assertEquals(20.0, cursor.getY(), UtilTests.PRECISION);
        Assert.assertEquals(10.0, cursor.getScreenX(), UtilTests.PRECISION);
        Assert.assertEquals(20.0, cursor.getScreenY(), UtilTests.PRECISION);
    }

    /**
     * Test set offset.
     */
    @Test
    public void testSetOffset()
    {
        final Cursor cursor = new Cursor();
        final MouseMock mouse = new MouseMock();
        cursor.setInputDevice(mouse);
        cursor.setRenderingOffset(10, 20);

        mouse.move(1, 2);
        cursor.update(1.0);

        Assert.assertEquals(1.0, cursor.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, cursor.getY(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, cursor.getScreenX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, cursor.getScreenY(), UtilTests.PRECISION);
    }

    /**
     * Test set viewer.
     */
    @Test
    public void testSetViewer()
    {
        final Cursor cursor = new Cursor();
        final MouseMock mouse = new MouseMock();
        mouse.move(1, 2);
        cursor.setInputDevice(mouse);

        final ViewerMock viewer = new ViewerMock();
        cursor.setViewer(viewer);
        viewer.set(100, 200);

        cursor.update(1.0);

        Assert.assertEquals(101.0, cursor.getX(), UtilTests.PRECISION);
        Assert.assertEquals(438.0, cursor.getY(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, cursor.getScreenX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, cursor.getScreenY(), UtilTests.PRECISION);
    }

    /**
     * Test render no surface
     */
    @Test(expected = NullPointerException.class)
    public void testRenderNoSurface()
    {
        final Graphic g = Graphics.createGraphic();
        final Cursor cursor = new Cursor();
        cursor.setVisible(false);
        cursor.render(g);

        cursor.setVisible(true);
        cursor.render(g);
    }

    /**
     * Test render
     */
    @Test
    public void testRender()
    {
        final Cursor cursor = new Cursor();
        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.load();

        final Graphic g = Graphics.createGraphic();
        cursor.render(g);
    }
}
