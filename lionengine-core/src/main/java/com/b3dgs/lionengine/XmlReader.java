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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Describe an XML node, which can be read.
 */
public class XmlReader implements AttributesReader
{
    /** Node error. */
    static final String ERROR_NODE = "Node not found: ";
    /** Error when reading the file. */
    static final String ERROR_READING = "An error occured while reading";
    /** Attribute error. */
    static final String ERROR_ATTRIBUTE = "The following attribute does not exist: ";

    /** Document. */
    protected final Document document;
    /** Root reference. */
    protected final Element root;

    /**
     * Create node from media.
     * 
     * @param media The XML media path (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument or error when loading media.
     */
    public XmlReader(Media media)
    {
        super();

        Check.notNull(media);

        try (InputStream input = media.getInputStream())
        {
            document = DocumentFactory.createDocument(input);
            root = document.getDocumentElement();
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media, ERROR_READING);
        }
    }

    /**
     * Create node.
     * 
     * @param name The node name (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument or error when creating the node.
     */
    public XmlReader(String name)
    {
        super();

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
     * @param document The document reference (must not be <code>null</code>).
     * @param root The root reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    XmlReader(Document document, Element root)
    {
        super();

        Check.notNull(document);
        Check.notNull(root);

        this.document = document;
        this.root = root;
    }

    /**
     * Get the node text value.
     * 
     * @param path The node path.
     * @return The node text value.
     * @throws LionEngineException If unable to read node.
     */
    public String getText(String... path)
    {
        final XmlReader node = getNode(path);
        return node.getText();
    }

    /**
     * Get the node text value.
     * 
     * @param defaultValue The value used if node does not exist.
     * @param path The node path.
     * @return The node text value.
     */
    public String getTextDefault(String defaultValue, String... path)
    {
        final XmlReader node = getNodeDefault(path);
        if (node != null)
        {
            return node.getText();
        }
        return defaultValue;
    }

    @Override
    public boolean readBoolean(String attribute, String... path)
    {
        return Boolean.parseBoolean(getNodeString(attribute, path));
    }

    @Override
    public boolean readBoolean(boolean defaultValue, String attribute, String... path)
    {
        return Boolean.parseBoolean(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
    }

    @Override
    public Optional<Boolean> readBooleanOptional(String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return Optional.of(Boolean.valueOf(readBoolean(attribute, path)));
        }
        return Optional.empty();
    }

    @Override
    public byte readByte(String attribute, String... path)
    {
        return Byte.parseByte(getNodeString(attribute, path));
    }

    @Override
    public byte readByte(byte defaultValue, String attribute, String... path)
    {
        return Byte.parseByte(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
    }

    @Override
    public char readChar(String attribute, String... path)
    {
        return getNodeString(attribute, path).charAt(0);
    }

    @Override
    public char readChar(byte defaultValue, String attribute, String... path)
    {
        return getNodeStringDefault(String.valueOf(defaultValue), attribute, path).charAt(0);
    }

    @Override
    public short readShort(String attribute, String... path)
    {
        return Short.parseShort(getNodeString(attribute, path));
    }

    @Override
    public short readShort(short defaultValue, String attribute, String... path)
    {
        return Short.parseShort(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
    }

    @Override
    public int readInteger(String attribute, String... path)
    {
        try
        {
            return Integer.parseInt(getNodeString(attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception, ERROR_ATTRIBUTE + attribute);
        }
    }

    @Override
    public int readInteger(int defaultValue, String attribute, String... path)
    {
        try
        {
            return Integer.parseInt(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception, ERROR_ATTRIBUTE + attribute);
        }
    }

    @Override
    public OptionalInt readIntegerOptional(String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return OptionalInt.of(readInteger(attribute, path));
        }
        return OptionalInt.empty();
    }

    @Override
    public long readLong(String attribute, String... path)
    {
        try
        {
            return Long.parseLong(getNodeString(attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception, ERROR_ATTRIBUTE + attribute);
        }
    }

    @Override
    public long readLong(long defaultValue, String attribute, String... path)
    {
        try
        {
            return Long.parseLong(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception, ERROR_ATTRIBUTE + attribute);
        }
    }

    @Override
    public OptionalLong readLongOptional(String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return OptionalLong.of(readLong(attribute, path));
        }
        return OptionalLong.empty();
    }

    @Override
    public float readFloat(String attribute, String... path)
    {
        return Float.parseFloat(getNodeString(attribute, path));
    }

    @Override
    public float readFloat(float defaultValue, String attribute, String... path)
    {
        return Float.parseFloat(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
    }

    @Override
    public double readDouble(String attribute, String... path)
    {
        try
        {
            return Double.parseDouble(getNodeString(attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception, ERROR_ATTRIBUTE + attribute);
        }
    }

    @Override
    public double readDouble(double defaultValue, String attribute, String... path)
    {
        try
        {
            return Double.parseDouble(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception, ERROR_ATTRIBUTE + attribute);
        }
    }

    @Override
    public OptionalDouble readDoubleOptional(String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return OptionalDouble.of(readDouble(attribute, path));
        }
        return OptionalDouble.empty();
    }

    @Override
    public String readString(String attribute, String... path)
    {
        return getNodeString(attribute, path);
    }

    @Override
    public String readStringDefault(String defaultValue, String attribute, String... path)
    {
        return getNodeStringDefault(defaultValue, attribute, path);
    }

    @Override
    public Optional<String> readStringOptional(String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return Optional.ofNullable(readString(attribute, path));
        }
        return Optional.empty();
    }

    @Override
    public Media readMedia(String attribute, String... path)
    {
        return Medias.create(getNodeString(attribute, path));
    }

    @Override
    public Optional<Media> readMediaOptional(String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return Optional.of(Medias.create(getNodeString(attribute, path)));
        }
        return Optional.empty();
    }

    @Override
    public <E extends Enum<E>> E readEnum(Class<E> type, String attribute, String... path)
    {
        try
        {
            return Enum.valueOf(type, getNodeString(attribute, path));
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(exception, attribute);
        }
    }

    @Override
    public <E extends Enum<E>> Optional<E> readEnumOptional(Class<E> type, String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return Optional.of(readEnum(type, attribute, path));
        }
        return Optional.empty();
    }

    @Override
    public <T> T readImplementation(Class<T> type, String... path)
    {
        return readImplementation(getClass().getClassLoader(), type, path);
    }

    @Override
    public <T> T readImplementation(ClassLoader loader, Class<T> type, String... path)
    {
        return readImplementation(loader, type, new Class<?>[0], Collections.emptyList(), path);
    }

    @Override
    public <T> T readImplementation(Class<T> type, Class<?> paramType, Object paramValue, String... path)
    {
        return readImplementation(type, new Class<?>[]
        {
            paramType
        }, Arrays.asList(paramValue), path);
    }

    @Override
    public <T> T readImplementation(Class<T> type, Class<?>[] paramsType, Collection<?> paramsValue, String... path)
    {
        return readImplementation(getClass().getClassLoader(), type, paramsType, paramsValue, path);
    }

    @Override
    public <T> T readImplementation(ClassLoader loader,
                                    Class<T> type,
                                    Class<?>[] paramsType,
                                    Collection<?> paramsValue,
                                    String... path)
    {
        final String className = getText(path).trim();
        return readImplementation(loader, type, paramsType, paramsValue, className);
    }

    @Override
    public boolean hasAttribute(String attribute, String... path)
    {
        XmlReader node = this;
        for (final String element : path)
        {
            if (!node.hasChild(element))
            {
                return false;
            }
            node = node.getChild(element);
        }
        return node.existsAttribute(attribute);
    }

    /**
     * Check if node has the following attribute.
     * 
     * @param attribute The attribute name (can be <code>null</code>).
     * @return <code>true</code> if attribute exists, <code>false</code> else.
     */
    private boolean existsAttribute(String attribute)
    {
        if (attribute == null)
        {
            return false;
        }
        return root.hasAttribute(attribute);
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
     * Get all attributes.
     * 
     * @return The attributes map reference.
     */
    public Map<String, String> getAttributes()
    {
        final NamedNodeMap map = root.getAttributes();
        final int length = map.getLength();
        final Map<String, String> attributes = new HashMap<>(length);
        for (int i = 0; i < length; i++)
        {
            final Node node = map.item(i);
            attributes.put(node.getNodeName(), node.getNodeValue());
        }
        return attributes;
    }

    /**
     * Check if node has the following child.
     * 
     * @param child The child name (can be <code>null</code>).
     * @return <code>true</code> if child exists, <code>false</code> else.
     */
    public boolean hasChild(String child)
    {
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (node.getNodeName().equals(child))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a child node from its name.
     * 
     * @param name The child name (must not be <code>null</code>).
     * @return The child node reference.
     * @throws LionEngineException If no node is found at this child name.
     */
    public XmlReader getChild(String name)
    {
        Check.notNull(name);

        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (node instanceof Element && node.getNodeName().equals(name))
            {
                return new XmlReader(document, (Element) node);
            }
        }
        throw new LionEngineException(ERROR_NODE + name);
    }

    /**
     * Get a child node from its name.
     * 
     * @param name The child name (can be <code>null</code>).
     * @return The child node reference.
     */
    public Optional<XmlReader> getChildOptional(String name)
    {
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (node instanceof Element && node.getNodeName().equals(name))
            {
                return Optional.of(new XmlReader(document, (Element) node));
            }
        }
        return Optional.empty();
    }

    /**
     * Get the list of all children with this name.
     * 
     * @param name The children name (must not be <code>null</code>).
     * @return The children list.
     * @throws LionEngineException If invalid argument.
     */
    public Collection<XmlReader> getChildren(String name)
    {
        Check.notNull(name);

        final Collection<XmlReader> nodes = new ArrayList<>(1);
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (name.equals(node.getNodeName()))
            {
                nodes.add(new XmlReader(document, (Element) node));
            }
        }
        return nodes;
    }

    /**
     * Get list of all children.
     * 
     * @return The children list.
     */
    public Collection<XmlReader> getChildren()
    {
        final Collection<XmlReader> nodes = new ArrayList<>(1);
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node node = list.item(i);
            if (node instanceof Element)
            {
                nodes.add(new XmlReader(document, (Element) node));
            }
        }
        return nodes;
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
     * Get the node at the following path.
     * 
     * @param path The node path.
     * @return The node found.
     * @throws LionEngineException If node not found.
     */
    private XmlReader getNode(String... path)
    {
        XmlReader node = this;
        for (final String element : path)
        {
            try
            {
                node = node.getChild(element);
            }
            catch (final LionEngineException exception)
            {
                throw new LionEngineException(exception, Arrays.toString(path));
            }
        }
        return node;
    }

    /**
     * Get the node at the following path.
     * 
     * @param path The node path.
     * @return The node found, <code>null</code> if none.
     */
    private XmlReader getNodeDefault(String... path)
    {
        XmlReader node = this;
        for (final String element : path)
        {
            if (!node.hasChild(element))
            {
                return null;
            }
            node = node.getChild(element);
        }
        return node;
    }

    /**
     * Get the string from a node.
     * 
     * @param attribute The attribute to get.
     * @param path The attribute node path.
     * @return The string found.
     * @throws LionEngineException If node not found.
     */
    private String getNodeString(String attribute, String... path)
    {
        final XmlReader node = getNode(path);
        if (!node.root.hasAttribute(attribute))
        {
            throw new LionEngineException(ERROR_ATTRIBUTE + attribute);
        }
        final String value = node.root.getAttribute(attribute);
        if (Constant.NULL.equals(value))
        {
            return null;
        }
        return value;
    }

    /**
     * Get the string from a node.
     * 
     * @param defaultValue The default value returned if path not found.
     * @param attribute The attribute to get.
     * @param path The attribute node path.
     * @return The string found.
     * @throws LionEngineException If node not found.
     */
    private String getNodeStringDefault(String defaultValue, String attribute, String... path)
    {
        final XmlReader node = getNodeDefault(path);
        final String value;
        if (node != null && node.hasAttribute(attribute))
        {
            value = node.readString(attribute);
        }
        else if (Constant.NULL.equals(defaultValue))
        {
            value = null;
        }
        else
        {
            value = defaultValue;
        }
        return value;
    }
}
