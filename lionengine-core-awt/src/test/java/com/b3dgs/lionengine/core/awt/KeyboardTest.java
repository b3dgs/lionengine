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
import java.awt.event.KeyEvent;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the keyboard class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class KeyboardTest
{
    /** Keyboard instance. */
    private static final KeyboardAwt KEYBOARD = new KeyboardAwt();

    /**
     * Create a key event.
     * 
     * @param key The event key.
     * @return The event instance.
     */
    static KeyEvent createEvent(Integer key)
    {
        return new KeyEvent(new Label(), 0, 0L, 0, key.intValue(), ' ');
    }

    /**
     * Test the keyboard not pressed state.
     */
    @Test
    public void testNotPressed()
    {
        Assert.assertFalse(KEYBOARD.isPressed(Keyboard.ALT));
        Assert.assertFalse(KEYBOARD.isPressed(Keyboard.BACK_SPACE));
        Assert.assertFalse(KEYBOARD.isPressed(Keyboard.CONTROL));
        Assert.assertFalse(KEYBOARD.isPressed(Keyboard.DOWN));
        Assert.assertFalse(KEYBOARD.isPressed(Keyboard.ENTER));
        Assert.assertFalse(KEYBOARD.isPressed(Keyboard.ESCAPE));
        Assert.assertFalse(KEYBOARD.isPressed(Keyboard.LEFT));
        Assert.assertFalse(KEYBOARD.isPressed(Keyboard.RIGHT));
        Assert.assertFalse(KEYBOARD.isPressed(Keyboard.SPACE));
        Assert.assertFalse(KEYBOARD.isPressed(Keyboard.TAB));
        Assert.assertFalse(KEYBOARD.isPressed(Keyboard.UP));
        Assert.assertFalse(KEYBOARD.isPressedOnce(Keyboard.UP));
    }

    /**
     * Test the keyboard pressed.
     */
    @Test
    public void testPressed()
    {
        KEYBOARD.keyTyped(createEvent(Keyboard.ALT));

        KEYBOARD.keyPressed(createEvent(Keyboard.ALT));
        Assert.assertTrue(KEYBOARD.isPressed(Keyboard.ALT));
        Assert.assertTrue(KEYBOARD.isPressedOnce(Keyboard.ALT));
        Assert.assertFalse(KEYBOARD.isPressedOnce(Keyboard.ALT));
        Assert.assertEquals(KEYBOARD.getKeyCode(), Keyboard.ALT);
        Assert.assertEquals(KEYBOARD.getKeyName(), ' ');
        Assert.assertTrue(KEYBOARD.used());

        KEYBOARD.keyReleased(createEvent(Keyboard.ALT));
        Assert.assertFalse(KEYBOARD.isPressed(Keyboard.ALT));
        Assert.assertFalse(KEYBOARD.used());
    }

    /**
     * Test the keyboard directions.
     */
    @Test
    public void testDirections()
    {
        KEYBOARD.setHorizontalControlNegative(Keyboard.LEFT);
        KEYBOARD.setVerticalControlNegative(Keyboard.DOWN);
        KEYBOARD.setHorizontalControlPositive(Keyboard.RIGHT);
        KEYBOARD.setVerticalControlPositive(Keyboard.UP);

        KEYBOARD.keyPressed(createEvent(Keyboard.RIGHT));
        Assert.assertEquals(1.0, KEYBOARD.getHorizontalDirection(), 0.0001);
        KEYBOARD.keyReleased(createEvent(Keyboard.RIGHT));
        KEYBOARD.keyPressed(createEvent(Keyboard.LEFT));
        Assert.assertEquals(-1.0, KEYBOARD.getHorizontalDirection(), 0.0001);
        KEYBOARD.keyReleased(createEvent(Keyboard.LEFT));
        Assert.assertEquals(0.0, KEYBOARD.getHorizontalDirection(), 0.0001);

        KEYBOARD.keyPressed(createEvent(Keyboard.UP));
        Assert.assertEquals(1.0, KEYBOARD.getVerticalDirection(), 0.0001);
        KEYBOARD.keyReleased(createEvent(Keyboard.UP));
        KEYBOARD.keyPressed(createEvent(Keyboard.DOWN));
        Assert.assertEquals(-1.0, KEYBOARD.getVerticalDirection(), 0.0001);
        KEYBOARD.keyReleased(createEvent(Keyboard.DOWN));
        Assert.assertEquals(0.0, KEYBOARD.getVerticalDirection(), 0.0001);
    }
}
