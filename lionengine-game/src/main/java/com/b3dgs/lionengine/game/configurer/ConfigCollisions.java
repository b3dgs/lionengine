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
package com.b3dgs.lionengine.game.configurer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Collision;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collisions data from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Collision
 */
public class ConfigCollisions
{
    /** Collision node name. */
    public static final String COLLISION = Configurer.PREFIX + "collision";
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
    public static ConfigCollisions create(Configurer configurer) throws LionEngineException
    {
        final Map<String, Collision> collisions = new HashMap<>(0);
        for (final XmlNode node : configurer.getRoot().getChildren(ConfigCollisions.COLLISION))
        {
            final String coll = node.readString(ConfigCollisions.COLLISION_NAME);
            final Collision collision = ConfigCollisions.createCollision(node);
            collisions.put(coll, collision);
        }
        return new ConfigCollisions(collisions);
    }

    /**
     * Create an collision from its node.
     * 
     * @param node The collision node.
     * @return The collision instance.
     * @throws LionEngineException If error when reading collision data.
     */
    public static Collision createCollision(XmlNode node) throws LionEngineException
    {
        final int offsetX = node.readInteger(ConfigCollisions.COLLISION_OFFSETX);
        final int offsetY = node.readInteger(ConfigCollisions.COLLISION_OFFSETY);
        final int width = node.readInteger(ConfigCollisions.COLLISION_WIDTH);
        final int height = node.readInteger(ConfigCollisions.COLLISION_HEIGHT);
        final boolean mirror = node.readBoolean(ConfigCollisions.COLLISION_MIRROR);
        return new Collision(offsetX, offsetY, width, height, mirror);
    }

    /**
     * Create an XML node from a collision.
     * 
     * @param name The collision name.
     * @param collision The collision reference.
     * @return The collision node.
     */
    public static XmlNode createNode(String name, Collision collision)
    {
        final XmlNode node = Stream.createXmlNode(ConfigCollisions.COLLISION);
        node.writeString(ConfigCollisions.COLLISION_NAME, name);
        node.writeInteger(ConfigCollisions.COLLISION_OFFSETX, collision.getOffsetX());
        node.writeInteger(ConfigCollisions.COLLISION_OFFSETY, collision.getOffsetY());
        node.writeInteger(ConfigCollisions.COLLISION_WIDTH, collision.getWidth());
        node.writeInteger(ConfigCollisions.COLLISION_HEIGHT, collision.getHeight());
        node.writeBoolean(ConfigCollisions.COLLISION_MIRROR, collision.hasMirror());
        return node;
    }

    /** Collisions map. */
    private final Map<String, Collision> collisions;

    /**
     * Load collisions from configuration media.
     * 
     * @param collisions The collisions mapping.
     * @throws LionEngineException If error when opening the media.
     */
    private ConfigCollisions(Map<String, Collision> collisions) throws LionEngineException
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
    public Collision getCollision(String name) throws LionEngineException
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
     * @return The unmodifiable collisions map, where key is the collision name.
     */
    public Map<String, Collision> getCollisions()
    {
        return Collections.unmodifiableMap(collisions);
    }
}
