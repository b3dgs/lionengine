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
package com.b3dgs.lionengine.headless;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.UtilTests;

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

        Assert.assertFalse(keyboard.isPressed(KeyboardHeadless.DOWN));
        Assert.assertFalse(keyboard.isPressed(KeyboardHeadless.LEFT));
        Assert.assertFalse(keyboard.isPressed(KeyboardHeadless.RIGHT));
        Assert.assertFalse(keyboard.isPressed(KeyboardHeadless.SPACE));
        Assert.assertFalse(keyboard.isPressed(KeyboardHeadless.UP));
        Assert.assertFalse(keyboard.isPressedOnce(KeyboardHeadless.UP));
    }

    /**
     * Test pressed.
     */
    @Test
    public void testPressed()
    {
        final KeyboardHeadless keyboard = new KeyboardHeadless();

        keyboard.keyPressed(createEvent(KeyboardHeadless.DOWN));
        Assert.assertTrue(keyboard.isPressed(KeyboardHeadless.DOWN));
        Assert.assertTrue(keyboard.isPressedOnce(KeyboardHeadless.DOWN));
        Assert.assertFalse(keyboard.isPressedOnce(KeyboardHeadless.DOWN));
        Assert.assertEquals(KeyboardHeadless.DOWN, keyboard.getKeyCode());
        Assert.assertEquals(' ', keyboard.getKeyName());
        Assert.assertTrue(keyboard.used());

        keyboard.keyReleased(createEvent(KeyboardHeadless.DOWN));
        Assert.assertFalse(keyboard.isPressed(KeyboardHeadless.DOWN));
        Assert.assertFalse(keyboard.used());
    }

    /**
     * Test pressed twice.
     */
    @Test
    public void testPressedTwice()
    {
        final KeyboardHeadless keyboard = new KeyboardHeadless();

        keyboard.keyPressed(createEvent(KeyboardHeadless.DOWN));
        Assert.assertTrue(keyboard.isPressed(KeyboardHeadless.DOWN));

        keyboard.keyPressed(createEvent(KeyboardHeadless.DOWN));
        Assert.assertTrue(keyboard.isPressed(KeyboardHeadless.DOWN));
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

        keyboard.keyPressed(createEvent(KeyboardHeadless.RIGHT));
        Assert.assertEquals(1.0, keyboard.getHorizontalDirection(), UtilTests.PRECISION);
        keyboard.keyReleased(createEvent(KeyboardHeadless.RIGHT));
        keyboard.keyPressed(createEvent(KeyboardHeadless.LEFT));
        Assert.assertEquals(-1.0, keyboard.getHorizontalDirection(), UtilTests.PRECISION);
        keyboard.keyReleased(createEvent(KeyboardHeadless.LEFT));
        Assert.assertEquals(0.0, keyboard.getHorizontalDirection(), UtilTests.PRECISION);

        keyboard.keyPressed(createEvent(KeyboardHeadless.UP));
        Assert.assertEquals(1.0, keyboard.getVerticalDirection(), UtilTests.PRECISION);
        keyboard.keyReleased(createEvent(KeyboardHeadless.UP));
        keyboard.keyPressed(createEvent(KeyboardHeadless.DOWN));
        Assert.assertEquals(-1.0, keyboard.getVerticalDirection(), UtilTests.PRECISION);
        keyboard.keyReleased(createEvent(KeyboardHeadless.DOWN));
        Assert.assertEquals(0.0, keyboard.getVerticalDirection(), UtilTests.PRECISION);
    }
}
