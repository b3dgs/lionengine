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
package com.b3dgs.lionengine;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.ViewerMock;

/**
 * Test the graphic class.
 */
public class GraphicTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test the empty graphic.
     */
    @Test
    public void testEmptyGraphic()
    {
        final Graphic g = Graphics.createGraphic();
        Assert.assertNull(g.getGraphic());
    }

    /**
     * Test the graphic.
     */
    @Test
    public void testGraphic()
    {
        final ImageBuffer image = Graphics.createImageBuffer(100, 100, Transparency.OPAQUE);
        final Graphic g = image.createGraphic();

        Assert.assertNotNull(g.getGraphic());
        g.clear(0, 0, image.getWidth(), image.getHeight());
        g.copyArea(0, 0, image.getWidth(), image.getHeight(), 0, 0);

        g.drawImage(image, 0, 0);

        final Transform transform = Graphics.createTransform();
        g.drawImage(image, transform, 0, 0);
        g.drawImage(image, transform, 0, 0);
        g.drawImage(image, 0, 0, 0, 0, 0, 0, 0, 0);

        g.drawLine(0, 0, 0, 0);
        g.drawOval(0, 0, image.getWidth(), image.getHeight(), true);
        g.drawOval(0, 0, image.getWidth(), image.getHeight(), false);
        g.drawRect(0, 0, image.getWidth(), image.getHeight(), true);
        g.drawRect(0, 0, image.getWidth(), image.getHeight(), false);

        Assert.assertEquals(ColorRgba.WHITE.getRgba(), g.getColor().getRgba());
        g.setColor(ColorRgba.BLUE);
        Assert.assertEquals(ColorRgba.BLUE.getRgba(), g.getColor().getRgba());

        g.setColorGradient(new ColorGradient(0, 0, ColorRgba.CYAN, 100, 100, ColorRgba.RED));
        g.drawGradient(0, 0, 100, 100);

        g.drawLine(new ViewerMock(), 0, 0, 0, 0);
        g.drawOval(new ViewerMock(), Origin.BOTTOM_LEFT, 0, 0, image.getWidth(), image.getHeight(), true);
        g.drawRect(new ViewerMock(), Origin.BOTTOM_LEFT, 0, 0, image.getWidth(), image.getHeight(), true);
        g.drawGradient(new ViewerMock(), Origin.BOTTOM_LEFT, 0, 0, 100, 100);

        g.dispose();

        g.setGraphic(null);
        Assert.assertNull(g.getGraphic());

        image.dispose();
    }
}
