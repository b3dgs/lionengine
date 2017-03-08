/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.io;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Describe a file writer, which performs file exploration.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Media file = Medias.create(&quot;test.txt&quot;);
 * try (FileWriting writing = new FileWriting(file))
 * {
 *     writing.writeBoolean(true);
 *     writing.writeByte((byte) 1);
 *     writing.writeChar('c');
 *     writing.writeShort((short) 2);
 *     writing.writeInteger(1);
 *     writing.writeFloat(5.1f);
 *     writing.writeLong(6L);
 *     writing.writeDouble(7.1);
 * }
 * catch (final IOException exception)
 * {
 *     Assert.fail(exception.getMessage());
 * }
 * </pre>
 */
public final class FileWriting implements Closeable
{
    /** Output stream reference. */
    private final DataOutputStream out;

    /**
     * Internal constructor.
     * 
     * @param media The media path.
     * @throws LionEngineException If error when opening the media.
     */
    public FileWriting(Media media)
    {
        Check.notNull(media);

        out = new DataOutputStream(new BufferedOutputStream(media.getOutputStream()));
    }

    /**
     * Write a boolean (1 bit, <code>true</code> or <code>false</code>).
     * 
     * @param b The boolean to write
     * @throws IOException If write failed.
     */
    public void writeBoolean(boolean b) throws IOException
    {
        out.writeBoolean(b);
    }

    /**
     * Write a byte (1 byte, -128 to 127 both included).
     * 
     * @param b The byte to write.
     * @throws IOException If write failed.
     */
    public void writeByte(byte b) throws IOException
    {
        out.writeByte(b);
    }

    /**
     * Write a char (2 bytes, 0 to 65535 both included).
     * 
     * @param c The char to write.
     * @throws IOException If write failed.
     */
    public void writeChar(char c) throws IOException
    {
        out.writeChar(c);
    }

    /**
     * Write a short (2 bytes, -32.768 to 32.767 both included).
     * 
     * @param s The short to write.
     * @throws IOException If write failed.
     */
    public void writeShort(short s) throws IOException
    {
        out.writeShort(s);
    }

    /**
     * Write an integer (4 bytes, -2.147.483.648 to 2.147.483.647 both included).
     * 
     * @param i The integer to write.
     * @throws IOException If write failed.
     */
    public void writeInteger(int i) throws IOException
    {
        out.writeInt(i);
    }

    /**
     * Write a float (4 bytes, 1.40129846432481707e-45 to 3.40282346638528860e+38).
     * 
     * @param f The float to write.
     * @throws IOException If write failed.
     */
    public void writeFloat(float f) throws IOException
    {
        out.writeFloat(f);
    }

    /**
     * Write a long (8 bytes, -9.223.372.036.854.775.808 to 9.223.372.036.854.775.807).
     * 
     * @param l The long to write.
     * @throws IOException If write failed.
     */
    public void writeLong(long l) throws IOException
    {
        out.writeLong(l);
    }

    /**
     * Write a double (8 bytes, 4.94065645841246544e-324 to 1.79769313486231570e+308).
     * 
     * @param d The double to write.
     * @throws IOException If write failed.
     */
    public void writeDouble(double d) throws IOException
    {
        out.writeDouble(d);
    }

    /**
     * Write a sequence of characters (2 bytes and more).
     * 
     * @param s The string to write.
     * @throws IOException If write failed.
     */
    public void writeString(String s) throws IOException
    {
        out.writeUTF(s);
    }

    /*
     * Closeable
     */

    /**
     * Terminate writing, close file.
     * 
     * @throws IOException If write failed.
     */
    @Override
    public void close() throws IOException
    {
        out.flush();
        out.close();
    }
}
