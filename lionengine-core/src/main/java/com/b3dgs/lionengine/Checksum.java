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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * SHA-256 based checksum manipulation.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Checksum checksum = Checksum.createSha256();
 * final int integer = 489464795;
 * final String value = &quot;keyToBeEncoded&quot;;
 * final String other = &quot;anotherKey&quot;;
 * final String signature = checksum.getSha256(value);
 * final String test = checksum.getSha256(integer);
 * 
 * Assert.assertTrue(checksum.check(value, signature));
 * Assert.assertFalse(checksum.check(other, signature));
 * Assert.assertTrue(checksum.check(integer, test));
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Checksum
{
    /** Instance error message. */
    private static final String ERROR_SHA_INSTANCE = "SHA-256 can not be instantiated !";
    /** SHA Mode. */
    private static final String SHA = "SHA-256";

    /**
     * Create a new checksum.
     * 
     * @return The checksum instance.
     * @throws LionEngineException If algorithm is invalid.
     */
    public static Checksum createSha256() throws LionEngineException
    {
        return new Checksum(SHA);
    }

    /** Message digest instance. */
    private final MessageDigest sha;

    /**
     * Private constructor.
     * 
     * @param algorithm The algorithm name.
     * @throws LionEngineException If algorithm is invalid.
     */
    private Checksum(String algorithm) throws LionEngineException
    {
        try
        {
            sha = MessageDigest.getInstance(algorithm);
        }
        catch (final NoSuchAlgorithmException exception)
        {
            throw new LionEngineException(exception, ERROR_SHA_INSTANCE);
        }
    }

    /**
     * Compare a checksum with its supposed original value.
     * 
     * @param value The original value.
     * @param signature The checksum value.
     * @return <code>true</code> if corresponding (checksum of value is equal to its signature), <code>false</code>
     *         else.
     */
    public boolean check(String value, String signature)
    {
        return Arrays.equals(getSha256(value).getBytes(Constant.UTF_8), signature.getBytes(Constant.UTF_8));
    }

    /**
     * Compare a checksum with its supposed original value.
     * 
     * @param value The original value.
     * @param signature The checksum value.
     * @return <code>true</code> if corresponding (checksum of value is equal to its signature), <code>false</code>
     *         else.
     */
    public boolean check(int value, String signature)
    {
        return Arrays.equals(getSha256(value).getBytes(Constant.UTF_8), signature.getBytes(Constant.UTF_8));
    }

    /**
     * Get the SHA-256 signature of the input bytes.
     * 
     * @param bytes The input bytes.
     * @return The bytes signature.
     */
    public String getSha256(byte[] bytes)
    {
        final byte[] v = sha.digest(bytes);
        final StringBuilder builder = new StringBuilder(84);
        for (final byte b : v)
        {
            builder.append(0xFF & b);
        }
        return builder.toString();
    }

    /**
     * Get the SHA-256 signature of the input integer.
     * 
     * @param i The input integer.
     * @return The integer signature.
     */
    public String getSha256(int i)
    {
        return getSha256(UtilConversion.intToByteArray(i));
    }

    /**
     * Get the SHA-256 signature of the input string.
     * 
     * @param str The input integer.
     * @return The string signature.
     */
    public String getSha256(String str)
    {
        return getSha256(str.getBytes(Constant.UTF_8));
    }
}
