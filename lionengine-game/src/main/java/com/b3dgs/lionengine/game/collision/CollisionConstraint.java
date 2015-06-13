/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.collision;

import com.b3dgs.lionengine.game.configurer.ConfigCollisionConstraint;

/**
 * Represents the collision constraints around a collision.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see ConfigCollisionConstraint
 */
public class CollisionConstraint
{
    /** Top constraint. */
    private final String top;
    /** Bottom constraint. */
    private final String bottom;
    /** Left constraint. */
    private final String left;
    /** Right constraint. */
    private final String right;

    /**
     * Create a collision constraint.
     * 
     * @param top The top constraint.
     * @param bottom The bottom constraint.
     * @param left The left constraint.
     * @param right The right constraint.
     */
    public CollisionConstraint(String top, String bottom, String left, String right)
    {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    /**
     * Get the top constraint.
     * 
     * @return The top constraint.
     */
    public String getTop()
    {
        return top;
    }

    /**
     * Get the bottom constraint.
     * 
     * @return The bottom constraint.
     */
    public String getBottom()
    {
        return bottom;
    }

    /**
     * Get the left constraint.
     * 
     * @return The left constraint.
     */
    public String getLeft()
    {
        return left;
    }

    /**
     * Get the right constraint.
     * 
     * @return The right constraint.
     */
    public String getRight()
    {
        return right;
    }
}
