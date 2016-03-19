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
 * Test the checksum class.
 */
public class UtilChecksumTest
{
    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(UtilChecksum.class);
    }

    /**
     * Test checksum encoding.
     */
    @Test
    public void testEncodingString()
    {
        final String keyToBeEncoded = "keyToBeEncoded";
        final String signature = UtilChecksum.getSha256(keyToBeEncoded);

        Assert.assertTrue(UtilChecksum.checkSha256(keyToBeEncoded, signature));
        Assert.assertFalse(UtilChecksum.checkSha256("anotherKey", signature));
    }

    /**
     * Test checksum encoding integer.
     */
    @Test
    public void testEncodingInt()
    {
        final int value = 489464795;
        final String signature = UtilChecksum.getSha256(value);

        Assert.assertTrue(UtilChecksum.checkSha256(value, signature));
        Assert.assertFalse(UtilChecksum.checkSha256(2456135, signature));
    }

    /**
     * Test checksum encoding null string.
     */
    @Test(expected = NullPointerException.class)
    public void testEncodingEmptyNullString()
    {
        final String signature = UtilChecksum.getSha256((String) null);

        Assert.assertNull(signature);
    }

    /**
     * Test checksum encoding bytes.
     */
    @Test(expected = NullPointerException.class)
    public void testEncodingEmptyNullBytes()
    {
        final String signature = UtilChecksum.getSha256((byte[]) null);

        Assert.assertNull(signature);
    }

    /**
     * Test checksum encoding integer.
     */
    @Test(expected = LionEngineException.class)
    public void testUnknownAlgorithm()
    {
        Assert.assertNotNull(UtilReflection.getMethod(UtilChecksum.class, "create", "void"));
    }
}
