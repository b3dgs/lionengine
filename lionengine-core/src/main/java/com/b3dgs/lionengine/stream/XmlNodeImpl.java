/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

/**
 * XML node implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class XmlNodeImpl
        implements XmlNode
{
    /** Node error. */
    private static final String ERROR_NODE = "Node not found: ";
    /** Attribute error. */
    private static final String ERROR_ATTRIBUTE = "The following attribute does not exist: ";
    /** Attribute error. */
    private static final String ERROR_WRITE_ATTRIBUTE = "Error when setting the attribute:";
    /** Attribute error. */
    private static final String ERROR_WRITE_CONTENT = " with the following content: ";
    /** Document. */
    private static Document document;

    /** Root reference. */
    private final Element root;

    /**
     * Constructor.
     * 
     * @param name The node name.
     * @throws LionEngineException If error when creating the node.
     */
    XmlNodeImpl(String name) throws LionEngineException
    {
        Check.notNull(name);

        try
        {
            final DocumentBuilder constructeur = XmlFactory.getDocumentFactory().newDocumentBuilder();
            synchronized (XmlNodeImpl.class)
            {
                if (XmlNodeImpl.document == null)
                {
                    XmlNodeImpl.document = constructeur.newDocument();
                }
            }
            root = XmlNodeImpl.document.createElement(name);
        }
        catch (final ParserConfigurationException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Constructor.
     * 
     * @param root The root reference.
     */
    XmlNodeImpl(Element root)
    {
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
     * @throws LionEngineException If attribute is not valid or does not exist.
     */
    private String getValue(String attribute) throws LionEngineException
    {
        if (root.hasAttribute(attribute))
        {
            return root.getAttribute(attribute);
        }
        throw new LionEngineException(XmlNodeImpl.ERROR_ATTRIBUTE, attribute);
    }

    /**
     * Write a data to the root.
     * 
     * @param attribute The attribute name.
     * @param content The content value.
     * @throws LionEngineException If error when setting the attribute.
     */
    private void write(String attribute, String content) throws LionEngineException
    {
        try
        {
            root.setAttribute(attribute, content);
        }
        catch (final DOMException exception)
        {
            throw new LionEngineException(exception, XmlNodeImpl.ERROR_WRITE_ATTRIBUTE, attribute,
                    XmlNodeImpl.ERROR_WRITE_CONTENT, content);
        }
    }

    /*
     * XmlNode
     */

    @Override
    public void add(XmlNode node) throws LionEngineException
    {
        try
        {
            root.appendChild(XmlNodeImpl.class.cast(node).getElement());
        }
        catch (final DOMException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    @Override
    public void setText(String text) throws LionEngineException
    {
        try
        {
            root.setTextContent(text);
        }
        catch (final DOMException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    @Override
    public void writeBoolean(String attribute, boolean content) throws LionEngineException
    {
        write(attribute, String.valueOf(content));
    }

    @Override
    public void writeByte(String attribute, byte content) throws LionEngineException
    {
        write(attribute, String.valueOf(content));
    }

    @Override
    public void writeShort(String attribute, short content) throws LionEngineException
    {
        write(attribute, String.valueOf(content));
    }

    @Override
    public void writeInteger(String attribute, int content) throws LionEngineException
    {
        write(attribute, String.valueOf(content));
    }

    @Override
    public void writeLong(String attribute, long content) throws LionEngineException
    {
        write(attribute, String.valueOf(content));
    }

    @Override
    public void writeFloat(String attribute, float content) throws LionEngineException
    {
        write(attribute, String.valueOf(content));
    }

    @Override
    public void writeDouble(String attribute, double content) throws LionEngineException
    {
        write(attribute, String.valueOf(content));
    }

    @Override
    public void writeString(String attribute, String content) throws LionEngineException
    {
        if (content == null)
        {
            write(attribute, XmlNode.NULL);
        }
        else
        {
            write(attribute, content);
        }
    }

    @Override
    public boolean readBoolean(String attribute) throws LionEngineException
    {
        return Boolean.parseBoolean(getValue(attribute));
    }

    @Override
    public byte readByte(String attribute) throws LionEngineException
    {
        return Byte.parseByte(getValue(attribute));
    }

    @Override
    public short readShort(String attribute) throws LionEngineException
    {
        return Short.parseShort(getValue(attribute));
    }

    @Override
    public int readInteger(String attribute) throws LionEngineException
    {
        return Integer.parseInt(getValue(attribute));
    }

    @Override
    public long readLong(String attribute) throws LionEngineException
    {
        return Long.parseLong(getValue(attribute));
    }

    @Override
    public float readFloat(String attribute) throws LionEngineException
    {
        return Float.parseFloat(getValue(attribute));
    }

    @Override
    public double readDouble(String attribute) throws LionEngineException
    {
        return Double.parseDouble(getValue(attribute));
    }

    @Override
    public String readString(String attribute) throws LionEngineException
    {
        final String value = getValue(attribute);
        if (XmlNode.NULL.equals(value))
        {
            return null;
        }
        return value;
    }

    @Override
    public String getText()
    {
        return root.getTextContent();
    }

    @Override
    public XmlNode getChild(String name) throws LionEngineException
    {
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (node instanceof Element && node.getNodeName().equals(name))
            {
                return new XmlNodeImpl((Element) node);
            }
        }
        throw new LionEngineException(XmlNodeImpl.ERROR_NODE, name);
    }

    @Override
    public List<XmlNode> getChildren(String name)
    {
        final List<XmlNode> nodes = new ArrayList<>(1);
        final NodeList list = root.getElementsByTagName(name);
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (node instanceof Element)
            {
                nodes.add(new XmlNodeImpl((Element) node));
            }
        }
        return nodes;
    }

    @Override
    public List<XmlNode> getChildren()
    {
        final List<XmlNode> nodes = new ArrayList<>(1);
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (node instanceof Element)
            {
                nodes.add(new XmlNodeImpl((Element) node));
            }
        }
        return nodes;
    }

    @Override
    public Map<String, String> getAttributes()
    {
        final Map<String, String> attributes = new HashMap<>();
        final NamedNodeMap map = root.getAttributes();
        for (int i = 0; i < map.getLength(); i++)
        {
            final Node node = map.item(i);
            attributes.put(node.getNodeName(), node.getNodeValue());
        }
        return attributes;
    }
}
