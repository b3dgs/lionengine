/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.geom.Geom;

/**
 * Test {@link Graphic}.
 */
public class GraphicTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test empty graphic.
     */
    @Test
    void testEmptyGraphic()
    {
        final Graphic g = Graphics.createGraphic();

        assertNull(g.getGraphic());
    }

    /**
     * Test graphic.
     */
    @Test
    protected void testGraphic()
    {
        final ImageBuffer image = Graphics.createImageBuffer(320, 240);
        final Graphic g = image.createGraphic();

        assertNotNull(g.getGraphic());

        g.clear(0, 0, image.getWidth(), image.getHeight());
        g.copyArea(0, 0, image.getWidth(), image.getHeight(), 0, 0);

        g.drawImage(image, 0, 0);

        final Transform transform = Graphics.createTransform();
        g.drawImage(image, transform, 0, 0);
        g.drawImage(image, transform, 0, 0);
        g.drawImage(image, 0, 0, 0, 0, 0, 0, 0, 0);
        g.drawImage(image, 0, 0, 0, 0, 2, 0, 0, 0);
        g.drawImage(image, 0, 0, 0, 0, 2, 0, 0, 0);

        g.drawImage(image, 0, 0, 0, 0, 2, 0, 0, 0, 0, 20, 30);
        g.drawImage(image, 0, 0, 0, 0, 2, 0, 0, 0, 10, 20, 30);

        g.drawLine(0, 0, 0, 0);
        g.drawOval(0, 0, image.getWidth(), image.getHeight(), true);
        g.drawOval(0, 0, image.getWidth(), image.getHeight(), false);
        g.drawRect(0, 0, 1, 0, true);
        g.drawRect(0, 0, 0, 1, true);
        g.drawRect(0, 0, 0, 0, true);
        g.drawRect(0, 0, image.getWidth(), image.getHeight(), true);
        g.drawRect(0, 0, image.getWidth(), image.getHeight(), false);

        assertEquals(ColorRgba.WHITE.getRgba(), g.getColor().getRgba());

        g.setColor(ColorRgba.BLUE);
        g.setColor(ColorRgba.BLUE);

        assertEquals(ColorRgba.BLUE.getRgba(), g.getColor().getRgba());

        g.setColorGradient(new ColorGradient(0, 0, ColorRgba.CYAN, 100, 100, ColorRgba.RED));
        g.setColorGradient(new ColorGradient(0, 0, ColorRgba.CYAN, 100, 100, ColorRgba.RED));
        g.drawGradient(0, 0, 100, 100);

        g.drawLine(new ViewerMock(), 1, 2, 3, 4);
        g.drawLine(new ViewerMock(), 1, 3, 2, 4);
        g.drawLine(new ViewerMock(), 1, 2, 2, 1);
        g.drawLine(new ViewerMock(), 4, 3, 2, 3);
        g.drawOval(new ViewerMock(), Origin.BOTTOM_LEFT, 0, 0, image.getWidth(), image.getHeight(), true);
        g.drawRect(new ViewerMock(), Origin.BOTTOM_LEFT, 0, 0, image.getWidth(), image.getHeight(), true);
        g.drawRect(new ViewerMock(),
                   Origin.BOTTOM_LEFT,
                   Geom.createArea(0, 0, image.getWidth(), image.getHeight()),
                   true);
        g.drawGradient(new ViewerMock(), Origin.BOTTOM_LEFT, 0, 0, 100, 100);

        g.dispose();

        g.setGraphic(null);

        assertNull(g.getGraphic());

        image.dispose();
    }
}
