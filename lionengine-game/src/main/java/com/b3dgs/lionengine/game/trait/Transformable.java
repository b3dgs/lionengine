/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.trait;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.game.Direction;

/**
 * Represents something that can be transformed with a translation or a size modification.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Transformable
        extends Trait, Localizable
{
    /**
     * Move location using different directions. Old location is stored before moving and the movement is updated after
     * calculation.
     * 
     * @param extrp The extrapolation value.
     * @param direction The primary direction.
     * @param directions The other directions.
     */
    void moveLocation(double extrp, Direction direction, Direction... directions);

    /**
     * Move location using a simple force. Old location is stored before moving and the movement is updated after
     * calculation.
     * 
     * @param extrp The extrapolation value.
     * @param vx The horizontal force.
     * @param vy The vertical force.
     */
    void moveLocation(double extrp, double vx, double vy);

    /**
     * Teleport to a new location. Old location is not stored..
     * 
     * @param x The new horizontal location.
     * @param y The new vertical location.
     */
    void teleport(double x, double y);

    /**
     * Teleport to a new horizontal location. Old location is not stored.
     * 
     * @param x The new horizontal location.
     */
    void teleportX(double x);

    /**
     * Teleport to a new vertical location. Old location is not stored.
     * 
     * @param y The new vertical location.
     */
    void teleportY(double y);

    /**
     * Set new location. Old location is stored.
     * 
     * @param x The new horizontal location.
     * @param y The new vertical location.
     */
    void setLocation(double x, double y);

    /**
     * Set new x location. Old x location is stored.
     * 
     * @param x The new horizontal location.
     */
    void setLocationX(double x);

    /**
     * Set new y location. Old y location is stored.
     * 
     * @param y The new vertical location.
     */
    void setLocationY(double y);

    /**
     * Set surface size. Old size is stored.
     * 
     * @param width The width.
     * @param height The height.
     */
    void setSize(int width, int height);

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

    /**
     * Get the old width.
     * 
     * @return The old width.
     */
    int getOldWidth();

    /**
     * Get the old height.
     * 
     * @return The old height.
     */
    int getOldHeight();
}
