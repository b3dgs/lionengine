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
package com.b3dgs.lionengine.graphic;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Align;

/**
 * Test the text class.
 */
public class TextTest
{
    /** Text value. */
    private static final String VALUE = "test";
    /** Graphic. */
    private static Graphic g;

    /**
     * Setup test.
     */
    @BeforeClass
    public static void setUp()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        prepare();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        g.dispose();
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Prepare test resources.
     */
    protected static void prepare()
    {
        final ImageBuffer buffer = Graphics.createImageBuffer(320, 240, Transparency.OPAQUE);
        buffer.prepare();
        g = buffer.createGraphic();
    }

    /**
     * Test the text normal.
     */
    @Test
    public void testNormal()
    {
        final Text text = Graphics.createText(Text.DIALOG, 12, TextStyle.NORMAL);
        text.draw(g, 0, 0, VALUE);
        text.draw(g, 0, 0, Align.CENTER, VALUE);
        text.draw(g, 0, 0, Align.LEFT, VALUE);
        text.draw(g, 0, 0, Align.RIGHT, VALUE);
        text.setAlign(Align.CENTER);
        text.setColor(ColorRgba.BLACK);
        text.setLocation(1, 5);
        text.setText(VALUE);

        Assert.assertEquals(12, text.getSize());
        Assert.assertEquals(1, text.getLocationX());
        Assert.assertEquals(5, text.getLocationY());
        Assert.assertTrue(text.getWidth() == 0);
        Assert.assertTrue(text.getHeight() == 0);

        text.render(g);
        text.render(g);

        Assert.assertTrue(text.getWidth() > 0);
        Assert.assertTrue(text.getHeight() > 0);
    }

    /**
     * Test the text bold.
     */
    @Test
    public void testBold()
    {
        final Text text = Graphics.createText(Text.DIALOG, 12, TextStyle.BOLD);
        text.draw(g, 0, 0, VALUE);
    }

    /**
     * Test the text italic.
     */
    @Test
    public void testItalic()
    {
        final Text text = Graphics.createText(Text.DIALOG, 12, TextStyle.ITALIC);
        text.draw(g, 0, 0, VALUE);
    }
}
