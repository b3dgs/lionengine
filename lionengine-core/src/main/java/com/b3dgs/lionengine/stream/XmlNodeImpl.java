/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;

/**
 * XML node implementation.
 */
final class XmlNodeImpl implements XmlNode
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
    private final Document document;
    /** Root reference. */
    private final Element root;

    /**
     * Internal constructor.
     * 
     * @param name The node name.
     * @throws LionEngineException If error when creating the node.
     */
    XmlNodeImpl(String name)
    {
        Check.notNull(name);
        try
        {
            document = DocumentFactory.createDocument();
            root = document.createElement(name);
        }
        catch (final DOMException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Internal constructor.
     * 
     * @param document The document reference.
     * @param root The root reference.
     */
    XmlNodeImpl(Document document, Element root)
    {
        this.document = document;
        this.root = root;
    }

    /**
     * Normalize document.
     * 
     * @param expression The expression to evaluate.
     */
    void normalize(String expression)
    {
        final XPath xPath = XPathFactory.newInstance().newXPath();
        try
        {
            final NodeList nodeList = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); ++i)
            {
                final Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }
        }
        catch (final XPathExpressionException exception)
        {
            Verbose.exception(exception);
        }
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
    private String getValue(String attribute)
    {
        if (root.hasAttribute(attribute))
        {
            return root.getAttribute(attribute);
        }
        throw new LionEngineException(ERROR_ATTRIBUTE, attribute);
    }

    /**
     * Get the attribute value.
     * 
     * @param defaultValue The value returned if attribute does not exist.
     * @param attribute The attribute name.
     * @return The attribute value.
     * @throws LionEngineException If attribute is not valid or does not exist.
     */
    private String getValue(String defaultValue, String attribute)
    {
        if (root.hasAttribute(attribute))
        {
            return root.getAttribute(attribute);
        }
        return defaultValue;
    }

    /**
     * Write a data to the root.
     * 
     * @param attribute The attribute name.
     * @param content The content value.
     * @throws LionEngineException If error when setting the attribute.
     */
    private void write(String attribute, String content)
    {
        try
        {
            root.setAttribute(attribute, content);
        }
        catch (final DOMException exception)
        {
            throw new LionEngineException(exception, ERROR_WRITE_ATTRIBUTE, attribute, ERROR_WRITE_CONTENT, content);
        }
    }

    /*
     * XmlNode
     */

    @Override
    public XmlNode createChild(String child)
    {
        final Element element = document.createElement(child);
        root.appendChild(element);
        return new XmlNodeImpl(document, element);
    }

    @Override
    public void add(XmlNode node)
    {
        if (node instanceof XmlNodeImpl)
        {
            final Element element = XmlNodeImpl.class.cast(node).getElement();
            document.adoptNode(element);
            root.appendChild(element);
        }
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
    public boolean readBoolean(boolean defaultValue, String attribute)
    {
        return Boolean.parseBoolean(getValue(String.valueOf(defaultValue), attribute));
    }

    @Override
    public byte readByte(String attribute)
    {
        return Byte.parseByte(getValue(attribute));
    }

    @Override
    public byte readByte(byte defaultValue, String attribute)
    {
        return Byte.parseByte(getValue(String.valueOf(defaultValue), attribute));
    }

    @Override
    public short readShort(String attribute)
    {
        return Short.parseShort(getValue(attribute));
    }

    @Override
    public short readShort(short defaultValue, String attribute)
    {
        return Short.parseShort(getValue(String.valueOf(defaultValue), attribute));
    }

    @Override
    public int readInteger(String attribute)
    {
        return Integer.parseInt(getValue(attribute));
    }

    @Override
    public int readInteger(int defaultValue, String attribute)
    {
        return Integer.parseInt(getValue(String.valueOf(defaultValue), attribute));
    }

    @Override
    public long readLong(String attribute)
    {
        return Long.parseLong(getValue(attribute));
    }

    @Override
    public long readLong(long defaultValue, String attribute)
    {
        return Long.parseLong(getValue(String.valueOf(defaultValue), attribute));
    }

    @Override
    public float readFloat(String attribute)
    {
        return Float.parseFloat(getValue(attribute));
    }

    @Override
    public float readFloat(float defaultValue, String attribute)
    {
        return Float.parseFloat(getValue(String.valueOf(defaultValue), attribute));
    }

    @Override
    public double readDouble(String attribute)
    {
        return Double.parseDouble(getValue(attribute));
    }

    @Override
    public double readDouble(double defaultValue, String attribute)
    {
        return Double.parseDouble(getValue(String.valueOf(defaultValue), attribute));
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
    public String readString(String defaultValue, String attribute)
    {
        final String value = getValue(defaultValue, attribute);
        if (XmlNode.NULL.equals(value))
        {
            return null;
        }
        return value;
    }

    @Override
    public void removeAttribute(String attribute)
    {
        root.removeAttribute(attribute);
    }

    @Override
    public void removeChild(String child)
    {
        final XmlNode node = getChild(child);
        root.removeChild(((XmlNodeImpl) node).getElement());
    }

    @Override
    public void removeChild(XmlNode child)
    {
        if (child instanceof XmlNodeImpl)
        {
            root.removeChild(((XmlNodeImpl) child).getElement());
        }
    }

    @Override
    public void removeChildren(String children)
    {
        for (final XmlNode child : getChildren(children))
        {
            root.removeChild(((XmlNodeImpl) child).getElement());
        }
    }

    @Override
    public String getNodeName()
    {
        return root.getTagName();
    }

    @Override
    public String getText()
    {
        return root.getTextContent();
    }

    @Override
    public XmlNode getChild(String name)
    {
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (node instanceof Element && node.getNodeName().equals(name))
            {
                return new XmlNodeImpl(document, (Element) node);
            }
        }
        throw new LionEngineException(ERROR_NODE, name);
    }

    @Override
    public Collection<XmlNode> getChildren(String name)
    {
        final Collection<XmlNode> nodes = new ArrayList<XmlNode>(1);
        final NodeList list = root.getElementsByTagName(name);
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (root.equals(node.getParentNode()) && node instanceof Element)
            {
                nodes.add(new XmlNodeImpl(document, (Element) node));
            }
        }
        return nodes;
    }

    @Override
    public Collection<XmlNode> getChildren()
    {
        final Collection<XmlNode> nodes = new ArrayList<XmlNode>(1);
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (root.equals(node.getParentNode()) && node instanceof Element)
            {
                nodes.add(new XmlNodeImpl(document, (Element) node));
            }
        }
        return nodes;
    }

    @Override
    public Map<String, String> getAttributes()
    {
        final Map<String, String> attributes = new HashMap<String, String>();
        final NamedNodeMap map = root.getAttributes();
        for (int i = 0; i < map.getLength(); i++)
        {
            final Node node = map.item(i);
            attributes.put(node.getNodeName(), node.getNodeValue());
        }
        return attributes;
    }

    @Override
    public boolean hasAttribute(String attribute)
    {
        return root.hasAttribute(attribute);
    }

    @Override
    public boolean hasChild(String child)
    {
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (root.equals(node.getParentNode()) && node instanceof Element && node.getNodeName().equals(child))
            {
                return true;
            }
        }
        return false;
    }
}
