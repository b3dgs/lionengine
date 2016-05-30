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
package com.b3dgs.lionengine.game.feature.launchable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.ForceConfig;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the launchable data from a configurer.
 */
public final class LaunchableConfig
{
    /** Launchable node name. */
    public static final String NODE_LAUNCHABLE = Constant.XML_PREFIX + "launchable";
    /** Media attribute name. */
    public static final String ATT_MEDIA = "media";
    /** Rate attribute name. */
    public static final String ATT_DELAY = "delay";

    /**
     * Import the launchable data from node.
     * 
     * @param node The node reference.
     * @return The launchable data.
     * @throws LionEngineException If unable to read node.
     */
    public static LaunchableConfig imports(XmlNode node)
    {
        final String media = node.readString(ATT_MEDIA);
        final int delay = node.readInteger(ATT_DELAY);

        return new LaunchableConfig(media, delay, ForceConfig.imports(node));
    }

    /**
     * Export the launchable node from data.
     * 
     * @param config The config reference.
     * @return The node data.
     * @throws LionEngineException If unable to write node.
     */
    public static XmlNode exports(LaunchableConfig config)
    {
        final XmlNode node = Xml.create(NODE_LAUNCHABLE);
        node.writeString(ATT_MEDIA, config.getMedia());
        node.writeInteger(ATT_DELAY, config.getDelay());
        node.add(ForceConfig.exports(config.getVector()));

        return node;
    }

    /** The media value. */
    private final String media;
    /** The delay value. */
    private final int delay;
    /** The launchable vector. */
    private final Force vector;

    /**
     * Constructor.
     * 
     * @param media The media value.
     * @param delay The delay value.
     * @param vector The vector force.
     * @throws LionEngineException If <code>null</code> arguments.
     */
    public LaunchableConfig(String media, int delay, Force vector)
    {
        Check.notNull(media);
        Check.notNull(vector);

        this.media = media;
        this.delay = delay;
        this.vector = vector;
    }

    /**
     * Get the media.
     * 
     * @return The launchable media.
     */
    public String getMedia()
    {
        return media;
    }

    /**
     * Get the launch delay value.
     * 
     * @return The launch delay value.
     */
    public int getDelay()
    {
        return delay;
    }

    /**
     * Get the vector value.
     * 
     * @return The vector value.
     */
    public Force getVector()
    {
        return vector;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + delay;
        result = prime * result + media.hashCode();
        result = prime * result + vector.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof LaunchableConfig))
        {
            return false;
        }
        final LaunchableConfig other = (LaunchableConfig) obj;
        return other.getMedia().equals(getMedia())
               && other.getVector().equals(getVector())
               && other.getDelay() == getDelay();
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(getClass().getSimpleName())
                                  .append(" [media=")
                                  .append(media)
                                  .append(", delay=")
                                  .append(delay)
                                  .append(", vector=")
                                  .append(vector)
                                  .append("]")
                                  .toString();
    }
}
