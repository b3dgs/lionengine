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

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the launcher data from a configurer node.
 * 
 * @see com.b3dgs.lionengine.game.object.trait.launchable.Launcher
 */
public final class ConfigLauncher
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
    public static ConfigLauncher create(Configurer configurer)
    {
        final Collection<ConfigLaunchable> launchables = new ArrayList<ConfigLaunchable>();
        final XmlNode launcher = configurer.getRoot().getChild(LAUNCHER);
        for (final XmlNode launchable : launcher.getChildren(ConfigLaunchable.LAUNCHABLE))
        {
            launchables.add(ConfigLaunchable.create(launchable));
        }
        final int rate = configurer.getInteger(ConfigLauncher.RATE, ConfigLauncher.LAUNCHER);

        return new ConfigLauncher(rate, launchables);
    }

    /** The rate value. */
    private final int rate;
    /** The launchable configurations. */
    private final Collection<ConfigLaunchable> launchables;

    /**
     * Disabled constructor.
     */
    private ConfigLauncher()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }

    /**
     * Create a launcher configuration.
     * 
     * @param rate The rate value.
     * @param launchables The launchables reference.
     */
    private ConfigLauncher(int rate, Collection<ConfigLaunchable> launchables)
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
    public Iterable<ConfigLaunchable> getLaunchables()
    {
        return launchables;
    }
}
