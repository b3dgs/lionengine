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
 * Test {@link UtilChecksum}.
 */
public final class UtilChecksumTest
{
    /**
     * Test constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(UtilChecksum.class);
    }

    /**
     * Test encoding string.
     */
    @Test
    public void testEncodingString()
    {
        final String keyToBeEncoded = "keyToBeEncoded";
        final String signature = UtilChecksum.getSha(keyToBeEncoded);

        Assert.assertTrue(UtilChecksum.checkSha(keyToBeEncoded, signature));
        Assert.assertFalse(UtilChecksum.checkSha("anotherKey", signature));
    }

    /**
     * Test encoding integer.
     */
    @Test
    public void testEncodingInt()
    {
        final int value = 489464795;
        final String signature = UtilChecksum.getSha(value);

        Assert.assertTrue(UtilChecksum.checkSha(value, signature));
        Assert.assertFalse(UtilChecksum.checkSha(2456135, signature));
    }

    /**
     * Test encoding <code>null</code> string.
     */
    @Test(expected = LionEngineException.class)
    public void testEncodingEmptyNullString()
    {
        Assert.assertNull(UtilChecksum.getSha((String) null));
    }

    /**
     * Test encoding bytes.
     */
    @Test(expected = LionEngineException.class)
    public void testEncodingEmptyNullBytes()
    {
        Assert.assertNull(UtilChecksum.getSha((byte[]) null));
    }

    /**
     * Test unknown algorithm.
     */
    @Test(expected = LionEngineException.class)
    public void testUnknownAlgorithm()
    {
        Assert.assertNotNull(UtilReflection.getMethod(UtilChecksum.class, "create", "void"));
    }
}
