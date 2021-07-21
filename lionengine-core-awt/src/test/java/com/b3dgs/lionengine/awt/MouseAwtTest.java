/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.awt.Label;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Resolution;

/**
 * Test {@link MouseAwt}.
 */
final class MouseAwtTest
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
    private static MouseEvent createEvent(Integer click, int x, int y)
    {
        return new MouseEvent(new Label(), 0, 0L, 0, x, y, 3, false, click.intValue());
    }

    /**
     * Test clicked state.
     */
    @Test
    void testClicked()
    {
        final MouseAwt mouse = createMouse();
        final MouseListener clicker = mouse.getClicker();

        assertFalse(mouse.isPushed(MouseAwt.LEFT));
        clicker.mousePressed(createEvent(MouseAwt.LEFT, 0, 0));
        assertTrue(mouse.isPushed(MouseAwt.LEFT));
        clicker.mouseReleased(createEvent(MouseAwt.LEFT, 0, 0));
        assertFalse(mouse.isPushed(MouseAwt.LEFT));

        assertFalse(mouse.isPushed(MouseAwt.RIGHT));
        clicker.mousePressed(createEvent(MouseAwt.RIGHT, 0, 0));
        assertTrue(mouse.isPushed(MouseAwt.RIGHT));
        clicker.mouseReleased(createEvent(MouseAwt.RIGHT, 0, 0));
        assertFalse(mouse.isPushed(MouseAwt.RIGHT));
    }

    /**
     * Test click.
     */
    @Test
    void testClick()
    {
        final MouseAwt mouse = createMouse();
        final MouseListener clicker = mouse.getClicker();

        clicker.mousePressed(createEvent(MouseAwt.MIDDLE, 0, 0));

        assertEquals(MouseAwt.MIDDLE, mouse.getPushed());

        clicker.mouseReleased(createEvent(MouseAwt.MIDDLE, 0, 0));

        assertNotEquals(MouseAwt.MIDDLE, mouse.getPushed());
    }

    /**
     * Test location.
     */
    @Test
    void testLocation()
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
    void testDoClick()
    {
        final MouseAwt mouse = createMouse();
        final MouseMotionListener mover = mouse.getMover();

        assertFalse(mouse.isPushed(MouseAwt.RIGHT));

        mover.mouseMoved(createEvent(MouseAwt.LEFT, 0, 0));
        mouse.doClickAt(MouseAwt.RIGHT, 500, 500);
        mouse.update(1.0);
        try
        {
            assertEquals(500, mouse.getOnScreenX());
            assertEquals(500, mouse.getOnScreenY());
            assertTrue(mouse.isPushed(MouseAwt.RIGHT));
        }
        finally
        {
            mouse.doClickAt(MouseAwt.LEFT, 499, 499);
        }

        assertTrue(mouse.isPushed(MouseAwt.LEFT));

        mouse.doClick(MouseAwt.LEFT);

        assertTrue(mouse.isPushed(MouseAwt.LEFT));

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
    void testDoClickOutRange()
    {
        final MouseAwt mouse = createMouse();
        mouse.doClick(Integer.valueOf(Integer.MAX_VALUE));
        mouse.update(1.0);
        mouse.update(1.0);

        assertFalse(mouse.isPushed(Integer.valueOf(Integer.MAX_VALUE)));
    }

    /**
     * Test move.
     */
    @Test
    void testMouse()
    {
        final MouseAwt mouse = createMouse();
        final MouseListener clicker = mouse.getClicker();
        final MouseMotionListener mover = mouse.getMover();

        mover.mouseMoved(createEvent(MouseAwt.LEFT, 0, 0));
        mover.mouseDragged(createEvent(Integer.valueOf(0), 0, 0));
        mouse.update(1.0);

        assertEquals(0, mouse.getMoveX());
        assertEquals(0, mouse.getMoveY());

        clicker.mouseEntered(null);
        clicker.mouseExited(null);
        clicker.mouseClicked(null);
    }

    /**
     * Test event.
     */
    @Test
    void testEvent()
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
}
