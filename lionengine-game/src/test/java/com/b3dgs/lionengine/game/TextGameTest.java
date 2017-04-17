/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.GraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Test the text game.
 */
public class TextGameTest
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

    private final Graphic g = new GraphicMock();
    private final TextGame text = new TextGame("Arial", 10, TextStyle.NORMAL);
    private final ViewerMock viewer = new ViewerMock();

    /**
     * Test the text game location.
     */
    @Test
    public void testLocation()
    {
        Assert.assertEquals(0, text.getLocationX());
        Assert.assertEquals(0, text.getLocationY());

        viewer.set(1, 2);
        text.update(viewer);

        Assert.assertEquals(0, text.getLocationX());
        Assert.assertEquals(0, text.getLocationY());

        text.setLocation(3, 4);

        Assert.assertEquals(3, text.getLocationX());
        Assert.assertEquals(4, text.getLocationY());
    }

    /**
     * Test the text game size.
     */
    @Test
    public void testSize()
    {
        Assert.assertEquals(10, text.getSize());

        Assert.assertEquals(0, text.getWidth());
        Assert.assertEquals(0, text.getHeight());

        Assert.assertEquals(0, text.getStringWidth(g, "text"));
        Assert.assertEquals(0, text.getStringHeight(g, "text"));
    }

    /**
     * Test the text game draw.
     */
    @Test
    public void testDraw()
    {
        text.setText("text");
        text.setAlign(Align.CENTER);
        text.setColor(ColorRgba.WHITE);

        text.draw(g, 0, 0, "toto");
        text.draw(g, 1, 2, Align.LEFT, "tata");
        text.draw(g, Geom.createLocalizable(3, 4), 1, 2, Align.RIGHT, "titi");
        text.drawRect(g, ColorRgba.BLACK, 0, 0, 10, 20);
        text.render(g);
    }
}
