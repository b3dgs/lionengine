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
package com.b3dgs.lionengine.awt;

import java.awt.GraphicsEnvironment;
import java.awt.Label;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilReflection;

/**
 * Test {@link MouseAwt}.
 */
public final class MouseAwtTest
{
    /**
     * Create a mouse and configure for test.
     * 
     * @return The created mouse.
     */
    static MouseAwt createMouse()
    {
        final Resolution resolution = new Resolution(320, 240, 60);
        final Config config = new Config(resolution, 32, false);
        config.setSource(resolution);

        final MouseAwt mouse = new MouseAwt();
        mouse.setResolution(resolution, resolution);
        mouse.setCenter(160, 120);

        return mouse;
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
     * Test clicked state.
     */
    @Test
    public void testClicked()
    {
        final MouseAwt mouse = createMouse();
        final MouseListener clicker = mouse.getClicker();

        Assert.assertFalse(mouse.hasClicked(MouseAwt.LEFT));
        clicker.mousePressed(createEvent(MouseAwt.LEFT, 0, 0));
        Assert.assertTrue(mouse.hasClicked(MouseAwt.LEFT));
        clicker.mouseReleased(createEvent(MouseAwt.LEFT, 0, 0));
        Assert.assertFalse(mouse.hasClicked(MouseAwt.LEFT));

        Assert.assertFalse(mouse.hasClicked(MouseAwt.RIGHT));
        clicker.mousePressed(createEvent(MouseAwt.RIGHT, 0, 0));
        Assert.assertTrue(mouse.hasClicked(MouseAwt.RIGHT));
        clicker.mouseReleased(createEvent(MouseAwt.RIGHT, 0, 0));
        Assert.assertFalse(mouse.hasClicked(MouseAwt.RIGHT));

        Assert.assertFalse(mouse.hasClickedOnce(MouseAwt.MIDDLE));
        clicker.mousePressed(createEvent(MouseAwt.MIDDLE, 0, 0));
        Assert.assertTrue(mouse.hasClickedOnce(MouseAwt.MIDDLE));
        clicker.mouseReleased(createEvent(MouseAwt.MIDDLE, 0, 0));
        Assert.assertFalse(mouse.hasClickedOnce(MouseAwt.MIDDLE));

        clicker.mousePressed(createEvent(MouseAwt.MIDDLE, 0, 0));
        Assert.assertTrue(mouse.hasClickedOnce(MouseAwt.MIDDLE));
        Assert.assertFalse(mouse.hasClickedOnce(MouseAwt.MIDDLE));
    }

    /**
     * Test click.
     */
    @Test
    public void testClick()
    {
        final MouseAwt mouse = createMouse();
        final MouseListener clicker = mouse.getClicker();

        clicker.mousePressed(createEvent(MouseAwt.MIDDLE, 0, 0));
        Assert.assertEquals(MouseAwt.MIDDLE, mouse.getClick());
        clicker.mouseReleased(createEvent(MouseAwt.MIDDLE, 0, 0));
        Assert.assertNotEquals(MouseAwt.MIDDLE, mouse.getClick());
    }

    /**
     * Test location.
     */
    @Test
    public void testLocation()
    {
        final MouseAwt mouse = createMouse();
        final MouseMotionListener mover = mouse.getMover();

        mover.mouseMoved(createEvent(MouseAwt.LEFT, 0, 0));
        Assert.assertEquals(0, mouse.getX());
        Assert.assertEquals(0, mouse.getY());

        mover.mouseMoved(createEvent(MouseAwt.LEFT, 10, 20));
        Assert.assertEquals(10, mouse.getX());
        Assert.assertEquals(20, mouse.getY());
    }

    /**
     * Test do click robot.
     */
    @Test
    public void testDoClick()
    {
        final MouseAwt mouse = createMouse();
        final MouseMotionListener mover = mouse.getMover();

        Assert.assertFalse(mouse.hasClicked(MouseAwt.RIGHT));
        mover.mouseMoved(createEvent(MouseAwt.LEFT, 0, 0));
        mouse.doClickAt(MouseAwt.RIGHT, 500, 500);
        mouse.update(1.0);
        try
        {
            Assert.assertEquals(500, mouse.getOnScreenX());
            Assert.assertEquals(500, mouse.getOnScreenY());
            Assert.assertTrue(mouse.hasClicked(MouseAwt.RIGHT));
        }
        finally
        {
            mouse.doClickAt(MouseAwt.LEFT, 499, 499);
        }

        Assert.assertTrue(mouse.hasClicked(MouseAwt.LEFT));
        mouse.doClick(MouseAwt.LEFT);
        Assert.assertTrue(mouse.hasClicked(MouseAwt.LEFT));
        mouse.update(1.0);
        mouse.doClick(MouseAwt.MIDDLE);
        mouse.update(1.0);

        mouse.doSetMouse(1, 2);
        Assert.assertEquals(1, mouse.getOnScreenX());
        Assert.assertEquals(2, mouse.getOnScreenY());

        mouse.doMoveMouse(3, 4);
        Assert.assertEquals(3, mouse.getOnScreenX());
        Assert.assertEquals(4, mouse.getOnScreenY());

        mouse.lock();
        mouse.lock(500, 500);
    }

    /**
     * Test do click robot with out range click.
     */
    @Test
    public void testDoClickOutRange()
    {
        final MouseAwt mouse = createMouse();
        mouse.doClick(Integer.MAX_VALUE);
        mouse.update(1.0);
        mouse.update(1.0);

        Assert.assertFalse(mouse.hasClickedOnce(Integer.MAX_VALUE));
        Assert.assertFalse(mouse.hasClicked(Integer.MAX_VALUE));
    }

    /**
     * Test move.
     */
    @Test
    public void testMouse()
    {
        final MouseAwt mouse = createMouse();
        final MouseListener clicker = mouse.getClicker();
        final MouseMotionListener mover = mouse.getMover();

        mover.mouseMoved(createEvent(MouseAwt.LEFT, 0, 0));
        mover.mouseDragged(createEvent(0, 0, 0));
        mouse.update(1.0);
        Assert.assertEquals(0, mouse.getMoveX());
        Assert.assertEquals(0, mouse.getMoveY());
        Assert.assertTrue(mouse.hasMoved());
        Assert.assertFalse(mouse.hasMoved());

        clicker.mouseEntered(null);
        clicker.mouseExited(null);
        clicker.mouseClicked(null);
    }

    /**
     * Test event.
     */
    @Test
    public void testEvent()
    {
        final MouseAwt mouse = createMouse();
        final AtomicBoolean left = new AtomicBoolean(false);

        mouse.addActionPressed(MouseAwt.LEFT, () -> left.set(true));
        mouse.addActionPressed(MouseAwt.LEFT, () -> left.set(true));
        mouse.addActionReleased(MouseAwt.LEFT, () -> left.set(false));
        mouse.addActionReleased(MouseAwt.LEFT, () -> left.set(false));
        Assert.assertFalse(left.get());

        final MouseListener clicker = mouse.getClicker();

        clicker.mousePressed(createEvent(MouseAwt.LEFT, 0, 0));
        Assert.assertTrue(left.get());

        clicker.mouseReleased(createEvent(MouseAwt.LEFT, 0, 0));
        Assert.assertFalse(left.get());
    }

    /**
     * Test headless case.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testHeadless() throws Exception
    {
        final Object old = UtilReflection.getField(GraphicsEnvironment.class, "headless");
        final Field field = GraphicsEnvironment.class.getDeclaredField("headless");
        UtilReflection.setAccessible(field, true);
        field.set(GraphicsEnvironment.class, Boolean.TRUE);
        try
        {
            final MouseAwt mouse = MouseAwtTest.createMouse();
            mouse.lock(1, 2);
            Assert.assertEquals(1, mouse.getX());
            Assert.assertEquals(2, mouse.getY());
        }
        finally
        {
            field.set(GraphicsEnvironment.class, old);
        }
    }
}
