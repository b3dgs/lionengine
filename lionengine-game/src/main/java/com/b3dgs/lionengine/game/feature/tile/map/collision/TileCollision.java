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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import java.util.Collection;

import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * Tile representation with the following data:
 * <ul>
 * <li><code>formulas</code> : collision formulas used</li>
 * </ul>
 * <p>
 * This allows to compute collision from existing {@link CollisionFormula}, with a {@link CollisionCategory} as input
 * (which represents the object entering in collision).
 * </p>
 * 
 * @see CollisionFormula
 */
@FeatureInterface
public interface TileCollision extends Feature
{
    /**
     * Add a collision formula.
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
     * Get the horizontal collision location between the tile and the movement vector.
     * 
     * @param category The collision category.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The horizontal collision (<code>null</code> if none).
     */
    Double getCollisionX(CollisionCategory category, double x, double y);

    /**
     * Get the vertical collision location between the tile and the movement vector.
     * 
     * @param category The collision category.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The vertical collision (<code>null</code> if none).
     */
    Double getCollisionY(CollisionCategory category, double x, double y);

    /**
     * Get tile collision formulas.
     * 
     * @return The tile collision formulas.
     */
    Collection<CollisionFormula> getCollisionFormulas();

}
