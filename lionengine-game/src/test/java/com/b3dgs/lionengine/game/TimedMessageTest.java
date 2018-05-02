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

import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Test {@link TimedMessage}.
 */
public final class TimedMessageTest
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
     * Test add message.
     */
    @Test
    public void testAddMessage()
    {
        final TimedMessage message = new TimedMessage(Graphics.createText(Constant.FONT_DIALOG, 8, TextStyle.NORMAL));

        assertFalse(message.hasMessage());

        message.addMessage("test", 0, 0, 50L);

        assertTrue(message.hasMessage());
    }

    /**
     * Test update.
     */
    @Test
    public void testUpdate()
    {
        final TimedMessage message = new TimedMessage(Graphics.createText(Constant.FONT_DIALOG, 8, TextStyle.NORMAL));
        message.update(1.0);

        assertFalse(message.hasMessage());

        message.addMessage("test", 0, 0, 0L);
        message.addMessage("test2", 0, 0, 100L);

        assertTrue(message.hasMessage());

        message.update(1.0); // First message consumed, one remain

        assertTrue(message.hasMessage());

        UtilTests.pause(25L);

        message.update(1.0); // Message time still not elapsed

        assertTrue(message.hasMessage());

        UtilTests.pause(100L); // Message time elapsed

        message.update(1.0);

        assertFalse(message.hasMessage());
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

        assertFalse(message.hasMessage());
    }
}
