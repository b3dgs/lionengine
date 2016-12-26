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
package com.b3dgs.lionengine.game.feature.collidable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.io.Xml;

/**
 * Represents the collisions data from a configurer.
 * 
 * @see Collision
 */
public final class CollisionConfig
{
    /** Collision node name. */
    public static final String COLLISION = Constant.XML_PREFIX + "collision";
    /** Collision attribute name. */
    public static final String COLLISION_NAME = "name";
    /** Collision attribute offset x. */
    public static final String COLLISION_OFFSETX = "offsetX";
    /** Collision attribute offset y. */
    public static final String COLLISION_OFFSETY = "offsetY";
    /** Collision attribute width. */
    public static final String COLLISION_WIDTH = "width";
    /** Collision attribute height. */
    public static final String COLLISION_HEIGHT = "height";
    /** Collision attribute mirror. */
    public static final String COLLISION_MIRROR = "mirror";
    /** Error collision not found. */
    private static final String ERROR_COLLISION_NOT_FOUND = "Collision not found: ";

    /**
     * Create the collision data from node.
     * 
     * @param configurer The configurer reference.
     * @return The collisions data.
     * @throws LionEngineException If unable to read node.
     */
    public static CollisionConfig imports(Configurer configurer)
    {
        final Map<String, Collision> collisions = new HashMap<String, Collision>(0);
        for (final Xml node : configurer.getRoot().getChildren(COLLISION))
        {
            final String coll = node.readString(COLLISION_NAME);
            final Collision collision = createCollision(node);
            collisions.put(coll, collision);
        }
        return new CollisionConfig(collisions);
    }

    /**
     * Create an collision from its node.
     * 
     * @param node The collision node.
     * @return The collision instance.
     * @throws LionEngineException If error when reading collision data.
     */
    public static Collision createCollision(Xml node)
    {
        final String name = node.readString(COLLISION_NAME);
        final int offsetX = node.readInteger(COLLISION_OFFSETX);
        final int offsetY = node.readInteger(COLLISION_OFFSETY);
        final int width = node.readInteger(COLLISION_WIDTH);
        final int height = node.readInteger(COLLISION_HEIGHT);
        final boolean mirror = node.readBoolean(COLLISION_MIRROR);

        return new Collision(name, offsetX, offsetY, width, height, mirror);
    }

    /**
     * Create an XML node from a collision.
     * 
     * @param root The node root.
     * @param collision The collision reference.
     */
    public static void exports(Xml root, Collision collision)
    {
        final Xml node = root.createChild(COLLISION);
        node.writeString(COLLISION_NAME, collision.getName());
        node.writeInteger(COLLISION_OFFSETX, collision.getOffsetX());
        node.writeInteger(COLLISION_OFFSETY, collision.getOffsetY());
        node.writeInteger(COLLISION_WIDTH, collision.getWidth());
        node.writeInteger(COLLISION_HEIGHT, collision.getHeight());
        node.writeBoolean(COLLISION_MIRROR, collision.hasMirror());
    }

    /** Collisions map. */
    private final Map<String, Collision> collisions;

    /**
     * Disabled constructor.
     */
    private CollisionConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }

    /**
     * Load collisions from configuration media.
     * 
     * @param collisions The collisions mapping.
     */
    private CollisionConfig(Map<String, Collision> collisions)
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
     * @throws LionEngineException If the collision with the specified name was not found.
     */
    public Collision getCollision(String name)
    {
        if (collisions.containsKey(name))
        {
            return collisions.get(name);
        }
        throw new LionEngineException(ERROR_COLLISION_NOT_FOUND, name);
    }

    /**
     * Get all collisions.
     * 
     * @return The collisions list.
     */
    public Collection<Collision> getCollisions()
    {
        return collisions.values();
    }
}
