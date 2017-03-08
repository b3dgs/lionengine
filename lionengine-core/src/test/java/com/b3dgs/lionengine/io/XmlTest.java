/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.lang.reflect.Field;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.util.UtilFolder;
import com.b3dgs.lionengine.util.UtilReflection;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the XML parser.
 */
public class XmlTest
{
    /** Float precision. */
    private static final float FLOAT_PRECISION = 0.00000001f;
    /** Double precision. */
    private static final double DOUBLE_PRECISION = 0.000000000000001;
    /** Boolean value. */
    private static final boolean BOOL_VALUE = true;
    /** Byte value. */
    private static final byte BYTE_VALUE = 1;
    /** Short value. */
    private static final short SHORT_VALUE = 3;
    /** Int value. */
    private static final int INT_VALUE = 4;
    /** Float value. */
    private static final float FLOAT_VALUE = 5.1f;
    /** Long value. */
    private static final long LONG_VALUE = 6L;
    /** Double value. */
    private static final double DOUBLE_VALUE = 7.1;
    /** String value. */
    private static final String STRING_VALUE = "string";

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
     * Test create node.
     */
    @Test
    public void testCreate()
    {
        Assert.assertNotNull(new Xml("test"));
    }

    /**
     * Test create node with <code>null</code> parameter.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateNull()
    {
        Assert.assertNull(new Xml((Media) null));
    }

    /**
     * Test save with normalized output.
     */
    @Test
    public void testSaveNormalized()
    {
        final Media output = Medias.create("out.xml");
        new Xml(Medias.create("normalize.xml")).save(output);
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
     * Test the transformer error.
     * 
     * @throws IllegalArgumentException If error.
     * @throws IllegalAccessException If error.
     * @throws NoSuchFieldException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testTransformerError() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException
    {
        final Field field = DocumentFactory.class.getDeclaredField("transformerFactory");
        UtilReflection.setAccessible(field, true);
        final javax.xml.transform.TransformerFactory old = (TransformerFactory) field.get(DocumentFactory.class);
        try
        {
            field.set(DocumentFactory.class, new javax.xml.transform.TransformerFactory()
            {
                @Override
                public Transformer newTransformer() throws TransformerConfigurationException
                {
                    throw new TransformerConfigurationException();
                }

                @Override
                public Transformer newTransformer(Source source) throws TransformerConfigurationException
                {
                    return null;
                }

                @Override
                public Templates newTemplates(Source source) throws TransformerConfigurationException
                {
                    return null;
                }

                @Override
                public Source getAssociatedStylesheet(Source source, String media, String title, String charset)
                        throws TransformerConfigurationException
                {
                    return null;
                }

                @Override
                public void setURIResolver(URIResolver resolver)
                {
                    // Mock
                }

                @Override
                public URIResolver getURIResolver()
                {
                    return null;
                }

                @Override
                public void setFeature(String name, boolean value) throws TransformerConfigurationException
                {
                    // Mock
                }

                @Override
                public boolean getFeature(String name)
                {
                    return false;
                }

                @Override
                public void setAttribute(String name, Object value)
                {
                    // Mock
                }

                @Override
                public Object getAttribute(String name)
                {
                    return null;
                }

                @Override
                public void setErrorListener(ErrorListener listener)
                {
                    // Mock
                }

                @Override
                public ErrorListener getErrorListener()
                {
                    return null;
                }
            });
            final Media output = Medias.create("out.xml");
            new Xml(Medias.create("normalize.xml")).save(output);
            Assert.assertTrue(output.getFile().delete());
        }
        finally
        {
            field.set(DocumentFactory.class, old);
        }
    }

    /**
     * Test children in xml node.
     */
    @Test
    public void testXmlnode()
    {
        final Xml root = new Xml("root");
        final Xml child1 = root.createChild("child1");
        final Xml child2 = root.createChild("child2");

        child1.writeString("str", "str");

        root.add(child1);
        root.add(child2);

        Assert.assertEquals(root.getNodeName(), "root");
        Assert.assertNotNull(root.getChild("child1"));
        Assert.assertNotNull(root.getChild("child2"));

        try
        {
            Assert.assertEquals(child1.readString("str"), root.getChild("child1").readString("str"));
        }
        catch (final LionEngineException exception)
        {
            Assert.fail(exception.getMessage());
        }

        for (final Xml child : root.getChildren())
        {
            Assert.assertNotNull(child);
        }
        for (final Xml child : root.getChildren("child1"))
        {
            Assert.assertNotNull(child);
        }
        Assert.assertEquals("str", child1.getAttributes().get("str"));
        Assert.assertEquals(Constant.EMPTY_STRING, child1.getText());

        final String text = "text";
        root.setText(text);
        Assert.assertEquals(text, root.getText());
    }

    /**
     * Test the remove element function on node.
     */
    @Test
    public void testXmlNodeRemove()
    {
        final Xml root = new Xml("root");
        final Xml child1 = root.createChild("child1");
        final Xml child2 = root.createChild("child2");

        child1.writeString("str", "str");

        root.add(child1);
        root.add(child2);

        root.removeChild("child1");
        root.removeChildren("child2");
        root.removeAttribute("str");
    }

    /**
     * Test the write read to xml node.
     */
    @Test
    public void testXmlNodeWriteRead()
    {
        final Xml node = new Xml("node");
        node.add(new Xml("test"));
        try
        {
            node.getChild("void");
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }

        node.writeBoolean("boolean", BOOL_VALUE);
        node.writeByte("byte", BYTE_VALUE);
        node.writeShort("short", SHORT_VALUE);
        node.writeInteger("integer", INT_VALUE);
        node.writeFloat("float", FLOAT_VALUE);
        node.writeLong("long", LONG_VALUE);
        node.writeDouble("double", DOUBLE_VALUE);
        node.writeString("string", STRING_VALUE);
        node.writeString("null", null);

        Assert.assertEquals(Boolean.valueOf(BOOL_VALUE), Boolean.valueOf(node.readBoolean("boolean")));
        Assert.assertEquals(BYTE_VALUE, node.readByte("byte"));
        Assert.assertEquals(SHORT_VALUE, node.readShort("short"));
        Assert.assertEquals(INT_VALUE, node.readInteger("integer"));
        Assert.assertEquals(FLOAT_VALUE, node.readFloat("float"), FLOAT_PRECISION);
        Assert.assertEquals(LONG_VALUE, node.readLong("long"));
        Assert.assertEquals(DOUBLE_VALUE, node.readDouble("double"), DOUBLE_PRECISION);
        Assert.assertEquals(STRING_VALUE, node.readString("string"));
        Assert.assertEquals(null, node.readString("null"));
    }

    /**
     * Test the node name error.
     */
    @Test(expected = LionEngineException.class)
    public void testXmlNodeNameError()
    {
        final Xml node = new Xml("%éàç-èyrd");
        Assert.assertNull(node);
    }

    /**
     * Test the node write error.
     */
    @Test(expected = LionEngineException.class)
    public void testXmlNodeWriteError()
    {
        final Xml node = new Xml("test");
        node.writeString("%éàç-èyrd", "error");
    }

    /**
     * Test the node read error.
     */
    @Test(expected = LionEngineException.class)
    public void testXmlNodeReadError()
    {
        final Xml node = new Xml("test");
        node.readString("%éàç-èyrd");
    }

    /**
     * Test the node read with default value.
     */
    @Test
    public void testXmlNodeReadDefault()
    {
        final Xml node = new Xml("test");

        Assert.assertTrue(node.readBoolean(true, "void"));
        Assert.assertEquals((byte) 1, node.readByte((byte) 1, "void"));
        Assert.assertEquals((short) 1, node.readShort((short) 1, "void"));
        Assert.assertEquals(1, node.readInteger(1, "void"));
        Assert.assertEquals(1L, node.readLong(1L, "void"));
        Assert.assertEquals(1.0f, node.readFloat(1.0f, "void"), UtilTests.PRECISION);
        Assert.assertEquals(1.0, node.readDouble(1.0, "void"), UtilTests.PRECISION);
        Assert.assertEquals("default", node.readString("default", "void"));
        Assert.assertNull(node.readString("null", "void"));

        node.writeBoolean("boolean", BOOL_VALUE);
        node.writeByte("byte", BYTE_VALUE);
        node.writeShort("short", SHORT_VALUE);
        node.writeInteger("integer", INT_VALUE);
        node.writeFloat("float", FLOAT_VALUE);
        node.writeLong("long", LONG_VALUE);
        node.writeDouble("double", DOUBLE_VALUE);
        node.writeString("string", STRING_VALUE);
        node.writeString("null", null);

        Assert.assertEquals(Boolean.valueOf(BOOL_VALUE), Boolean.valueOf(node.readBoolean("boolean")));
        Assert.assertEquals(BYTE_VALUE, node.readByte((byte) 1, "byte"));
        Assert.assertEquals(SHORT_VALUE, node.readShort((short) 1, "short"));
        Assert.assertEquals(INT_VALUE, node.readInteger(1, "integer"));
        Assert.assertEquals(FLOAT_VALUE, node.readFloat(1.0f, "float"), FLOAT_PRECISION);
        Assert.assertEquals(LONG_VALUE, node.readLong(1L, "long"));
        Assert.assertEquals(DOUBLE_VALUE, node.readDouble(1.0, "double"), DOUBLE_PRECISION);
        Assert.assertEquals(STRING_VALUE, node.readString("default", "string"));
        Assert.assertEquals(null, node.readString("default", "null"));
    }

    /**
     * Test the node has element.
     */
    @Test
    public void testXmlNodeHas()
    {
        final Xml node = new Xml("test");
        final Xml child = new Xml("child");
        node.writeString("attribute", "none");
        node.add(child);

        Assert.assertTrue(node.hasAttribute("attribute"));
        Assert.assertTrue(node.hasChild("child"));
        Assert.assertFalse(node.hasAttribute("test"));
        Assert.assertFalse(node.hasChild("attribute"));
    }

    /**
     * Test wrong normalization.
     */
    @Test
    public void testBadNormalize()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        final Xml node = new Xml("test");
        node.normalize("\\//text()[test]");
        Verbose.info("****************************************************************************************");
    }

