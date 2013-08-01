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
import com.b3dgs.lionengine.file.XmlNodeNotFoundException;
import com.b3dgs.lionengine.file.XmlParser;
import com.b3dgs.lionengine.utility.UtilityFile;

/**
 * Test file package.
 */
public class TestFile
{
    /** Float precision. */
    private static final float FLOAT_PRECISION = 0.00000001f;
    /** Double precision. */
    private static final double DOUBLE_PRECISION = 0.000000000000001;
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
    /** Default test file xml. */
    private Media fileXml;

    /**
     * Test the create file reading utility function.
     * 
     * @param media The media reference.
     */
    private static void testCreateFileReading(Media media)
    {
        try (FileReading reading = File.createFileReading(media);)
        {
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
    }

    /**
     * Test the create xml node utility function.
     * 
     * @param name The node name.
     */
    private static void testCreateXmlNode(String name)
    {
        try
        {
            final XmlNode node = File.createXmlNode(name);
            node.readBoolean("void");
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the create xml parser utility function.
     * 
     * @param media The media file.
     */
    private static void testCreateXmlParser(Media media)
    {
        final XmlParser parser = File.createXmlParser();
        try
        {
            parser.load(media);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test write in data file.
     */
    private void testWriteFileData()
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
    private void testReadData()
    {
        try (FileReading reading = File.createFileReading(fileData);)
        {
            Assert.assertEquals(Boolean.valueOf(boolValue), Boolean.valueOf(reading.readBoolean()));
            Assert.assertEquals(byteValue, reading.readByte());
            Assert.assertEquals(charValue, reading.readChar());
            Assert.assertEquals(shortValue, reading.readShort());
            Assert.assertEquals(intValue, reading.readInteger());
            Assert.assertEquals(floatValue, reading.readFloat(), TestFile.FLOAT_PRECISION);
            Assert.assertEquals(longValue, reading.readLong());
            Assert.assertEquals(doubleValue, reading.readDouble(), TestFile.DOUBLE_PRECISION);
            Assert.assertEquals(stringValue, reading.readString());
        }
        catch (final IOException exception)
        {
            Assert.fail(exception.getMessage());
        }
    }

    /**
     * Test read in data file with wrong value.
     */
    private void testWrongReadData()
    {
        try (FileReading readingError = File.createFileReading(fileData);)
        {
            readingError.readDouble();
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
     * Test write in xml file.
     */
    private void testWriteXml()
    {
        final XmlNode root = File.createXmlNode("root");
        final XmlNode child = File.createXmlNode("child");
        root.add(child);

        child.writeBoolean("boolean", boolValue);
        child.writeByte("byte", byteValue);
        child.writeShort("short", shortValue);
        child.writeInteger("integer", intValue);
        child.writeFloat("float", floatValue);
        child.writeLong("long", longValue);
        child.writeDouble("double", doubleValue);
        child.writeString("string", stringValue);
        child.writeString("null", null);

        final XmlParser parserSave = File.createXmlParser();
        parserSave.save(root, fileXml);
    }

    /**
     * Test read in xml file.
     * 
     * @throws XmlNodeNotFoundException If node note found, error case.
     */
    private void testReadXml() throws XmlNodeNotFoundException
    {
        final XmlParser parserLoad = File.createXmlParser();
        final XmlNode root = parserLoad.load(fileXml);
        final XmlNode child = root.getChild("child");

        Assert.assertEquals(Boolean.valueOf(boolValue), Boolean.valueOf(child.readBoolean("boolean")));
        Assert.assertEquals(byteValue, child.readByte("byte"));
        Assert.assertEquals(shortValue, child.readShort("short"));
        Assert.assertEquals(intValue, child.readInteger("integer"));
        Assert.assertEquals(floatValue, child.readFloat("float"), TestFile.FLOAT_PRECISION);
        Assert.assertEquals(longValue, child.readLong("long"));
        Assert.assertEquals(doubleValue, child.readDouble("double"), TestFile.DOUBLE_PRECISION);
        Assert.assertEquals(stringValue, child.readString("string"));
        Assert.assertEquals(null, child.readString("null"));
    }

    /**
     * Test wrong read data in xml file.
     */
    private void testWrongReadXml()
    {
        final XmlParser parserLoad = File.createXmlParser();
        final XmlNode root = parserLoad.load(fileXml);
        try
        {
            root.getChild("none");
            Assert.fail();
        }
        catch (final XmlNodeNotFoundException exception)
        {
            // Success
        }
        try
        {
            root.readBoolean("wrong");
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            final XmlParser parser = File.createXmlParser();
            parser.load(Media.get("malformed.xml"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test children in xml node.
     */
    private static void testChildXml()
    {
        final XmlNode root = File.createXmlNode("root");
        final XmlNode child1 = File.createXmlNode("child1");
        final XmlNode child2 = File.createXmlNode("child2");

        child1.writeString("str", "str");

        root.add(child1);
        root.add(child2);

        try
        {
            Assert.assertEquals(child1.readString("str"), root.getChild("child1").readString("str"));
        }
        catch (final XmlNodeNotFoundException exception)
        {
            Assert.fail();
        }

        for (final XmlNode child : root.getChildren())
        {
            Assert.assertNotNull(child);
        }
        for (final XmlNode child : root.getChildren("child1"))
        {
            Assert.assertNotNull(child);
        }
        Assert.assertEquals("str", child1.getAttributes().get("str"));
        Assert.assertEquals("", child1.getText());
    }

    /**
     * Prepare test.
     */
    @Before
    public void setUp()
    {
        Engine.start("UnitTest", Version.create(1, 0, 0), Media.getPath("resources"));
        fileData = Media.get("test.txt");
        fileXml = Media.get("test.xml");
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
        TestFile.testCreateFileReading(null);
        TestFile.testCreateFileReading(Media.get("void"));

        testWriteFileData();
        testReadData();

        testWrongReadData();

        UtilityFile.deleteFile(new java.io.File(fileData.getPath()));
    }

    /**
     * Test xml functions.
     * 
     * @throws XmlNodeNotFoundException If node note found, error case.
     */
    @Test
    public void testXml() throws XmlNodeNotFoundException
    {
        TestFile.testCreateXmlNode(null);
        TestFile.testCreateXmlNode("root");

        TestFile.testCreateXmlParser(null);
        TestFile.testCreateXmlParser(Media.get("void"));

        testWriteXml();
        testReadXml();

        testWrongReadXml();

        TestFile.testChildXml();

        UtilityFile.deleteFile(new java.io.File(fileXml.getPath()));
    }
}
