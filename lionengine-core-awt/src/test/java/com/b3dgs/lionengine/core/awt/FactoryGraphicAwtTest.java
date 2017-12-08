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
package com.b3dgs.lionengine.core.awt;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.graphic.FactoryGraphicTest;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Test the factory graphic provider class.
 */
public class FactoryGraphicAwtTest extends FactoryGraphicTest
{
    /**
     * Prepare test.
     * 
     * @throws IOException If error.
     */
    @BeforeClass
    public static void setUp() throws IOException
    {
        prepare();
        Graphics.setFactoryGraphic(new FactoryGraphicAwt());
        loadResources();
    }

    /**
     * Clean test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Graphics.setFactoryGraphic(null);
    }

    /*
     * FactoryGraphicTest
     */

    /**
     * Test rotate.
     */
    @Test
    @Override
    public void testRotate()
    {
        final ImageBuffer rotate = Graphics.rotate(image, 90);

        Assert.assertNotEquals(image, rotate);
        Assert.assertEquals(image.getWidth(), rotate.getHeight());
        Assert.assertEquals(image.getHeight(), rotate.getWidth());

        rotate.dispose();
    }

    @Override
    public void testCreateScreen()
    {
        Assume.assumeFalse("Unable to perform this test", false);
    }

    /**
     * Test get image with error.
     */
    @Test(expected = LionEngineException.class)
    public void testGetImageError()
    {
        Assert.assertNull(Graphics.getImageBuffer(new MediaMock()));
    }

    /**
     * Test save image with error.
     */
    @Test(expected = LionEngineException.class)
    public void testSaveImageError()
    {
        Graphics.saveImage(image, new MediaMock());
    }
}
