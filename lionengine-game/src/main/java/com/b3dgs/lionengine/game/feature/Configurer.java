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
package com.b3dgs.lionengine.game.feature;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Allows to retrieve informations from an external XML configuration file.
 */
public class Configurer
{
    /** Class instance error. */
    private static final String ERROR_CLASS_INSTANCE = "Class instantiation error: ";
    /** Class constructor error. */
    private static final String ERROR_CLASS_CONSTRUCTOR = "Class constructor error: ";
    /** Class accessibility error. */
    private static final String ERROR_CLASS_ACCESSIBILITY = "Class not accessible: ";
    /** Class not found error. */
    private static final String ERROR_CLASS_PRESENCE = "Class not found: ";
    /** Class cache. */
    private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<String, Class<?>>();

    /** Media reference. */
    private final Media media;
    /** Root path. */
    private final String path;
    /** Root node. */
    private final XmlNode root;

    /**
     * Load data from configuration media.
     * 
     * @param media The xml media.
     * @throws LionEngineException If error when opening the media.
     */
    public Configurer(Media media)
    {
        Check.notNull(media);

        this.media = media;
        path = media.getFile().getParent();
        root = Xml.load(media);
    }

    /**
     * Save the configurer.
     * 
     * @throws LionEngineException If error on saving.
     */
    public final void save()
    {
        Xml.save(root, media);
    }

    /**
     * Get the data root container for raw access.
     * 
     * @return The root node.
     */
    public final XmlNode getRoot()
    {
        return root;
    }

    /**
     * Get the configuration directory path.
     * 
     * @return The configuration directory path.
     */
    public final String getPath()
    {
        return path;
    }

    /**
     * Return the associated media.
     * 
     * @return The associated media.
     */
    public final Media getMedia()
    {
        return media;
    }

    /**
     * Get the node text value.
     * 
     * @param path The node path.
     * @return The node text value.
     * @throws LionEngineException If unable to read node.
     */
    public final String getText(String... path)
    {
        final XmlNode node = getNode(path);
        return node.getText();
    }

    /**
     * Get the node text value.
     * 
     * @param defaultValue Value used if node does not exist.
     * @param path The node path.
     * @return The node text value.
     */
    public final String getTextDefault(String defaultValue, String... path)
    {
        final XmlNode node = getNodeDefault(path);
        if (node != null)
        {
            return node.getText();
        }
        return defaultValue;
    }

    /**
     * Get a string in the xml tree.
     * 
     * @param attribute The attribute to get as string.
     * @param path The node path (child list)
     * @return The string value.
     * @throws LionEngineException If unable to read node.
     */
    public final String getString(String attribute, String... path)
    {
        return getNodeString(attribute, path);
    }

    /**
     * Get a string in the xml tree.
     * 
     * @param defaultValue Value used if node does not exist.
     * @param attribute The attribute to get as string.
     * @param path The node path (child list)
     * @return The string value.
     * @throws LionEngineException If unable to read node.
     */
    public final String getStringDefault(String defaultValue, String attribute, String... path)
    {
        return getNodeStringDefault(defaultValue, attribute, path);
    }

    /**
     * Get a boolean in the xml tree.
     * 
     * @param attribute The attribute to get as boolean.
     * @param path The node path (child list)
     * @return The boolean value.
     * @throws LionEngineException If unable to read node.
     */
    public final boolean getBoolean(String attribute, String... path)
    {
        return Boolean.parseBoolean(getNodeString(attribute, path));
    }

