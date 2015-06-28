/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.trait.launchable.Launchable;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the launchable data from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Launchable
 */
public final class ConfigLaunchable
{
    /** Launchable node name. */
    public static final String LAUNCHABLE = Configurer.PREFIX + "launchable";
    /** Media attribute name. */
    public static final String MEDIA = "media";
    /** Rate attribute name. */
    public static final String DELAY = "delay";

    /**
     * Create the launchable data from node.
     * 
     * @param node The node reference.
     * @return The launchable data.
     * @throws LionEngineException If unable to read node.
     */
    public static ConfigLaunchable create(XmlNode node) throws LionEngineException
    {
        final String media = node.readString(MEDIA);
        final int delay = node.readInteger(ConfigLaunchable.DELAY);

        return new ConfigLaunchable(media, delay, ConfigForce.create(node));
    }

    /** The media value. */
    private final String media;
    /** The delay value. */
    private final int delay;
    /** The launchable vector. */
    private final Force vector;

    /**
     * Disabled constructor.
     */
    private ConfigLaunchable()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }

    /**
     * Constructor.
     * 
     * @param media The media value.
     * @param delay The delay value.
     * @param vector The vector force.
     */
    private ConfigLaunchable(String media, int delay, Force vector)
    {
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
}
