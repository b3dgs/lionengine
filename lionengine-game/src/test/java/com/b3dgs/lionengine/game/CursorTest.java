/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.GraphicMock;

/**
 * Test the cursor class.
 */
public class CursorTest
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
     * Test the cursor class.
     */
    @Test
    public void testCursor()
    {
        final Graphic g = new GraphicMock();
        final MouseMock mouse = new MouseMock();

        final Cursor cursor = new Cursor();
        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.setArea(0, 0, 320, 240);
        cursor.setSensibility(1.0, 2.0);
        cursor.setInputDevice(mouse);
        cursor.setSurfaceId(0);

        cursor.update(1.0);
        cursor.setSyncMode(true);
        Assert.assertTrue(cursor.isSynchronized());
        cursor.update(1.0);
        cursor.setSyncMode(false);
        Assert.assertTrue(!cursor.isSynchronized());
        cursor.update(1.0);
        cursor.setSyncMode(true);
        cursor.update(1.0);

        cursor.setLocation(10, 20);
        Assert.assertEquals(0.0, cursor.getX(), 0.000001);
        Assert.assertEquals(0.0, cursor.getY(), 0.000001);
        Assert.assertEquals(10.0, cursor.getScreenX(), 0.000001);
        Assert.assertEquals(20.0, cursor.getScreenY(), 0.000001);
        Assert.assertEquals(1.0, cursor.getSensibilityHorizontal(), 0.000001);
        Assert.assertEquals(2.0, cursor.getSensibilityVertical(), 0.000001);
        cursor.setRenderingOffset(0, 0);
        Assert.assertEquals(Integer.valueOf(0), cursor.getSurfaceId());
        Assert.assertEquals(0, cursor.getClick());
        cursor.render(g);
    }
}
