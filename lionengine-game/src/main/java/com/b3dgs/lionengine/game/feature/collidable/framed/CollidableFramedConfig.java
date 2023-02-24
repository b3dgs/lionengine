/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable.framed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    /** Collision attribute prefix. */
    public static final String ATT_PREFIX = "prefix";
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
    /** Frame separator. */
    private static final String FRAME_SEPARATOR = Constant.PERCENT;
    /** Default mirror. */
    private static final boolean DEFAULT_MIRROR = true;
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 64;

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

        return imports(configurer.getRoot());
    }

    /**
     * Create the collision data from node.
     * 
     * @param root The node reference (must not be <code>null</code>).
     * @return The collisions data.
     * @throws LionEngineException If unable to read node.
     */
    public static CollidableFramedConfig imports(XmlReader root)
    {
        Check.notNull(root);

        final Map<Integer, List<Collision>> collisions = new HashMap<>(0);

        if (root.hasNode(AnimationConfig.NODE_ANIMATIONS))
        {
            final Collection<XmlReader> children = root.getChild(AnimationConfig.NODE_ANIMATIONS)
                                                       .getChildren(AnimationConfig.NODE_ANIMATION);
            for (final XmlReader node : children)
            {
                final int start = node.getInteger(AnimationConfig.ANIMATION_START);
                for (final XmlReader framed : node.getChildren(NODE_COLLISION_FRAMED))
                {
                    importFrame(node, framed, start, collisions);
                }
            }
            children.clear();
        }

        return new CollidableFramedConfig(collisions);
    }

    /**
     * Create an XML node from a collision.
     * 
     * @param root The node root (must not be <code>null</code>).
     * @param collisions The collisions reference (must not be <code>null</code>).
     */
    public static void exports(Xml root, Map<Integer, List<Collision>> collisions)
    {
        Check.notNull(root);
        Check.notNull(collisions);

        for (final Entry<Integer, List<Collision>> entry : collisions.entrySet())
        {
            for (final Collision collision : entry.getValue())
            {
                final Xml node = root.createChild(NODE_COLLISION_FRAMED);
                node.writeInteger(ATT_NUMBER, entry.getKey().intValue());
                node.writeInteger(ATT_OFFSETX, collision.getOffsetX());
                node.writeInteger(ATT_OFFSETY, collision.getOffsetY());
                node.writeInteger(ATT_WIDTH, collision.getWidth());
                node.writeInteger(ATT_HEIGHT, collision.getHeight());
                node.writeBoolean(ATT_MIRROR, collision.hasMirror());
            }
        }
    }

    /**
     * Import frame collision data.
     * 
     * @param node The node root reference.
     * @param framed The framed node reference.
     * @param start The collision start number.
     * @param collisions The imported collisions.
     */
    private static void importFrame(XmlReader node,
                                    XmlReader framed,
                                    int start,
                                    Map<Integer, List<Collision>> collisions)
    {
        final String name = getFrameName(node, framed);
        if (framed.hasAttribute(ATT_NUMBER))
        {
            final int number = start + framed.getInteger(ATT_NUMBER);
            final Collision collision = createCollision(name, framed, number - start);
            final Integer key = Integer.valueOf(number - 1);
            collisions.computeIfAbsent(key, k -> new ArrayList<>()).add(collision);
        }
        else
        {
            final int end = node.getInteger(AnimationConfig.ANIMATION_END);
            for (int number = start; number <= end; number++)
            {
                final Collision collision = createCollision(name, framed, number - start + 1);
                final Integer key = Integer.valueOf(number);
                collisions.computeIfAbsent(key, k -> new ArrayList<>()).add(collision);
            }
        }
    }

    /**
     * Get the frame name.
     * 
     * @param node The node root reference.
     * @param framed The framed node reference.
     * @return The frame name.
     */
    private static String getFrameName(XmlReader node, XmlReader framed)
    {
        final String anim = node.getString(AnimationConfig.ANIMATION_NAME);
        final String prefix = framed.getStringDefault(Constant.EMPTY_STRING, ATT_PREFIX);
        if (prefix.isEmpty())
        {
            return anim;
        }
        return prefix + FRAME_SEPARATOR + anim;
    }

    /**
     * Create an collision from its node.
     * 
     * @param name The animation name.
     * @param node The collision node (must not be <code>null</code>).
     * @param number The frame number.
     * @return The collision instance.
     * @throws LionEngineException If error when reading collision data.
     */
    private static Collision createCollision(String name, XmlReader node, int number)
    {
        Check.notNull(node);

        final int offsetX = node.getInteger(ATT_OFFSETX);
        final int offsetY = node.getInteger(ATT_OFFSETY);
        final int width = node.getInteger(ATT_WIDTH);
        final int height = node.getInteger(ATT_HEIGHT);
        final boolean mirror = node.getBoolean(DEFAULT_MIRROR, ATT_MIRROR);

        return new Collision(name + FRAME_SEPARATOR + number, offsetX, offsetY, width, height, mirror);
    }

    /** Collisions map. */
    private final Map<Integer, List<Collision>> collisions;

    /**
     * Load collisions from configuration media.
     * 
     * @param collisions The collisions mapping (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public CollidableFramedConfig(Map<Integer, List<Collision>> collisions)
    {
        super();

        Check.notNull(collisions);

        this.collisions = new HashMap<>(collisions);
    }

    /**
     * Get a collision data from its frame number.
     * 
     * @param frame The collision frame.
     * @return The collisions reference.
     */
    public List<Collision> getCollision(Integer frame)
    {
        final List<Collision> found = collisions.get(frame);
        if (found != null)
        {
            return found;
        }
        return Collections.emptyList();
    }

    /**
     * Get all collisions.
     * 
     * @return The collisions.
     */
    public Collection<Collision> getCollisions()
    {
        final Collection<Collision> all = new ArrayList<>();
        for (final Collection<Collision> current : collisions.values())
        {
            all.addAll(current);
        }
        return all;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + collisions.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final CollidableFramedConfig other = (CollidableFramedConfig) object;
        return collisions.equals(other.collisions);
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName()).append(collisions).toString();
    }
}
