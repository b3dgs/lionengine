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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;

/**
 * Represents the size data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class SizeConfig
{
    /** Size node name. */
    public static final String NODE_SIZE = Constant.XML_PREFIX + "size";
    /** Size width attribute. */
    public static final String ATT_WIDTH = "width";
    /** Size height attribute. */
    public static final String ATT_HEIGHT = "height";
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 30;

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
    public static SizeConfig imports(Xml root)
    {
        Check.notNull(root);

        final Xml node = root.getChild(NODE_SIZE);
        final int width = node.readInteger(ATT_WIDTH);
        final int height = node.readInteger(ATT_HEIGHT);

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
        super();

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

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + width;
        result = prime * result + height;
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final SizeConfig other = (SizeConfig) object;
        return other.width == width && other.height == height;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [width=")
                                            .append(width)
                                            .append(", height=")
                                            .append(height)
                                            .append("]")
                                            .toString();
    }
}
