/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.body;

import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;

/**
 * Represents something designed to receive a gravitational force.
 */
@FeatureInterface
public interface Body extends RoutineUpdate, Direction
{
    /**
     * Reset current gravity force (usually when hit the ground).
     */
    void resetGravity();

    /**
     * Set the gravity to use.
     * 
     * @param gravity The gravity to use.
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
     * Get the gravity.
     * 
     * @return The gravity.
     */
    double getGravity();

    /**
     * Get the gravity max.
     * 
     * @return The gravity max.
     */
    double getGravityMax();

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
