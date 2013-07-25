package com.b3dgs.lionengine.file;

import java.io.Closeable;
import java.io.IOException;

/**
 * Describe a file writer, which performs file exploration.
 */
public interface FileWriting
        extends Closeable
{
    /**
     * Write boolean (1 bit, <code>true</code> or <code>false</code>).
     * 
     * @param b The boolean to write
     * @throws IOException If write failed.
     */
    void writeBoolean(boolean b) throws IOException;

    /**
     * Write byte (1 byte, -128 to 127).
     * 
     * @param b The byte to write.
     * @throws IOException If write failed.
     */
    void writeByte(byte b) throws IOException;

    /**
     * Write char (2 bytes, 0 to 65535).
     * 
     * @param c The char to write.
     * @throws IOException If write failed.
     */
    void writeChar(char c) throws IOException;

    /**
     * Write short (2 bytes, -32.768 to 32.767).
     * 
     * @param s The short to write.
     * @throws IOException If write failed.
     */
    void writeShort(short s) throws IOException;

    /**
     * Write integer (4 bytes, -2.147.483.648 to 2.147.483.647).
     * 
     * @param i The integer to write.
     * @throws IOException If write failed.
     */
    void writeInteger(int i) throws IOException;

    /**
     * Write float (4 bytes, 1.40129846432481707e-45 to 3.40282346638528860e+38).
     * 
     * @param f The float to write.
     * @throws IOException If write failed.
     */
    void writeFloat(float f) throws IOException;

    /**
     * Write long (8 bytes, -9.223.372.036.854.775.808 to 9.223.372.036.854.775.807).
     * 
     * @param l The long to write.
     * @throws IOException If write failed.
     */
    void writeLong(long l) throws IOException;

    /**
     * Write double (8 bytes, 4.94065645841246544e-324d to 1.79769313486231570e+308d).
     * 
     * @param d The double to write.
     * @throws IOException If write failed.
     */
    void writeDouble(double d) throws IOException;

    /**
     * Write a sequence of chars (2 bytes and more).
     * 
     * @param s The string to write.
     * @throws IOException If write failed.
     */
    void writeString(String s) throws IOException;

    /**
     * Terminate writing, close file.
     * 
     * @throws IOException If write failed.
     */
    @Override
    void close() throws IOException;
}
