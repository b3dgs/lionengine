/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.collidable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the collisions data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see Collision
 */
public final class CollisionConfig
{
    /** Collisions node name. */
    public static final String NODE_COLLISIONS = Constant.XML_PREFIX + "collisions";
    /** Collision node name. */
    public static final String NODE_COLLISION = Constant.XML_PREFIX + "collision";
    /** Collision attribute name. */
    public static final String ATT_NAME = "name";
    /** Collision attribute offset x. */
    public static final String ATT_OFFSETX = "offsetX";
    /** Collision attribute offset y. */
    public static final String ATT_OFFSETY = "offsetY";
    /** Collision attribute width. */
    public static final String ATT_WIDTH = "width";
    /** Collision attribute height. */
    public static final String ATT_HEIGHT = "height";
    /** Collision attribute mirror. */
    public static final String ATT_MIRROR = "mirror";
    /** Error collision not found. */
    static final String ERROR_COLLISION_NOT_FOUND = "Collision not found: ";
    /** Default mirror. */
    private static final boolean DEFAULT_MIRROR = false;

    /**
     * Create the collision data from node.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The collisions data.
     * @throws LionEngineException If unable to read node.
     */
    public static CollisionConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Create the collision data from node.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The collisions data.
     * @throws LionEngineException If unable to read node.
     */
    public static CollisionConfig imports(Xml root)
    {
        Check.notNull(root);

        final Map<String, Collision> collisions = new HashMap<>(0);

        final Collection<Xml> children;
        if (root.hasChild(NODE_COLLISIONS))
        {
            children = root.getChild(NODE_COLLISIONS).getChildren(NODE_COLLISION);
        }
        else
        {
            children = Collections.emptyList();
        }
        for (final Xml node : children)
        {
            final String coll = node.readString(ATT_NAME);
            final Collision collision = createCollision(node);
            collisions.put(coll, collision);
        }
        children.clear();

        return new CollisionConfig(collisions);
    }

    /**
     * Create an collision from its node.
     * 
     * @param node The collision node (must not be <code>null</code>).
     * @return The collision instance.
     * @throws LionEngineException If error when reading collision data.
     */
    public static Collision createCollision(XmlReader node)
    {
        Check.notNull(node);

        final String name = node.readString(ATT_NAME);
        final int offsetX = node.readInteger(ATT_OFFSETX);
        final int offsetY = node.readInteger(ATT_OFFSETY);
        final int width = node.readInteger(ATT_WIDTH);
        final int height = node.readInteger(ATT_HEIGHT);
        final boolean mirror = node.readBoolean(DEFAULT_MIRROR, ATT_MIRROR);

        return new Collision(name, offsetX, offsetY, width, height, mirror);
    }

    /**
     * Create an XML node from a collision.
     * 
     * @param root The node root (must not be <code>null</code>).
     * @param collision The collision reference (must not be <code>null</code>).
     */
    public static void exports(Xml root, Collision collision)
    {
        Check.notNull(root);
        Check.notNull(collision);

        final Xml collisions;
        if (root.hasChild(NODE_COLLISIONS))
        {
            collisions = root.getChild(NODE_COLLISIONS);
        }
        else
        {
            collisions = root.createChild(NODE_COLLISIONS);
        }

        final Xml node = collisions.createChild(NODE_COLLISION);
        node.writeString(ATT_NAME, collision.getName());
        node.writeInteger(ATT_OFFSETX, collision.getOffsetX());
        node.writeInteger(ATT_OFFSETY, collision.getOffsetY());
        node.writeInteger(ATT_WIDTH, collision.getWidth());
        node.writeInteger(ATT_HEIGHT, collision.getHeight());
        node.writeBoolean(ATT_MIRROR, collision.hasMirror());
    }

    /** Collisions map. */
    private final Map<String, Collision> collisions;

    /**
     * Load collisions from configuration media.
     * 
     * @param collisions The collisions mapping (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public CollisionConfig(Map<String, Collision> collisions)
    {
        super();

        Check.notNull(collisions);

        this.collisions = new HashMap<>(collisions);
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
        throw new LionEngineException(ERROR_COLLISION_NOT_FOUND + name);
    }

    /**
     * Get all collisions as read only.
     * 
     * @return The collisions list.
     */
    public Collection<Collision> getCollisions()
    {
        return Collections.unmodifiableCollection(collisions.values());
    }
}
