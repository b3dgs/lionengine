/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.EngineCore;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.FactoryMediaMock;

/**
 * Test the cursor class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CursorTest
{
    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        EngineCore.start("CursorTest", Version.create(1, 0, 0), Verbose.NONE, new FactoryGraphicMock(),
                new FactoryMediaMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        EngineCore.terminate();
    }

    /**
     * Test the cursor class.
     */
    @Test
    public void testCursor()
    {
        final Graphic g = Core.GRAPHIC.createImageBuffer(320, 240, Transparency.BITMASK).createGraphic();
        final MouseMock mouse = new MouseMock();

        final Resolution output0 = new Resolution(320, 240, 60);
        try
        {
            final Cursor cursor = new Cursor(mouse, output0, null);
            Assert.assertNotNull(cursor);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Cursor cursor = new Cursor(mouse, output0, Core.MEDIA.create("src", "test", "resources", "cursor.png"));
        cursor.setArea(0, 0, 320, 240);
        cursor.setSensibility(1.0, 2.0);
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
        Assert.assertEquals(10, cursor.getLocationX());
        Assert.assertEquals(20, cursor.getLocationY());
        Assert.assertEquals(1.0, cursor.getSensibilityHorizontal(), 0.000001);
        Assert.assertEquals(2.0, cursor.getSensibilityVertical(), 0.000001);
        cursor.setRenderingOffset(0, 0);
        Assert.assertEquals(0, cursor.getSurfaceId());
        Assert.assertEquals(0, cursor.getClick());
        cursor.render(g);
    }
}
