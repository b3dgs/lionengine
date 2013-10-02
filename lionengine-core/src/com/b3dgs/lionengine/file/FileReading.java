/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.file;

import java.io.Closeable;
import java.io.IOException;

/**
 * Describe a file reader, which performs file exploration.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Media file = Media.get(&quot;test.txt&quot;);
 * try (FileReading reading = File.createFileReading(file);)
 * {
 *     final boolean boolRead = reading.readBoolean();
 *     final byte byteRead = reading.readByte();
 *     final char charRead = reading.readChar();
 *     final short shortRead = reading.readShort();
 *     final int intRead = reading.readInteger();
 *     final float floatRead = reading.readFloat();
 *     final long longRead = reading.readLong();
 *     final double doubleRead = reading.readDouble();
 * }
 * catch (final IOException exception)
 * {
 *     Assert.fail(exception.getMessage());
 * }
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface FileReading
        extends Closeable
{
    /**
     * Read a boolean (1 bit, <code>true</code> or <code>false</code>).
     * 
     * @return The boolean read.
     * @throws IOException If read failed.
     */
    boolean readBoolean() throws IOException;

    /**
     * Read a byte (1 byte, -128 to 127 both included).
     * 
     * @return The byte read.
     * @throws IOException If read failed.
     */
    byte readByte() throws IOException;

    /**
     * Read a char (2 bytes, 0 to 65535 both included).
     * 
     * @return The char read.
     * @throws IOException If read failed.
     */
    char readChar() throws IOException;

    /**
     * Read a short (2 bytes, -32.768 to 32.767 both included).
     * 
     * @return The short read.
     * @throws IOException If read failed.
     */
    short readShort() throws IOException;

    /**
     * Read an integer (4 bytes, -2.147.483.648 to 2.147.483.647 both included).
     * 
     * @return The integer read.
     * @throws IOException If read failed.
     */
    int readInteger() throws IOException;

    /**
     * Read a float (4 bytes, 1.40129846432481707e-45 to 3.40282346638528860e+38 both included).
     * 
     * @return The float read.
     * @throws IOException If read failed.
     */
    float readFloat() throws IOException;

    /**
     * Read a long (8 bytes, -9.223.372.036.854.775.808 to 9.223.372.036.854.775.807 both included).
     * 
     * @return The long read.
     * @throws IOException If read failed.
     */
    long readLong() throws IOException;

    /**
     * Read a double (8 bytes, 4.94065645841246544e-324 to 1.79769313486231570e+308 both included).
     * 
     * @return The double read.
     * @throws IOException If read failed.
     */
    double readDouble() throws IOException;

    /**
     * Read a sequence of characters (2 bytes and more).
     * 
     * @return The string read.
     * @throws IOException If read failed.
     */
    String readString() throws IOException;

    /*
     * Closeable
     */

    /**
     * Terminate reading, close file.
     * 
     * @throws IOException If close failed.
     */
    @Override
    void close() throws IOException;
}
