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
package com.b3dgs.lionengine.game.map;

import java.util.Collection;

import com.b3dgs.lionengine.game.collision.CollisionCategory;
import com.b3dgs.lionengine.game.collision.CollisionFormula;

/**
 * Tile representation with the following data:
 * <ul>
 * <li><code>formulas</code> : collision formulas used</li>
 * </ul>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface TileCollision
        extends TileFeature
{
    /**
     * Add a formula.
     * 
     * @param formula The formula to add.
     */
    void addCollisionFormula(CollisionFormula formula);

    /**
     * Remove a collision formula.
     * 
     * @param formula The formula reference.
     */
    void removeCollisionFormula(CollisionFormula formula);

    /**
     * Remove all supported collision formulas.
     */
    void removeCollisionFormulas();

    /**
     * Set the collision group name.
     * 
     * @param name The collision group name.
     */
    void setGroup(String name);

    /**
     * Get the horizontal collision location between the tile and the transformable.
     * 
     * @param category The collision category.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The horizontal collision (<code>null</code> if none).
     */
    Double getCollisionX(CollisionCategory category, double ox, double oy, double x, double y);

    /**
     * Get the vertical collision location between the tile and the transformable.
     * 
     * @param category The collision category.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The vertical collision (<code>null</code> if none).
     */
    Double getCollisionY(CollisionCategory category, double ox, double oy, double x, double y);

    /**
     * Get the collision group name.
     * 
     * @return The collision group name.
     */
    String getGroup();

    /**
     * Get tile collision formulas.
     * 
     * @return The tile collision formulas.
     */
    Collection<CollisionFormula> getCollisionFormulas();

}