    /**
     * Get a boolean in the xml tree.
     * 
     * @param defaultValue Value used if node does not exist.
     * @param attribute The attribute to get as boolean.
     * @param path The node path (child list)
     * @return The boolean value.
     * @throws LionEngineException If unable to read node.
     */
    public final boolean getBooleanDefault(boolean defaultValue, String attribute, String... path)
    {
        return Boolean.parseBoolean(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
    }

    /**
     * Get an integer in the xml tree.
     * 
     * @param attribute The attribute to get as integer.
     * @param path The node path (child list)
     * @return The integer value.
     * @throws LionEngineException If unable to read node or not a valid integer read.
     */
    public final int getInteger(String attribute, String... path)
    {
        try
        {
            return Integer.parseInt(getNodeString(attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception, media);
        }
    }

    /**
     * Get an integer in the xml tree.
     * 
     * @param defaultValue Value used if node does not exist.
     * @param attribute The attribute to get as integer.
     * @param path The node path (child list)
     * @return The integer value.
     * @throws LionEngineException If not a valid integer read.
     */
    public final int getIntegerDefault(int defaultValue, String attribute, String... path)
    {
        try
        {
            return Integer.parseInt(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception, media);
        }
    }

    /**
     * Get a double in the xml tree.
     * 
     * @param attribute The attribute to get as double.
     * @param path The node path (child list)
     * @return The double value.
     * @throws LionEngineException If unable to read node.
     */
    public final double getDouble(String attribute, String... path)
    {
        try
        {
            return Double.parseDouble(getNodeString(attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception, media);
        }
    }

    /**
     * Get a double in the xml tree.
     * 
     * @param defaultValue Value used if node does not exist.
     * @param attribute The attribute to get as double.
     * @param path The node path (child list)
     * @return The double value.
     * @throws LionEngineException If unable to read node.
     */
    public final double getDoubleDefault(double defaultValue, String attribute, String... path)
    {
        try
        {
            return Double.parseDouble(getNodeStringDefault(String.valueOf(defaultValue), attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception, media);
        }
    }

    /**
     * Get the class implementation from its name. Default constructor must be available.
     * 
     * @param <T> The instance type.
     * @param type The class type.
     * @param path The node path.
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    public final <T> T getImplementation(Class<T> type, String... path)
    {
        return getImplementation(getClass().getClassLoader(), type, path);
    }

    /**
     * Get the class implementation from its name. Default constructor must be available.
     * 
     * @param <T> The instance type.
     * @param loader The class loader to use.
     * @param type The class type.
     * @param path The node path.
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    public final <T> T getImplementation(ClassLoader loader, Class<T> type, String... path)
    {
        return getImplementation(loader, type, new Class<?>[0], Collections.emptyList(), path);
    }

    /**
     * Get the class implementation from its name by using a custom constructor.
     * 
     * @param <T> The instance type.
     * @param type The class type.
     * @param paramType The parameter type.
     * @param paramValue The parameter value.
     * @param path The node path.
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    public final <T> T getImplementation(Class<T> type, Class<?> paramType, Object paramValue, String... path)
    {
        return getImplementation(type, new Class<?>[]
        {
            paramType
        }, Arrays.asList(paramValue), path);
    }

    /**
     * Get the class implementation from its name by using a custom constructor.
     * 
     * @param <T> The instance type.
     * @param type The class type.
     * @param paramsType The parameters type.
     * @param paramsValue The parameters value.
     * @param path The node path.
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    public final <T> T getImplementation(Class<T> type,
                                         Class<?>[] paramsType,
                                         Collection<?> paramsValue,
                                         String... path)
    {
        return getImplementation(getClass().getClassLoader(), type, paramsType, paramsValue, path);
    }

    /**
     * Get the class implementation from its name by using a custom constructor.
     * 
     * @param <T> The instance type.
     * @param loader The class loader to use.
     * @param type The class type.
     * @param paramsType The parameters type.
     * @param paramsValue The parameters value.
     * @param path The node path.
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    public final <T> T getImplementation(ClassLoader loader,
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
    public static final <T> T getImplementation(ClassLoader loader,
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
            if (!constructor.isAccessible())
            {
                UtilReflection.setAccessible(constructor, true);
            }
            return type.cast(constructor.newInstance(paramsValue.toArray()));
        }
        catch (final InstantiationException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS_INSTANCE, className);
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS_INSTANCE, className);
        }
        catch (final InvocationTargetException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS_INSTANCE, className);
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS_CONSTRUCTOR, className);
        }
        catch (final IllegalAccessException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS_ACCESSIBILITY, className);
        }
        catch (final ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS_PRESENCE, className);
        }
    }

    /**
     * Check if node exists.
     * 
     * @param path The node path.
     * @return <code>true</code> if node exists, <code>false</code> else.
     */
    public boolean hasNode(String... path)
    {
        XmlNode node = root;
        for (final String element : path)
        {
            if (!node.hasChild(element))
            {
                return false;
            }
            node = node.getChild(element);
        }
        return true;
    }

    /**
     * Get the node at the following path.
     * 
     * @param path The node path.
     * @return The node found.
     * @throws LionEngineException If node not found.
     */
    private XmlNode getNode(String... path)
    {
        XmlNode node = root;
        for (final String element : path)
        {
            try
            {
                node = node.getChild(element);
            }
            catch (final LionEngineException exception)
            {
                throw new LionEngineException(exception, media);
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
    private XmlNode getNodeDefault(String... path)
    {
        XmlNode node = root;
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
        final XmlNode node = getNode(path);
        return node.readString(attribute);
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
        final XmlNode node = getNodeDefault(path);
        if (node != null && node.hasAttribute(attribute))
        {
            return node.readString(attribute);
        }
        return defaultValue;
    }
}
