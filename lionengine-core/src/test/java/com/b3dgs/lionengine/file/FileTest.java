/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilityFile;
import com.b3dgs.lionengine.core.Media;

/**
 * Test the file package.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FileTest
{
    /** Float precision. */
    private static final float FLOAT_PRECISION = 0.00000001f;
    /** Double precision. */
    private static final double DOUBLE_PRECISION = 0.000000000000001;

    /**
     * Test the failure cases.
     * 
     * @throws IOException If error.
     */
    private static void testFailures() throws IOException
    {
        try
        {
            File.createFileWriting(null);
            Assert.fail();
        }
        catch (NullPointerException exception)
        {
            // Success
        }

        try
        {
            File.createFileReading(null);
            Assert.fail();
        }
        catch (LionEngineException
               | IOException exception)
        {
            // Success
        }

        try
        {
            File.createXmlNode(null);
            Assert.fail();
        }
        catch (LionEngineException exception)
        {
            // Success
        }
    }

    /** Boolean value. */
    private final boolean boolValue = true;
    /** Byte value. */
    private final byte byteValue = 1;
    /** Char value. */
    private final char charValue = 2;
    /** Short value. */
    private final short shortValue = 3;
    /** Int value. */
    private final int intValue = 4;
    /** Float value. */
    private final float floatValue = 5.1f;
    /** Long value. */
    private final long longValue = 6L;
    /** Double value. */
    private final double doubleValue = 7.1;
    /** String value. */
    private final String stringValue = "string";
    /** Default test file data. */
    private Media fileData;

    /**
     * Test the file factory.
     * 
     * @throws NoSuchMethodException If error.
     * @throws SecurityException If error.
     * @throws InstantiationException If error.
     * @throws IllegalAccessException If error.
     * @throws IllegalArgumentException If error.
     * @throws IOException If error.
     */
    @Test
    public void testFactory() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, IOException
    {
        final Constructor<File> constructor = File.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try
        {
            final File file = constructor.newInstance();
            Assert.assertNotNull(file);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }

        FileTest.testFailures();
        fileData = new Media("test");
        testFileWriting();
        testFileReading();
        UtilityFile.deleteFile(new java.io.File(fileData.getPath()));
    }

    /**
     * Test write in data file.
     */
    private void testFileWriting()
    {
        try (FileWriting writing = File.createFileWriting(fileData);)
        {
            writing.writeBoolean(boolValue);
            writing.writeByte(byteValue);
            writing.writeChar(charValue);
            writing.writeShort(shortValue);
            writing.writeInteger(intValue);
            writing.writeFloat(floatValue);
            writing.writeLong(longValue);
            writing.writeDouble(doubleValue);
            writing.writeString(stringValue);
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
        try (FileReading reading = File.createFileReading(fileData);)
        {
            Assert.assertEquals(Boolean.valueOf(boolValue), Boolean.valueOf(reading.readBoolean()));
            Assert.assertEquals(byteValue, reading.readByte());
            Assert.assertEquals(charValue, reading.readChar());
            Assert.assertEquals(shortValue, reading.readShort());
            Assert.assertEquals(intValue, reading.readInteger());
            Assert.assertEquals(floatValue, reading.readFloat(), FileTest.FLOAT_PRECISION);
            Assert.assertEquals(longValue, reading.readLong());
            Assert.assertEquals(doubleValue, reading.readDouble(), FileTest.DOUBLE_PRECISION);
            Assert.assertEquals(stringValue, reading.readString());
        }
        catch (final IOException exception)
        {
            Assert.fail(exception.getMessage());
        }
    }
}
