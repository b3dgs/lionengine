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
package com.b3dgs.lionengine.game;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import com.b3dgs.lionengine.AttributesReader;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;

/**
 * Allows to retrieve information from an external XML configuration file.
 */
public class Configurer implements AttributesReader
{
    /** Media reference. */
    private final Media media;
    /** Root path. */
    private final String path;
    /** Root node. */
    private final Xml root;

    protected static final String ERROR_SURFACE_FILE = "No surface file found !";
    /** No icon file. */
    protected static final String ERROR_ICON_FILE = "No icon file found !";
    /** No surface. */
    protected static final String ERROR_SURFACE = "No surface found !";
    /** No icon. */
    protected static final String ERROR_ICON = "No icon found !";
    /** Class error. */
    protected static final String ERROR_CLASS = "Class not found for: ";

    /**
     * Load data from configuration media.
     * 
     * @param media The xml media.
     * @throws LionEngineException If error when opening the media.
     */
    public Configurer(Media media)
    {
        super();

        Check.notNull(media);

        this.media = media;
        path = media.getFile().getParent();
        root = new Xml(media);
    }

    /**
     * Save the configurer.
     * 
     * @throws LionEngineException If error on saving.
     */
    public final void save()
    {
        root.save(media);
    }

    /**
     * Get the data root container for raw access.
     * 
     * @return The root node.
     */
    public final Xml getRoot()
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

    @Override
    public final String getText(String... path)
    {
        return root.getText(path);
    }

    @Override
    public final String getTextDefault(String defaultValue, String... path)
    {
        return root.getTextDefault(defaultValue, path);
    }

    @Override
    public final boolean getBoolean(String attribute, String... path)
    {
        return root.getBoolean(attribute, path);
    }

    @Override
    public final boolean getBoolean(boolean defaultValue, String attribute, String... path)
    {
        return root.getBoolean(defaultValue, attribute, path);
    }

    @Override
    public final Optional<Boolean> getBooleanOptional(String attribute, String... path)
    {
        return root.getBooleanOptional(attribute, path);
    }

    @Override
    public final byte getByte(String attribute, String... path)
    {
        return root.getByte(attribute, path);
    }

    @Override
    public final byte getByte(byte defaultValue, String attribute, String... path)
    {
        return root.getByte(defaultValue, attribute, path);
    }

    @Override
    public final char getChar(String attribute, String... path)
    {
        return root.getChar(attribute, path);
    }

    @Override
    public final char getChar(byte defaultValue, String attribute, String... path)
    {
        return root.getChar(defaultValue, attribute, path);
    }

    @Override
    public final short getShort(String attribute, String... path)
    {
        return root.getShort(attribute, path);
    }

    @Override
    public final short getShort(short defaultValue, String attribute, String... path)
    {
        return root.getShort(defaultValue, attribute, path);
    }

    @Override
    public final int getInteger(String attribute, String... path)
    {
        return root.getInteger(attribute, path);
    }

    @Override
    public final int getInteger(int defaultValue, String attribute, String... path)
    {
        return root.getInteger(defaultValue, attribute, path);
    }

    @Override
    public final OptionalInt getIntegerOptional(String attribute, String... path)
    {
        return root.getIntegerOptional(attribute, path);
    }

    @Override
    public final long getLong(String attribute, String... path)
    {
        return root.getLong(attribute, path);
    }

    @Override
    public final long getLong(long defaultValue, String attribute, String... path)
    {
        return root.getLong(defaultValue, attribute, path);
    }

    @Override
    public final OptionalLong getLongOptional(String attribute, String... path)
    {
        return root.getLongOptional(attribute, path);
    }

    @Override
    public final float getFloat(String attribute, String... path)
    {
        return root.getFloat(attribute, path);
    }

    @Override
    public final float getFloat(float defaultValue, String attribute, String... path)
    {
        return root.getFloat(defaultValue, attribute, path);
    }

    @Override
    public final double getDouble(String attribute, String... path)
    {
        return root.getDouble(attribute, path);
    }

    @Override
    public final double getDouble(double defaultValue, String attribute, String... path)
    {
        return root.getDouble(defaultValue, attribute, path);
    }

    @Override
    public final OptionalDouble getDoubleOptional(String attribute, String... path)
    {
        return root.getDoubleOptional(attribute, path);
    }

    @Override
    public final String getString(String attribute, String... path)
    {
        return root.getString(attribute, path);
    }

    @Override
    public final String getStringDefault(String defaultValue, String attribute, String... path)
    {
        return root.getStringDefault(defaultValue, attribute, path);
    }

    @Override
    public final Optional<String> getStringOptional(String attribute, String... path)
    {
        return root.getStringOptional(attribute, path);
    }

    @Override
    public final Media getMedia(String attribute, String... path)
    {
        return root.getMedia(attribute, path);
    }

    @Override
    public final Optional<Media> getMediaOptional(String attribute, String... path)
    {
        return root.getMediaOptional(attribute, path);
    }

    @Override
    public final <E extends Enum<E>> E getEnum(Class<E> type, String attribute, String... path)
    {
        return root.getEnum(type, attribute, path);
    }

    @Override
    public final <E extends Enum<E>> E getEnum(Class<E> type, E defaultValue, String attribute, String... path)
    {
        return root.getEnum(type, defaultValue, attribute, path);
    }

    @Override
    public final <E extends Enum<E>> Optional<E> getEnumOptional(Class<E> type, String attribute, String... path)
    {
        return root.getEnumOptional(type, attribute, path);
    }

    @Override
    public final <T> T getImplementation(Class<T> type, String... path)
    {
        return root.getImplementation(type, path);
    }

    @Override
    public final <T> T getImplementation(ClassLoader loader, Class<T> type, String... path)
    {
        return root.getImplementation(loader, type, path);
    }

    @Override
    public final <T> T getImplementation(Class<T> type, Class<?> paramType, Object paramValue, String... path)
    {
        return root.getImplementation(type, paramType, paramValue, path);
    }

    @Override
    public final <T> T getImplementation(Class<T> type,
                                         Class<?>[] paramsType,
                                         Collection<?> paramsValue,
                                         String... path)
    {
        return root.getImplementation(type, paramsType, paramsValue, path);
    }

    @Override
    public final <T> T getImplementation(ClassLoader loader,
                                         Class<T> type,
                                         Class<?>[] paramsType,
                                         Collection<?> paramsValue,
                                         String... path)
    {
        return root.getImplementation(loader, type, paramsType, paramsValue, path);
    }

    @Override
    public final XmlReader getChild(String name, String... path)
    {
        return root.getChild(name, path);
    }

    @Override
    public final Optional<XmlReader> getChildOptional(String name, String... path)
    {
        return root.getChildOptional(name, path);
    }

    @Override
    public final List<XmlReader> getChildren(String name, String... path)
    {
        return root.getChildren(name, path);
    }

    @Override
    public final boolean hasAttribute(String attribute, String... path)
    {
        return root.hasAttribute(attribute, path);
    }

    @Override
    public final boolean hasNode(String child, String... path)
    {
        return root.hasNode(child, path);
    }
}
