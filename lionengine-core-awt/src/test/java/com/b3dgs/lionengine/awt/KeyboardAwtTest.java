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
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.awt.Label;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

/**
 * Test {@link KeyboardAwt}.
 */
final class KeyboardAwtTest
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
    void testNotPressed()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();

        assertFalse(keyboard.isPushed(KeyboardAwt.ALT));
        assertFalse(keyboard.isPushed(KeyboardAwt.BACK_SPACE));
        assertFalse(keyboard.isPushed(KeyboardAwt.CONTROL_LEFT));
        assertFalse(keyboard.isPushed(KeyboardAwt.CONTROL_RIGHT));
        assertFalse(keyboard.isPushed(KeyboardAwt.DOWN));
        assertFalse(keyboard.isPushed(KeyboardAwt.ENTER));
        assertFalse(keyboard.isPushed(KeyboardAwt.ESCAPE));
        assertFalse(keyboard.isPushed(KeyboardAwt.LEFT));
        assertFalse(keyboard.isPushed(KeyboardAwt.RIGHT));
        assertFalse(keyboard.isPushed(KeyboardAwt.SPACE));
        assertFalse(keyboard.isPushed(KeyboardAwt.TAB));
        assertFalse(keyboard.isPushed(KeyboardAwt.UP));
    }

    /**
     * Test pressed.
     */
    @Test
    void testPressed()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();
        keyboard.keyTyped(createEvent(KeyboardAwt.ALT));
        keyboard.keyPressed(createEvent(KeyboardAwt.ALT));

        assertTrue(keyboard.isPushed(KeyboardAwt.ALT));
        assertEquals(KeyboardAwt.ALT, keyboard.getPushed());
        assertEquals(' ', keyboard.getKeyName());
        assertTrue(keyboard.used());

        keyboard.keyReleased(createEvent(KeyboardAwt.ALT));

        assertFalse(keyboard.isPushed(KeyboardAwt.ALT));
        assertFalse(keyboard.used());
    }

    /**
     * Test pressed twice.
     */
    @Test
    void testPressedTwice()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();

        keyboard.keyPressed(createEvent(KeyboardAwt.ALT));

        assertTrue(keyboard.isPushed(KeyboardAwt.ALT));

        keyboard.keyPressed(createEvent(KeyboardAwt.ALT));

        assertTrue(keyboard.isPushed(KeyboardAwt.ALT));
    }

    /**
     * Test events.
     */
    @Test
    void testEvents()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();
        final AtomicBoolean left = new AtomicBoolean(false);

        keyboard.addActionPressed(KeyboardAwt.LEFT, () -> left.set(true));
        keyboard.addActionPressed(KeyboardAwt.LEFT, () -> left.set(true));
        keyboard.addActionReleased(KeyboardAwt.LEFT, () -> left.set(false));
        keyboard.addActionReleased(KeyboardAwt.LEFT, () -> left.set(false));

        assertFalse(left.get());

        keyboard.keyPressed(createEvent(KeyboardAwt.LEFT));

        assertTrue(left.get());

        keyboard.keyReleased(createEvent(KeyboardAwt.LEFT));

        assertFalse(left.get());

        keyboard.removeActionsPressed();
        keyboard.removeActionsReleased();

        keyboard.keyPressed(createEvent(KeyboardAwt.LEFT));

        assertFalse(left.get());

        keyboard.keyReleased(createEvent(KeyboardAwt.LEFT));

        assertFalse(left.get());
    }
}
