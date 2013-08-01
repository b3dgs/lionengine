package com.b3dgs.lionengine.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Element;

import com.b3dgs.lionengine.Check;

/**
 * Xml node implementation.
 */
class XmlNodeImpl
        implements XmlNode
{
    /** Root reference. */
    private final Element root;

    /**
     * Create a root node.
     * 
     * @param name The node name.
     */
    XmlNodeImpl(String name)
    {
        Check.notNull(name, "The node name must not be null !");
        root = new Element(name);
    }

    /**
     * Create a node.
     * 
     * @param root The root reference.
     */
    XmlNodeImpl(Element root)
    {
        Check.notNull(root, "The root node must not be null !");
        this.root = root;
    }

    /**
     * Get the original element.
     * 
     * @return The jdom element.
     */
    Element getElement()
    {
        return root;
    }

    /**
     * Get the attribute value.
     * 
     * @param attribute The attribute name.
     * @return The attribute value.
     */
    private String getAttributeValue(String attribute)
    {
        Check.notNull(attribute, "The attribute must not be null !");
        final String value = root.getAttributeValue(attribute);
        Check.notNull(value, "Can not read the attribute value for: \"", attribute, "\"");
        return value;
    }

    /**
     * Write a data to the root.
     * 
     * @param attribute The attribute name.
     * @param content The content value.
     */
    private void write(String attribute, String content)
    {
        Check.notNull(attribute, "The attribute must not be null !");
        root.setAttribute(attribute, content);
    }

    /*
     * XmlNode
     */

    @Override
    public void add(XmlNode node)
    {
        if (node instanceof XmlNodeImpl)
        {
            root.addContent(((XmlNodeImpl) node).getElement());
        }
    }

    @Override
    public void writeBoolean(String attribute, boolean content)
    {
        Check.notNull(attribute, "The attribute must not be null !");
        root.setAttribute(attribute, String.valueOf(content));
    }

    @Override
    public void writeByte(String attribute, byte content)
    {
        write(attribute, String.valueOf(content));
    }

    @Override
    public void writeShort(String attribute, short content)
    {
        write(attribute, String.valueOf(content));
    }

    @Override
    public void writeInteger(String attribute, int content)
    {
        write(attribute, String.valueOf(content));
    }

    @Override
    public void writeLong(String attribute, long content)
    {
        write(attribute, String.valueOf(content));
    }

    @Override
    public void writeFloat(String attribute, float content)
    {
        write(attribute, String.valueOf(content));
    }

    @Override
    public void writeDouble(String attribute, double content)
    {
        write(attribute, String.valueOf(content));
    }

    @Override
    public void writeString(String attribute, String content)
    {
        Check.notNull(attribute, "The attribute must not be null !");
        if (content == null)
        {
            root.setAttribute(attribute, XmlNode.NULL);
        }
        else
        {
            root.setAttribute(attribute, content);
        }
    }

    @Override
    public boolean readBoolean(String attribute)
    {
        Check.notNull(attribute, "The attribute must not be null !");
        return Boolean.parseBoolean(getAttributeValue(attribute));
    }

    @Override
    public byte readByte(String attribute)
    {
        Check.notNull(attribute, "The attribute must not be null !");
        return Byte.parseByte(getAttributeValue(attribute));
    }

    @Override
    public short readShort(String attribute)
    {
        Check.notNull(attribute, "The attribute must not be null !");
        return Short.parseShort(getAttributeValue(attribute));
    }

    @Override
    public int readInteger(String attribute)
    {
        Check.notNull(attribute, "The attribute must not be null !");
        return Integer.parseInt(getAttributeValue(attribute));
    }

    @Override
    public long readLong(String attribute)
    {
        Check.notNull(attribute, "The attribute must not be null !");
        return Long.parseLong(getAttributeValue(attribute));
    }

    @Override
    public float readFloat(String attribute)
    {
        Check.notNull(attribute, "The attribute must not be null !");
        return Float.parseFloat(getAttributeValue(attribute));
    }

    @Override
    public double readDouble(String attribute)
    {
        Check.notNull(attribute, "The attribute must not be null !");
        return Double.parseDouble(getAttributeValue(attribute));
    }

    @Override
    public String readString(String attribute)
    {
        Check.notNull(attribute, "The attribute must not be null !");
        final String value = getAttributeValue(attribute);
        if (XmlNode.NULL.equals(value))
        {
            return null;
        }
        return value;
    }

    @Override
    public String getText()
    {
        return root.getText();
    }

    @Override
    public XmlNode getChild(String name) throws XmlNodeNotFoundException
    {
        Check.notNull(name, "The child name must not be null !");
        final Element child = root.getChild(name);
        if (child == null)
        {
            throw new XmlNodeNotFoundException("The following node " + name + " was not found !");
        }
        return new XmlNodeImpl(child);
    }

    @Override
    public List<XmlNode> getChildren(String name)
    {
        final List<XmlNode> nodes = new ArrayList<>(1);
        for (final Element element : root.getChildren(name))
        {
            nodes.add(new XmlNodeImpl(element));
        }
        return nodes;
    }

    @Override
    public List<XmlNode> getChildren()
    {
        final List<XmlNode> nodes = new ArrayList<>(1);
        for (final Element element : root.getChildren())
        {
            nodes.add(new XmlNodeImpl(element));
        }
        return nodes;
    }

    @Override
    public Map<String, String> getAttributes()
    {
        final Map<String, String> attributes = new HashMap<>();
        for (final Attribute attribute : root.getAttributes())
        {
            final String key = attribute.getName();
            attributes.put(key, getAttributeValue(key));
        }
        return attributes;
    }
}
