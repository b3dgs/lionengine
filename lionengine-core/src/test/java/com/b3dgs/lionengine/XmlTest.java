/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test {@link Xml and XmlReader}.
 */
final class XmlTest
{
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
    @BeforeEach
    public void beforeTest()
    {
        Medias.setResourcesDirectory(null);
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Medias.setLoadFromJar(XmlTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterEach
    public void afterTest()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test create node.
     */
    @Test
    void testCreate()
    {
        assertNotNull(new Xml("test"));
    }

    /**
     * Test create node with <code>null</code> parameter.
     */
    @Test
    void testCreateNull()
    {
        assertThrows(() -> new Xml((Media) null), Check.ERROR_NULL);
    }

    /**
     * Test save with normalized output.
     * 
     * @throws IOException If error.
     */
    @Test
    void testSaveNormalized() throws IOException
    {
        final Media output = Medias.create("out.xml");
        new Xml(Medias.create("normalize.xml")).save(output);

        assertTrue(output.getFile().delete());
    }

    /**
     * Test write and read in XML with parser.
     * 
     * @throws IOException If error.
     */
    @Test
    void testWriteRead() throws IOException
    {
        Medias.setLoadFromJar(null);

        final Path file = Files.createTempFile("test", "xml");
        Medias.setResourcesDirectory(file.getParent().toFile().getAbsolutePath());

        final Media media = Medias.get(file.toFile());

        testWriteXml(media);
        testReadXml(media);

        final XmlReader reader = new XmlReader(media);

        assertEquals(reader.getChild("child").getNodeName(),
                     reader.getChildren("child").iterator().next().getNodeName());
        assertEquals(reader.getChild("child").getNodeName(), reader.getChildren().iterator().next().getNodeName());
        assertEquals(1, reader.getChildren().size());

        Medias.setLoadFromJar(XmlTest.class);
        testWrongReadXml(media);

        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        testWrongWriteXml();
        Verbose.info("****************************************************************************************");

        Files.delete(file);
    }

    /**
     * Test transformer error.
     * 
     * @throws IllegalArgumentException If error.
     * @throws IllegalAccessException If error.
     * @throws NoSuchFieldException If error.
     */
    @Test
    void testTransformerError() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException
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
            assertThrows(() -> new Xml(Medias.create("normalize.xml")).save(output), "[out.xml] " + Xml.ERROR_WRITING);
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
    void testNode()
    {
        final Xml root = new Xml("root");
        final Xml child1 = root.createChild("child1");
        final Xml child2 = root.createChild("child2");

        child1.writeString("str", "str");

        root.add(child1);
        root.add(child2);

        assertEquals("root", root.getNodeName());
        assertNotNull(root.getChild("child1"));
        assertNotNull(root.getChild("child2"));
        assertFalse(root.getChildOptional("void").isPresent());
        assertTrue(root.getChildOptional("child1").isPresent());

        assertEquals(child1.readString("str"), root.getChild("child1").readString("str"));

        for (final Xml child : root.getChildren())
        {
            assertNotNull(child);
        }
        for (final Xml child : root.getChildren("child1"))
        {
            assertNotNull(child);
        }
        assertEquals("str", child1.getAttributes().get("str"));
        assertEquals(Constant.EMPTY_STRING, child1.getText());

        final String text = "text";
        root.setText(text);

        assertEquals(text, root.getText());
    }

    /**
     * Test remove element function on node.
     */
    @Test
    void testNodeRemove()
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
     * Test write read to xml node.
     */
    @Test
    void testNodeWriteRead()
    {
        final Xml node = new Xml("node");
        node.add(new Xml("test"));

        assertThrows(() -> node.getChild("void"), XmlReader.ERROR_NODE + "void");

        node.writeBoolean("boolean", BOOL_VALUE);
        node.writeByte("byte", BYTE_VALUE);
        node.writeShort("short", SHORT_VALUE);
        node.writeInteger("integer", INT_VALUE);
        node.writeFloat("float", FLOAT_VALUE);
        node.writeLong("long", LONG_VALUE);
        node.writeDouble("double", DOUBLE_VALUE);
        node.writeString("string", STRING_VALUE);
        node.writeString("null", null);

        assertEquals(Boolean.valueOf(BOOL_VALUE), Boolean.valueOf(node.readBoolean("boolean")));
        assertEquals(Boolean.valueOf(BOOL_VALUE), node.readBooleanOptional("boolean").get());
        assertEquals(BYTE_VALUE, node.readByte("byte"));
        assertEquals(SHORT_VALUE, node.readShort("short"));
        assertEquals(INT_VALUE, node.readInteger("integer"));
        assertEquals(INT_VALUE, node.readIntegerOptional("integer").getAsInt());
        assertEquals(FLOAT_VALUE, node.readFloat("float"));
        assertEquals(LONG_VALUE, node.readLong("long"));
        assertEquals(DOUBLE_VALUE, node.readDouble("double"));
        assertEquals(DOUBLE_VALUE, node.readDoubleOptional("double").getAsDouble());
        assertEquals(STRING_VALUE, node.readString("string"));
        assertNull(node.readString("null"));
        assertEquals(STRING_VALUE, node.readStringOptional("string").get());
    }

    /**
     * Test node name error.
     */
    @Test
    void testNodeNameError()
    {
        assertCause(() -> new Xml("%éàç-èyrd"), org.w3c.dom.DOMException.class);
    }

    /**
     * Test node write error.
     */
    @Test
    void testNodeWriteError()
    {
        final Xml node = new Xml("test");

        assertThrows(() -> node.writeString("%éàç-èyrd", "error"),
                     Xml.ERROR_WRITE_ATTRIBUTE + "%éàç-èyrd" + Xml.ERROR_WRITE_CONTENT + "error");
    }

    /**
     * Test node read error.
     */
    @Test
    void testNodeReadError()
    {
        final Xml node = new Xml("test");

        assertThrows(() -> node.readString("%éàç-èyrd"), XmlReader.ERROR_ATTRIBUTE + "%éàç-èyrd");
    }

    /**
     * Test node read with default value.
     */
    @Test
    void testNodeReadDefault()
    {
        final Xml node = new Xml("test");

        assertTrue(node.readBoolean(true, "void"));
        assertFalse(node.readBooleanOptional("void").isPresent());
        assertEquals((byte) 1, node.readByte((byte) 1, "void"));
        assertEquals((short) 1, node.readShort((short) 1, "void"));
        assertEquals(1, node.readInteger(1, "void"));
        assertFalse(node.readIntegerOptional("void").isPresent());
        assertEquals(1L, node.readLong(1L, "void"));
        assertEquals(1.0f, node.readFloat(1.0f, "void"));
        assertEquals(1.0, node.readDouble(1.0, "void"));
        assertFalse(node.readDoubleOptional("void").isPresent());
        assertEquals("default", node.readString("default", "void"));
        assertNull(node.readString("null", "void"));
        assertFalse(node.readStringOptional("void").isPresent());

        node.writeBoolean("boolean", BOOL_VALUE);
        node.writeByte("byte", BYTE_VALUE);
        node.writeShort("short", SHORT_VALUE);
        node.writeInteger("integer", INT_VALUE);
        node.writeFloat("float", FLOAT_VALUE);
        node.writeLong("long", LONG_VALUE);
        node.writeDouble("double", DOUBLE_VALUE);
        node.writeString("string", STRING_VALUE);
        node.writeString("null", null);

        assertEquals(Boolean.valueOf(BOOL_VALUE), Boolean.valueOf(node.readBoolean("boolean")));
        assertTrue(node.readBooleanOptional("boolean").isPresent());
        assertEquals(BYTE_VALUE, node.readByte((byte) 1, "byte"));
        assertEquals(SHORT_VALUE, node.readShort((short) 1, "short"));
        assertEquals(INT_VALUE, node.readInteger(1, "integer"));
        assertTrue(node.readIntegerOptional("integer").isPresent());
        assertEquals(FLOAT_VALUE, node.readFloat(1.0f, "float"));
        assertEquals(LONG_VALUE, node.readLong(1L, "long"));
        assertEquals(DOUBLE_VALUE, node.readDouble(1.0, "double"));
        assertTrue(node.readDoubleOptional("double").isPresent());
        assertEquals(STRING_VALUE, node.readString("default", "string"));
        assertNull(node.readString("default", "null"));
        assertFalse(node.readStringOptional("null").isPresent());
    }

    /**
     * Test node has element.
     */
    @Test
    void testNodeHas()
    {
        final Xml node = new Xml("test");
        final Xml child = new Xml("child");
        node.writeString("attribute", "none");
        node.add(child);

        assertTrue(node.hasAttribute("attribute"));
        assertTrue(node.hasChild("child"));
        assertFalse(node.hasAttribute(null));
        assertFalse(node.hasAttribute("test"));
        assertFalse(node.hasChild("attribute"));
    }

    /**
     * Test wrong normalization.
     */
    @Test
    void testBadNormalize()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        final Xml node = new Xml("test");
        node.normalize("\\//text()[test]");
        Verbose.info("****************************************************************************************");
    }

    /**
     * Test write in xml file.
     * 
     * @param media The media reference.
     */
    private void testWriteXml(Media media)
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

        root.save(media);

        final Xml child2 = root.createChild("test");
        root.add(child2);
        root.save(media);

        root.removeChild(child2);
        root.save(media);
    }

