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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * SHA-512 based checksum manipulation.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class UtilChecksum
{
    /** Instance error message. */
    static final String ERROR_ALGORITHM = "Unable to create algorithm: ";
    /** Message digest instance. */
    private static final MessageDigest SHA512 = create("SHA-512");
    /** Maximum length. */
    private static final int MAX_LENGTH = 178;

    /**
     * Compare a checksum with its supposed original value.
     * 
     * @param value The original value (must not be <code>null</code>).
     * @param signature The checksum value (must not be <code>null</code>).
     * @return <code>true</code> if corresponding (checksum of value is equal to its signature), <code>false</code>
     *         else.
     * @throws LionEngineException If invalid arguments.
     */
    public static boolean checkSha(String value, String signature)
    {
        Check.notNull(signature);

        return Arrays.equals(getSha(value).getBytes(StandardCharsets.UTF_8),
                             signature.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Compare a checksum with its supposed original value.
     * 
     * @param value The original value.
     * @param signature The checksum value (must not be <code>null</code>).
     * @return <code>true</code> if corresponding (checksum of value is equal to its signature), <code>false</code>
     *         else.
     * @throws LionEngineException If invalid arguments.
     */
    public static boolean checkSha(int value, String signature)
    {
        Check.notNull(signature);

        return Arrays.equals(getSha(value).getBytes(StandardCharsets.UTF_8),
                             signature.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Get the SHA-256 signature of the input bytes.
     * 
     * @param bytes The input bytes (must not be <code>null</code>).
     * @return The bytes signature.
     * @throws LionEngineException If invalid arguments.
     */
    public static String getSha(byte[] bytes)
    {
        Check.notNull(bytes);

        final StringBuilder builder = new StringBuilder(MAX_LENGTH);
        for (final byte b : SHA512.digest(bytes))
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
    public static String getSha(int i)
    {
        return getSha(UtilConversion.intToByteArray(i));
    }

    /**
     * Get the SHA-256 signature of the input string.
     * 
     * @param str The input string (must not be <code>null</code>).
     * @return The string signature.
     * @throws LionEngineException If invalid arguments.
     */
    public static String getSha(String str)
    {
        Check.notNull(str);

        return getSha(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Create digest.
     * 
     * @param algorithm The algorithm name.
     * @return The created digest.
     * @throws LionEngineException If algorithm is invalid.
     */
    private static MessageDigest create(String algorithm)
    {
        try
        {
            return MessageDigest.getInstance(algorithm);
        }
        catch (final NoSuchAlgorithmException exception)
        {
            throw new LionEngineException(exception, ERROR_ALGORITHM + algorithm);
        }
    }

    /**
     * Private constructor.
     */
    private UtilChecksum()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
