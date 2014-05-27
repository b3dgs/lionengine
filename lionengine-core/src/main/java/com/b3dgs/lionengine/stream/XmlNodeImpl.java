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
    /** Node name error. */
    private static final String ERROR_NODE_NAME = "The node name must not be null !";
    /** Node error. */
    private static final String ERROR_NODE = "Node not found: ";
    /** Attribute error. */
    private static final String ERROR_ATTRIBUTE = "The following attribute does not exist: ";
    /** Document. */
    private static Document document;

    /** Root reference. */
    private final Element root;

    /**
     * Constructor.
     * 
     * @param name The node name.
     */
    XmlNodeImpl(String name)
    {
        Check.notNull(name, XmlNodeImpl.ERROR_NODE_NAME);
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
     */
    private void write(String attribute, String content)
    {
        root.setAttribute(attribute, content);
    }

    /*
     * XmlNode
     */

    @Override
    public void add(XmlNode node)
    {
        root.appendChild(XmlNodeImpl.class.cast(node).getElement());
    }

    @Override
    public void setText(String text)
    {
        root.setTextContent(text);
    }

    @Override
    public void writeBoolean(String attribute, boolean content)
    {
        write(attribute, String.valueOf(content));
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
    public boolean readBoolean(String attribute)
    {
        return Boolean.parseBoolean(getValue(attribute));
    }

    @Override
    public byte readByte(String attribute)
    {
        return Byte.parseByte(getValue(attribute));
    }

    @Override
    public short readShort(String attribute)
    {
        return Short.parseShort(getValue(attribute));
    }

    @Override
    public int readInteger(String attribute)
    {
        return Integer.parseInt(getValue(attribute));
    }

    @Override
    public long readLong(String attribute)
    {
        return Long.parseLong(getValue(attribute));
    }

    @Override
    public float readFloat(String attribute)
    {
        return Float.parseFloat(getValue(attribute));
    }

    @Override
    public double readDouble(String attribute)
    {
        return Double.parseDouble(getValue(attribute));
    }

    @Override
    public String readString(String attribute)
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
