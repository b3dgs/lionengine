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
package com.b3dgs.lionengine.game.configurer;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Allows to retrieve informations from an external XML configuration file.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Configurer
{
    /** Prefix XML node. */
    public static final String PREFIX = "lionengine:";
    /** Class node name. */
    public static final String CLASS = Configurer.PREFIX + "class";

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
    public Configurer(Media media) throws LionEngineException
    {
        Check.notNull(media);

        this.media = media;
        path = media.getFile().getParent();
        root = Stream.loadXml(media);
    }

    /**
     * Save the configurer.
     */
    public void save()
    {
        Stream.saveXml(root, media);
    }

    /**
     * Get the data root container for raw access.
     * 
     * @return The root node.
     */
    public XmlNode getRoot()
    {
        return root;
    }

    /**
     * Get the configuration directory path.
     * 
     * @return The configuration directory path.
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Get the node text value.
     * 
     * @param path The node path.
     * @return The node text value.
     * @throws LionEngineException If unable to read node.
     */
    public String getText(String... path) throws LionEngineException
    {
        final XmlNode node = getNode(path);
        return node.getText();
    }

    /**
     * Get a string in the xml tree.
     * 
     * @param attribute The attribute to get as string.
     * @param path The node path (child list)
     * @return The string value.
     * @throws LionEngineException If unable to read node.
     */
    public String getString(String attribute, String... path) throws LionEngineException
    {
        return getNodeString(attribute, path);
    }

    /**
     * Get a boolean in the xml tree.
     * 
     * @param attribute The attribute to get as boolean.
     * @param path The node path (child list)
     * @return The boolean value.
     * @throws LionEngineException If unable to read node.
     */
    public boolean getBoolean(String attribute, String... path) throws LionEngineException
    {
        return Boolean.parseBoolean(getNodeString(attribute, path));
    }

    /**
     * Get an integer in the xml tree.
     * 
     * @param attribute The attribute to get as integer.
     * @param path The node path (child list)
     * @return The integer value.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public int getInteger(String attribute, String... path) throws LionEngineException
    {
        try
        {
            return Integer.parseInt(getNodeString(attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception);
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
    public double getDouble(String attribute, String... path) throws LionEngineException
    {
        try
        {
            return Double.parseDouble(getNodeString(attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get the class name node value.
     * 
     * @return The class name node value.
     * @throws LionEngineException If unable to read node.
     */
    public String getClassName() throws LionEngineException
    {
        return getText(Configurer.CLASS);
    }

    /**
     * Get the node at the following path.
     * 
     * @param path The node path.
     * @return The node found.
     * @throws LionEngineException If node not found.
     */
    private XmlNode getNode(String... path) throws LionEngineException
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
     * Get the string from a node.
     * 
     * @param attribute The attribute to get.
     * @param path The attribute node path.
     * @return The string found.
     * @throws LionEngineException If nod not found.
     */
    private String getNodeString(String attribute, String... path) throws LionEngineException
    {
        final XmlNode node = getNode(path);
        return node.readString(attribute);
    }
}
