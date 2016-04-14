/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test utility conversion class.
 */
public class UtilConversionTest
{
    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(UtilConversion.class);
    }

    /**
     * Test the utility conversion short.
     */
    @Test
    public void testShort()
    {
        final short s = 12345;
        Assert.assertEquals(s, UtilConversion.byteArrayToShort(UtilConversion.shortToByteArray(s)));
        Assert.assertEquals(s, UtilConversion.fromUnsignedShort(UtilConversion.toUnsignedShort(s)));
    }

    /**
     * Test the utility conversion int.
     */
    @Test
    public void testInt()
    {
        final int i = 123456789;
        Assert.assertEquals(i, UtilConversion.byteArrayToInt(UtilConversion.intToByteArray(i)));
    }

    /**
     * Test the utility conversion byte.
     */
    @Test
    public void testByte()
    {
        final byte b = 123;
        Assert.assertEquals(b, UtilConversion.fromUnsignedByte(UtilConversion.toUnsignedByte(b)));
    }

    /**
     * Test the utility conversion string.
     */
    @Test
    public void testString()
    {
        final String title = UtilConversion.toTitleCase("title");
        Assert.assertEquals("Title", title);

        final String word = UtilConversion.toTitleCaseWord("title toto");
        Assert.assertEquals("Title Toto", word);
    }

    /**
     * Test the utility conversion boolean.
     */
    @Test
    public void testBool()
    {
        Assert.assertEquals(0, UtilConversion.boolToInt(false));
        Assert.assertEquals(1, UtilConversion.boolToInt(true));
    }

    /**
     * Test the utility conversion from binary.
     */
    @Test
    public void testfromBinary()
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
     * Test the utility conversion to binary.
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
     * Test the utility invert binary.
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
