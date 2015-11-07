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
package com.b3dgs.lionengine.core.swt;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.swt.EventAction;
import com.b3dgs.lionengine.core.swt.Mouse;
import com.b3dgs.lionengine.core.swt.MouseSwt;
import com.b3dgs.lionengine.core.swt.ToolsSwt;

/**
 * Test the mouse class.
 */
public class MouseAwtTest
{
    /** Shell. */
    private static final Shell SHELL = new Shell(ToolsSwt.getDisplay());

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        SHELL.dispose();
    }

    /**
     * Create a configured test mouse.
     * 
     * @return The mouse instance.
     */
    public static MouseSwt createMouse()
    {
        final Resolution resolution = new Resolution(320, 240, 60);
        final Config config = new Config(resolution, 32, false);
        config.setSource(resolution);

        final MouseSwt mouse = new MouseSwt(ToolsSwt.getDisplay());
        mouse.setConfig(config);

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
        final Event event = new Event();
        event.x = x;
        event.y = y;
        event.button = click;
        event.widget = SHELL;
        return new MouseEvent(event);
    }

    /**
     * Test the mouse clicked state.
     */
    @Test
    public void testClicked()
    {
        final MouseSwt mouse = createMouse();

        Assert.assertFalse(mouse.hasClicked(Mouse.LEFT));
        mouse.mouseDown(createEvent(Mouse.LEFT, 0, 0));
        Assert.assertTrue(mouse.hasClicked(Mouse.LEFT));
        mouse.mouseUp(createEvent(Mouse.LEFT, 0, 0));
        Assert.assertFalse(mouse.hasClicked(Mouse.LEFT));

        Assert.assertFalse(mouse.hasClicked(Mouse.RIGHT));
        mouse.mouseDown(createEvent(Mouse.RIGHT, 0, 0));
        Assert.assertTrue(mouse.hasClicked(Mouse.RIGHT));
        mouse.mouseUp(createEvent(Mouse.RIGHT, 0, 0));
        Assert.assertFalse(mouse.hasClicked(Mouse.RIGHT));

        Assert.assertFalse(mouse.hasClickedOnce(Mouse.MIDDLE));
        mouse.mouseDown(createEvent(Mouse.MIDDLE, 0, 0));
        Assert.assertTrue(mouse.hasClickedOnce(Mouse.MIDDLE));
        mouse.mouseUp(createEvent(Mouse.MIDDLE, 0, 0));
        Assert.assertFalse(mouse.hasClickedOnce(Mouse.MIDDLE));
    }

    /**
     * Test the mouse click.
     */
    @Test
    public void testClick()
    {
        final MouseSwt mouse = createMouse();

        mouse.mouseDown(createEvent(Mouse.MIDDLE, 0, 0));
        Assert.assertEquals(Mouse.MIDDLE, mouse.getClick());
        mouse.mouseUp(createEvent(Mouse.MIDDLE, 0, 0));
        Assert.assertNotEquals(Mouse.MIDDLE, mouse.getClick());
    }

    /**
     * Test the mouse on screen.
     */
    @Test
    public void testLocation()
    {
        final MouseSwt mouse = createMouse();

        mouse.mouseMove(createEvent(Mouse.LEFT, 0, 0));
        Assert.assertEquals(0, mouse.getX());
        Assert.assertEquals(0, mouse.getY());

        mouse.mouseMove(createEvent(Mouse.LEFT, 10, 20));
        Assert.assertEquals(10, mouse.getX());
        Assert.assertEquals(20, mouse.getY());
    }

    /**
     * Test the mouse move.
     */
    @Test
    public void testMouse()
    {
        final MouseSwt mouse = createMouse();

        mouse.mouseMove(createEvent(0, 0, 0));
        mouse.mouseMove(createEvent(0, 0, 0));
        mouse.update(1.0);
        Assert.assertEquals(0, mouse.getMoveX());
        Assert.assertEquals(0, mouse.getMoveY());
        Assert.assertTrue(mouse.hasMoved());
        Assert.assertFalse(mouse.hasMoved());
    }

    /**
     * Test the mouse event.
     */
    @Test
    public void testEvent()
    {
        final MouseSwt mouse = createMouse();
        final AtomicBoolean left = new AtomicBoolean(false);

        mouse.addActionPressed(Mouse.LEFT, new EventAction()
        {

            @Override
            public void action()
            {
                left.set(true);
            }
        });
        mouse.addActionPressed(Mouse.LEFT, new EventAction()
        {

            @Override
            public void action()
            {
                left.set(true);
            }
        });
        mouse.addActionReleased(Mouse.LEFT, new EventAction()
        {

            @Override
            public void action()
            {
                left.set(false);
            }
        });
        mouse.addActionReleased(Mouse.LEFT, new EventAction()
        {

            @Override
            public void action()
            {
                left.set(false);
            }
        });
        Assert.assertFalse(left.get());

        mouse.mouseDown(createEvent(Mouse.LEFT, 0, 0));
        Assert.assertTrue(left.get());

        mouse.mouseUp(createEvent(Mouse.LEFT, 0, 0));
        Assert.assertFalse(left.get());
    }
}
