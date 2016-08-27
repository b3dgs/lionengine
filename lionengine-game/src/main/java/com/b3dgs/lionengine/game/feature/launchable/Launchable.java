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

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Feature;

/**
 * Represents something which can be launched with a specified vector.
 * 
 * @see Launcher
 */
public interface Launchable extends Feature, Updatable
{
    /**
     * Add a launchable listener.
     * 
     * @param listener The launchable listener to add.
     */
    void addListener(LaunchableListener listener);

    /**
     * Initiate launch by using the defined vector with {@link #setVector(Force)} starting at the location defined by
     * {@link #setLocation(double, double)}.
     */
    void launch();

    /**
     * Set the launch starting location. Must be called before {@link #launch()}.
     * 
     * @param x The starting horizontal location.
     * @param y The starting vertical location.
     */
    void setLocation(double x, double y);

    /**
     * Set the vector to use when launched. Must be called before {@link #launch()}.
     * 
     * @param force The force to use.
     */
    void setVector(Force force);
}
