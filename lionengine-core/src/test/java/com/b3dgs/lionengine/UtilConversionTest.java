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
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertArrayEquals;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link UtilConversion}.
 */
final class UtilConversionTest
{
    /**
     * Test the constructor.
     */
    @Test
    void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilConversion.class);
    }

    /**
     * Test short.
     */
    @Test
    void testShort()
    {
        final short s = 12_345;

        assertEquals(s, UtilConversion.byteArrayToShort(UtilConversion.shortToByteArray(s)));
        assertEquals(s, UtilConversion.fromUnsignedShort(UtilConversion.toUnsignedShort(s)));
    }

    /**
     * Test int.
     */
    @Test
    void testInt()
    {
        final int i = 123_456_789;

        assertEquals(i, UtilConversion.byteArrayToInt(UtilConversion.intToByteArray(i)));
    }

    /**
     * Test byte.
     */
    @Test
    void testByte()
    {
        final byte b = 123;

        assertEquals(b, UtilConversion.fromUnsignedByte(UtilConversion.toUnsignedByte(b)));
    }

    /**
     * Test mask.
     */
    @Test
    void testMask()
    {
        assertEquals(255, UtilConversion.mask(255));
        assertEquals(0, UtilConversion.mask(256));
        assertEquals(1, UtilConversion.mask(257));
    }

    /**
     * Test to title case.
     */
    @Test
    void testToTitleCase()
    {
        assertEquals("Title", UtilConversion.toTitleCase("title"));
    }

    /**
     * Test to title case word.
     */
    @Test
    void testToTitleCaseWord()
    {
        assertEquals("Title Toto", UtilConversion.toTitleCaseWord("title toto"));
        assertEquals("Title Toto", UtilConversion.toTitleCaseWord("title_toto"));
        assertEquals("Title Toto", UtilConversion.toTitleCaseWord("title-toto"));
        assertEquals("Title Toto", UtilConversion.toTitleCaseWord("title%toto"));
        assertEquals("Titletoto", UtilConversion.toTitleCaseWord("titletoto"));
        assertEquals("Title To To", UtilConversion.toTitleCaseWord("title to to"));
    }

    /**
     * Test boolean to int.
     */
    @Test
    void testBoolToInt()
    {
        assertEquals(0, UtilConversion.boolToInt(false));
        assertEquals(1, UtilConversion.boolToInt(true));
    }

    /**
     * Test from binary.
     */
    @Test
    void testFromBinary()
    {
        assertEquals(0, UtilConversion.fromBinary(new boolean[1]));
        assertEquals(1, UtilConversion.fromBinary(new boolean[]
        {
            true
        }));
        assertEquals(2, UtilConversion.fromBinary(new boolean[]
        {
            true, false
        }));
        assertEquals(3, UtilConversion.fromBinary(new boolean[]
        {
            true, true
        }));
        assertEquals(4, UtilConversion.fromBinary(new boolean[]
        {
            true, false, false
        }));
    }

    /**
     * Test to binary.
     */
    @Test
    void testToBinary()
    {
        assertArrayEquals(new boolean[1], UtilConversion.toBinary(0, 1));
        assertArrayEquals(new boolean[4], UtilConversion.toBinary(0, 4));
        assertArrayEquals(new boolean[]
        {
            true
        }, UtilConversion.toBinary(1, 1));
        assertArrayEquals(new boolean[]
        {
            true, false
        }, UtilConversion.toBinary(2, 2));
    }

    /**
     * Test invert binary.
     */
    @Test
    void testInvert()
    {
        assertArrayEquals(new boolean[]
        {
            true, false, true
        }, UtilConversion.invert(new boolean[]
        {
            false, true, false
        }));

        for (int i = 0; i < 32; i++)
        {
            final boolean[] table = new boolean[i];
            for (int j = 0; j < table.length; j++)
            {
                if (j % 1 == 0)
                {
                    table[j] = false;
                }
                else
                {
                    table[j] = true;
                }
            }
            final boolean[] inverted = UtilConversion.invert(table);

            assertEquals(table.length, inverted.length);

            for (int j = 0; j < inverted.length; j++)
            {
                assertTrue(inverted[j] == !table[j]);
            }
        }
    }
}
