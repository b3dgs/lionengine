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
package com.b3dgs.lionengine.game.feature.launchable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;

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
    /** Delay attribute name. */
    public static final String ATT_DELAY = "delay";
    /** Mirrorable attribute name. */
    public static final String ATT_MIRRORABLE = "mirrorable";
    /** Centered attribute name. */
    public static final String ATT_CENTERED = "centered";
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 66;

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

        final Collection<XmlReader> children = configurer.getRoot().getChildren(NODE_LAUNCHER);
        final List<LauncherConfig> launchers = new ArrayList<>(children.size());

        for (final XmlReader launcher : children)
        {
            launchers.add(imports(launcher));
        }
        children.clear();

        return launchers;
    }

    /**
     * Import the launcher data from node.
     * 
     * @param node The node reference (must not be <code>null</code>).
     * @return The launcher data.
     * @throws LionEngineException If unable to read node.
     */
    public static LauncherConfig imports(XmlReader node)
    {
        Check.notNull(node);

        final Collection<XmlReader> children = node.getChildren(LaunchableConfig.NODE_LAUNCHABLE);
        final Collection<LaunchableConfig> launchables = new ArrayList<>(children.size());

        for (final XmlReader launchable : children)
        {
            launchables.add(LaunchableConfig.imports(launchable));
        }
        children.clear();

        final int level = node.getInteger(0, ATT_LEVEL);
        final int delay = node.getInteger(0, ATT_DELAY);
        final boolean mirrorable = node.getBoolean(true, ATT_MIRRORABLE);
        final boolean centered = node.getBoolean(false, ATT_CENTERED);

        return new LauncherConfig(level, delay, mirrorable, centered, launchables);
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
        node.writeInteger(ATT_LEVEL, config.getLevel());
        node.writeInteger(ATT_DELAY, config.getDelay());
        node.writeBoolean(ATT_MIRRORABLE, config.hasMirrorable());

        for (final LaunchableConfig launchable : config.getLaunchables())
        {
            node.add(LaunchableConfig.exports(launchable));
        }

        return node;
    }

    /** The level index. */
    private final int level;
    /** The fire delay. */
    private final int delay;
    /** The mirrorable flag. */
    private final boolean mirrorable;
    /** The centered flag. */
    private final boolean centered;
    /** The launchable configurations. */
    private final Collection<LaunchableConfig> launchables;

    /**
     * Create a launcher configuration.
     * 
     * @param level The associated level.
     * @param delay The delay value.
     * @param mirrorable The mirrorable flag.
     * @param centered The centered flag.
     * @param launchables The launchables reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public LauncherConfig(int level,
                          int delay,
                          boolean mirrorable,
                          boolean centered,
                          Collection<LaunchableConfig> launchables)
    {
        super();

        this.level = level;
        this.delay = delay;
        this.mirrorable = mirrorable;
        this.centered = centered;
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
     * Get the launch delay value.
     * 
     * @return The launch delay value.
     */
    public int getDelay()
    {
        return delay;
    }

    /**
     * Get the mirrorable flag.
     * 
     * @return <code>true</code> if apply mirror on fire if present, <code>false</code> else.
     */
    public boolean hasMirrorable()
    {
        return mirrorable;
    }

    /**
     * Get the centered flag.
     * 
     * @return <code>true</code> if center on fire if present, <code>false</code> else.
     */
    public boolean isCentered()
    {
        return centered;
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
        result = prime * result + delay;
        result = prime * result + (mirrorable ? 1 : 0);
        result = prime * result + (centered ? 1 : 0);
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
        return level == other.level
               && delay == other.delay
               && mirrorable == other.mirrorable
               && centered == other.centered
               && Arrays.equals(launchables.toArray(), other.launchables.toArray());
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
                                            .append(", delay=")
                                            .append(delay)
                                            .append(", mirrorable=")
                                            .append(mirrorable)
                                            .append(", centered=")
                                            .append(centered)
                                            .append(", launchables=")
                                            .append(System.lineSeparator())
                                            .append(Constant.TAB)
                                            .append(launchablesToString.toString())
                                            .append("]")
                                            .toString();
    }
}
