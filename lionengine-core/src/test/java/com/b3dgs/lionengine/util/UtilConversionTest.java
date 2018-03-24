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
package com.b3dgs.lionengine.util;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Test {@link UtilConversion}.
 */
public class UtilConversionTest
{
    /**
     * Test constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(UtilConversion.class);
    }

    /**
     * Test short.
     */
    @Test
    public void testShort()
    {
        final short s = 12345;
        Assert.assertEquals(s, UtilConversion.byteArrayToShort(UtilConversion.shortToByteArray(s)));
        Assert.assertEquals(s, UtilConversion.fromUnsignedShort(UtilConversion.toUnsignedShort(s)));
    }

    /**
     * Test int.
     */
    @Test
    public void testInt()
    {
        final int i = 123456789;
        Assert.assertEquals(i, UtilConversion.byteArrayToInt(UtilConversion.intToByteArray(i)));
    }

    /**
     * Test byte.
     */
    @Test
    public void testByte()
    {
        final byte b = 123;
        Assert.assertEquals(b, UtilConversion.fromUnsignedByte(UtilConversion.toUnsignedByte(b)));
    }

    /**
     * Test mask.
     */
    @Test
    public void testMask()
    {
        Assert.assertEquals(255, UtilConversion.mask(255));
        Assert.assertEquals(0, UtilConversion.mask(256));
        Assert.assertEquals(1, UtilConversion.mask(257));
    }

    /**
     * Test to title case.
     */
    @Test
    public void testToTitleCase()
    {
        final String title = UtilConversion.toTitleCase("title");
        Assert.assertEquals("Title", title);
    }

    /**
     * Test to title case word.
     */
    @Test
    public void testToTitleCaseWord()
    {
        final String word = UtilConversion.toTitleCaseWord("title toto");
        Assert.assertEquals("Title Toto", word);
    }

    /**
     * Test boolean to int.
     */
    @Test
    public void testBoolToInt()
    {
        Assert.assertEquals(0, UtilConversion.boolToInt(false));
        Assert.assertEquals(1, UtilConversion.boolToInt(true));
    }

    /**
     * Test from binary.
     */
    @Test
    public void testFromBinary()
    {
        Assert.assertEquals(0, UtilConversion.fromBinary(new boolean[1]));
        Assert.assertEquals(1, UtilConversion.fromBinary(new boolean[]
        {
            true
        }));
        Assert.assertEquals(2, UtilConversion.fromBinary(new boolean[]
        {
            true, false
        }));
        Assert.assertEquals(3, UtilConversion.fromBinary(new boolean[]
        {
            true, true
        }));
        Assert.assertEquals(4, UtilConversion.fromBinary(new boolean[]
        {
            true, false, false
        }));
    }

    /**
     * Test to binary.
     */
    @Test
    public void testToBinary()
    {
        Assert.assertArrayEquals(new boolean[1], UtilConversion.toBinary(0, 1));
        Assert.assertArrayEquals(new boolean[4], UtilConversion.toBinary(0, 4));
        Assert.assertArrayEquals(new boolean[]
        {
            true
        }, UtilConversion.toBinary(1, 1));
        Assert.assertArrayEquals(new boolean[]
        {
            true, false
        }, UtilConversion.toBinary(2, 2));
    }

    /**
     * Test invert binary.
     */
    @Test
    public void testInvert()
    {
        Assert.assertArrayEquals(new boolean[]
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

            Assert.assertEquals(table.length, inverted.length);
            for (int j = 0; j < inverted.length; j++)
            {
                Assert.assertTrue(inverted[j] == !table[j]);
            }
        }
    }
}
