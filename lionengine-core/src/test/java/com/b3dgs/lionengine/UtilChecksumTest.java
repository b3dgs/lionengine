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

import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link UtilChecksum}.
 */
public final class UtilChecksumTest
{
    /**
     * Test the constructor.
     */
    @Test
    public void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilChecksum.class);
    }

    /**
     * Test encoding string.
     */
    @Test
    public void testEncodingString()
    {
        final String keyToBeEncoded = "keyToBeEncoded";
        final String signature = UtilChecksum.getSha(keyToBeEncoded);

        assertTrue(UtilChecksum.checkSha(keyToBeEncoded, signature));
        assertFalse(UtilChecksum.checkSha("anotherKey", signature));
    }

    /**
     * Test encoding integer.
     */
    @Test
    public void testEncodingInt()
    {
        final int value = 489_464_795;
        final String signature = UtilChecksum.getSha(value);

        assertTrue(UtilChecksum.checkSha(value, signature));
        assertFalse(UtilChecksum.checkSha(2_456_135, signature));
    }

    /**
     * Test encoding <code>null</code> string.
     */
    @Test
    public void testEncodingEmptyNullString()
    {
        assertThrows(() -> UtilChecksum.getSha((String) null), Check.ERROR_NULL);
    }

    /**
     * Test encoding bytes.
     */
    @Test
    public void testEncodingEmptyNullBytes()
    {
        assertThrows(() -> UtilChecksum.getSha((byte[]) null), Check.ERROR_NULL);
    }

    /**
     * Test unknown algorithm.
     */
    @Test
    public void testUnknownAlgorithm()
    {
        assertThrows(() -> UtilReflection.getMethod(UtilChecksum.class, "create", "void"),
                     UtilChecksum.ERROR_ALGORITHM + "void");
    }
}
