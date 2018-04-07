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

import java.awt.Label;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link KeyboardAwt}.
 */
public final class KeyboardAwtTest
{
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
     * Test not pressed state.
     */
    @Test
    public void testNotPressed()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();

        Assert.assertFalse(keyboard.isPressed(KeyboardAwt.ALT));
        Assert.assertFalse(keyboard.isPressed(KeyboardAwt.BACK_SPACE));
        Assert.assertFalse(keyboard.isPressed(KeyboardAwt.CONTROL));
        Assert.assertFalse(keyboard.isPressed(KeyboardAwt.DOWN));
        Assert.assertFalse(keyboard.isPressed(KeyboardAwt.ENTER));
        Assert.assertFalse(keyboard.isPressed(KeyboardAwt.ESCAPE));
        Assert.assertFalse(keyboard.isPressed(KeyboardAwt.LEFT));
        Assert.assertFalse(keyboard.isPressed(KeyboardAwt.RIGHT));
        Assert.assertFalse(keyboard.isPressed(KeyboardAwt.SPACE));
        Assert.assertFalse(keyboard.isPressed(KeyboardAwt.TAB));
        Assert.assertFalse(keyboard.isPressed(KeyboardAwt.UP));
        Assert.assertFalse(keyboard.isPressedOnce(KeyboardAwt.UP));
    }

    /**
     * Test pressed.
     */
    @Test
    public void testPressed()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();

        keyboard.keyTyped(createEvent(KeyboardAwt.ALT));

        keyboard.keyPressed(createEvent(KeyboardAwt.ALT));
        Assert.assertTrue(keyboard.isPressed(KeyboardAwt.ALT));
        Assert.assertTrue(keyboard.isPressedOnce(KeyboardAwt.ALT));
        Assert.assertFalse(keyboard.isPressedOnce(KeyboardAwt.ALT));
        Assert.assertEquals(KeyboardAwt.ALT, keyboard.getKeyCode());
        Assert.assertEquals(' ', keyboard.getKeyName());
        Assert.assertTrue(keyboard.used());

        keyboard.keyReleased(createEvent(KeyboardAwt.ALT));
        Assert.assertFalse(keyboard.isPressed(KeyboardAwt.ALT));
        Assert.assertFalse(keyboard.used());
    }

    /**
     * Test pressed twice.
     */
    @Test
    public void testPressedTwice()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();

        keyboard.keyPressed(createEvent(KeyboardAwt.ALT));
        Assert.assertTrue(keyboard.isPressed(KeyboardAwt.ALT));

        keyboard.keyPressed(createEvent(KeyboardAwt.ALT));
        Assert.assertTrue(keyboard.isPressed(KeyboardAwt.ALT));
    }

    /**
     * Test directions.
     */
    @Test
    public void testDirections()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();

        keyboard.setHorizontalControlNegative(KeyboardAwt.LEFT);
        keyboard.setVerticalControlNegative(KeyboardAwt.DOWN);
        keyboard.setHorizontalControlPositive(KeyboardAwt.RIGHT);
        keyboard.setVerticalControlPositive(KeyboardAwt.UP);

        keyboard.keyPressed(createEvent(KeyboardAwt.RIGHT));
        Assert.assertEquals(1.0, keyboard.getHorizontalDirection(), 0.0001);
        keyboard.keyReleased(createEvent(KeyboardAwt.RIGHT));
        keyboard.keyPressed(createEvent(KeyboardAwt.LEFT));
        Assert.assertEquals(-1.0, keyboard.getHorizontalDirection(), 0.0001);
        keyboard.keyReleased(createEvent(KeyboardAwt.LEFT));
        Assert.assertEquals(0.0, keyboard.getHorizontalDirection(), 0.0001);

        keyboard.keyPressed(createEvent(KeyboardAwt.UP));
        Assert.assertEquals(1.0, keyboard.getVerticalDirection(), 0.0001);
        keyboard.keyReleased(createEvent(KeyboardAwt.UP));
        keyboard.keyPressed(createEvent(KeyboardAwt.DOWN));
        Assert.assertEquals(-1.0, keyboard.getVerticalDirection(), 0.0001);
        keyboard.keyReleased(createEvent(KeyboardAwt.DOWN));
        Assert.assertEquals(0.0, keyboard.getVerticalDirection(), 0.0001);
    }

    /**
     * Test events.
     */
    @Test
    public void testEvents()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();
        final AtomicBoolean left = new AtomicBoolean(false);

        keyboard.addActionPressed(KeyboardAwt.LEFT, () -> left.set(true));
        keyboard.addActionPressed(KeyboardAwt.LEFT, () -> left.set(true));
        keyboard.addActionReleased(KeyboardAwt.LEFT, () -> left.set(false));
        keyboard.addActionReleased(KeyboardAwt.LEFT, () -> left.set(false));
        Assert.assertFalse(left.get());

        keyboard.keyPressed(createEvent(KeyboardAwt.LEFT));
        Assert.assertTrue(left.get());

        keyboard.keyReleased(createEvent(KeyboardAwt.LEFT));
        Assert.assertFalse(left.get());

        keyboard.removeActionsPressed();
        keyboard.removeActionsReleased();

        keyboard.keyPressed(createEvent(KeyboardAwt.LEFT));
        Assert.assertFalse(left.get());

        keyboard.keyReleased(createEvent(KeyboardAwt.LEFT));
        Assert.assertFalse(left.get());
    }
}
