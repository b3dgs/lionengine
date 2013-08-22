package com.b3dgs.lionengine;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.b3dgs.lionengine.utility.UtilityConversion;

/**
 * SHA-256 based checksum manipulation.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Checksum checksum = Checksum.create();
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
 */
public final class Checksum
{
    /** Instance error message. */
    private static final String MESSAGE_ERROR_INSTANCE = "SHA-256 can not be instantiated !";
    /** UTF8. */
    private static final Charset UTF8 = Charset.forName("UTF-8");
    /** SHA Mode. */
    private static final String SHA = "SHA-256";

    /**
     * Create a new checksum.
     * 
     * @return The checksum instance.
     */
    public static Checksum create()
    {
        return new Checksum();
    }

    /** Message digest instance. */
    private final MessageDigest sha;

    /**
     * Create a new checksum.
     */
    private Checksum()
    {
        try
        {
            sha = MessageDigest.getInstance(Checksum.SHA);
        }
        catch (final NoSuchAlgorithmException exception)
        {
            throw new LionEngineException(exception, Checksum.MESSAGE_ERROR_INSTANCE);
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
        return Arrays.equals(getSha256(value).getBytes(Checksum.UTF8), signature.getBytes(Checksum.UTF8));
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
        return Arrays.equals(getSha256(value).getBytes(Checksum.UTF8), signature.getBytes(Checksum.UTF8));
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
        return getSha256(UtilityConversion.intToByteArray(i));
    }

    /**
     * Get the SHA-256 signature of the input string.
     * 
     * @param str The input integer.
     * @return The string signature.
     */
    public String getSha256(String str)
    {
        return getSha256(str.getBytes(Checksum.UTF8));
    }
}
