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
package com.b3dgs.lionengine.io;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Describe a file reader, which performs file exploration.
 */
public final class FileReading implements Closeable
{
    /** Input stream reference. */
    private final DataInputStream in;

    /**
     * Internal constructor.
     * 
     * @param media The media path (must not be <code>null</code>).
     * @throws LionEngineException If error when opening the media.
     */
    public FileReading(Media media)
    {
        super();

        Check.notNull(media);

        in = new DataInputStream(new BufferedInputStream(media.getInputStream()));
    }

    /**
     * Read a boolean (1 bit, <code>true</code> or <code>false</code>).
     * 
     * @return The boolean read.
     * @throws IOException If read failed.
     */
    public boolean readBoolean() throws IOException
    {
        return in.readBoolean();
    }

    /**
     * Read a byte (1 byte, -128 to 127 both included).
     * 
     * @return The byte read.
     * @throws IOException If read failed.
     */
    public byte readByte() throws IOException
    {
        return in.readByte();
    }

    /**
     * Read a char (2 bytes, 0 to 65535 both included).
     * 
     * @return The char read.
     * @throws IOException If read failed.
     */
    public char readChar() throws IOException
    {
        return in.readChar();
    }

    /**
     * Read a short (2 bytes, -32.768 to 32.767 both included).
     * 
     * @return The short read.
     * @throws IOException If read failed.
     */
    public short readShort() throws IOException
    {
        return in.readShort();
    }

    /**
     * Read an integer (4 bytes, -2.147.483.648 to 2.147.483.647 both included).
     * 
     * @return The integer read.
     * @throws IOException If read failed.
     */
    public int readInteger() throws IOException
    {
        return in.readInt();
    }

    /**
     * Read a float (4 bytes, 1.40129846432481707e-45 to 3.40282346638528860e+38 both included).
     * 
     * @return The float read.
     * @throws IOException If read failed.
     */
    public float readFloat() throws IOException
    {
        return in.readFloat();
    }

    /**
     * Read a long (8 bytes, -9.223.372.036.854.775.808 to 9.223.372.036.854.775.807 both included).
     * 
     * @return The long read.
     * @throws IOException If read failed.
     */
    public long readLong() throws IOException
    {
        return in.readLong();
    }

    /**
     * Read a double (8 bytes, 4.94065645841246544e-324 to 1.79769313486231570e+308 both included).
     * 
     * @return The double read.
     * @throws IOException If read failed.
     */
    public double readDouble() throws IOException
    {
        return in.readDouble();
    }

    /**
     * Read a sequence of characters (2 bytes and more).
     * 
     * @return The string read.
     * @throws IOException If read failed.
     */
    public String readString() throws IOException
    {
        return in.readUTF();
    }

    /*
     * Closeable
     */

    /**
     * Terminate reading, close file.
     * 
     * @throws IOException If close failed.
     */
    @Override
    public void close() throws IOException
    {
        in.close();
    }
}
