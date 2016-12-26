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
package com.b3dgs.lionengine.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.util.UtilStream;

/**
 * Describe an XML node, which can be modified (reading and writing). All primitive types are written as string inside
 * any XML file. XmlNode can be save in a file, using an XmlParser.
 * <p>
 * Note: Special case for the string stored as <code>null</code> which is in fact stored as {@link Xml#NULL}. When
 * read, the {@link Xml#NULL} string is return if the stored string was <code>null</code>.
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final XmlNode node = XmlFactory.createXmlNode(&quot;node&quot;);
 * node.writeBoolean(&quot;value&quot;, true);
 * </pre>
 */
public class Xml
{
    /** Null string (represents a string stored as <code>null</code>). */
    public static final String NULL = "null";
    /** Error when reading the file. */
    private static final String ERROR_READING = "An error occured while reading";
    /** Error when writing into file. */
    private static final String ERROR_WRITING = "An error occured while writing";
    /** Header attribute. */
    private static final String HEADER_ATTRIBUTE = "xmlns:lionengine";
    /** Header value. */
    private static final String HEADER_VALUE = "http://lionengine.b3dgs.com";
    /** Property indent. */
    private static final String PROPERTY_INDENT = "{http://xml.apache.org/xslt}indent-amount";
    /** Normalize. */
    private static final String NORMALIZE = "//text()[normalize-space()='']";
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
     * Create node from media.
     * 
     * @param media The XML media path.
     * @throws LionEngineException If error when loading media.
     */
    public Xml(Media media)
    {
        Check.notNull(media);

        final InputStream input = media.getInputStream();
        try
        {
            document = DocumentFactory.createDocument(input);
            root = document.getDocumentElement();
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media, ERROR_READING);
        }
        finally
        {
            UtilStream.safeClose(input);
        }
    }

    /**
     * Create node.
     * 
     * @param name The node name.
     * @throws LionEngineException If error when creating the node.
     */
    public Xml(String name)
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
    Xml(Document document, Element root)
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

    /**
     * Save an XML tree to a file.
     * 
     * @param media The output media path.
     * @throws LionEngineException If error when saving media.
     */
    public void save(Media media)
    {
        Check.notNull(root);
        Check.notNull(media);

        final OutputStream output = media.getOutputStream();
        try
        {
            final Transformer transformer = DocumentFactory.createTransformer();
            normalize(NORMALIZE);
            writeString(HEADER_ATTRIBUTE, HEADER_VALUE);
            final DOMSource source = new DOMSource(root);
            final StreamResult result = new StreamResult(output);
            final String yes = "yes";
            transformer.setOutputProperty(OutputKeys.INDENT, yes);
            transformer.setOutputProperty(OutputKeys.STANDALONE, yes);
            transformer.setOutputProperty(PROPERTY_INDENT, "4");
            transformer.transform(source, result);
        }
        catch (final TransformerException exception)
        {
            throw new LionEngineException(exception, media, ERROR_WRITING);
        }
        finally
        {
            UtilStream.safeClose(output);
        }
    }

    /**
     * Create a child node.
     * 
     * @param child The child name.
     * @return The child node.
     */
    public Xml createChild(String child)
    {
        final Element element = document.createElement(child);
        root.appendChild(element);
        return new Xml(document, element);
    }

    /**
     * Add a child node.
     * 
     * @param node The child node.
     * @throws LionEngineException If error when adding the node.
     */
    public void add(Xml node)
    {
        final Element element = node.getElement();
        document.adoptNode(element);
        root.appendChild(element);
    }

    /**
     * Set the text inside the node.
     * 
     * @param text The text content.
     * @throws LionEngineException If error when setting the node text.
     */
    public void setText(String text)
    {
        root.setTextContent(text);
    }

    /**
     * Write a boolean.
     * 
     * @param attribute The attribute name.
     * @param content The boolean value.
     * @throws LionEngineException If error when writing.
     */
    public void writeBoolean(String attribute, boolean content)
    {
        write(attribute, String.valueOf(content));
    }

    /**
     * Write a byte.
     * 
     * @param attribute The attribute name.
     * @param content The byte value.
     * @throws LionEngineException If error when writing.
     */
    public void writeByte(String attribute, byte content)
    {
        write(attribute, String.valueOf(content));
    }

    /**
     * Write a short.
     * 
     * @param attribute The attribute name.
     * @param content The short value.
     * @throws LionEngineException If error when writing.
     */
    public void writeShort(String attribute, short content)
    {
        write(attribute, String.valueOf(content));
    }

    /**
     * Write an integer.
     * 
     * @param attribute The attribute name.
     * @param content The integer value.
     * @throws LionEngineException If error when writing.
     */
    public void writeInteger(String attribute, int content)
    {
        write(attribute, String.valueOf(content));
    }

    /**
     * Write a long.
     * 
     * @param attribute The attribute name.
     * @param content The long value.
     * @throws LionEngineException If error when writing.
     */
    public void writeLong(String attribute, long content)
    {
        write(attribute, String.valueOf(content));
    }

    /**
     * Write a float.
     * 
     * @param attribute The float name.
     * @param content The float value.
     * @throws LionEngineException If error when writing.
     */
    public void writeFloat(String attribute, float content)
    {
        write(attribute, String.valueOf(content));
    }

    /**
     * Write a double.
     * 
     * @param attribute The attribute name.
     * @param content The double value.
     * @throws LionEngineException If error when writing.
     */
    public void writeDouble(String attribute, double content)
    {
        write(attribute, String.valueOf(content));
    }

    /**
     * Write a string. If the content is equal to <code>null</code>, {@link Xml#NULL} is wrote instead.
     * 
     * @param attribute The attribute name.
     * @param content The string value.
     * @throws LionEngineException If error when writing.
     */
    public void writeString(String attribute, String content)
    {
        if (content == null)
        {
            write(attribute, Xml.NULL);
        }
        else
        {
            write(attribute, content);
        }
    }

    /**
     * Read a boolean.
     * 
     * @param attribute The boolean name.
     * @return The boolean value.
     * @throws LionEngineException If error when reading.
     */
    public boolean readBoolean(String attribute)
    {
        return Boolean.parseBoolean(getValue(attribute));
    }

    /**
     * Read a boolean.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The boolean name.
     * @return The boolean value.
     */
    public boolean readBoolean(boolean defaultValue, String attribute)
    {
        return Boolean.parseBoolean(getValue(String.valueOf(defaultValue), attribute));
    }

    /**
     * Read a byte.
     * 
     * @param attribute The integer name.
     * @return The byte value.
     * @throws LionEngineException If error when reading.
     */
    public byte readByte(String attribute)
    {
        return Byte.parseByte(getValue(attribute));
    }

    /**
     * Read a byte.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The integer name.
     * @return The byte value.
     */
    public byte readByte(byte defaultValue, String attribute)
    {
        return Byte.parseByte(getValue(String.valueOf(defaultValue), attribute));
    }

    /**
     * Read a short.
     * 
     * @param attribute The integer name.
     * @return The short value.
     * @throws LionEngineException If error when reading.
     */
    public short readShort(String attribute)
    {
        return Short.parseShort(getValue(attribute));
    }

    /**
     * Read a short.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The integer name.
     * @return The short value.
     */
    public short readShort(short defaultValue, String attribute)
    {
        return Short.parseShort(getValue(String.valueOf(defaultValue), attribute));
    }

    /**
     * Read an integer.
     * 
     * @param attribute The integer name.
     * @return The integer value.
     * @throws LionEngineException If error when reading.
     */
    public int readInteger(String attribute)
    {
        return Integer.parseInt(getValue(attribute));
    }

    /**
     * Read an integer.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The integer name.
     * @return The integer value.
     */
    public int readInteger(int defaultValue, String attribute)
    {
        return Integer.parseInt(getValue(String.valueOf(defaultValue), attribute));
    }

    /**
     * Read a long.
     * 
     * @param attribute The float name.
     * @return The long value.
     * @throws LionEngineException If error when reading.
     */
    public long readLong(String attribute)
    {
        return Long.parseLong(getValue(attribute));
    }

    /**
     * Read a long.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The float name.
     * @return The long value.
     */
    public long readLong(long defaultValue, String attribute)
    {
        return Long.parseLong(getValue(String.valueOf(defaultValue), attribute));
    }

    /**
     * Read a float.
     * 
     * @param attribute The float name.
     * @return The float value.
     * @throws LionEngineException If error when reading.
     */
    public float readFloat(String attribute)
    {
        return Float.parseFloat(getValue(attribute));
    }

    /**
     * Read a float.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The float name.
     * @return The float value.
     */
    public float readFloat(float defaultValue, String attribute)
    {
        return Float.parseFloat(getValue(String.valueOf(defaultValue), attribute));
    }

    /**
     * Read a double.
     * 
     * @param attribute The double name.
     * @return The double value.
     * @throws LionEngineException If error when reading.
     */
    public double readDouble(String attribute)
    {
        return Double.parseDouble(getValue(attribute));
    }

    /**
     * Read a double.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The double name.
     * @return The double value.
     */
    public double readDouble(double defaultValue, String attribute)
    {
        return Double.parseDouble(getValue(String.valueOf(defaultValue), attribute));
    }

    /**
     * Read a string. If the read string is equal to {@link Xml#NULL}, <code>null</code> will be returned instead.
     * 
     * @param attribute The string name.
     * @return The string value.
     * @throws LionEngineException If error when reading.
     */
    public String readString(String attribute)
    {
        final String value = getValue(attribute);
        if (Xml.NULL.equals(value))
        {
            return null;
        }
        return value;
    }

    /**
     * Read a string. If the read string is equal to {@link Xml#NULL}, <code>null</code> will be returned instead.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The string name.
     * @return The string value.
     */
    public String readString(String defaultValue, String attribute)
    {
        final String value = getValue(defaultValue, attribute);
        if (Xml.NULL.equals(value))
        {
            return null;
        }
        return value;
    }

    /**
     * Remove attribute.
     * 
     * @param attribute The attribute to remove.
     */
    public void removeAttribute(String attribute)
    {
        root.removeAttribute(attribute);
    }

    /**
     * Remove child.
     * 
     * @param child The child to remove.
     * @throws LionEngineException If no node is found at this child name.
     */
    public void removeChild(String child)
    {
        final Xml node = getChild(child);
        root.removeChild(node.getElement());
    }

    /**
     * Remove child.
     * 
     * @param child The child to remove.
     */
    public void removeChild(Xml child)
    {
        root.removeChild(child.getElement());
    }

    /**
     * Remove all children.
     * 
     * @param children The children to remove.
     */
    public void removeChildren(String children)
    {
        for (final Xml child : getChildren(children))
        {
            root.removeChild(child.getElement());
        }
    }

    /**
     * Get the name of the current node.
     * 
     * @return The node name.
     */
    public String getNodeName()
    {
        return root.getTagName();
    }

    /**
     * Return the text inside the node.
     * 
     * @return The text.
     */
    public String getText()
    {
        return root.getTextContent();
    }

    /**
     * Get a child node from its name.
     * 
     * @param name The child name.
     * @return The child node reference.
     * @throws LionEngineException If no node is found at this child name.
     */
    public Xml getChild(String name)
    {
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (node instanceof Element && node.getNodeName().equals(name))
            {
                return new Xml(document, (Element) node);
            }
        }
        throw new LionEngineException(ERROR_NODE, name);
    }

    /**
     * Get the list of all children with this name.
     * 
     * @param name The children name.
     * @return The children list.
     */
    public Collection<Xml> getChildren(String name)
    {
        final Collection<Xml> nodes = new ArrayList<Xml>(1);
        final NodeList list = root.getElementsByTagName(name);
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (root.equals(node.getParentNode()) && node instanceof Element)
            {
                nodes.add(new Xml(document, (Element) node));
            }
        }
        return nodes;
    }

    /**
     * Get list of all children.
     * 
     * @return The children list.
     */
    public Collection<Xml> getChildren()
    {
        final Collection<Xml> nodes = new ArrayList<Xml>(1);
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (root.equals(node.getParentNode()) && node instanceof Element)
            {
                nodes.add(new Xml(document, (Element) node));
            }
        }
        return nodes;
    }

    /**
     * Get all attributes.
     * 
     * @return The attributes map reference.
     */
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

    /**
     * Check if node has the following attribute.
     * 
     * @param attribute The attribute name.
     * @return <code>true</code> if attribute exists, <code>false</code> else.
     */
    public boolean hasAttribute(String attribute)
    {
        return root.hasAttribute(attribute);
    }

    /**
     * Check if node has the following child.
     * 
     * @param child The child name.
     * @return <code>true</code> if child exists, <code>false</code> else.
     */
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
