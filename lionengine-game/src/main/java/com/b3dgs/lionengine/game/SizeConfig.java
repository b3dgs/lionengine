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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the size data from a configurer.
 * 
 * @see Configurer
 */
public final class SizeConfig
{
    /** Size node name. */
    public static final String NODE_SIZE = Configurer.PREFIX + "size";
    /** Size width node. */
    public static final String ATT_WIDTH = "width";
    /** Size height node. */
    public static final String ATT_HEIGHT = "height";

    /**
     * Import the size data from setup.
     * 
     * @param setup The setup reference.
     * @return The size data.
     * @throws LionEngineException If unable to read node.
     */
    public static SizeConfig imports(Setup setup)
    {
        return imports(setup.getConfigurer().getRoot());
    }

    /**
     * Import the size data from configurer.
     * 
     * @param configurer The configurer reference.
     * @return The size data.
     * @throws LionEngineException If unable to read node.
     */
    public static SizeConfig imports(Configurer configurer)
    {
        return imports(configurer.getRoot());
    }

    /**
     * Import the size data from configurer.
     * 
     * @param root The root reference.
     * @return The size data.
     * @throws LionEngineException If unable to read node.
     */
    public static SizeConfig imports(XmlNode root)
    {
        final XmlNode node = root.getChild(NODE_SIZE);
        final int width = node.readInteger(SizeConfig.ATT_WIDTH);
        final int height = node.readInteger(SizeConfig.ATT_HEIGHT);

        return new SizeConfig(width, height);
    }

    /**
     * Export the size node from data.
     * 
     * @param config The config reference.
     * @return The size node.
     * @throws LionEngineException If unable to read node.
     */
    public static XmlNode exports(SizeConfig config)
    {
        final XmlNode node = Xml.create(NODE_SIZE);
        node.writeInteger(ATT_WIDTH, config.getWidth());
        node.writeInteger(ATT_HEIGHT, config.getHeight());

        return node;
    }

    /** The width value. */
    private final int width;
    /** The height value. */
    private final int height;

    /**
     * Create a size configuration.
     * 
     * @param width The width value.
     * @param height The height value.
     */
    public SizeConfig(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    /**
     * Get the width value.
     * 
     * @return The width value.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get the height value.
     * 
     * @return The height value.
     */
    public int getHeight()
    {
        return height;
    }
}
