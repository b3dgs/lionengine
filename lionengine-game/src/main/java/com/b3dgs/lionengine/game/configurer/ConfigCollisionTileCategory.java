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

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.map.CollisionRefential;
import com.b3dgs.lionengine.game.map.CollisionTile;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Collision tile category.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurer
 * @see XmlNode
 */
public class ConfigCollisionTileCategory
{
    /** Collision tile category node name. */
    public static final String COLLISION_TILE_CATEGORY = Configurer.PREFIX + "collisionTileCategory";
    /** Collision tile node name. */
    public static final String COLLISION_TILE = Configurer.PREFIX + "collisionTile";
    /** Collision tile category name node. */
    public static final String COLLISION_TILE_CATEGORY_NAME = "name";
    /** Collision tile category horizontal offset. */
    public static final String COLLISION_TILE_CATEGORY_X = "x";
    /** Collision tile category vertical offset. */
    public static final String COLLISION_TILE_CATEGORY_Y = "y";
    /** Slide value. */
    public static final String COLLISION_TILE_CATEGORY_SLIDE = "slide";
    /** Collision not found. */
    private static final String ERROR_COLLISION_NOT_FOUND = "Collision where not found: ";
    /** Collision not found tip. */
    private static final String ERROR_MAP_NOT_LOADED_TIP = " (map may not have been loaded)";

    /**
     * Create the categories nodes.
     * 
     * @param configurer The configurer reference.
     * @param map The map reference.
     * @return The category collisions instance.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public static Collection<ConfigCollisionTileCategory> create(Configurer configurer, MapTile<?> map)
    {
        final Collection<ConfigCollisionTileCategory> collisions = new ArrayList<>();
        for (final XmlNode node : configurer.getRoot().getChildren(COLLISION_TILE_CATEGORY))
        {
            final ConfigCollisionTileCategory category = create(node, map);
            collisions.add(category);
        }
        return collisions;
    }

    /**
     * Create the categories node.
     * 
     * @param root The root reference.
     * @param map The map reference.
     * @return The category node value.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public static ConfigCollisionTileCategory create(XmlNode root, MapTile<?> map) throws LionEngineException
    {
        final Collection<CollisionTile> collisions = new ArrayList<>();
        for (final XmlNode current : root.getChildren(COLLISION_TILE))
        {
            final String name = current.getText();
            final CollisionTile collisionTile = map.getCollision(name);
            if (collisionTile != null)
            {
                collisions.add(collisionTile);
            }
            else
            {
                throw new LionEngineException(ERROR_COLLISION_NOT_FOUND, name, ERROR_MAP_NOT_LOADED_TIP);
            }
        }
        return new ConfigCollisionTileCategory(root.readString(COLLISION_TILE_CATEGORY_NAME),
                root.readInteger(COLLISION_TILE_CATEGORY_X), root.readInteger(COLLISION_TILE_CATEGORY_Y),
                root.hasAttribute(COLLISION_TILE_CATEGORY_SLIDE) ? root.readString(COLLISION_TILE_CATEGORY_SLIDE)
                        : null, collisions);
    }

    /** Category name. */
    private final String name;
    /** Horizontal offset. */
    private final int x;
    /** Vertical offset. */
    private final int y;
    /** Slide used. */
    private final CollisionRefential slide;
    /** Collision tile used list. */
    private final Collection<CollisionTile> collisions;

    /**
     * Constructor.
     * 
     * @param name The category name.
     * @param x The horizontal offset.
     * @param y The vertical offset.
     * @param slide The slide referential used.
     * @param collisions The collisions used list.
     */
    public ConfigCollisionTileCategory(String name, int x, int y, String slide, Collection<CollisionTile> collisions)
    {
        this.name = name;
        this.x = x;
        this.y = y;
        this.slide = slide != null ? CollisionRefential.valueOf(slide) : null;
        this.collisions = collisions;
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
     * Get the list of collisions to test.
     * 
     * @return The collisions list.
     */
    public Collection<CollisionTile> getCollisions()
    {
        return collisions;
    }

    /**
     * Get the axis where slide should be performed.
     * 
     * @return The slide axis (<code>null</code> if none).
     */
    public CollisionRefential getSlide()
    {
        return slide;
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
