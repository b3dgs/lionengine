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

import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * Represents something which can be launched with a specified vector.
 * 
 * @see Launcher
 */
@FeatureInterface
public interface Launchable extends Feature, Updatable, Listenable<LaunchableListener>
{
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

    /**
     * Get the current direction.
     * 
     * @return The current direction, <code>null</code> if undefined.
     */
    Force getDirection();
}
