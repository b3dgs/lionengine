/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.GraphicMock;

/**
 * Test {@link Bar}.
 */
final class BarTest
{
    /**
     * Test the bar class.
     */
    @Test
    void testBar()
    {
        final Graphic g = new GraphicMock();
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

        assertEquals(10, bar.getWidthMax());
        assertEquals(20, bar.getHeightMax());
        assertEquals(100, bar.getWidthPercent());
        assertEquals(100, bar.getHeightPercent());
        assertEquals(10, bar.getWidth());
        assertEquals(20, bar.getHeight());

        bar.setWidthPercent(50);
        bar.setHeightPercent(50);

        assertEquals(50, bar.getWidthPercent());
        assertEquals(50, bar.getHeightPercent());
        assertEquals(5, bar.getWidth());
        assertEquals(10, bar.getHeight());

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
        bar.setColor(null, null);
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

        bar.setColorGradient(ColorRgba.WHITE, ColorRgba.BLACK);
        bar.render(g);
    }
}
