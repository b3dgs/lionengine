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
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Test {@link DeviceAxis}.
 */
final class DeviceAxisTest
{
    /**
     * Test invalid arguments.
     */
    @Test
    void testConstructorInvalid()
    {
        assertThrows(LionEngineException.class,
                     () -> new DeviceAxis(null, Integer.valueOf(0)),
                     "Unexpected null argument !");

        assertThrows(LionEngineException.class,
                     () -> new DeviceAxis(Integer.valueOf(0), null),
                     "Unexpected null argument !");
    }

    /**
     * Test getter.
     */
    @Test
    void testGetter()
    {
        final DeviceAxis axis = new DeviceAxis(Integer.valueOf(1), Integer.valueOf(-1));

        assertEquals(Integer.valueOf(1), axis.getPositive());
        assertEquals(Integer.valueOf(-1), axis.getNegative());
    }
}
