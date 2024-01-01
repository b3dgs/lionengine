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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;

/**
 * Represents the size data.
 * 
 * @param width The width value.
 * @param height The height value.
 */
public record SizeConfig(int width, int height)
{
    /** Size node name. */
    public static final String NODE_SIZE = Constant.XML_PREFIX + "size";
    /** Size width attribute. */
    public static final String ATT_WIDTH = "width";
    /** Size height attribute. */
    public static final String ATT_HEIGHT = "height";

    /**
     * Import the size data from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The size data.
     * @throws LionEngineException If unable to read node.
     */
    public static SizeConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Import the size data from configurer.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The size data.
     * @throws LionEngineException If unable to read node.
     */
    public static SizeConfig imports(XmlReader root)
    {
        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_SIZE);
        final int width = node.getInteger(ATT_WIDTH);
        final int height = node.getInteger(ATT_HEIGHT);

        return new SizeConfig(width, height);
    }

    /**
     * Export the size node from data.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @return The size node.
     * @throws LionEngineException If unable to read node.
     */
    public static Xml exports(SizeConfig config)
    {
        Check.notNull(config);

        final Xml node = new Xml(NODE_SIZE);
        node.writeInteger(ATT_WIDTH, config.getWidth());
        node.writeInteger(ATT_HEIGHT, config.getHeight());

        return node;
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
