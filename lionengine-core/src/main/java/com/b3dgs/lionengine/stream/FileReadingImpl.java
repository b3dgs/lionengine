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
package com.b3dgs.lionengine.stream;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * File reading implementation.
 */
final class FileReadingImpl implements FileReading
{
    /** Input stream reference. */
    private final DataInputStream in;

    /**
     * Internal constructor.
     * 
     * @param media The media path.
     * @throws LionEngineException If error when opening the media.
     */
    FileReadingImpl(Media media)
    {
        Check.notNull(media);

        in = new DataInputStream(new BufferedInputStream(media.getInputStream()));
    }

    /*
     * FileReadering
     */

    @Override
    public boolean readBoolean() throws IOException
    {
        return in.readBoolean();
    }

    @Override
    public byte readByte() throws IOException
    {
        return in.readByte();
    }

    @Override
    public char readChar() throws IOException
    {
        return in.readChar();
    }

    @Override
    public short readShort() throws IOException
    {
        return in.readShort();
    }

    @Override
    public int readInteger() throws IOException
    {
        return in.readInt();
    }

    @Override
    public float readFloat() throws IOException
    {
        return in.readFloat();
    }

    @Override
    public long readLong() throws IOException
    {
        return in.readLong();
    }

    @Override
    public double readDouble() throws IOException
    {
        return in.readDouble();
    }

    @Override
    public String readString() throws IOException
    {
        return in.readUTF();
    }

    @Override
    public void close() throws IOException
    {
        in.close();
    }
}
