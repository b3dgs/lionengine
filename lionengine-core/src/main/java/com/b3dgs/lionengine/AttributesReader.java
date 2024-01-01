/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * Describe an attributes reader.
 */
public interface AttributesReader
{
    /**
     * Get the node text value.
     * 
     * @param path The node path.
     * @return The node text value.
     * @throws LionEngineException If unable to read node.
     */
    String getText(String... path);

    /**
     * Get the node text value.
     * 
     * @param defaultValue The value used if node does not exist.
     * @param path The node path.
     * @return The node text value.
     */
    String getTextDefault(String defaultValue, String... path);

    /**
     * Read a boolean.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The boolean value.
     * @throws LionEngineException If error when reading.
     */
    boolean getBoolean(String attribute, String... path);

    /**
     * Read a boolean.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The boolean value.
     */
    boolean getBoolean(boolean defaultValue, String attribute, String... path);

    /**
     * Read a boolean.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The boolean value.
     * @throws LionEngineException If error when reading.
     */
    Optional<Boolean> getBooleanOptional(String attribute, String... path);

    /**
     * Read a byte.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The byte value.
     * @throws LionEngineException If error when reading.
     */
    byte getByte(String attribute, String... path);

    /**
     * Read a byte.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The byte value.
     */
    byte getByte(byte defaultValue, String attribute, String... path);

    /**
     * Read a char.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The char value.
     * @throws LionEngineException If error when reading.
     */
    char getChar(String attribute, String... path);

    /**
     * Read a char.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The char value.
     */
    char getChar(char defaultValue, String attribute, String... path);

    /**
     * Read a short.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The short value.
     * @throws LionEngineException If error when reading.
     */
    short getShort(String attribute, String... path);

    /**
     * Read a short.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The short value.
     * @throws LionEngineException If invalid argument.
     */
    short getShort(short defaultValue, String attribute, String... path);

    /**
     * Read an integer.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The integer value.
     * @throws LionEngineException If error when reading.
     */
    int getInteger(String attribute, String... path);

    /**
     * Read an integer.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The integer value.
     * @throws LionEngineException If invalid argument.
     */
    int getInteger(int defaultValue, String attribute, String... path);

    /**
     * Read an integer.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The integer value.
     * @throws LionEngineException If error when reading.
     */
    OptionalInt getIntegerOptional(String attribute, String... path);

    /**
     * Read a long.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The long value.
     * @throws LionEngineException If error when reading.
     */
    long getLong(String attribute, String... path);

    /**
     * Read a long.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The long value.
     * @throws LionEngineException If invalid argument.
     */
    long getLong(long defaultValue, String attribute, String... path);

    /**
     * Read a long.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The long value.
     * @throws LionEngineException If error when reading.
     */
    OptionalLong getLongOptional(String attribute, String... path);

    /**
     * Read a float.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The float value.
     * @throws LionEngineException If error when reading.
     */
    float getFloat(String attribute, String... path);

    /**
     * Read a float.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The float value.
     * @throws LionEngineException If invalid argument.
     */
    float getFloat(float defaultValue, String attribute, String... path);

    /**
     * Read a double.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The double value.
     * @throws LionEngineException If error when reading.
     */
    double getDouble(String attribute, String... path);

    /**
     * Read a double.
     * 
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The double value.
     * @throws LionEngineException If invalid argument.
     */
    double getDouble(double defaultValue, String attribute, String... path);

    /**
     * Read a double.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The double value.
     * @throws LionEngineException If error when reading.
     */
    OptionalDouble getDoubleOptional(String attribute, String... path);

    /**
     * Read a string. If the read string is equal to {@link Constant#NULL}, <code>null</code> will be returned instead.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The string value.
     * @throws LionEngineException If error when reading.
     */
    String getString(String attribute, String... path);

    /**
     * Read a string. If the read string is equal to {@link Constant#NULL}, <code>null</code> will be returned instead.
     * 
     * @param defaultValue The value returned if attribute not found (can be <code>null</code>).
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The string value.
     * @throws LionEngineException If invalid arguments.
     */
    String getStringDefault(String defaultValue, String attribute, String... path);

