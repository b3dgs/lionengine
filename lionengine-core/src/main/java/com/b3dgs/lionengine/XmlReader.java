/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
    /** Attribute error. */
    public static final String ERROR_ATTRIBUTE = "The following attribute does not exist: ";
    /** Node error. */
    public static final String ERROR_NODE = "Node not found: ";
    /** Enum error. */
    public static final String ERROR_ENUM = "No corresponding enum: ";
    /** Error when reading the file. */
    static final String ERROR_READING = "An error occured while reading";
    /** Class instance error. */
    private static final String ERROR_CLASS_INSTANCE = "Class instantiation error: ";
    /** Class constructor error. */
    private static final String ERROR_CLASS_CONSTRUCTOR = "Class constructor error: ";
    /** Class not found error. */
    private static final String ERROR_CLASS_PRESENCE = "Class not found: ";
    /** Class cache. */
    private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<>();

    /**
     * Clear classes cache.
     */
    public static void clearCache()
    {
        CLASS_CACHE.clear();
    }

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

    @Override
    public String getText(String... path)
    {
        final XmlReader node = getNodeReader(path);
        return node.getText();
    }

    @Override
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
    public boolean getBoolean(String attribute, String... path)
    {
        return Boolean.parseBoolean(getNodeString(attribute, path));
    }

    @Override
    public boolean getBoolean(boolean defaultValue, String attribute, String... path)
    {
        return Boolean.parseBoolean(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
    }

    @Override
    public Optional<Boolean> getBooleanOptional(String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return Optional.of(Boolean.valueOf(getBoolean(attribute, path)));
        }
        return Optional.empty();
    }

    @Override
    public byte getByte(String attribute, String... path)
    {
        return Byte.parseByte(getNodeString(attribute, path));
    }

    @Override
    public byte getByte(byte defaultValue, String attribute, String... path)
    {
        return Byte.parseByte(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
    }

    @Override
    public char getChar(String attribute, String... path)
    {
        return getNodeString(attribute, path).charAt(0);
    }

    @Override
    public char getChar(byte defaultValue, String attribute, String... path)
    {
        return getNodeStringDefault(String.valueOf(defaultValue), attribute, path).charAt(0);
    }

    @Override
    public short getShort(String attribute, String... path)
    {
        return Short.parseShort(getNodeString(attribute, path));
    }

    @Override
    public short getShort(short defaultValue, String attribute, String... path)
    {
        return Short.parseShort(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
    }

    @Override
    public int getInteger(String attribute, String... path)
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
    public int getInteger(int defaultValue, String attribute, String... path)
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
    public OptionalInt getIntegerOptional(String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return OptionalInt.of(getInteger(attribute, path));
        }
        return OptionalInt.empty();
    }

    @Override
    public long getLong(String attribute, String... path)
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
    public long getLong(long defaultValue, String attribute, String... path)
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
    public OptionalLong getLongOptional(String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return OptionalLong.of(getLong(attribute, path));
        }
        return OptionalLong.empty();
    }

    @Override
    public float getFloat(String attribute, String... path)
    {
        return Float.parseFloat(getNodeString(attribute, path));
    }

    @Override
    public float getFloat(float defaultValue, String attribute, String... path)
    {
        return Float.parseFloat(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
    }

    @Override
    public double getDouble(String attribute, String... path)
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
    public double getDouble(double defaultValue, String attribute, String... path)
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
    public OptionalDouble getDoubleOptional(String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return OptionalDouble.of(getDouble(attribute, path));
        }
        return OptionalDouble.empty();
    }

    @Override
    public String getString(String attribute, String... path)
    {
        return getNodeString(attribute, path);
    }

    @Override
    public String getStringDefault(String defaultValue, String attribute, String... path)
    {
        return getNodeStringDefault(defaultValue, attribute, path);
    }

    @Override
    public Optional<String> getStringOptional(String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return Optional.ofNullable(getString(attribute, path));
        }
        return Optional.empty();
    }

    @Override
    public Media getMedia(String attribute, String... path)
    {
        return Medias.create(getNodeString(attribute, path));
    }

    @Override
    public Optional<Media> getMediaOptional(String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return Optional.of(Medias.create(getNodeString(attribute, path)));
        }
        return Optional.empty();
    }

    @Override
    public <E extends Enum<E>> E getEnum(Class<E> type, String attribute, String... path)
    {
        final String value = getNodeString(attribute, path);
        try
        {
            return Enum.valueOf(type, value);
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(exception, ERROR_ENUM + value);
        }
    }

    @Override
    public <E extends Enum<E>> E getEnum(Class<E> type, E defaultValue, String attribute, String... path)
    {
        final String value = getNodeStringDefault(defaultValue.name(), attribute, path);
        try
        {
            return Enum.valueOf(type, value);
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(exception, ERROR_ENUM + value);
        }
    }

    @Override
    public <E extends Enum<E>> Optional<E> getEnumOptional(Class<E> type, String attribute, String... path)
    {
        if (hasAttribute(attribute, path))
        {
            return Optional.of(getEnum(type, attribute, path));
        }
        return Optional.empty();
    }

    @Override
    public <T> T getImplementation(Class<T> type, String... path)
    {
        return getImplementation(getClass().getClassLoader(), type, path);
    }

    @Override
    public <T> T getImplementation(ClassLoader loader, Class<T> type, String... path)
    {
        return getImplementation(loader, type, new Class<?>[0], Collections.emptyList(), path);
    }

    @Override
    public <T> T getImplementation(Class<T> type, Class<?> paramType, Object paramValue, String... path)
    {
        return getImplementation(type, new Class<?>[]
        {
            paramType
        }, Arrays.asList(paramValue), path);
    }

    @Override
    public <T> T getImplementation(Class<T> type, Class<?>[] paramsType, Collection<?> paramsValue, String... path)
    {
        return getImplementation(getClass().getClassLoader(), type, paramsType, paramsValue, path);
    }

    @Override
    public <T> T getImplementation(ClassLoader loader,
                                   Class<T> type,
                                   Class<?>[] paramsType,
                                   Collection<?> paramsValue,
                                   String... path)
    {
        final String className = getText(path).trim();
        return getImplementation(loader, type, paramsType, paramsValue, className);
    }

    /**
     * Get the class implementation from its name by using a custom constructor.
     * 
     * @param <T> The instance type.
     * @param loader The class loader to use.
     * @param type The class type.
     * @param paramsType The parameters type.
     * @param paramsValue The parameters value.
     * @param className The class name.
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    private static <T> T getImplementation(ClassLoader loader,
                                           Class<T> type,
                                           Class<?>[] paramsType,
                                           Collection<?> paramsValue,
                                           String className)
    {
        try
        {
            if (!CLASS_CACHE.containsKey(className))
            {
                final Class<?> clazz = loader.loadClass(className);
                CLASS_CACHE.put(className, clazz);
            }

            final Class<?> clazz = CLASS_CACHE.get(className);
            final Constructor<?> constructor = UtilReflection.getCompatibleConstructor(clazz, paramsType);
            UtilReflection.setAccessible(constructor, true);
            return type.cast(constructor.newInstance(paramsValue.toArray()));
        }
        catch (final InstantiationException | IllegalArgumentException | InvocationTargetException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS_INSTANCE + className);
        }
        catch (final NoSuchMethodException | IllegalAccessException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS_CONSTRUCTOR + className);
        }
        catch (final ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS_PRESENCE + className);
        }
    }

    @Override
    public boolean hasAttribute(String attribute, String... path)
    {
        XmlReader node = this;
        for (final String element : path)
        {
            if (!node.hasNode(element))
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

    @Override
    public boolean hasNode(String child, String... path)
    {
        final XmlReader node = getNodeDefault(path);
        if (node != null)
        {
            final NodeList list = node.root.getChildNodes();
            for (int i = 0; i < list.getLength(); i++)
            {
                final Node current = list.item(i);
                if (current.getNodeName().equals(child))
                {
                    return true;
                }
            }
        }
        return false;
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
    private String getText()
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

    @Override
    public XmlReader getChild(String name, String... path)
    {
        Check.notNull(name);

        final XmlReader xml = getNodeReader(path);

        final NodeList list = xml.root.getChildNodes();
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

    @Override
    public Optional<XmlReader> getChildOptional(String name, String... path)
    {
        final XmlReader xml = getNodeReader(path);

        final NodeList list = xml.root.getChildNodes();
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

    @Override
    public List<XmlReader> getChildren(String name, String... path)
    {
        Check.notNull(name);

        final List<XmlReader> nodes = new ArrayList<>(1);
        if (hasNode(name, path))
        {
            final XmlReader xml = getNodeReader(path);

            final NodeList list = xml.root.getChildNodes();
            for (int i = 0; i < list.getLength(); i++)
            {
                final Node node = list.item(i);
                if (name.equals(node.getNodeName()))
                {
                    nodes.add(new XmlReader(document, (Element) node));
                }
            }
        }
        return nodes;
    }

    /**
     * Get list of all children.
     * 
     * @return The children list.
     */
    public List<XmlReader> getChildren()
    {
        final List<XmlReader> nodes = new ArrayList<>(1);
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
    private XmlReader getNodeReader(String... path)
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
                throw new LionEngineException(exception, ERROR_NODE + Arrays.toString(path));
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
            if (!node.hasNode(element))
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
        final XmlReader node = getNodeReader(path);
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
            value = node.getString(attribute);
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
