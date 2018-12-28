/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Origin}.
 */
public final class OriginTest
{
    /**
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testEnum() throws Exception
    {
        UtilTests.testEnum(Origin.class);
    }

    /**
     * Test the origin enum switch.
     */
    @Test
    public void testEnumSwitch()
    {
        for (final Origin origin : Origin.values())
        {
            switch (origin)
            {
                case TOP_LEFT:
                    assertEquals(3.0, origin.getX(3.0, 2.0));
                    assertEquals(3.0, origin.getY(3.0, 2.0));

                    assertEquals(3.0, origin.getX(3.0, 0.0));
                    assertEquals(3.0, origin.getY(3.0, 0.0));
                    break;
                case BOTTOM_LEFT:
                    assertEquals(3.0, origin.getX(3.0, 2.0));
                    assertEquals(1.0, origin.getY(3.0, 2.0));

                    assertEquals(3.0, origin.getX(3.0, 0.0));
                    assertEquals(3.0, origin.getY(3.0, 0.0));
                    break;
                case BOTTOM_RIGHT:
                    assertEquals(1.0, origin.getX(3.0, 2.0));
                    assertEquals(1.0, origin.getY(3.0, 2.0));

                    assertEquals(3.0, origin.getX(3.0, 0.0));
                    assertEquals(3.0, origin.getY(3.0, 0.0));
                    break;
                case CENTER_TOP:
                    assertEquals(2.0, origin.getX(3.0, 2.0));
                    assertEquals(3.0, origin.getY(3.0, 2.0));

                    assertEquals(3.0, origin.getX(3.0, 0.0));
                    assertEquals(3.0, origin.getY(3.0, 0.0));
                    break;
                case CENTER_BOTTOM:
                    assertEquals(2.0, origin.getX(3.0, 2.0));
                    assertEquals(1.0, origin.getY(3.0, 2.0));

                    assertEquals(3.0, origin.getX(3.0, 0.0));
                    assertEquals(3.0, origin.getY(3.0, 0.0));
                    break;
                case MIDDLE:
                    assertEquals(2.0, origin.getX(3.0, 2.0));
                    assertEquals(2.0, origin.getY(3.0, 2.0));

                    assertEquals(3.0, origin.getX(3.0, 0.0));
                    assertEquals(3.0, origin.getY(3.0, 0.0));
                    break;
                default:
                    throw new LionEngineException(origin);
            }
        }
    }

    /**
     * Test the origin enum fail.
     */
    @Test
    public void testEnumFail()
    {
        final Origin fail = UtilEnum.make(Origin.class, "FAIL");

        assertThrows(() -> assertEquals(-1.0, fail.getX(0.0, 0.0)), LionEngineException.ERROR_UNKNOWN_ENUM + fail);
        assertThrows(() -> assertEquals(-1.0, fail.getY(0.0, 0.0)), LionEngineException.ERROR_UNKNOWN_ENUM + fail);
    }
}
