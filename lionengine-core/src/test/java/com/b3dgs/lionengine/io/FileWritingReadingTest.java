/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;

/**
 * Test {@link FileWriting} and {@link FileReading}.
 */
final class FileWritingReadingTest
{
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
     * Test writer and reader
     * 
     * @throws IOException If error.
     */
    @Test
    void testReaderWriter() throws IOException
    {
        final Path file = Files.createTempFile("test", "dat");
        Medias.setResourcesDirectory(file.getParent().toFile().getAbsolutePath());
        try
        {
            fileData = Medias.get(file.toFile());

            testFileWriting();
            testFileReading();
        }
        finally
        {
            Medias.setLoadFromJar(null);
            Files.delete(file);
        }
    }

    /**
     * Test write in data file.
     * 
     * @throws IOException If error.
     */
    private void testFileWriting() throws IOException
    {
        try (FileWriting writing = new FileWriting(fileData))
        {
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
    }

    /**
     * Test read in data file.
     * 
     * @throws IOException If error.
     */
    private void testFileReading() throws IOException
    {
        try (FileReading reading = new FileReading(fileData))
        {
            assertEquals(Boolean.valueOf(BOOL_VALUE), Boolean.valueOf(reading.readBoolean()));
            assertEquals(BYTE_VALUE, reading.readByte());
            assertEquals(CHAR_VALUE, reading.readChar());
            assertEquals(SHORT_VALUE, reading.readShort());
            assertEquals(INT_VALUE, reading.readInteger());
            assertEquals(FLOAT_VALUE, reading.readFloat());
            assertEquals(LONG_VALUE, reading.readLong());
            assertEquals(DOUBLE_VALUE, reading.readDouble());
            assertEquals(STRING_VALUE, reading.readString());
        }
    }
}
