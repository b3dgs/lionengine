/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Represents the collision feature of a map tile.
 * 
 * @see CollisionFormula
 * @see CollisionGroup
 */
public interface MapTileCollision extends Feature
{
    /**
     * Load map collision from an external file.
     * 
     * @param formulasConfig The collision formulas descriptor.
     * @param groupsConfig The tile collision groups descriptor.
     * @throws LionEngineException If error when reading collisions.
     */
    void loadCollisions(Media formulasConfig, Media groupsConfig);

    /**
     * Load map collision with default files.
     * 
     * @param formulasConfig The collision formulas descriptor.
     * @param groupsConfig The tile collision groups descriptor.
     * @throws LionEngineException If error when reading collisions.
     */
    void loadCollisions(CollisionFormulaConfig formulasConfig, CollisionGroupConfig groupsConfig);

    /**
     * Save the current collisions to the collision file.
     * 
     * @throws LionEngineException If error when saving collisions.
     */
    void saveCollisions();

    /**
     * Search first tile hit by the transformable that contains collision, applying a ray tracing from its old location
     * to its current. This way, the transformable can not pass through a collidable tile.
     * 
     * @param transformable The transformable reference.
     * @param category The collisions category to search in.
     * @return The collision result, <code>null</code> if nothing found.
     */
    CollisionResult computeCollision(Transformable transformable, CollisionCategory category);

    /**
     * Get the collision formula from its name.
     * 
     * @param name The collision formula name.
     * @return The collision formula from name reference.
     * @throws LionEngineException If formula not found.
     */
    CollisionFormula getCollisionFormula(String name);

    /**
     * Get the collision group from its name.
     * 
     * @param name The collision group name.
     * @return The supported collision group reference.
     * @throws LionEngineException If group not found.
     */
    CollisionGroup getCollisionGroup(String name);

    /**
     * Get the collision formulas list.
     * 
     * @return The collision formulas list.
     */
    Collection<CollisionFormula> getCollisionFormulas();

    /**
     * Get the collision groups list.
     * 
     * @return The collision groups list.
     */
    Collection<CollisionGroup> getCollisionGroups();

    /**
     * Get the formulas config file.
     * 
     * @return The formulas config file.
     */
    Media getFormulasConfig();

    /**
     * Get the collisions config file.
     * 
     * @return The collisions config file.
     */
    Media getCollisionsConfig();
}
