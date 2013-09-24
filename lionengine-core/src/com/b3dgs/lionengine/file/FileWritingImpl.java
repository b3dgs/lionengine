package com.b3dgs.lionengine.file;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Media;

/**
 * File writer implementation.
 */
final class FileWritingImpl
        implements FileWriting
{
    /** Output stream reference. */
    private final DataOutputStream out;

    /**
     * Create a file writer.
     * 
     * @param media The media path.
     * @throws IOException If open failed.
     */
    FileWritingImpl(Media media) throws IOException
    {
        Check.notNull(media, "The media must exist !");
        out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(media.getPath()))));
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
