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

import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Represents a delayed launch.
 */
final class DelayedLaunch implements Updatable
{
    /** Delay tick. */
    private final Tick tick = new Tick();
    /** Source reference. */
    private final SourceResolutionProvider source;
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
     * @param source The source reference.
     * @param config The launch configuration.
     * @param initial The launch initial direction.
     * @param featurable The featurable to launch.
     * @param launchable The launchable to launch.
     */
    DelayedLaunch(SourceResolutionProvider source,
                  LaunchableConfig config,
                  Direction initial,
                  Featurable featurable,
                  Launchable launchable)
    {
        super();

        this.source = source;
        this.config = config;
        this.initial = initial;
        this.featurable = featurable;
        this.launchable = launchable;
        tick.start();
    }

    /**
     * Check if launch can be performed.
     * 
     * @return <code>true</code> if delayed elapsed, <code>false</code> else.
     */
    public boolean isReady()
    {
        return tick.elapsedTime(source.getRate(), config.getDelay());
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

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
    }
}
