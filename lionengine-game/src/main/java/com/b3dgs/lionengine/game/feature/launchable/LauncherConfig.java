/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.io.Xml;

/**
 * Represents the launcher data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class LauncherConfig
{
    /** Launcher node name. */
    public static final String NODE_LAUNCHER = Constant.XML_PREFIX + "launcher";
    /** Level attribute name. */
    public static final String ATT_LEVEL = "level";
    /** Rate attribute name. */
    public static final String ATT_RATE = "rate";
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 48;

    /**
     * Import the launcher data from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The launcher data.
     * @throws LionEngineException If unable to read node.
     */
    public static List<LauncherConfig> imports(Configurer configurer)
    {
        Check.notNull(configurer);

        final Collection<Xml> children = configurer.getRoot().getChildren(NODE_LAUNCHER);
        final List<LauncherConfig> launchers = new ArrayList<>(children.size());

        for (final Xml launcher : children)
        {
            launchers.add(imports(launcher));
        }

        return launchers;
    }

    /**
     * Import the launcher data from node.
     * 
     * @param node The node reference (must not be <code>null</code>).
     * @return The launcher data.
     * @throws LionEngineException If unable to read node.
     */
    public static LauncherConfig imports(Xml node)
    {
        Check.notNull(node);

        final Collection<Xml> children = node.getChildren(LaunchableConfig.NODE_LAUNCHABLE);
        final Collection<LaunchableConfig> launchables = new ArrayList<>(children.size());

        for (final Xml launchable : children)
        {
            launchables.add(LaunchableConfig.imports(launchable));
        }

        final int level = node.readInteger(0, ATT_RATE);
        final int rate = node.readInteger(ATT_RATE);

        return new LauncherConfig(level, rate, launchables);
    }

    /**
     * Export the launcher node from config.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @return The launcher data.
     * @throws LionEngineException If unable to read node.
     */
    public static Xml exports(LauncherConfig config)
    {
        Check.notNull(config);

        final Xml node = new Xml(NODE_LAUNCHER);
        node.writeInteger(ATT_RATE, config.getRate());

        for (final LaunchableConfig launchable : config.getLaunchables())
        {
            node.add(LaunchableConfig.exports(launchable));
        }

        return node;
    }

    /** The level index. */
    private final int level;
    /** The rate value. */
    private final int rate;
    /** The launchable configurations. */
    private final Collection<LaunchableConfig> launchables;

    /**
     * Create a launcher configuration.
     * 
     * @param level The associated level.
     * @param rate The rate value.
     * @param launchables The launchables reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public LauncherConfig(int level, int rate, Collection<LaunchableConfig> launchables)
    {
        super();

        this.level = level;
        this.rate = rate;
        this.launchables = new ArrayList<>(launchables);
    }

    /**
     * Get the associated level.
     * 
     * @return The associated level.
     */
    public int getLevel()
    {
        return level;
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
     * Get the launchables configuration as read only.
     * 
     * @return The launchables configuration.
     */
    public Iterable<LaunchableConfig> getLaunchables()
    {
        return Collections.unmodifiableCollection(launchables);
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + level;
        result = prime * result + rate;
        result = prime * result + launchables.hashCode();
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
        final LauncherConfig other = (LauncherConfig) object;
        return other.getLevel() == getLevel()
               && other.getRate() == getRate()
               && Arrays.equals(other.launchables.toArray(), launchables.toArray());
    }

    @Override
    public String toString()
    {
        final StringBuilder launchablesToString = new StringBuilder();
        final int n = launchables.size();
        int i = 0;
        for (final LaunchableConfig config : launchables)
        {
            launchablesToString.append(config);
            i++;
            if (i < n)
            {
                launchablesToString.append(System.lineSeparator()).append(Constant.TAB);
            }
        }
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [level=")
                                            .append(level)
                                            .append(", rate=")
                                            .append(rate)
                                            .append(", launchables=")
                                            .append(System.lineSeparator())
                                            .append(Constant.TAB)
                                            .append(launchablesToString.toString())
                                            .append("]")
                                            .toString();
    }
}
