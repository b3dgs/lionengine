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
package com.b3dgs.lionengine.game.configurer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Collision;
import com.b3dgs.lionengine.game.map.CollisionTile;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collisions data from a configurer node.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurer
 * @see Collision
 * @see XmlNode
 */
public class ConfigCollisionTileFormulas
{
    /** Tile collision node. */
    public static final String COLLISION = Configurer.PREFIX + "tileCollisionFormula";
    /** Tile collision input node. */
    public static final String INPUT = Configurer.PREFIX + "input";
    /** Tile collision output node. */
    public static final String OUTPUT = Configurer.PREFIX + "output";
    /** Tile collision name attribute. */
    public static final String NAME = "name";

    /**
     * Create the size node.
     * 
     * @param root The root reference.
     * @return The config collisions instance.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public static ConfigCollisionTileFormulas create(XmlNode root) throws LionEngineException
    {
        final Map<String, CollisionTile> collisions = new HashMap<>(0);
        for (final XmlNode node : root.getChildren(COLLISION))
        {
            final String name = node.readString(NAME);
            final CollisionTile collision = createCollision(node);
            collisions.put(name, collision);
        }
        return new ConfigCollisionTileFormulas(collisions);
    }

    /**
     * Create a tile collision from its node.
     * 
     * @param node The collision node.
     * @return The tile collision instance.
     * @throws LionEngineException If error when reading data.
     */
    private static CollisionTile createCollision(XmlNode node) throws LionEngineException
    {
        return new CollisionTile(node.readString(NAME), ConfigCollisionRange.create(node.getChild(INPUT)),
                ConfigCollisionRange.create(node.getChild(OUTPUT)), ConfigCollisionFormula.create(node), null);
    }

    /** Tile collisions map. */
    private final Map<String, CollisionTile> collisions;

    /**
     * Load collisions from configuration media.
     * 
     * @param collisions The collisions mapping.
     * @throws LionEngineException If error when opening the media.
     */
    private ConfigCollisionTileFormulas(Map<String, CollisionTile> collisions) throws LionEngineException
    {
        this.collisions = collisions;
    }

    /**
     * Clear the collisions data.
     */
    public void clear()
    {
        collisions.clear();
    }

    /**
     * Get a collision data from its name.
     * 
     * @param name The collision name.
     * @return The collision reference.
     * @throws LionEngineException If the collision with the specified name is not found.
     */
    public CollisionTile getCollision(String name) throws LionEngineException
    {
        final CollisionTile collision = collisions.get(name);
        Check.notNull(collision);
        return collision;
    }

    /**
     * Get all collisions.
     * 
     * @return The unmodifiable collisions map, where key is the collision name.
     */
    public Map<String, CollisionTile> getCollisions()
    {
        return Collections.unmodifiableMap(collisions);
    }
}
