/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.lionheart.entity.patrol;

import com.b3dgs.lionengine.game.purview.Localizable;
import com.b3dgs.lionengine.game.purview.Mirrorable;

/**
 * Represents something that can patrol.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Patrollable
        extends Localizable, Mirrorable, Patroller
{
    /**
     * Set the movement force.
     * 
     * @param fh The horizontal force.
     * @param fv The vertical force.
     */
    void setMovementForce(double fh, double fv);

    /**
     * Set the maximum movement speed.
     * 
     * @param speed The maximum movement speed.
     */
    void setMovementSpeedMax(double speed);

    /**
     * Get the max movement speed.
     * 
     * @return The max movement speed.
     */
    double getMovementSpeedMax();

    /**
     * Get the horizontal movement force.
     * 
     * @return The horizontal movement force.
     */
    double getForceHorizontal();
}
