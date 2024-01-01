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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.Localizable;

/**
 * Represents something that can move.
 */
public interface Mover extends Localizable
{
    /**
     * Backup current location.
     */
    void backup();

    /**
     * Move location using different directions.
     * 
     * @param extrp The extrapolation value.
     * @param direction The primary direction.
     * @param directions The other directions.
     */
    void moveLocation(double extrp, Direction direction, Direction... directions);

    /**
     * Move location using a simple force.
     * 
     * @param extrp The extrapolation value.
     * @param vx The horizontal force.
     */
    void moveLocationX(double extrp, double vx);

    /**
     * Move location using a simple force.
     * 
     * @param extrp The extrapolation value.
     * @param vy The vertical force.
     */
    void moveLocationY(double extrp, double vy);

    /**
     * Move location using a simple force.
     * 
     * @param extrp The extrapolation value.
     * @param vx The horizontal force.
     * @param vy The vertical force.
     */
    void moveLocation(double extrp, double vx, double vy);

    /**
     * Teleport to a new location without movement.
     * 
     * @param x The new horizontal location.
     * @param y The new vertical location.
     */
    void teleport(double x, double y);

    /**
     * Teleport to a new horizontal location without movement.
     * 
     * @param x The new horizontal location.
     */
    void teleportX(double x);

    /**
     * Teleport to a new vertical location without movement.
     * 
     * @param y The new vertical location.
     */
    void teleportY(double y);

    /**
     * Set new location.
     * 
     * @param x The new horizontal location.
     * @param y The new vertical location.
     */
    void setLocation(double x, double y);

    /**
     * Set new x location.
     * 
     * @param x The new horizontal location.
     */
    void setLocationX(double x);

    /**
     * Set new y location.
     * 
     * @param y The new vertical location.
     */
    void setLocationY(double y);

    /**
     * Get the old horizontal location.
     * 
     * @return The old horizontal location.
     */
    double getOldX();

    /**
     * Get the old vertical location.
     * 
     * @return The old vertical location.
     */
    double getOldY();
}
