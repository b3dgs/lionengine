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
package com.b3dgs.lionengine.game.feature.launchable;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.feature.Featurable;

/**
 * Represents a delayed launch.
 */
final class DelayedLaunch
{
    /** Delay timing. */
    private final Timing timing = new Timing();
    /** Launchable configuration reference. */
    private final LaunchableConfig config;
    /** Initial direction for launch. */
    private final Direction initial;
    /** Featurable reference to launch. */
    private final Featurable featurable;
    /** Launchable reference to launch. */
    private final Launchable launchable;

    /**
     * Create a delayed launch.
     * 
     * @param config The launch configuration.
     * @param initial The launch initial direction.
     * @param featurable The featurable to launch.
     * @param launchable The launchable to launch.
     */
    DelayedLaunch(LaunchableConfig config, Direction initial, Featurable featurable, Launchable launchable)
    {
        super();

        this.config = config;
        this.initial = initial;
        this.featurable = featurable;
        this.launchable = launchable;
        timing.start();
    }

    /**
     * Check if launch can be performed.
     * 
     * @return <code>true</code> if delayed elapsed, <code>false</code> else.
     */
    public boolean isReady()
    {
        return timing.elapsed(config.getDelay());
    }

    /**
     * Get configuration.
     * 
     * @return The configuration.
     */
    public LaunchableConfig getConfig()
    {
        return config;
    }

    /**
     * Get initial direction.
     * 
     * @return The initial direction.
     */
    public Direction getInitial()
    {
        return initial;
    }

    /**
     * Get associated featurable.
     * 
     * @return The featurable associated.
     */
    public Featurable getFeaturable()
    {
        return featurable;
    }

    /**
     * Get launchable feature.
     * 
     * @return The launchable feature.
     */
    public Launchable getLaunchable()
    {
        return launchable;
    }
}
