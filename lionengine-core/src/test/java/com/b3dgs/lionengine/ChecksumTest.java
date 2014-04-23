/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the checksum class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ChecksumTest
{
    /** Key to encode. */
    private static final String STRING = "keyToBeEncoded";
    /** Another key to encode. */
    private static final String OTHER = "anotherKey";
    /** A value to encode. */
    private static final int VALUE = 489464795;

    /**
     * Test the checksum constructor.
     * 
     * @throws IllegalArgumentException If error.
     * @throws IllegalAccessException If error.
     * @throws InstantiationException If error.
     * @throws SecurityException If error.
     * @throws NoSuchMethodException If error.
     */
    @Test
    public void testChecksumConstructor() throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, NoSuchMethodException, SecurityException
    {
        final Constructor<Checksum> checksum = Checksum.class.getDeclaredConstructor(String.class);
        checksum.setAccessible(true);
        try
        {
            final Checksum clazz = checksum.newInstance("null");
            Assert.assertNotNull(clazz);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }
    }

    /**
     * Test checksum creation.
     */
    @Test
    public void testChecksumCreation()
    {
        final Checksum checksum = Checksum.createSha256();
        Assert.assertNotNull(checksum);
    }

    /**
     * Test checksum encoding.
     */
    @Test
    public void testChecksumEncoding()
    {
        final Checksum checksum = Checksum.createSha256();
        final String signature = checksum.getSha256(ChecksumTest.STRING);
        final String test = checksum.getSha256(ChecksumTest.VALUE);

        Assert.assertTrue(checksum.check(ChecksumTest.STRING, signature));
        Assert.assertFalse(checksum.check(ChecksumTest.OTHER, signature));
        Assert.assertTrue(checksum.check(ChecksumTest.VALUE, test));
    }
}