    /**
     * Test write in xml file.
     */
    private void testWriteXml()
    {
        final Xml root = new Xml("root");
        final Xml child = new Xml("child");
        root.add(child);

        child.writeBoolean("boolean", BOOL_VALUE);
        child.writeByte("byte", BYTE_VALUE);
        child.writeShort("short", SHORT_VALUE);
        child.writeInteger("integer", INT_VALUE);
        child.writeFloat("float", FLOAT_VALUE);
        child.writeLong("long", LONG_VALUE);
        child.writeDouble("double", DOUBLE_VALUE);
        child.writeString("string", STRING_VALUE);
        child.writeString("null", null);

        root.save(fileXml);

        final Xml child2 = root.createChild("test");
        root.add(child2);
        root.save(fileXml);

        root.removeChild(child2);
        root.save(fileXml);
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
            new Xml("child").save(Medias.create(Constant.EMPTY_STRING));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }

        final File file = File.createTempFile("foo", null);
        file.deleteOnExit();
        if (file.mkdir())
        {
            try
            {
                new Xml("child").save(Medias.create(file.getPath()));
                Assert.fail();
            }
            catch (final LionEngineException exception)
            {
                // Success
                Assert.assertNotNull(exception);
            }
            finally
            {
                UtilFolder.deleteDirectory(file);
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
        final Xml root = new Xml(fileXml);
        final Xml child = root.getChild("child");

        Assert.assertEquals(Boolean.valueOf(BOOL_VALUE), Boolean.valueOf(child.readBoolean("boolean")));
        Assert.assertEquals(BYTE_VALUE, child.readByte("byte"));
        Assert.assertEquals(SHORT_VALUE, child.readShort("short"));
        Assert.assertEquals(INT_VALUE, child.readInteger("integer"));
        Assert.assertEquals(FLOAT_VALUE, child.readFloat("float"), FLOAT_PRECISION);
        Assert.assertEquals(LONG_VALUE, child.readLong("long"));
        Assert.assertEquals(DOUBLE_VALUE, child.readDouble("double"), DOUBLE_PRECISION);
        Assert.assertEquals(STRING_VALUE, child.readString("string"));
        Assert.assertEquals(null, child.readString("null"));
    }

    /**
     * Test wrong read data in xml file.
     */
    private void testWrongReadXml()
    {
        final Xml root = new Xml(fileXml);
        try
        {
            root.getChild("none");
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
        try
        {
            root.readInteger("wrong");
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }

        try
        {
            Assert.assertNotNull(new Xml(Medias.create("malformed.xml")));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
    }
}
