package com.b3dgs.lionengine.utility;

import java.nio.ByteBuffer;

/**
 * Conversion class utility.
 */
public final class UtilityConversion
{
    /**
     * Convert an integer to an array of byte.
     * 
     * @param value The integer value.
     * @return The byte array.
     */
    public static byte[] intToByteArray(int value)
    {
        return new byte[]
        {
                (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value
        };
    }

    /**
     * Convert a byte array to an integer.
     * 
     * @param bytes The byte array.
     * @return The integer value.
     */
    public static int byteArrayToInt(byte[] bytes)
    {
        return ByteBuffer.wrap(bytes).getInt();
    }

    /**
     * Convert a short to an array of byte.
     * 
     * @param value The short value.
     * @return The byte array.
     */
    public static byte[] shortToByteArray(short value)
    {
        return new byte[]
        {
                (byte) (value >>> 8), (byte) value
        };
    }

    /**
     * Convert a byte array to an integer.
     * 
     * @param bytes The byte array.
     * @return The integer value.
     */
    public static short byteArrayToShort(byte[] bytes)
    {
        return ByteBuffer.wrap(bytes).getShort();
    }

    /**
     * Return the java byte value [-128|127] from an unsigned byte [0|255].
     * 
     * @param value The unsigned byte value [0|255].
     * @return The java byte value [-128|127].
     */
    public static byte fromUnsignedByte(short value)
    {
        return (byte) (value - (Byte.MAX_VALUE + 1));
    }

    /**
     * Return the unsigned byte value [0|255] from the java byte value [-128|127].
     * 
     * @param value The java byte value [-128|127].
     * @return The unsigned byte value [0|255].
     */
    public static short toUnsignedByte(byte value)
    {
        return (short) (value - Byte.MIN_VALUE);
    }

    /**
     * Return the java short value [-32768|32767] from an unsigned short [0|65535].
     * 
     * @param value The unsigned short value [0|65535].
     * @return The java short value [-32768|32767].
     */
    public static short fromUnsignedShort(int value)
    {
        return (short) (value - (Short.MAX_VALUE + 1));
    }

    /**
     * Return the unsigned short value [0|65535] from the java short value [-32768|32767].
     * 
     * @param value The java short value [-32768|32767].
     * @return The unsigned short value [0|65535].
     */
    public static int toUnsignedShort(short value)
    {
        return value - Short.MIN_VALUE;
    }

    /**
     * Private constructor.
     */
    private UtilityConversion()
    {
        throw new RuntimeException();
    }
}
