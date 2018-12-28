/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;

/**
 * Represents the frames data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class FramesConfig
{
    /** Frames node name. */
    public static final String NODE_FRAMES = Constant.XML_PREFIX + "frames";
    /** Frames horizontal node name. */
    public static final String ATT_HORIZONTAL = "horizontal";
    /** Frames vertical attribute name. */
    public static final String ATT_VERTICAL = "vertical";
    /** Frames offset horizontal attribute name. */
    public static final String ATT_OFFSET_X = "offsetX";
    /** Frames offset vertical attribute name. */
    public static final String ATT_OFFSET_Y = "offsetY";
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 73;

    /**
     * Imports the frames config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The frames data.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static FramesConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Imports the frames config from node.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The frames data.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static FramesConfig imports(Xml root)
    {
        Check.notNull(root);

        final Xml node = root.getChild(NODE_FRAMES);
        final int horizontals = node.readInteger(ATT_HORIZONTAL);
        final int verticals = node.readInteger(ATT_VERTICAL);
        final int offsetX = node.readInteger(0, ATT_OFFSET_X);
        final int offsetY = node.readInteger(0, ATT_OFFSET_Y);

        return new FramesConfig(horizontals, verticals, offsetX, offsetY);
    }

    /**
     * Exports the frames node from config.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @return The frames node.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static Xml exports(FramesConfig config)
    {
        Check.notNull(config);

        final Xml node = new Xml(NODE_FRAMES);
        node.writeInteger(ATT_HORIZONTAL, config.getHorizontal());
        node.writeInteger(ATT_VERTICAL, config.getVertical());
        node.writeInteger(ATT_OFFSET_X, config.getOffsetX());
        node.writeInteger(ATT_OFFSET_Y, config.getOffsetY());

        return node;
    }

    /** The number of horizontal frames. */
    private final int horizontalFrames;
    /** The number of vertical frames. */
    private final int verticalFrames;
    /** The horizontal offset. */
    private final int offsetX;
    /** The vertical offset. */
    private final int offsetY;

    /**
     * Create the frames configuration.
     * 
     * @param horizontalFrames The horizontal frames value.
     * @param verticalFrames The vertical frames value.
     * @param offsetX The horizontal offset.
     * @param offsetY The vertical offset.
     */
    public FramesConfig(int horizontalFrames, int verticalFrames, int offsetX, int offsetY)
    {
        super();

        this.horizontalFrames = horizontalFrames;
        this.verticalFrames = verticalFrames;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * Get the number of horizontal frames.
     * 
     * @return The number of horizontal frames.
     */
    public int getHorizontal()
    {
        return horizontalFrames;
    }

    /**
     * Get the number of vertical frames.
     * 
     * @return The number of vertical frames.
     */
    public int getVertical()
    {
        return verticalFrames;
    }

    /**
     * Get the horizontal offset.
     * 
     * @return The horizontal offset.
     */
    public int getOffsetX()
    {
        return offsetX;
    }

    /**
     * Get the vertical offset.
     * 
     * @return The vertical offset.
     */
    public int getOffsetY()
    {
        return offsetY;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + horizontalFrames;
        result = prime * result + verticalFrames;
        result = prime * result + offsetX;
        result = prime * result + offsetY;
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
        final FramesConfig other = (FramesConfig) object;
        return horizontalFrames == other.horizontalFrames
               && verticalFrames == other.verticalFrames
               && offsetX == other.offsetX
               && offsetY == other.offsetY;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [horizontalFrames=")
                                            .append(horizontalFrames)
                                            .append(", verticalFrames=")
                                            .append(verticalFrames)
                                            .append(", offsetX=")
                                            .append(offsetX)
                                            .append(", offsetY=")
                                            .append(offsetY)
                                            .append("]")
                                            .toString();
    }
}
