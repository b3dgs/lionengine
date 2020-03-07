/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.headless;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link KeyboardHeadless}.
 */
public final class KeyboardHeadlessTest
{
    /**
     * Create a key event.
     * 
     * @param key The event key.
     * @return The event instance.
     */
    private static KeyEvent createEvent(Integer key)
    {
        return new KeyEvent(key.intValue(), ' ');
    }

    /**
     * Test not pressed state.
     */
    @Test
    public void testNotPressed()
    {
        final KeyboardHeadless keyboard = new KeyboardHeadless();

        assertFalse(keyboard.isPressed(KeyboardHeadless.DOWN));
        assertFalse(keyboard.isPressed(KeyboardHeadless.LEFT));
        assertFalse(keyboard.isPressed(KeyboardHeadless.RIGHT));
        assertFalse(keyboard.isPressed(KeyboardHeadless.SPACE));
        assertFalse(keyboard.isPressed(KeyboardHeadless.UP));
        assertFalse(keyboard.isPressedOnce(KeyboardHeadless.UP));
    }

    /**
     * Test pressed.
     */
    @Test
    public void testPressed()
    {
        final KeyboardHeadless keyboard = new KeyboardHeadless();
        keyboard.keyPressed(createEvent(KeyboardHeadless.DOWN));

        assertTrue(keyboard.isPressed(KeyboardHeadless.DOWN));
        assertTrue(keyboard.isPressedOnce(KeyboardHeadless.DOWN));
        assertFalse(keyboard.isPressedOnce(KeyboardHeadless.DOWN));
        assertEquals(KeyboardHeadless.DOWN, keyboard.getKeyCode());
        assertEquals(' ', keyboard.getKeyName());
        assertTrue(keyboard.used());

        keyboard.keyReleased(createEvent(KeyboardHeadless.DOWN));

        assertFalse(keyboard.isPressed(KeyboardHeadless.DOWN));
        assertFalse(keyboard.used());
    }

    /**
     * Test pressed twice.
     */
    @Test
    public void testPressedTwice()
    {
        final KeyboardHeadless keyboard = new KeyboardHeadless();
        keyboard.keyPressed(createEvent(KeyboardHeadless.DOWN));

        assertTrue(keyboard.isPressed(KeyboardHeadless.DOWN));

        keyboard.keyPressed(createEvent(KeyboardHeadless.DOWN));

        assertTrue(keyboard.isPressed(KeyboardHeadless.DOWN));
    }

    /**
     * Test directions.
     */
    @Test
    public void testDirections()
    {
        final KeyboardHeadless keyboard = new KeyboardHeadless();

        keyboard.setHorizontalControlNegative(KeyboardHeadless.LEFT);
        keyboard.setVerticalControlNegative(KeyboardHeadless.DOWN);
        keyboard.setHorizontalControlPositive(KeyboardHeadless.RIGHT);
        keyboard.setVerticalControlPositive(KeyboardHeadless.UP);

        assertEquals(KeyboardHeadless.LEFT, keyboard.getHorizontalControlNegative());
        assertEquals(KeyboardHeadless.DOWN, keyboard.getVerticalControlNegative());
        assertEquals(KeyboardHeadless.RIGHT, keyboard.getHorizontalControlPositive());
        assertEquals(KeyboardHeadless.UP, keyboard.getVerticalControlPositive());

        keyboard.keyPressed(createEvent(KeyboardHeadless.RIGHT));

        assertEquals(1.0, keyboard.getHorizontalDirection());

        keyboard.keyReleased(createEvent(KeyboardHeadless.RIGHT));
        keyboard.keyPressed(createEvent(KeyboardHeadless.LEFT));

        assertEquals(-1.0, keyboard.getHorizontalDirection());

        keyboard.keyReleased(createEvent(KeyboardHeadless.LEFT));

        assertEquals(0.0, keyboard.getHorizontalDirection());

        keyboard.keyPressed(createEvent(KeyboardHeadless.UP));

        assertEquals(1.0, keyboard.getVerticalDirection());

        keyboard.keyReleased(createEvent(KeyboardHeadless.UP));
        keyboard.keyPressed(createEvent(KeyboardHeadless.DOWN));

        assertEquals(-1.0, keyboard.getVerticalDirection());

        keyboard.keyReleased(createEvent(KeyboardHeadless.DOWN));

        assertEquals(0.0, keyboard.getVerticalDirection());
    }
}
