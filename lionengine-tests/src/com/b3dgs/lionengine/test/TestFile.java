package com.b3dgs.lionengine.test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.file.XmlParser;

/**
 * Test file package.
 */
public class TestFile
{
    /**
     * Prepare test.
     */
    @Before
    public void setUp()
    {
        Engine.start("UnitTest", Version.create(1, 0, 0), Media.getPath("resources"));
    }

    /**
     * Test File class.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testFile() throws Exception
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
    }
    
    /**
     * Test files functions.
     */
    @Test
    public void testFiles()
    {
        try
        {
            File.createFileReading(null);
            Assert.fail();
        }
        catch (final IOException exception)
        {
            Assert.fail(exception.getMessage());
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            File.createFileReading(Media.get("void"));
            Assert.fail();
        }
        catch (final IOException exception)
        {
            Assert.fail(exception.getMessage());
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        // Write and read
        final Media file = Media.get("test.txt");
        final boolean boolValue = true;
        final byte byteValue = 1;
        final char charValue = 2;
        final short shortValue = 3;
        final int intValue = 4;
        final float floatvalue = 5.1f;
        final long longValue = 6L;
        final double doubleValue = 7.1;

        // Write data in the file
        try (FileWriting writing = File.createFileWriting(file);)
        {
            writing.writeBoolean(boolValue);
            writing.writeByte(byteValue);
            writing.writeChar(charValue);
            writing.writeShort(shortValue);
            writing.writeInteger(intValue);
            writing.writeFloat(floatvalue);
            writing.writeLong(longValue);
            writing.writeDouble(doubleValue);
        }
        catch (final IOException exception)
        {
            Assert.fail(exception.getMessage());
        }

        // Read data from the file and compare
        try (FileReading reading = File.createFileReading(file);)
        {
            Assert.assertEquals(Boolean.valueOf(boolValue), Boolean.valueOf(reading.readBoolean()));
            Assert.assertEquals(byteValue, reading.readByte());
            Assert.assertEquals(charValue, reading.readChar());
            Assert.assertEquals(shortValue, reading.readShort());
            Assert.assertEquals(intValue, reading.readInteger());
            Assert.assertEquals(floatvalue, reading.readFloat(), 0.00000001f);
            Assert.assertEquals(longValue, reading.readLong());
            Assert.assertEquals(doubleValue, reading.readDouble(), 0.000000000000001);
        }
        catch (final IOException exception)
        {
            Assert.fail(exception.getMessage());
        }

        // Wrong read
        try (FileReading readingError = File.createFileReading(file);)
        {
            readingError.readDouble();
            readingError.readDouble();
            readingError.readDouble();
            readingError.readDouble();
            Assert.fail();
        }
        catch (final IOException exception)
        {
            // Success
        }
    }

    /**
     * Test xml functions.
     */
    @Test
    public void testXml()
    {
        final XmlNode node = File.createXmlNode("root");
        try
        {
            node.readBoolean("void");
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final XmlParser parser = File.createXmlParser();
        try
        {
            parser.load(Media.get("void"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }
}
