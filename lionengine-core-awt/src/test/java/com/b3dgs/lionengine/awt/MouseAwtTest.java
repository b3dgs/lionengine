/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.awt;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.awt.GraphicsEnvironment;
import java.awt.Label;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.Verbose;

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

        assertFalse(mouse.hasClicked(MouseAwt.LEFT));
        clicker.mousePressed(createEvent(MouseAwt.LEFT, 0, 0));
        assertTrue(mouse.hasClicked(MouseAwt.LEFT));
        clicker.mouseReleased(createEvent(MouseAwt.LEFT, 0, 0));
        assertFalse(mouse.hasClicked(MouseAwt.LEFT));

        assertFalse(mouse.hasClicked(MouseAwt.RIGHT));
        clicker.mousePressed(createEvent(MouseAwt.RIGHT, 0, 0));
        assertTrue(mouse.hasClicked(MouseAwt.RIGHT));
        clicker.mouseReleased(createEvent(MouseAwt.RIGHT, 0, 0));
        assertFalse(mouse.hasClicked(MouseAwt.RIGHT));

        assertFalse(mouse.hasClickedOnce(MouseAwt.MIDDLE));
        clicker.mousePressed(createEvent(MouseAwt.MIDDLE, 0, 0));
        assertTrue(mouse.hasClickedOnce(MouseAwt.MIDDLE));
        clicker.mouseReleased(createEvent(MouseAwt.MIDDLE, 0, 0));
        assertFalse(mouse.hasClickedOnce(MouseAwt.MIDDLE));

        clicker.mousePressed(createEvent(MouseAwt.MIDDLE, 0, 0));
        assertTrue(mouse.hasClickedOnce(MouseAwt.MIDDLE));
        assertFalse(mouse.hasClickedOnce(MouseAwt.MIDDLE));
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

        assertEquals(MouseAwt.MIDDLE, mouse.getClick());

        clicker.mouseReleased(createEvent(MouseAwt.MIDDLE, 0, 0));

        assertNotEquals(MouseAwt.MIDDLE, mouse.getClick());
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

        assertEquals(0, mouse.getX());
        assertEquals(0, mouse.getY());

        mover.mouseMoved(createEvent(MouseAwt.LEFT, 10, 20));

        assertEquals(10, mouse.getX());
        assertEquals(20, mouse.getY());
    }

    /**
     * Test do click robot.
     */
    @Test
    public void testDoClick()
    {
        final MouseAwt mouse = createMouse();
        final MouseMotionListener mover = mouse.getMover();

        assertFalse(mouse.hasClicked(MouseAwt.RIGHT));

        mover.mouseMoved(createEvent(MouseAwt.LEFT, 0, 0));
        mouse.doClickAt(MouseAwt.RIGHT, 500, 500);
        mouse.update(1.0);
        try
        {
            assertEquals(500, mouse.getOnScreenX());
            assertEquals(500, mouse.getOnScreenY());
            assertTrue(mouse.hasClicked(MouseAwt.RIGHT));
        }
        finally
        {
            mouse.doClickAt(MouseAwt.LEFT, 499, 499);
        }

        assertTrue(mouse.hasClicked(MouseAwt.LEFT));

        mouse.doClick(MouseAwt.LEFT);

        assertTrue(mouse.hasClicked(MouseAwt.LEFT));

        mouse.update(1.0);
        mouse.doClick(MouseAwt.MIDDLE);
        mouse.update(1.0);
        mouse.doSetMouse(1, 2);

        assertEquals(1, mouse.getOnScreenX());
        assertEquals(2, mouse.getOnScreenY());

        mouse.doMoveMouse(3, 4);

        assertEquals(3, mouse.getOnScreenX());
        assertEquals(4, mouse.getOnScreenY());

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

        assertFalse(mouse.hasClickedOnce(Integer.MAX_VALUE));
        assertFalse(mouse.hasClicked(Integer.MAX_VALUE));
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

        assertEquals(0, mouse.getMoveX());
        assertEquals(0, mouse.getMoveY());
        assertTrue(mouse.hasMoved());
        assertFalse(mouse.hasMoved());

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

        assertFalse(left.get());

        final MouseListener clicker = mouse.getClicker();
        clicker.mousePressed(createEvent(MouseAwt.LEFT, 0, 0));

        assertTrue(left.get());

        clicker.mouseReleased(createEvent(MouseAwt.LEFT, 0, 0));

        assertFalse(left.get());
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
            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
            final MouseAwt mouse = MouseAwtTest.createMouse();
            Verbose.info("****************************************************************************************");
            mouse.lock(1, 2);

            assertEquals(1, mouse.getX());
            assertEquals(2, mouse.getY());
        }
        finally
        {
            field.set(GraphicsEnvironment.class, old);
        }
    }
}
