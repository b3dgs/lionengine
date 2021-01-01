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
package com.b3dgs.lionengine.io;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link InputDeviceControlDelegate}.
 */
final class InputDeviceControlDelegateTest
{
    private final InputDeviceControl delegate = new Delegate();

    /**
     * Get delegate.
     * 
     * @return The delegate.
     */
    private InputDeviceControl get()
    {
        return delegate;
    }

    /**
     * Test the device.
     */
    @Test
    void testDevice()
    {
        final InputDeviceControl device = new InputDeviceControlDelegate(this::get);

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

        assertTrue(device.isFireButton(Integer.valueOf(0)));
        assertTrue(device.isFireButtonOnce(Integer.valueOf(0)));
        assertTrue(device.isDownButtonOnce());
        assertTrue(device.isLeftButtonOnce());
        assertTrue(device.isRightButtonOnce());
        assertTrue(device.isUpButtonOnce());
    }

    private static class Delegate implements InputDeviceControl
    {
        private Integer hp;
        private Integer hn;
        private Integer vp;
        private Integer vn;

        @Override
        public void setVerticalControlPositive(Integer code)
        {
            vp = code;
        }

        @Override
        public void setVerticalControlNegative(Integer code)
        {
            vn = code;
        }

        @Override
        public void setHorizontalControlPositive(Integer code)
        {
            hp = code;
        }

        @Override
        public void setHorizontalControlNegative(Integer code)
        {
            hn = code;
        }

        @Override
        public double getVerticalDirection()
        {
            return 1.0;
        }

        @Override
        public Integer getVerticalControlPositive()
        {
            return vp;
        }

        @Override
        public Integer getVerticalControlNegative()
        {
            return vn;
        }

        @Override
        public double getHorizontalDirection()
        {
            return -1.0;
        }

        @Override
        public Integer getHorizontalControlPositive()
        {
            return hp;
        }

        @Override
        public Integer getHorizontalControlNegative()
        {
            return hn;
        }

        @Override
        public void setFireButton(Integer index, Integer code)
        {
            // Nothing to do
        }

        @Override
        public boolean isUpButtonOnce()
        {
            return true;
        }

        @Override
        public boolean isRightButtonOnce()
        {
            return true;
        }

        @Override
        public boolean isLeftButtonOnce()
        {
            return true;
        }

        @Override
        public boolean isFireButtonOnce(Integer index)
        {
            return true;
        }

        @Override
        public boolean isFireButton(Integer index)
        {
            return true;
        }

        @Override
        public boolean isDownButtonOnce()
        {
            return true;
        }
    }
}
