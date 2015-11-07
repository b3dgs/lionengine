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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.mock.XmlNodeMock;

/**
 * Test the XML node.
 */
public class XmlNodeTest
{
    /** Float precision. */
    private static final float FLOAT_PRECISION = 0.00000001f;
    /** Double precision. */
    private static final double DOUBLE_PRECISION = 0.000000000000001;

    /** Boolean value. */
    private final boolean boolValue = true;
    /** Byte value. */
    private final byte byteValue = 1;
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

    /**
     * Test children in xml node.
     */
    @Test
    public void testXmlnode()
    {
        final XmlNode root = Xml.create("root");
        final XmlNode child1 = root.createChild("child1");
        final XmlNode child2 = root.createChild("child2");

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
        final XmlNode root = Xml.create("root");
        final XmlNode child1 = root.createChild("child1");
        final XmlNode child2 = root.createChild("child2");

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
        final XmlNode node = Xml.create("node");
        node.add(Xml.create("test"));
        try
        {
            node.getChild("void");
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        node.writeBoolean("boolean", boolValue);
        node.writeByte("byte", byteValue);
        node.writeShort("short", shortValue);
        node.writeInteger("integer", intValue);
        node.writeFloat("float", floatValue);
        node.writeLong("long", longValue);
        node.writeDouble("double", doubleValue);
        node.writeString("string", stringValue);
        node.writeString("null", null);

        Assert.assertEquals(Boolean.valueOf(boolValue), Boolean.valueOf(node.readBoolean("boolean")));
        Assert.assertEquals(byteValue, node.readByte("byte"));
        Assert.assertEquals(shortValue, node.readShort("short"));
        Assert.assertEquals(intValue, node.readInteger("integer"));
        Assert.assertEquals(floatValue, node.readFloat("float"), FLOAT_PRECISION);
        Assert.assertEquals(longValue, node.readLong("long"));
        Assert.assertEquals(doubleValue, node.readDouble("double"), DOUBLE_PRECISION);
        Assert.assertEquals(stringValue, node.readString("string"));
        Assert.assertEquals(null, node.readString("null"));
    }

    /**
     * Test the node name error.
     */
    @Test(expected = LionEngineException.class)
    public void testXmlNodeNameError()
    {
        final XmlNode node = Xml.create("%éàç-èyrd");
        Assert.assertNull(node);
    }

    /**
     * Test the node write error.
     */
    @Test(expected = LionEngineException.class)
    public void testXmlNodeWriteError()
    {
        final XmlNode node = Xml.create("test");
        node.writeString("%éàç-èyrd", "error");
    }

    /**
     * Test the node read error.
     */
    @Test(expected = LionEngineException.class)
    public void testXmlNodeReadError()
    {
        final XmlNode node = Xml.create("test");
        node.readString("%éàç-èyrd");
    }

    /**
     * Test the node has element.
     */
    @Test
    public void testXmlNodeHas()
    {
        final XmlNode node = Xml.create("test");
        final XmlNode child = Xml.create("child");
        node.writeString("attribute", "none");
        node.add(child);

        Assert.assertTrue(node.hasAttribute("attribute"));
        Assert.assertTrue(node.hasChild("child"));
        Assert.assertFalse(node.hasAttribute("test"));
        Assert.assertFalse(node.hasChild("attribute"));
    }

    /**
     * Test the node with bad implementation.
     */
    @Test(expected = LionEngineException.class)
    public void testXmlBadImpl()
    {
        final XmlNode node = Xml.create("test");
        node.removeChild(new XmlNodeMock());
        final XmlNode child = new XmlNodeMock();
        node.add(child);
        node.removeChild("mock");
    }
}
