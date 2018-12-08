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
package com.b3dgs.lionengine.game.feature.body;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * Represents something designed to receive a gravitational force.
 */
@FeatureInterface
public interface Body extends Feature, Updatable, Direction
{
    /**
     * Reset current gravity force (usually when hit the ground).
     */
    void resetGravity();

    /**
     * Set the desired fps.
     * 
     * @param desiredFps The desired fps.
     */
    void setDesiredFps(int desiredFps);

    /**
     * Set the gravity to use. {@link com.b3dgs.lionengine.Constant#GRAVITY_EARTH} is used by default.
     * 
     * @param gravity The gravity to use (in m/s²).
     */
    void setGravity(double gravity);

    /**
     * Set the maximum gravity value.
     * 
     * @param max The maximum gravity value.
     */
    void setGravityMax(double max);

    /**
     * Set body mass.
     * 
     * @param mass The body mass.
     */
    void setMass(double mass);

    /**
     * Get body mass.
     * 
     * @return The body mass.
     */
    double getMass();

    /**
     * Get body weight (mass * gravity).
     * 
     * @return The body weight.
     */
    double getWeight();
}
