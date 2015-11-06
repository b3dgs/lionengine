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
package com.b3dgs.lionengine.test.core.awt;

import java.awt.Label;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.core.awt.EventAction;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.KeyboardAwt;

/**
 * Test the keyboard class.
 */
public class KeyboardAwtTest
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
     * Test the keyboard not pressed state.
     */
    @Test
    public void testNotPressed()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();

        Assert.assertFalse(keyboard.isPressed(Keyboard.ALT));
        Assert.assertFalse(keyboard.isPressed(Keyboard.BACK_SPACE));
        Assert.assertFalse(keyboard.isPressed(Keyboard.CONTROL));
        Assert.assertFalse(keyboard.isPressed(Keyboard.DOWN));
        Assert.assertFalse(keyboard.isPressed(Keyboard.ENTER));
        Assert.assertFalse(keyboard.isPressed(Keyboard.ESCAPE));
        Assert.assertFalse(keyboard.isPressed(Keyboard.LEFT));
        Assert.assertFalse(keyboard.isPressed(Keyboard.RIGHT));
        Assert.assertFalse(keyboard.isPressed(Keyboard.SPACE));
        Assert.assertFalse(keyboard.isPressed(Keyboard.TAB));
        Assert.assertFalse(keyboard.isPressed(Keyboard.UP));
        Assert.assertFalse(keyboard.isPressedOnce(Keyboard.UP));
    }

    /**
     * Test the keyboard pressed.
     */
    @Test
    public void testPressed()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();

        keyboard.keyTyped(createEvent(Keyboard.ALT));

        keyboard.keyPressed(createEvent(Keyboard.ALT));
        Assert.assertTrue(keyboard.isPressed(Keyboard.ALT));
        Assert.assertTrue(keyboard.isPressedOnce(Keyboard.ALT));
        Assert.assertFalse(keyboard.isPressedOnce(Keyboard.ALT));
        Assert.assertEquals(keyboard.getKeyCode(), Keyboard.ALT);
        Assert.assertEquals(keyboard.getKeyName(), ' ');
        Assert.assertTrue(keyboard.used());

        keyboard.keyReleased(createEvent(Keyboard.ALT));
        Assert.assertFalse(keyboard.isPressed(Keyboard.ALT));
        Assert.assertFalse(keyboard.used());
    }

    /**
     * Test the keyboard directions.
     */
    @Test
    public void testDirections()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();

        keyboard.setHorizontalControlNegative(Keyboard.LEFT);
        keyboard.setVerticalControlNegative(Keyboard.DOWN);
        keyboard.setHorizontalControlPositive(Keyboard.RIGHT);
        keyboard.setVerticalControlPositive(Keyboard.UP);

        keyboard.keyPressed(createEvent(Keyboard.RIGHT));
        Assert.assertEquals(1.0, keyboard.getHorizontalDirection(), 0.0001);
        keyboard.keyReleased(createEvent(Keyboard.RIGHT));
        keyboard.keyPressed(createEvent(Keyboard.LEFT));
        Assert.assertEquals(-1.0, keyboard.getHorizontalDirection(), 0.0001);
        keyboard.keyReleased(createEvent(Keyboard.LEFT));
        Assert.assertEquals(0.0, keyboard.getHorizontalDirection(), 0.0001);

        keyboard.keyPressed(createEvent(Keyboard.UP));
        Assert.assertEquals(1.0, keyboard.getVerticalDirection(), 0.0001);
        keyboard.keyReleased(createEvent(Keyboard.UP));
        keyboard.keyPressed(createEvent(Keyboard.DOWN));
        Assert.assertEquals(-1.0, keyboard.getVerticalDirection(), 0.0001);
        keyboard.keyReleased(createEvent(Keyboard.DOWN));
        Assert.assertEquals(0.0, keyboard.getVerticalDirection(), 0.0001);
    }

    /**
     * Test the keyboard events.
     */
    @Test
    public void testEvents()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();
        final AtomicBoolean left = new AtomicBoolean(false);

        keyboard.addActionPressed(Keyboard.LEFT, new EventAction()
        {
            @Override
            public void action()
            {
                left.set(true);
            }
        });
        keyboard.addActionPressed(Keyboard.LEFT, new EventAction()
        {
            @Override
            public void action()
            {
                left.set(true);
            }
        });
        keyboard.addActionReleased(Keyboard.LEFT, new EventAction()
        {
            @Override
            public void action()
            {
                left.set(false);
            }
        });
        keyboard.addActionReleased(Keyboard.LEFT, new EventAction()
        {
            @Override
            public void action()
            {
                left.set(false);
            }
        });
        Assert.assertFalse(left.get());

        keyboard.keyPressed(createEvent(Keyboard.LEFT));
        Assert.assertTrue(left.get());

        keyboard.keyReleased(createEvent(Keyboard.LEFT));
        Assert.assertFalse(left.get());

        keyboard.removeActionsPressed();
        keyboard.removeActionsReleased();

        keyboard.keyPressed(createEvent(Keyboard.LEFT));
        Assert.assertFalse(left.get());

        keyboard.keyReleased(createEvent(Keyboard.LEFT));
        Assert.assertFalse(left.get());
    }
}
