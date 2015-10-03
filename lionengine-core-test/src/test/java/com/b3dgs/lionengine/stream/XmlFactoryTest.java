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
package com.b3dgs.lionengine.stream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.test.MediaMock;
import com.b3dgs.lionengine.test.UtilTests;
import com.b3dgs.lionengine.test.XmlNodeMock;

/**
 * Test the XML parser.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class XmlFactoryTest
{
    /** Default test file xml. */
    private Media fileXml;

    /**
     * Test the constructor.
     * 
     * @throws Throwable If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Throwable
    {
        UtilTests.testPrivateConstructor(XmlFactory.class);
    }

    /**
     * Test the write and read in XML with parser.
     * 
     * @throws LionEngineException If error.
     * @throws IOException If error.
     */
    @Test
    public void testXmlWriteRead() throws LionEngineException, IOException
    {
        final File file = Files.createTempFile("test", "xml").toFile();
        file.deleteOnExit();
        fileXml = new MediaMock(file.getAbsolutePath(), true);

        testWriteXml();
        testReadXml();
        testWrongReadXml();
        testWrongWriteXml();
    }

    /**
     * Test write in xml file.
     */
    private void testWriteXml()
    {
        final XmlNode root = Stream.createXmlNode("root");
        final XmlNode child = Stream.createXmlNode("child");
        root.add(child);

        child.writeBoolean("boolean", XmlNodeMock.BOOL_VALUE);
        child.writeByte("byte", XmlNodeMock.BYTE_VALUE);
        child.writeShort("short", XmlNodeMock.SHORT_VALUE);
        child.writeInteger("integer", XmlNodeMock.INT_VALUE);
        child.writeFloat("float", XmlNodeMock.FLOAT_VALUE);
        child.writeLong("long", XmlNodeMock.LONG_VALUE);
        child.writeDouble("double", XmlNodeMock.DOUBLE_VALUE);
        child.writeString("string", XmlNodeMock.STRING_VALUE);
        child.writeString("null", null);

        Stream.saveXml(root, fileXml);

        final XmlNode child2 = root.createChild("test");
        root.add(child2);
        Stream.saveXml(root, fileXml);

        root.removeChild(child2);
        Stream.saveXml(root, fileXml);
    }

    /**
     * Test wrong write in xml file.
     * 
     * @throws IOException If error.
     */
    private void testWrongWriteXml() throws IOException
    {
        try
        {
            Stream.saveXml(Stream.createXmlNode("child"), new MediaMock(Constant.EMPTY_STRING));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        Stream.saveXml(new XmlNodeMock(), fileXml);
        final File file = Files.createTempFile("foo", null).toFile();
        file.deleteOnExit();
        if (file.mkdir())
        {
            try
            {
                Stream.saveXml(Stream.createXmlNode("child"), new MediaMock(file.getPath(), true));
                Assert.fail();
            }
            catch (final LionEngineException exception)
            {
                // Success
            }
            finally
            {
                UtilFile.deleteDirectory(file);
            }
        }
    }

    /**
     * Test read in xml file.
     * 
     * @throws LionEngineException If node note found, error case.
     */
    private void testReadXml() throws LionEngineException
    {
        final XmlNode root = Stream.loadXml(fileXml);
        final XmlNode child = root.getChild("child");

        Assert.assertEquals(Boolean.valueOf(XmlNodeMock.BOOL_VALUE), Boolean.valueOf(child.readBoolean("boolean")));
        Assert.assertEquals(XmlNodeMock.BYTE_VALUE, child.readByte("byte"));
        Assert.assertEquals(XmlNodeMock.SHORT_VALUE, child.readShort("short"));
        Assert.assertEquals(XmlNodeMock.INT_VALUE, child.readInteger("integer"));
        Assert.assertEquals(XmlNodeMock.FLOAT_VALUE, child.readFloat("float"), XmlNodeMock.FLOAT_PRECISION);
        Assert.assertEquals(XmlNodeMock.LONG_VALUE, child.readLong("long"));
        Assert.assertEquals(XmlNodeMock.DOUBLE_VALUE, child.readDouble("double"), XmlNodeMock.DOUBLE_PRECISION);
        Assert.assertEquals(XmlNodeMock.STRING_VALUE, child.readString("string"));
        Assert.assertEquals(null, child.readString("null"));
    }

    /**
     * Test wrong read data in xml file.
     */
    private void testWrongReadXml()
    {
        final XmlNode root = Stream.loadXml(fileXml);
        try
        {
            root.getChild("none");
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            root.readInteger("wrong");
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            Stream.loadXml(new MediaMock("malformed.xml"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }
}
