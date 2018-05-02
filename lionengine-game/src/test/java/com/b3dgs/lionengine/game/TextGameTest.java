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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.GraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Test {@link TextGame}.
 */
public final class TextGameTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test location.
     */
    @Test
    public void testLocation()
    {
        final TextGame text = new TextGame(Constant.FONT_DIALOG, 8, TextStyle.NORMAL);

        assertEquals(0, text.getLocationX());
        assertEquals(0, text.getLocationY());

        final ViewerMock viewer = new ViewerMock();
        viewer.set(1, 2);
        text.update(viewer);

        assertEquals(0, text.getLocationX());
        assertEquals(0, text.getLocationY());

        text.setLocation(3, 4);

        assertEquals(3, text.getLocationX());
        assertEquals(4, text.getLocationY());
    }

    /**
     * Test size.
     */
    @Test
    public void testSize()
    {
        final Graphic g = new GraphicMock();
        final TextGame text = new TextGame(Constant.FONT_DIALOG, 8, TextStyle.NORMAL);

        assertEquals(8, text.getSize());

        assertEquals(0, text.getWidth());
        assertEquals(0, text.getHeight());

        assertEquals(0, text.getStringWidth(g, "text"));
        assertEquals(0, text.getStringHeight(g, "text"));

        g.dispose();
    }

    /**
     * Test draw.
     */
    @Test
    public void testDraw()
    {
        final Graphic g = new GraphicMock();
        final TextGame text = new TextGame(Constant.FONT_DIALOG, 8, TextStyle.NORMAL);

        text.setText("text");
        text.setAlign(Align.CENTER);
        text.setColor(ColorRgba.WHITE);
        text.render(g);

        text.draw(g, 0, 0, "toto");
        text.draw(g, 1, 2, Align.LEFT, "tata");
        text.draw(g, Geom.createLocalizable(3, 4), 1, 2, Align.RIGHT, "titi");
        text.drawRect(g, ColorRgba.BLACK, 0, 0, 10, 20);

        g.dispose();
    }
}
