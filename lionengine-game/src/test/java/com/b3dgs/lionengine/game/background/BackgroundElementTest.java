/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.background;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;

/**
 * Test {@link BackgroundElement}.
 */
public final class BackgroundElementTest
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
     * Test data.
     */
    @Test
    public void testData()
    {
        final Image image = Drawable.loadImage(Graphics.createImageBuffer(16, 32));
        final BackgroundElement element = new BackgroundElement(1, 2, image);

        assertEquals(1, element.getMainX());
        assertEquals(2, element.getMainY());
        assertEquals(0.0, element.getOffsetX());
        assertEquals(0.0, element.getOffsetY());
        assertEquals(image, element.getRenderable());

        element.setMainX(3);
        element.setMainY(4);
        element.setOffsetX(1.0);
        element.setOffsetY(2.0);

        assertEquals(3, element.getMainX());
        assertEquals(4, element.getMainY());
        assertEquals(1.0, element.getOffsetX());
        assertEquals(2.0, element.getOffsetY());
    }
}
