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
     * Test the core class.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test(expected = InvocationTargetException.class)
    public void testConstructor() throws ReflectiveOperationException
    {
        final Constructor<Checksum> checksum = Checksum.class.getDeclaredConstructor(String.class);
        checksum.setAccessible(true);
        final Checksum clazz = checksum.newInstance("null");
        Assert.assertNotNull(clazz);
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
}
