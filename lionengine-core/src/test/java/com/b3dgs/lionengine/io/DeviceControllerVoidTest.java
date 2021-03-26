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
import static com.b3dgs.lionengine.UtilAssert.assertFalse;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;

/**
 * Test {@link DeviceControllerVoid}.
 */
final class DeviceControllerVoidTest
{
    private final DeviceController device = DeviceControllerVoid.getInstance();

    /**
     * Test horizontal axis.
     */
    @Test
    public void testHorizontal()
    {
        assertEquals(0.0, device.getHorizontalDirection());

        device.addHorizontal(() -> null, () -> 1.0);

        assertEquals(0.0, device.getHorizontalDirection());

        device.update(1.0);

        assertEquals(0.0, device.getHorizontalDirection());
    }

    /**
     * Test vertical axis.
     */
    @Test
    public void testVertical()
    {
        assertEquals(0.0, device.getVerticalDirection());

        device.addVertical(() -> null, () -> 1.0);

        assertEquals(0.0, device.getVerticalDirection());

        device.update(1.0);

        assertEquals(0.0, device.getVerticalDirection());
    }

    /**
     * Test fire.
     */
    @Test
    public void testFire()
    {
        final Integer index = Integer.valueOf(1);
        final DeviceMapper mapper = new DeviceMapper()
        {
            @Override
            public Integer getIndex()
            {
                return index;
            }
        };
        final AtomicReference<Double> val = new AtomicReference<>(Double.valueOf(1.0));

        assertFalse(device.isFired(index));

        device.addFire(() -> null, index, () -> val.get().doubleValue());

        assertFalse(device.isFired(index));
        assertFalse(device.isFiredOnce(index));

        val.set(Double.valueOf(0.0));
        device.update(1.0);

        assertFalse(device.isFired(index));
        assertFalse(device.isFiredOnce(index));

        val.set(Double.valueOf(1.0));
        device.update(1.0);

        assertFalse(device.isFired(mapper));
        assertFalse(device.isFiredOnce(mapper));
    }

    /**
     * Test set disabled function.
     */
    @Test
    public void setDisabled()
    {
        final Integer index = Integer.valueOf(1);
        device.setDisabled(Constant.EMPTY_STRING, true, true);
        device.addHorizontal(() -> Constant.EMPTY_STRING, () -> 1.0);
        device.addVertical(() -> Constant.EMPTY_STRING, () -> 1.0);
        device.addFire(() -> Constant.EMPTY_STRING, index, () -> 1.0);
        device.update(1.0);

        assertEquals(0.0, device.getHorizontalDirection());
        assertEquals(0.0, device.getVerticalDirection());
        assertFalse(device.isFired(index));
        assertFalse(device.isFiredOnce(index));
    }
}
