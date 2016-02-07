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
package com.b3dgs.lionengine.game.object.trait.launchable;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the launcher data from a configurer node.
 * 
 * @see com.b3dgs.lionengine.game.object.trait.launchable.Launcher
 */
public final class LauncherConfig
{
    /** Launcher node name. */
    public static final String LAUNCHER = Configurer.PREFIX + "launcher";
    /** Rate attribute name. */
    public static final String RATE = "rate";

    /**
     * Create the launcher data from node.
     * 
     * @param configurer The configurer reference.
     * @return The launcher data.
     * @throws LionEngineException If unable to read node.
     */
    public static LauncherConfig create(Configurer configurer)
    {
        final Collection<LaunchableConfig> launchables = new ArrayList<LaunchableConfig>();
        final XmlNode launcher = configurer.getRoot().getChild(LAUNCHER);
        for (final XmlNode launchable : launcher.getChildren(LaunchableConfig.LAUNCHABLE))
        {
            launchables.add(LaunchableConfig.create(launchable));
        }
        final int rate = configurer.getInteger(LauncherConfig.RATE, LauncherConfig.LAUNCHER);

        return new LauncherConfig(rate, launchables);
    }

    /** The rate value. */
    private final int rate;
    /** The launchable configurations. */
    private final Collection<LaunchableConfig> launchables;

    /**
     * Disabled constructor.
     */
    private LauncherConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }

    /**
     * Create a launcher configuration.
     * 
     * @param rate The rate value.
     * @param launchables The launchables reference.
     */
    private LauncherConfig(int rate, Collection<LaunchableConfig> launchables)
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
}
