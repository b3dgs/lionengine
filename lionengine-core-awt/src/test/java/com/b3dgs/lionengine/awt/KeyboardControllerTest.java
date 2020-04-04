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
package com.b3dgs.lionengine.awt;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.io.InputDeviceControl;

/**
 * Test {@link KeyboardController}.
 */
public final class KeyboardControllerTest
{
    /**
     * Test the device.
     */
    @Test
    public void testDevice()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();
        final InputDeviceControl device = new KeyboardController(keyboard);
        keyboard.keyPressed(KeyboardAwtTest.createEvent(Integer.valueOf(1)));
        keyboard.keyPressed(KeyboardAwtTest.createEvent(Integer.valueOf(4)));
        keyboard.keyPressed(KeyboardAwtTest.createEvent(Integer.valueOf(0)));

        device.setFireButton(Integer.valueOf(0), Integer.valueOf(0));
        device.setHorizontalControlNegative(Integer.valueOf(1));
        device.setHorizontalControlPositive(Integer.valueOf(2));
        device.setVerticalControlNegative(Integer.valueOf(3));
        device.setVerticalControlPositive(Integer.valueOf(4));

        assertEquals(Integer.valueOf(1), device.getHorizontalControlNegative());
        assertEquals(Integer.valueOf(2), device.getHorizontalControlPositive());
        assertEquals(Integer.valueOf(3), device.getVerticalControlNegative());
        assertEquals(Integer.valueOf(4), device.getVerticalControlPositive());

        assertEquals(-1.0, device.getHorizontalDirection());
        assertEquals(1.0, device.getVerticalDirection());

        assertFalse(device.isFireButton(null));
        assertFalse(device.isFireButtonOnce(null));
        assertTrue(device.isFireButton(Integer.valueOf(0)));
        assertTrue(device.isFireButtonOnce(Integer.valueOf(0)));

        keyboard.keyReleased(KeyboardAwtTest.createEvent(Integer.valueOf(0)));
        keyboard.keyPressed(KeyboardAwtTest.createEvent(Integer.valueOf(3)));
        keyboard.keyPressed(KeyboardAwtTest.createEvent(Integer.valueOf(2)));

        assertTrue(device.isDownButtonOnce());
        assertTrue(device.isLeftButtonOnce());
        assertTrue(device.isRightButtonOnce());
        assertTrue(device.isUpButtonOnce());
    }
}
