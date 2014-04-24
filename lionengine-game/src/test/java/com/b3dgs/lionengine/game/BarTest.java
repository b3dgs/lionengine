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

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.EngineCore;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.FactoryMediaMock;

/**
 * Test the bar class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class BarTest
{
    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        EngineCore.start("BarTest", Version.create(1, 0, 0), Verbose.NONE, new FactoryGraphicMock(),
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
     * Test the bar class.
     */
    @Test
    public void testBar()
    {
        final Graphic g = Core.GRAPHIC.createImageBuffer(320, 240, Transparency.BITMASK).createGraphic();
        final Bar bar = new Bar(10, 20);
        bar.setBorderSize(1, 1);
        bar.setColorBackground(ColorRgba.WHITE);
        bar.setColorForeground(ColorRgba.BLACK);
        bar.setLocation(0, 0);
        bar.setMaximumSize(10, 20);
        bar.setVerticalReferential(true);
        bar.setHorizontalReferential(true);
        bar.setWidthPercent(100);
        bar.setHeightPercent(100);
        Assert.assertEquals(10, bar.getWidthMax());
        Assert.assertEquals(20, bar.getHeightMax());
        Assert.assertEquals(100, bar.getWidthPercent());
        Assert.assertEquals(100, bar.getHeightPercent());
        Assert.assertEquals(10, bar.getWidth());
        Assert.assertEquals(20, bar.getHeight());

        bar.setWidthPercent(50);
        bar.setHeightPercent(50);
        Assert.assertEquals(50, bar.getWidthPercent());
        Assert.assertEquals(50, bar.getHeightPercent());
        Assert.assertEquals(5, bar.getWidth());
        Assert.assertEquals(10, bar.getHeight());

        bar.render(g);
        bar.setColorBackground(null);
        bar.setVerticalReferential(false);
        bar.setHorizontalReferential(true);
        bar.render(g);
        bar.setColorBackground(ColorRgba.WHITE);
        bar.setColorForeground(null);
        bar.setVerticalReferential(false);
        bar.setHorizontalReferential(false);
        bar.render(g);
        bar.setColorBackground(null);
        bar.setColorForeground(null);
        bar.setVerticalReferential(true);
        bar.setHorizontalReferential(false);
        bar.render(g);

        bar.setWidthPercent(100);
        bar.setHeightPercent(0);
        bar.render(g);

        bar.setWidthPercent(0);
        bar.setHeightPercent(100);
        bar.render(g);

        bar.setWidthPercent(100);
        bar.setHeightPercent(100);
        bar.setColorGradient(0, 0, ColorRgba.WHITE, 50, 10, ColorRgba.BLACK);
        bar.render(g);
    }
}
