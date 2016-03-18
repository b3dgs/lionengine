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
package com.b3dgs.lionengine.stream;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilStream;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.mock.XmlNodeMock;

/**
 * Test the file writing and reading.
 */
public class FileWritingReadingTest
{
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
            writing = Stream.createFileWriting(fileData);

            writing.writeBoolean(XmlNodeMock.BOOL_VALUE);
            writing.writeByte(XmlNodeMock.BYTE_VALUE);
            writing.writeChar(XmlNodeMock.CHAR_VALUE);
            writing.writeShort(XmlNodeMock.SHORT_VALUE);
            writing.writeInteger(XmlNodeMock.INT_VALUE);
            writing.writeFloat(XmlNodeMock.FLOAT_VALUE);
            writing.writeLong(XmlNodeMock.LONG_VALUE);
            writing.writeDouble(XmlNodeMock.DOUBLE_VALUE);
            writing.writeString(XmlNodeMock.STRING_VALUE);
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
            reading = Stream.createFileReading(fileData);

            Assert.assertEquals(Boolean.valueOf(XmlNodeMock.BOOL_VALUE), Boolean.valueOf(reading.readBoolean()));
            Assert.assertEquals(XmlNodeMock.BYTE_VALUE, reading.readByte());
            Assert.assertEquals(XmlNodeMock.CHAR_VALUE, reading.readChar());
            Assert.assertEquals(XmlNodeMock.SHORT_VALUE, reading.readShort());
            Assert.assertEquals(XmlNodeMock.INT_VALUE, reading.readInteger());
            Assert.assertEquals(XmlNodeMock.FLOAT_VALUE, reading.readFloat(), XmlNodeMock.FLOAT_PRECISION);
            Assert.assertEquals(XmlNodeMock.LONG_VALUE, reading.readLong());
            Assert.assertEquals(XmlNodeMock.DOUBLE_VALUE, reading.readDouble(), XmlNodeMock.DOUBLE_PRECISION);
            Assert.assertEquals(XmlNodeMock.STRING_VALUE, reading.readString());
        }
        finally
        {
            UtilStream.close(reading);
        }
    }
}
