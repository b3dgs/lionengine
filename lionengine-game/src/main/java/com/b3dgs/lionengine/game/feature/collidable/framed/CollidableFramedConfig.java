/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable.framed;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.collidable.Collision;

/**
 * Represents the collisions framed data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see CollidableFramed
 */
public final class CollidableFramedConfig
{
    /** Collision framed node name. */
    public static final String NODE_COLLISION_FRAMED = Constant.XML_PREFIX + "collisionFramed";
    /** Collision attribute number. */
    public static final String ATT_NUMBER = "number";
    /** Collision attribute offset x. */
    public static final String ATT_OFFSETX = "x";
    /** Collision attribute offset y. */
    public static final String ATT_OFFSETY = "y";
    /** Collision attribute width. */
    public static final String ATT_WIDTH = "width";
    /** Collision attribute height. */
    public static final String ATT_HEIGHT = "height";
    /** Collision attribute mirror. */
    public static final String ATT_MIRROR = "mirror";
    /** Error collision framed not found. */
    static final String ERROR_COLLISION_NOT_FOUND = "Collision framed not found: ";
    /** Default mirror. */
    private static final boolean DEFAULT_MIRROR = true;

    /**
     * Create the collision data from node.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The collisions data.
     * @throws LionEngineException If unable to read node.
     */
    public static CollidableFramedConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        final Map<Integer, Collision> collisions = new HashMap<>(0);

        for (final Xml node : configurer.getRoot().getChildren(AnimationConfig.ANIMATION))
        {
            final int start = node.readInteger(AnimationConfig.ANIMATION_START);
            for (final Xml framed : node.getChildren(NODE_COLLISION_FRAMED))
            {
                final int number = framed.readInteger(ATT_NUMBER) - 1;
                final Collision collision = createCollision(node.readString(AnimationConfig.ANIMATION_NAME), framed);
                collisions.put(Integer.valueOf(start + number), collision);
            }
        }

        return new CollidableFramedConfig(collisions);
    }

    /**
     * Create an collision from its node.
     * 
     * @param name The animation name.
     * @param node The collision node (must not be <code>null</code>).
     * @return The collision instance.
     * @throws LionEngineException If error when reading collision data.
     */
    public static Collision createCollision(String name, XmlReader node)
    {
        Check.notNull(node);

        final int number = node.readInteger(ATT_NUMBER);
        final int offsetX = node.readInteger(ATT_OFFSETX);
        final int offsetY = node.readInteger(ATT_OFFSETY);
        final int width = node.readInteger(ATT_WIDTH);
        final int height = node.readInteger(ATT_HEIGHT);
        final boolean mirror = node.readBoolean(DEFAULT_MIRROR, ATT_MIRROR);

        return new Collision(name + Constant.PERCENT + number, offsetX, offsetY, width, height, mirror);
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

        final Xml node = root.createChild(NODE_COLLISION_FRAMED);
        node.writeString(ATT_NUMBER, collision.getName());
        node.writeInteger(ATT_OFFSETX, collision.getOffsetX());
        node.writeInteger(ATT_OFFSETY, collision.getOffsetY());
        node.writeInteger(ATT_WIDTH, collision.getWidth());
        node.writeInteger(ATT_HEIGHT, collision.getHeight());
        node.writeBoolean(ATT_MIRROR, collision.hasMirror());
    }

    /** Collisions map. */
    private final Map<Integer, Collision> collisions;

    /**
     * Load collisions from configuration media.
     * 
     * @param collisions The collisions mapping (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public CollidableFramedConfig(Map<Integer, Collision> collisions)
    {
        super();

        Check.notNull(collisions);

        this.collisions = new HashMap<>(collisions);
    }

    /**
     * Get a collision data from its frame number.
     * 
     * @param frame The collision frame.
     * @return The collision reference, <code>null</code> if none.
     */
    public Collision getCollision(Integer frame)
    {
        return collisions.get(frame);
    }

    /**
     * Get all collisions.
     * 
     * @return The collisions.
     */
    public Collection<Collision> getCollisions()
    {
        return collisions.values();
    }
}
