/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the checksum class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class ChecksumTest
{
    /** Key to encode. */
    private static final String STRING = "keyToBeEncoded";
    /** Another key to encode. */
    private static final String OTHER = "anotherKey";
    /** A value to encode. */
    private static final int VALUE = 489464795;

    /**
     * Test the constructor.
     * 
     * @throws Throwable If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Throwable
    {
        UtilTests.testPrivateConstructor(Checksum.class, "null");
    }

    /**
     * Test checksum creation.
     */
    @Test
    public void testCreation()
    {
        final Checksum checksum = Checksum.createSha256();
        Assert.assertNotNull(checksum);
    }

    /**
     * Test checksum encoding.
     */
    @Test
    public void testEncoding()
    {
        final Checksum checksum = Checksum.createSha256();
        final String signature = checksum.getSha256(STRING);
        final String test = checksum.getSha256(VALUE);

        Assert.assertTrue(checksum.check(STRING, signature));
        Assert.assertFalse(checksum.check(OTHER, signature));
        Assert.assertTrue(checksum.check(VALUE, test));
    }

    /**
     * Test checksum encoding null string.
     */
    @Test(expected = NullPointerException.class)
    public void testEncodingEmptyNullString()
    {
        final Checksum checksum = Checksum.createSha256();
        final String signature = checksum.getSha256((String) null);
        Assert.assertNull(signature);
    }

    /**
     * Test checksum encoding bytes.
     */
    @Test(expected = NullPointerException.class)
    public void testEncodingEmptyNullBytes()
    {
        final Checksum checksum = Checksum.createSha256();
        final String signature = checksum.getSha256((byte[]) null);
        Assert.assertNull(signature);
    }
}