    /**
     * Read a string.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The string value.
     * @throws LionEngineException If error when reading.
     */
    Optional<String> getStringOptional(String attribute, String... path);

    /**
     * Read a media.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The media value.
     * @throws LionEngineException If unable to read node.
     */
    Media getMedia(String attribute, String... path);

    /**
     * Read a media.
     * 
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The media value.
     */
    Optional<Media> getMediaOptional(String attribute, String... path);

    /**
     * Read an enum.
     * 
     * @param <E> The enum type.
     * @param type The enum class.
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The enum instance.
     * @throws LionEngineException If unable to read node.
     */
    <E extends Enum<E>> E getEnum(Class<E> type, String attribute, String... path);

    /**
     * Read an enum.
     * 
     * @param <E> The enum type.
     * @param type The enum class.
     * @param defaultValue The value returned if attribute not found.
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The enum instance.
     * @throws LionEngineException If unable to read node.
     */
    <E extends Enum<E>> E getEnum(Class<E> type, E defaultValue, String attribute, String... path);

    /**
     * Read an enum.
     * 
     * @param <E> The enum type.
     * @param type The enum class.
     * @param attribute The attribute name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The enum instance.
     * @throws LionEngineException If unable to read node.
     */
    <E extends Enum<E>> Optional<E> getEnumOptional(Class<E> type, String attribute, String... path);

    /**
     * Get the class implementation from its name. Default constructor must be available.
     * 
     * @param <T> The instance type.
     * @param type The class type.
     * @param path The node path (child list).
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    <T> T getImplementation(Class<T> type, String... path);

    /**
     * Get the class implementation from its name. Default constructor must be available.
     * 
     * @param <T> The instance type.
     * @param loader The class loader to use.
     * @param type The class type.
     * @param path The node path (child list).
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    <T> T getImplementation(ClassLoader loader, Class<T> type, String... path);

    /**
     * Get the class implementation from its name by using a custom constructor.
     * 
     * @param <T> The instance type.
     * @param type The class type.
     * @param paramType The parameter type.
     * @param paramValue The parameter value.
     * @param path The node path (child list).
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    <T> T getImplementation(Class<T> type, Class<?> paramType, Object paramValue, String... path);

    /**
     * Get the class implementation from its name by using a custom constructor.
     * 
     * @param <T> The instance type.
     * @param type The class type.
     * @param paramsType The parameters type.
     * @param paramsValue The parameters value.
     * @param path The node path (child list).
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    <T> T getImplementation(Class<T> type, Class<?>[] paramsType, Collection<?> paramsValue, String... path);

    /**
     * Get the class implementation from its name by using a custom constructor.
     * 
     * @param <T> The instance type.
     * @param loader The class loader to use.
     * @param type The class type.
     * @param paramsType The parameters type.
     * @param paramsValue The parameters value.
     * @param path The node path (child list).
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    <T> T getImplementation(ClassLoader loader,
                            Class<T> type,
                            Class<?>[] paramsType,
                            Collection<?> paramsValue,
                            String... path);

    /**
     * Get a child node from its name.
     * 
     * @param name The child name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The child node reference.
     * @throws LionEngineException If no node is found at this child name.
     */
    XmlReader getChild(String name, String... path);

    /**
     * Get a child node from its name.
     * 
     * @param name The child name (can be <code>null</code>).
     * @param path The node path (child list).
     * @return The child node reference.
     */
    Optional<XmlReader> getChildOptional(String name, String... path);

    /**
     * Get the list of all children with this name.
     * 
     * @param name The children name (must not be <code>null</code>).
     * @param path The node path (child list).
     * @return The children list.
     * @throws LionEngineException If invalid argument.
     */
    List<XmlReader> getChildren(String name, String... path);

    /**
     * Check if node has the following attribute.
     * 
     * @param attribute The attribute name (can be <code>null</code>).
     * @param path The node path (child list).
     * @return <code>true</code> if attribute exists, <code>false</code> else.
     */
    boolean hasAttribute(String attribute, String... path);

    /**
     * Check if node has the following child.
     * 
     * @param child The child name (can be <code>null</code>).
     * @param path The node path (child list).
     * @return <code>true</code> if child exists, <code>false</code> else.
     */
    boolean hasNode(String child, String... path);
}
