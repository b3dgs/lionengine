/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.awt;

import java.awt.Label;
import java.awt.event.MouseEvent;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Resolution;

/**
 * Test the mouse class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class MouseTest
{
    /** Mouse instance. */
    private static final MouseAwt MOUSE = new MouseAwt();

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        final Resolution resolution = new Resolution(320, 240, 60);
        final Config config = new Config(resolution, 32, false);
        config.setSource(resolution);
        MOUSE.setConfig(config);
        MOUSE.setCenter(160, 120);
    }

    /**
     * Create a mouse event.
     * 
     * @param click The click mouse.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The event instance.
     */
    private static MouseEvent createEvent(int click, int x, int y)
    {
        return new MouseEvent(new Label(), 0, 0L, 0, x, y, 3, false, click);
    }

    /**
     * Test the mouse clicked state.
     */
    @Test
    public void testClicked()
    {
        Assert.assertFalse(MOUSE.hasClicked(Mouse.LEFT));
        MOUSE.mousePressed(createEvent(Mouse.LEFT, 0, 0));
        Assert.assertTrue(MOUSE.hasClicked(Mouse.LEFT));
        MOUSE.mouseReleased(createEvent(Mouse.LEFT, 0, 0));
        Assert.assertFalse(MOUSE.hasClicked(Mouse.LEFT));

        Assert.assertFalse(MOUSE.hasClicked(Mouse.RIGHT));
        MOUSE.mousePressed(createEvent(Mouse.RIGHT, 0, 0));
        Assert.assertTrue(MOUSE.hasClicked(Mouse.RIGHT));
        MOUSE.mouseReleased(createEvent(Mouse.RIGHT, 0, 0));
        Assert.assertFalse(MOUSE.hasClicked(Mouse.RIGHT));

        Assert.assertFalse(MOUSE.hasClickedOnce(Mouse.MIDDLE));
        MOUSE.mousePressed(createEvent(Mouse.MIDDLE, 0, 0));
        Assert.assertTrue(MOUSE.hasClickedOnce(Mouse.MIDDLE));
        MOUSE.mouseReleased(createEvent(Mouse.MIDDLE, 0, 0));
        Assert.assertFalse(MOUSE.hasClickedOnce(Mouse.MIDDLE));
    }

    /**
     * Test the mouse click.
     */
    @Test
    public void testClick()
    {
        MOUSE.mousePressed(createEvent(Mouse.MIDDLE, 0, 0));
        Assert.assertEquals(Mouse.MIDDLE, MOUSE.getClick());
        MOUSE.mouseReleased(createEvent(Mouse.MIDDLE, 0, 0));
        Assert.assertNotEquals(Mouse.MIDDLE, MOUSE.getClick());
    }

    /**
     * Test the mouse on screen.
     */
    @Test
    public void testLocation()
    {
        MOUSE.mouseMoved(createEvent(Mouse.LEFT, 0, 0));
        Assert.assertEquals(0, MOUSE.getX());
        Assert.assertEquals(0, MOUSE.getY());

        MOUSE.mouseMoved(createEvent(Mouse.LEFT, 10, 20));
        Assert.assertEquals(10, MOUSE.getX());
        Assert.assertEquals(20, MOUSE.getY());
    }

    /**
     * Test the mouse do click robot.
     */
    @Test
    public void testDoClick()
    {
        Assert.assertFalse(MOUSE.hasClicked(Mouse.RIGHT));
        MOUSE.doClickAt(Mouse.RIGHT, 500, 500);
        Assert.assertEquals(0, MOUSE.getOnScreenX());
        Assert.assertEquals(0, MOUSE.getOnScreenY());
        Assert.assertFalse(MOUSE.hasClicked(Mouse.RIGHT));

        Assert.assertFalse(MOUSE.hasClicked(Mouse.LEFT));
        MOUSE.doClick(Mouse.LEFT);
        Assert.assertFalse(MOUSE.hasClicked(Mouse.LEFT));
        MOUSE.doClick(Mouse.MIDDLE);

        MOUSE.lock();
        MOUSE.lock(500, 500);
    }

    /**
     * Test the mouse move.
     */
    @Test
    public void testMouse()
    {
        MOUSE.mouseDragged(createEvent(0, 0, 0));
        MOUSE.update(1.0);
        Assert.assertEquals(0, MOUSE.getMoveX());
        Assert.assertEquals(0, MOUSE.getMoveY());
        Assert.assertTrue(MOUSE.hasMoved());
        Assert.assertFalse(MOUSE.hasMoved());

        MOUSE.mouseEntered(null);
        MOUSE.mouseExited(null);
        MOUSE.mouseWheelMoved(null);
        MOUSE.mouseClicked(null);
    }
}
