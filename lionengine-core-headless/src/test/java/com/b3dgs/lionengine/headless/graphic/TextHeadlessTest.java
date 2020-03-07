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
package com.b3dgs.lionengine.headless.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Test {@link TextHeadless}.
 */
public final class TextHeadlessTest
{
    /** Text value. */
    private static final String VALUE = "test";
    /** Graphic. */
    private static Graphic g;

    /**
     * Setup tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicHeadless());
        final ImageBuffer buffer = Graphics.createImageBuffer(320, 240);
        buffer.prepare();
        g = buffer.createGraphic();
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        g.dispose();
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test normal.
     */
    @Test
    public void testNormal()
    {
        final Text text = Graphics.createText(Constant.FONT_DIALOG, 12, TextStyle.NORMAL);

        text.draw(g, 0, 0, VALUE);
        text.draw(g, 0, 0, Align.CENTER, VALUE);
        text.draw(g, 0, 0, Align.LEFT, VALUE);
        text.draw(g, 0, 0, Align.RIGHT, VALUE);
        text.setAlign(Align.CENTER);
        text.setColor(ColorRgba.BLACK);
        text.setLocation(1, 5);
        text.setText(VALUE);

        assertEquals(12, text.getSize());
        assertEquals(1, text.getLocationX());
        assertEquals(5, text.getLocationY());
        assertTrue(text.getWidth() == 0);
        assertTrue(text.getHeight() == 0);

        text.render(g);
        text.render(g);

        assertTrue(text.getWidth() > 0);
        assertTrue(text.getHeight() > 0);
    }

    /**
     * Test bold.
     */
    @Test
    public void testBold()
    {
        final Text text = Graphics.createText(Constant.FONT_DIALOG, 12, TextStyle.BOLD);
        text.draw(g, 0, 0, VALUE);
    }

    /**
     * Test italic.
     */
    @Test
    public void testItalic()
    {
        final Text text = Graphics.createText(Constant.FONT_DIALOG, 12, TextStyle.ITALIC);
        text.draw(g, 0, 0, VALUE);
    }
}
