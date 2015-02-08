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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.game.collision.CollisionCategory;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionGroup;
import com.b3dgs.lionengine.game.collision.CollisionResult;
import com.b3dgs.lionengine.game.trait.Transformable;

/**
 * Represents the collision layer of a map tile.
 * <ul>
 * 
 * <pre>
 * {@code <lionengine:sheets xmlns:lionengine="http://lionengine.b3dgs.com">}
 *     {@code <lionengine:sheet>0.png</lionengine:sheet>}
 *     {@code <lionengine:sheet>1.png</lionengine:sheet>}
 *     ...
 * {@code </lionengine:sheets>}
 * </pre>
 * 
 * </li>
 * <li>{@value #FORMULAS_FILE_NAME} - defines the {@link CollisionFormula} which can be used.</li>
 * <li>{@value #GROUPS_FILE_NAME} - defines the {@link CollisionGroup} for each tiles.</li>
 * </ul>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see CollisionFormula
 */
public interface MapTileCollision
        extends MapTileFeature, Renderable
{
    /** Collision formulas file name. */
    String FORMULAS_FILE_NAME = "formulas.xml";
    /** Collision groups file name. */
    String GROUPS_FILE_NAME = "groups.xml";

    /**
     * Create the collision draw surface. Must be called after map creation to enable collision rendering.
     */
    void createCollisionDraw();

    /**
     * Load map collision from an external file.
     * 
     * @param collisionFormulas The collision formulas descriptor.
     * @param collisionGroups The tile collision groups descriptor.
     * @throws LionEngineException If error when reading collisions.
     */
    void loadCollisions(Media collisionFormulas, Media collisionGroups) throws LionEngineException;

    /**
     * Clear the cached collision image created with {@link #createCollisionDraw()}.
     */
    void clearCollisionDraw();

    /**
     * Add a collision formula.
     * 
     * @param formula The collision formula reference.
     */
    void addCollisionFormula(CollisionFormula formula);

    /**
     * Add a collision group.
     * 
     * @param group The collision group reference.
     */
    void addCollisionGroup(CollisionGroup group);

    /**
     * Remove a collision formula.
     * 
     * @param formula The collision formula to remove.
     */
    void removeCollisionFormula(CollisionFormula formula);

    /**
     * Remove a collision group.
     * 
     * @param group The collision group to remove.
     */
    void removeCollisionGroup(CollisionGroup group);

    /**
     * Remove all collision formulas.
     */
    void removeCollisionFormulas();

    /**
     * Remove all collision groups.
     */
    void removeCollisionGroups();

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
     * Save the current collisions to the collision file.
     * 
     * @throws LionEngineException If error when saving collisions.
     */
    void saveCollisions() throws LionEngineException;

    /**
     * Get the collision formula from its name.
     * 
     * @param name The collision formula name.
     * @return The collision formula from name reference.
     * @throws LionEngineException If formula not found.
     */
    CollisionFormula getCollisionFormula(String name) throws LionEngineException;

    /**
     * Get the collision group from its name.
     * 
     * @param name The collision group name.
     * @return The supported collision group reference.
     * @throws LionEngineException If group not found.
     */
    CollisionGroup getCollisionGroup(String name) throws LionEngineException;

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
}
