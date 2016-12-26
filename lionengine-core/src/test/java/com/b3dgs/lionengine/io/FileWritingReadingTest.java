/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.util.UtilStream;

/**
 * Test the file writing and reading.
 */
public class FileWritingReadingTest
{
    private static final float FLOAT_PRECISION = 0.0001f;
    private static final double DOUBLE_PRECISION = 0.000000000000001;
    private static final boolean BOOL_VALUE = true;
    private static final byte BYTE_VALUE = 1;
    private static final char CHAR_VALUE = 2;
    private static final short SHORT_VALUE = 3;
    private static final int INT_VALUE = 4;
    private static final float FLOAT_VALUE = 5.1f;
    private static final long LONG_VALUE = 6L;
    private static final double DOUBLE_VALUE = 7.1;
    private static final String STRING_VALUE = "string";

    /** Default test file data. */
    private Media fileData;

    /**
     * Test the writer and reader
     * 
     * @throws IOException If error.
     */
    @Test
    public void testReaderWriter() throws IOException
    {
        final File file = File.createTempFile("test", "dat");
        file.deleteOnExit();
        fileData = Medias.create(file.getAbsolutePath());

        testFileWriting();
        testFileReading();
    }

    /**
     * Test write in data file.
     * 
     * @throws IOException If error.
     */
    private void testFileWriting() throws IOException
    {
        FileWriting writing = null;
        try
        {
            writing = new FileWriting(fileData);

            writing.writeBoolean(BOOL_VALUE);
            writing.writeByte(BYTE_VALUE);
            writing.writeChar(CHAR_VALUE);
            writing.writeShort(SHORT_VALUE);
            writing.writeInteger(INT_VALUE);
            writing.writeFloat(FLOAT_VALUE);
            writing.writeLong(LONG_VALUE);
            writing.writeDouble(DOUBLE_VALUE);
            writing.writeString(STRING_VALUE);
        }
        finally
        {
            UtilStream.close(writing);
        }
    }

    /**
     * Test read in data file.
     * 
     * @throws IOException If error.
     */
    private void testFileReading() throws IOException
    {
        FileReading reading = null;
        try
        {
            reading = new FileReading(fileData);

            Assert.assertEquals(Boolean.valueOf(BOOL_VALUE), Boolean.valueOf(reading.readBoolean()));
            Assert.assertEquals(BYTE_VALUE, reading.readByte());
            Assert.assertEquals(CHAR_VALUE, reading.readChar());
            Assert.assertEquals(SHORT_VALUE, reading.readShort());
            Assert.assertEquals(INT_VALUE, reading.readInteger());
            Assert.assertEquals(FLOAT_VALUE, reading.readFloat(), FLOAT_PRECISION);
            Assert.assertEquals(LONG_VALUE, reading.readLong());
            Assert.assertEquals(DOUBLE_VALUE, reading.readDouble(), DOUBLE_PRECISION);
            Assert.assertEquals(STRING_VALUE, reading.readString());
        }
        finally
        {
            UtilStream.close(reading);
        }
    }
}
