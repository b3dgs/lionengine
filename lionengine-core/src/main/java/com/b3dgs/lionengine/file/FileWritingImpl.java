/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.core.Media;

/**
 * File writing implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class FileWritingImpl
        implements FileWriting
{
    /** Output stream reference. */
    private final DataOutputStream out;

    /**
     * Constructor.
     * 
     * @param media The media path.
     * @throws IOException If open failed.
     */
    FileWritingImpl(Media media) throws IOException
    {
        Check.notNull(media);
        out = new DataOutputStream(new BufferedOutputStream(media.getOutputStream()));
    }

    /*
     * FileWriter
     */

    @Override
    public void writeBoolean(boolean b) throws IOException
    {
        out.writeBoolean(b);
    }

    @Override
    public void writeByte(byte b) throws IOException
    {
        out.writeByte(b);

    }

    @Override
    public void writeChar(char c) throws IOException
    {
        out.writeChar(c);
    }

    @Override
    public void writeShort(short s) throws IOException
    {
        out.writeShort(s);
    }

    @Override
    public void writeInteger(int i) throws IOException
    {
        out.writeInt(i);
    }

    @Override
    public void writeFloat(float f) throws IOException
    {
        out.writeFloat(f);
    }

    @Override
    public void writeLong(long l) throws IOException
    {
        out.writeLong(l);
    }

    @Override
    public void writeDouble(double f) throws IOException
    {
        out.writeDouble(f);
    }

    @Override
    public void writeString(String s) throws IOException
    {
        out.writeUTF(s);
    }

    @Override
    public void close() throws IOException
    {
        out.close();
    }
}
