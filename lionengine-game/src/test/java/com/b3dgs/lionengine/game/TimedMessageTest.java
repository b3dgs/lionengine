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
package com.b3dgs.lionengine.game;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link TimedMessage}.
 */
public final class TimedMessageTest
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
     * Test add message.
     */
    @Test
    public void testAddMessage()
    {
        final TimedMessage message = new TimedMessage(Graphics.createText(Constant.FONT_DIALOG, 8, TextStyle.NORMAL));

        Assert.assertFalse(message.hasMessage());

        message.addMessage("test", 0, 0, 50L);

        Assert.assertTrue(message.hasMessage());
    }

    /**
     * Test update.
     */
    @Test
    public void testUpdate()
    {
        final TimedMessage message = new TimedMessage(Graphics.createText(Constant.FONT_DIALOG, 8, TextStyle.NORMAL));
        message.update(1.0);

        Assert.assertFalse(message.hasMessage());

        message.addMessage("test", 0, 0, 0L);
        message.addMessage("test2", 0, 0, 100L);

        Assert.assertTrue(message.hasMessage());

        message.update(1.0); // First message consumed, one remain

        Assert.assertTrue(message.hasMessage());

        UtilTests.pause(25L);

        message.update(1.0); // Message time still not elapsed

        Assert.assertTrue(message.hasMessage());

        UtilTests.pause(100L); // Message time elapsed

        message.update(1.0);

        Assert.assertFalse(message.hasMessage());
    }

    /**
     * Test render.
     */
    @Test
    public void testRender()
    {
        final TimedMessage message = new TimedMessage(Graphics.createText(Constant.FONT_DIALOG, 8, TextStyle.NORMAL));
        message.addMessage("test", 0, 0, 0L);

        final Graphic g = Graphics.createGraphic();
        message.render(g);
        message.update(1.0);
        message.render(g);
        g.dispose();

        Assert.assertFalse(message.hasMessage());
    }
}
