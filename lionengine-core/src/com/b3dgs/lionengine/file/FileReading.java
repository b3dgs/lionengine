package com.b3dgs.lionengine.file;

import java.io.Closeable;
import java.io.IOException;

/**
 * Describe a file reader, which performs file exploration.
 */
public interface FileReading
        extends Closeable
{
    /**
     * Read boolean (1 bit, <code>true</code> or <code>false</code>).
     * 
     * @return The boolean read.
     * @throws IOException If read failed.
     */
    boolean readBoolean() throws IOException;

    /**
     * Read byte (1 byte, -128 to 127).
     * 
     * @return The byte read.
     * @throws IOException If read failed.
     */
    byte readByte() throws IOException;

    /**
     * Read char (2 bytes, 0 to 65535).
     * 
     * @return The char read.
     * @throws IOException If read failed.
     */
    char readChar() throws IOException;

    /**
     * Read short (2 bytes, -32.768 to 32.767).
     * 
     * @return The short read.
     * @throws IOException If read failed.
     */
    short readShort() throws IOException;

    /**
     * Read integer (4 bytes, -2.147.483.648 to 2.147.483.647).
     * 
     * @return The integer read.
     * @throws IOException If read failed.
     */
    int readInteger() throws IOException;

    /**
     * Read float (4 bytes, 1.40129846432481707e-45 to 3.40282346638528860e+38).
     * 
     * @return The float read.
     * @throws IOException If read failed.
     */
    float readFloat() throws IOException;

    /**
     * Read long (8 bytes, -9.223.372.036.854.775.808 to 9.223.372.036.854.775.807).
     * 
     * @return The long read.
     * @throws IOException If read failed.
     */
    long readLong() throws IOException;

    /**
     * Read double (8 bytes, 4.94065645841246544e-324d to 1.79769313486231570e+308d).
     * 
     * @return The double read.
     * @throws IOException If read failed.
     */
    double readDouble() throws IOException;

    /**
     * Read a sequence of chars (2 bytes and more).
     * 
     * @return The string read.
     * @throws IOException If read failed.
     */
    String readString() throws IOException;

    /**
     * Terminate reading, close file.
     * 
     * @throws IOException If close failed.
     */
    @Override
    void close() throws IOException;
}
