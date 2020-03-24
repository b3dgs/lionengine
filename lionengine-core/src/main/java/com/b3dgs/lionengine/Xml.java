/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Describe an Xml node, which can be modified (reading and writing). All primitive types are written as string inside
 * any Xml file.
 */
public class Xml extends XmlReader
{
    /** Error when writing into file. */
    static final String ERROR_WRITING = "An error occured while writing";
    /** Node error. */
    static final String ERROR_NODE = "Node not found: ";
    /** Attribute error. */
    static final String ERROR_WRITE_ATTRIBUTE = "Error when setting the attribute:";
    /** Attribute error. */
    static final String ERROR_WRITE_CONTENT = " with the following content: ";
    /** Property indent. */
    private static final String PROPERTY_INDENT = "{http://xml.apache.org/xslt}indent-amount";
    /** Normalize. */
    private static final String NORMALIZE = "//text()[normalize-space()='']";

    /**
     * Create node from media.
     * 
     * @param media The XML media path (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument or error when loading media.
     */
    public Xml(Media media)
    {
        super(media);
    }

    /**
     * Create node.
     * 
     * @param name The node name (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument or error when creating the node.
     */
    public Xml(String name)
    {
        super(name);
    }

    /**
     * Internal constructor.
     * 
     * @param document The document reference (must not be <code>null</code>).
     * @param root The root reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    Xml(Document document, Element root)
    {
        super(document, root);
    }

    /**
     * Normalize document.
     * 
     * @param expression The expression to evaluate (must not be <code>null</code>).
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
     * Write a data to the root.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param content The content value (must not be <code>null</code>).
     * @throws LionEngineException If error when setting the attribute.
     */
    private void write(String attribute, String content)
    {
        Check.notNull(attribute);
        Check.notNull(content);

        try
        {
            root.setAttribute(attribute, content);
        }
        catch (final DOMException exception)
        {
            throw new LionEngineException(exception, ERROR_WRITE_ATTRIBUTE + attribute + ERROR_WRITE_CONTENT + content);
        }
    }

    /**
     * Save an XML tree to a file.
     * 
     * @param media The output media path (must not be <code>null</code>).
     * @throws LionEngineException If error when saving media.
     */
    public void save(Media media)
    {
        Check.notNull(media);

        try (OutputStream output = media.getOutputStream())
        {
            final Transformer transformer = DocumentFactory.createTransformer();
            normalize(NORMALIZE);
            writeString(Constant.XML_HEADER, Constant.ENGINE_WEBSITE);
            final DOMSource source = new DOMSource(root);
            final StreamResult result = new StreamResult(output);
            final String yes = "yes";
            transformer.setOutputProperty(OutputKeys.INDENT, yes);
            transformer.setOutputProperty(OutputKeys.STANDALONE, yes);
            transformer.setOutputProperty(PROPERTY_INDENT, "4");
            transformer.transform(source, result);
        }
        catch (final TransformerException | IOException exception)
        {
            throw new LionEngineException(exception, media, ERROR_WRITING);
        }
    }

    /**
     * Create a child node.
     * 
     * @param child The child name
     * @return The child node.
     * @throws LionEngineException If invalid argument.
     */
    public Xml createChild(String child)
    {
        Check.notNull(child);

        final Element element = document.createElement(child);
        root.appendChild(element);
        return new Xml(document, element);
    }

    /**
     * Add a child node.
     * 
     * @param node The child node
     * @throws LionEngineException If error when adding the node.
     */
    public void add(XmlReader node)
    {
        Check.notNull(node);

        final Element element = node.getElement();
        document.adoptNode(element);
        root.appendChild(element);
    }

    /**
     * Set the text inside the node.
     * 
     * @param text The text content (must not be <code>null</code>).
     * @throws LionEngineException If error when setting the node text.
     */
    public void setText(String text)
    {
        Check.notNull(text);

        root.setTextContent(text);
    }

    /**
     * Write a boolean.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
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
     * @param attribute The attribute name (must not be <code>null</code>).
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
     * @param attribute The attribute name (must not be <code>null</code>).
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
     * @param attribute The attribute name (must not be <code>null</code>).
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
     * @param attribute The attribute name (must not be <code>null</code>).
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
     * @param attribute The float name (must not be <code>null</code>).
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
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param content The double value.
     * @throws LionEngineException If error when writing.
     */
    public void writeDouble(String attribute, double content)
    {
        write(attribute, String.valueOf(content));
    }

    /**
     * Write a string. If the content is equal to <code>null</code>, {@link #NULL} is wrote instead.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param content The string value (can be <code>null</code>).
     * @throws LionEngineException If error when writing.
     */
    public void writeString(String attribute, String content)
    {
        if (content == null)
        {
            write(attribute, NULL);
        }
        else
        {
            write(attribute, content);
        }
    }

    /**
     * Remove attribute.
     * 
     * @param attribute The attribute to remove (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public void removeAttribute(String attribute)
    {
        Check.notNull(attribute);

        root.removeAttribute(attribute);
    }

    /**
     * Remove child.
     * 
     * @param child The child to remove (must not be <code>null</code>).
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
     * @param child The child to remove (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public void removeChild(Xml child)
    {
        Check.notNull(child);

        root.removeChild(child.getElement());
    }

    /**
     * Remove all children.
     * 
     * @param children The children to remove (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public void removeChildren(String children)
    {
        final Collection<Xml> all = getChildren(children);
        all.stream().map(Xml::getElement).forEach(root::removeChild);
        all.clear();
    }

    /**
     * Get a child node from its name.
     * 
     * @param name The child name (must not be <code>null</code>).
     * @return The child node reference.
     * @throws LionEngineException If no node is found at this child name.
     */
    public Xml getChild(String name)
    {
        Check.notNull(name);

        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (node instanceof Element && node.getNodeName().equals(name))
            {
                return new Xml(document, (Element) node);
            }
        }
        throw new LionEngineException(ERROR_NODE + name);
    }

    /**
     * Get the list of all children with this name.
     * 
     * @param name The children name (must not be <code>null</code>).
     * @return The children list.
     * @throws LionEngineException If invalid argument.
     */
    public Collection<Xml> getChildren(String name)
    {
        Check.notNull(name);

        final Collection<Xml> nodes = new ArrayList<>(1);
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (name.equals(node.getNodeName()))
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
        final Collection<Xml> nodes = new ArrayList<>(1);
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (node instanceof Element)
            {
                nodes.add(new Xml(document, (Element) node));
            }
        }
        return nodes;
    }
}
