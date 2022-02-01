/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Test {@link DeviceActionModel}.
 */
final class DeviceActionModelTest
{
    private final DevicePushMock mock = new DevicePushMock();

    /**
     * Test invalid arguments.
     */
    @Test
    void testConstructorInvalid()
    {
        assertThrows(LionEngineException.class,
                     () -> new DeviceActionModel((DeviceAxis) null, mock),
                     "Unexpected null argument !");

        assertThrows(LionEngineException.class,
                     () -> new DeviceActionModel((Integer) null, mock),
                     "Unexpected null argument !");

        assertThrows(LionEngineException.class,
                     () -> new DeviceActionModel(new DeviceAxis(Integer.valueOf(0), Integer.valueOf(0)), null),
                     "Unexpected null argument !");

        assertThrows(LionEngineException.class,
                     () -> new DeviceActionModel(Integer.valueOf(0), null),
                     "Unexpected null argument !");
    }

    /**
     * Test constructor 1.
     */
    @Test
    void testConstructor1()
    {
        final DeviceAxis axis = new DeviceAxis(Integer.valueOf(1), Integer.valueOf(2));
        final DeviceAction action = new DeviceActionModel(axis, mock);

        assertEquals(0.0, action.getAction());

        mock.setPushed(Integer.valueOf(1));

        assertEquals(1.0, action.getAction());

        mock.setPushed(Integer.valueOf(2));

        assertEquals(-1.0, action.getAction());
    }

    /**
     * Test constructor 2.
     */
    @Test
    void testConstructor2()
    {
        final DeviceAction action = new DeviceActionModel(Integer.valueOf(1), mock);

        assertEquals(0.0, action.getAction());

        mock.setPushed(Integer.valueOf(1));

        assertEquals(1.0, action.getAction());

        mock.setPushed(Integer.valueOf(2));

        assertEquals(0.0, action.getAction());
    }
}
