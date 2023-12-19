/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * Represents the frames data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @param horizontalFrames The horizontal frames value.
 * @param verticalFrames The vertical frames value.
 * @param offsetX The horizontal offset.
 * @param offsetY The vertical offset.
 */
public record FramesConfig(int horizontalFrames, int verticalFrames, int offsetX, int offsetY)
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
    public static FramesConfig imports(XmlReader root)
    {
        Check.notNull(root);

        if (root.hasNode(NODE_FRAMES))
        {
            final XmlReader node = root.getChild(NODE_FRAMES);
            final int horizontals = node.getInteger(ATT_HORIZONTAL);
            final int verticals = node.getInteger(ATT_VERTICAL);
            final int offsetX = node.getInteger(0, ATT_OFFSET_X);
            final int offsetY = node.getInteger(0, ATT_OFFSET_Y);

            return new FramesConfig(horizontals, verticals, offsetX, offsetY);
        }
        return new FramesConfig(1, 1, 0, 0);
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
}