    /**
     * Test wrong write in xml file.
     * 
     * @throws IOException If error.
     */
    private void testWrongWriteXml() throws IOException
    {
        assertThrows(() -> new Xml("child").save(Medias.create(Constant.EMPTY_STRING)),
                     "[] " + MediaDefault.ERROR_OPEN_MEDIA);

        assertThrows(() -> new Xml("child").save(new MediaMock()), "[null] " + Xml.ERROR_WRITING);
    }

    /**
     * Test read in xml file.
     * 
     * @param media The media reference.
     * @throws LionEngineException If node note found, error case.
     */
    private void testReadXml(Media media)
    {
        final Xml root = new Xml(media);
        final Xml child = root.getChild("child");

        assertEquals(Boolean.valueOf(BOOL_VALUE), Boolean.valueOf(child.readBoolean("boolean")));
        assertEquals(Boolean.valueOf(BOOL_VALUE), child.readBooleanOptional("boolean").get());
        assertEquals(BYTE_VALUE, child.readByte("byte"));
        assertEquals(SHORT_VALUE, child.readShort("short"));
        assertEquals(INT_VALUE, child.readInteger("integer"));
        assertEquals(INT_VALUE, child.readIntegerOptional("integer").getAsInt());
        assertEquals(FLOAT_VALUE, child.readFloat("float"));
        assertEquals(LONG_VALUE, child.readLong("long"));
        assertEquals(DOUBLE_VALUE, child.readDouble("double"));
        assertEquals(DOUBLE_VALUE, child.readDoubleOptional("double").getAsDouble());
        assertEquals(STRING_VALUE, child.readString("string"));
        assertNull(child.readString("null"));
        assertFalse(child.readStringOptional("null").isPresent());
    }

    /**
     * Test wrong read data in xml file.
     * 
     * @param media The media reference.
     */
    private void testWrongReadXml(Media media)
    {
        final Xml root = new Xml(media);
        final XmlReader rootReader = new XmlReader(media);

        assertThrows(() -> rootReader.getChild("none"), XmlReader.ERROR_NODE + "none");
        assertThrows(() -> root.getChild("none"), XmlReader.ERROR_NODE + "none");
        assertThrows(() -> root.readInteger("wrong"), XmlReader.ERROR_ATTRIBUTE + "wrong");
        assertThrows(() -> new Xml(Medias.create("malformed.xml")), "[malformed.xml] " + XmlReader.ERROR_READING);
    }
}
