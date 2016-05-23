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
package com.b3dgs.lionengine.game.object.feature.launchable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.object.Configurer;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the launcher data from a configurer node.
 * 
 * @see com.b3dgs.lionengine.game.object.feature.launchable.Launcher
 */
public final class LauncherConfig
{
    /** Launcher node name. */
    public static final String NODE_LAUNCHER = Constant.XML_PREFIX + "launcher";
    /** Rate attribute name. */
    public static final String ATT_RATE = "rate";

    /**
     * Import the launcher data from configurer.
     * 
     * @param configurer The configurer reference.
     * @return The launcher data.
     * @throws LionEngineException If unable to read node.
     */
    public static LauncherConfig imports(Configurer configurer)
    {
        return imports(configurer.getRoot().getChild(NODE_LAUNCHER));
    }

    /**
     * Import the launcher data from node.
     * 
     * @param node The node reference.
     * @return The launcher data.
     * @throws LionEngineException If unable to read node.
     */
    public static LauncherConfig imports(XmlNode node)
    {
        final Collection<LaunchableConfig> launchables = new ArrayList<LaunchableConfig>();
        for (final XmlNode launchable : node.getChildren(LaunchableConfig.NODE_LAUNCHABLE))
        {
            launchables.add(LaunchableConfig.imports(launchable));
        }
        final int rate = node.readInteger(ATT_RATE);

        return new LauncherConfig(rate, launchables);
    }

    /**
     * Export the launcher node from config.
     * 
     * @param config The config reference.
     * @return The launcher data.
     * @throws LionEngineException If unable to read node.
     */
    public static XmlNode exports(LauncherConfig config)
    {
        final XmlNode node = Xml.create(NODE_LAUNCHER);
        node.writeInteger(ATT_RATE, config.getRate());

        for (final LaunchableConfig launchable : config.getLaunchables())
        {
            node.add(LaunchableConfig.exports(launchable));
        }

        return node;
    }

    /** The rate value. */
    private final int rate;
    /** The launchable configurations. */
    private final Collection<LaunchableConfig> launchables;

    /**
     * Create a launcher configuration.
     * 
     * @param rate The rate value.
     * @param launchables The launchables reference.
     */
    public LauncherConfig(int rate, Collection<LaunchableConfig> launchables)
    {
        this.rate = rate;
        this.launchables = launchables;
    }

    /**
     * Get the launch rate value.
     * 
     * @return The launch rate value.
     */
    public int getRate()
    {
        return rate;
    }

    /**
     * Get the launchables configuration.
     * 
     * @return The launchables configuration.
     */
    public Iterable<LaunchableConfig> getLaunchables()
    {
        return launchables;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + launchables.hashCode();
        result = prime * result + rate;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof LauncherConfig))
        {
            return false;
        }
        final LauncherConfig other = (LauncherConfig) obj;
        return other.getRate() == getRate() && Arrays.equals(other.launchables.toArray(), launchables.toArray());
    }
}
