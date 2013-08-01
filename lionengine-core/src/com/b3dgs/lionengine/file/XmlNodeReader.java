package com.b3dgs.lionengine.file;

/**
 * The reading capacity of an xml node.
 */
public interface XmlNodeReader
{
    /**
     * Read a boolean.
     * 
     * @param attribute The boolean name.
     * @return The boolean value.
     */
    boolean readBoolean(String attribute);

    /**
     * Read a byte.
     * 
     * @param attribute The integer name.
     * @return The byte value.
     */
    byte readByte(String attribute);

    /**
     * Read a short.
     * 
     * @param attribute The integer name.
     * @return The short value.
     */
    short readShort(String attribute);

    /**
     * Read an integer.
     * 
     * @param attribute The integer name.
     * @return The integer value.
     */
    int readInteger(String attribute);

    /**
     * Read a long.
     * 
     * @param attribute The float name.
     * @return The long value.
     */
    long readLong(String attribute);

    /**
     * Read a float.
     * 
     * @param attribute The float name.
     * @return The float value.
     */
    float readFloat(String attribute);

    /**
     * Read a double.
     * 
     * @param attribute The double name.
     * @return The double value.
     */
    double readDouble(String attribute);

    /**
     * Read a string. If the read string is equal to {@link XmlNode#NULL}, <code>null</code> will be returned instead.
     * 
     * @param attribute The string name.
     * @return The string value.
     */
    String readString(String attribute);
}
