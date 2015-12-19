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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.mock.XmlNodeMock;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the XML parser.
 */
public class XmlTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void prepare()
    {
        Medias.setLoadFromJar(XmlTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

    /** Default test file xml. */
    private Media fileXml;

    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(Xml.class);
    }

    /**
     * Test create node.
     */
    @Test
    public void testCreate()
    {
        Assert.assertNotNull(Xml.create("test"));
    }

    /**
     * Test create node with <code>null</code> parameter.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateNull()
    {
        Assert.assertNull(Xml.create(null));
    }

    /**
     * Test save with normalized output.
     */
    @Test
    public void testSaveNormalized()
    {
        final Media output = Medias.create("out.xml");
        Xml.save(Xml.load(Medias.create("normalize.xml")), output);
        Assert.assertTrue(output.getFile().delete());
    }

    /**
     * Test the write and read in XML with parser.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testXmlWriteRead() throws IOException
    {
        Medias.setLoadFromJar(null);
        final File file = File.createTempFile("test", "xml");
        file.deleteOnExit();
        fileXml = Medias.create(file.getAbsolutePath());

        testWriteXml();
        testReadXml();

        Medias.setLoadFromJar(XmlTest.class);
        testWrongReadXml();
        testWrongWriteXml();
    }

    /**
     * Test write in xml file.
     */
    private void testWriteXml()
    {
        final XmlNode root = Xml.create("root");
        final XmlNode child = Xml.create("child");
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

        Xml.save(root, fileXml);

        final XmlNode child2 = root.createChild("test");
        root.add(child2);
        Xml.save(root, fileXml);

        root.removeChild(child2);
        Xml.save(root, fileXml);
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
            Xml.save(Xml.create("child"), Medias.create(Constant.EMPTY_STRING));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        Xml.save(new XmlNodeMock(), fileXml);
        final File file = File.createTempFile("foo", null);
        file.deleteOnExit();
        if (file.mkdir())
        {
            try
            {
                Xml.save(Xml.create("child"), Medias.create(file.getPath()));
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
    private void testReadXml()
    {
        final XmlNode root = Xml.load(fileXml);
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
        final XmlNode root = Xml.load(fileXml);
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
            Xml.load(Medias.create("malformed.xml"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }
}
