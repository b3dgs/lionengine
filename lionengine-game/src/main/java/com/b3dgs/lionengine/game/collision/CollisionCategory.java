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
package com.b3dgs.lionengine.game.collision;

import java.util.Collection;

/**
 * Collision tile category.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionCategory
{
    /** Category name. */
    private final String name;
    /** Horizontal offset. */
    private final int x;
    /** Vertical offset. */
    private final int y;
    /** Collision tile used list. */
    private final Collection<CollisionFormula> formulas;

    /**
     * Constructor.
     * 
     * @param name The category name.
     * @param x The horizontal offset.
     * @param y The vertical offset.
     * @param formulas The collision formulas used list.
     */
    public CollisionCategory(String name, int x, int y, Collection<CollisionFormula> formulas)
    {
        this.name = name;
        this.x = x;
        this.y = y;
        this.formulas = formulas;
    }

    /**
     * Get the category name.
     * 
     * @return The category name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the list of collision formulas to test.
     * 
     * @return The collision formulas list.
     */
    public Collection<CollisionFormula> getCollisionFormulas()
    {
        return formulas;
    }

    /**
     * Get the horizontal tile collision check offset.
     * 
     * @return The horizontal tile collision check offset.
     */
    public int getOffsetX()
    {
        return x;
    }

    /**
     * Get the vertical tile collision check offset.
     * 
     * @return The vertical tile collision check offset.
     */
    public int getOffsetY()
    {
        return y;
    }
}
