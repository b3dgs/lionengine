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
package com.b3dgs.lionengine.io;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Test {@link InputDeviceControlVoid}.
 */
public final class InputDeviceControlVoidTest
{
    /**
     * Test the device.
     */
    @Test
    public void testDevice()
    {
        final InputDeviceControl device = InputDeviceControlVoid.getInstance();
        device.setFireButton(Integer.valueOf(2));
        device.setHorizontalControlNegative(Integer.valueOf(2));
        device.setHorizontalControlPositive(Integer.valueOf(2));
        device.setVerticalControlNegative(Integer.valueOf(2));
        device.setVerticalControlPositive(Integer.valueOf(2));

        assertNull(device.getHorizontalControlNegative());
        assertNull(device.getHorizontalControlPositive());
        assertNull(device.getVerticalControlNegative());
        assertNull(device.getVerticalControlPositive());

        assertEquals(0.0, device.getHorizontalDirection());
        assertEquals(0.0, device.getVerticalDirection());

        assertFalse(device.isFireButton());
        assertFalse(device.isFireButtonOnce());
        assertFalse(device.isDownButtonOnce());
        assertFalse(device.isLeftButtonOnce());
        assertFalse(device.isRightButtonOnce());
        assertFalse(device.isUpButtonOnce());
    }
}
