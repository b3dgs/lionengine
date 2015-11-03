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
package com.b3dgs.lionengine.test.stream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.test.mock.XmlNodeMock;

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
        final File file = Files.createTempFile("test", "dat").toFile();
        file.deleteOnExit();
        fileData = Medias.create(file.getAbsolutePath());

        testFileWriting();
        testFileReading();
    }

    /**
     * Test write in data file.
     */
    private void testFileWriting()
    {
        try (FileWriting writing = Stream.createFileWriting(fileData))
        {
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
        catch (final IOException exception)
        {
            Assert.fail(exception.getMessage());
        }
    }

    /**
     * Test read in data file.
     */
    private void testFileReading()
    {
        try (FileReading reading = Stream.createFileReading(fileData))
        {
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
        catch (final IOException exception)
        {
            Assert.fail(exception.getMessage());
        }
    }
}
