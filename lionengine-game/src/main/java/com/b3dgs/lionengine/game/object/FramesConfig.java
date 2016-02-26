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
package com.b3dgs.lionengine.game.object;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the frames data from a configurer node.
 */
public final class FramesConfig
{
    /** Frames node name. */
    public static final String NODE_FRAMES = Configurer.PREFIX + "frames";
    /** Frames horizontal node name. */
    public static final String ATT_HORIZONTAL = "horizontal";
    /** Frames vertical node name. */
    public static final String ATT_VERTICAL = "vertical";

    /**
     * Imports the frames config from setup.
     * 
     * @param setup The setup reference.
     * @return The frames data.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static FramesConfig imports(Setup setup)
    {
        return imports(setup.getConfigurer().getRoot());
    }

    /**
     * Imports the frames config from configurer.
     * 
     * @param configurer The configurer reference.
     * @return The frames data.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static FramesConfig imports(Configurer configurer)
    {
        return imports(configurer.getRoot());
    }

    /**
     * Imports the frames config from node.
     * 
     * @param root The root reference.
     * @return The frames data.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static FramesConfig imports(XmlNode root)
    {
        final XmlNode node = root.getChild(NODE_FRAMES);
        final int horizontals = node.readInteger(FramesConfig.ATT_HORIZONTAL);
        final int verticals = node.readInteger(FramesConfig.ATT_VERTICAL);

        return new FramesConfig(horizontals, verticals);
    }

    /**
     * Exports the frames node from config.
     * 
     * @param config The config reference.
     * @return The frames data.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static XmlNode exports(FramesConfig config)
    {
        final XmlNode node = Xml.create(NODE_FRAMES);
        node.writeInteger(ATT_HORIZONTAL, config.getHorizontal());
        node.writeInteger(ATT_VERTICAL, config.getVertical());

        return node;
    }

    /** The number of horizontal frames. */
    private final int horizontalFrames;
    /** The number of vertical frames. */
    private final int verticalFrames;

    /**
     * Create the frames configuration.
     * 
     * @param horizontalFrames The horizontal frames value.
     * @param verticalFrames The vertical frames value.
     */
    public FramesConfig(int horizontalFrames, int verticalFrames)
    {
        this.horizontalFrames = horizontalFrames;
        this.verticalFrames = verticalFrames;
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
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || !(obj instanceof FramesConfig))
        {
            return false;
        }
        final FramesConfig other = (FramesConfig) obj;
        return other.getHorizontal() == getHorizontal() && other.getVertical() == getVertical();
    }
}
