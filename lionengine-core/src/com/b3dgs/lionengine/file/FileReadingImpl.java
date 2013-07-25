package com.b3dgs.lionengine.file;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.b3dgs.lionengine.Media;

/**
 * File reader implementation.
 */
class FileReadingImpl
        implements FileReading
{
    /** Input stream reference. */
    private final DataInputStream in;

    /**
     * Create a file reader.
     * 
     * @param media The media path.
     * @throws IOException If open failed.
     */
    FileReadingImpl(Media media) throws IOException
    {
        Media.exist(media);
        in = new DataInputStream(new BufferedInputStream(Media.getStream(media, "FileReading")));
    }

    /*
     * FileReader
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
