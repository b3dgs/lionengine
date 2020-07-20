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
package com.b3dgs.lionengine.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test {@link ColorGradient}.
 */
final class ColorGradientTest
{
    /**
     * Test <code>null</code> first color.
     */
    @Test
    void testNullFirstColor()
    {
        assertThrows(() -> new ColorGradient(0, 0, null, 0, 0, ColorRgba.BLACK), "Unexpected null argument !");
    }

    /**
     * Test <code>null</code> last color.
     */
    @Test
    void testNullLastColor()
    {
        assertThrows(() -> new ColorGradient(0, 0, ColorRgba.BLACK, 0, 0, null), "Unexpected null argument !");
    }

    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        final ColorGradient color = new ColorGradient(1, 2, ColorRgba.BLACK, 3, 4, ColorRgba.WHITE);

        assertEquals(1, color.getX1());
        assertEquals(2, color.getY1());
        assertEquals(ColorRgba.BLACK, color.getColor1());

        assertEquals(3, color.getX2());
        assertEquals(4, color.getY2());
        assertEquals(ColorRgba.WHITE, color.getColor2());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final ColorGradient color = new ColorGradient(0, 1, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE);

        assertEquals(color, color);
        assertEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE));

        assertNotEquals(color, null);
        assertNotEquals(color, new Object());
        assertNotEquals(color, new ColorGradient(1, 1, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE));
        assertNotEquals(color, new ColorGradient(0, 0, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE));
        assertNotEquals(color, new ColorGradient(0, 1, ColorRgba.WHITE, 2, 3, ColorRgba.WHITE));
        assertNotEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 0, 3, ColorRgba.WHITE));
        assertNotEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 2, 0, ColorRgba.WHITE));
        assertNotEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 2, 3, ColorRgba.BLACK));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final ColorGradient color = new ColorGradient(0, 1, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE);

        assertHashEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE));

        assertHashNotEquals(color, new Object());
        assertHashNotEquals(color, new ColorGradient(1, 1, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE));
        assertHashNotEquals(color, new ColorGradient(0, 0, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE));
        assertHashNotEquals(color, new ColorGradient(0, 1, ColorRgba.WHITE, 2, 3, ColorRgba.WHITE));
        assertHashNotEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 0, 3, ColorRgba.WHITE));
        assertHashNotEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 2, 0, ColorRgba.WHITE));
        assertHashNotEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 2, 3, ColorRgba.BLACK));
    }
}
