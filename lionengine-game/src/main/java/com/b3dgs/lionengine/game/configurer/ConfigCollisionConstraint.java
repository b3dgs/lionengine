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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.collision.CollisionConstraint;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collisions constraint from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see CollisionConstraint
 */
public final class ConfigCollisionConstraint
{
    /** Constraint node. */
    public static final String CONSTRAINT = Configurer.PREFIX + "constraint";
    /** Constraint top attribute. */
    public static final String TOP = "top";
    /** Constraint bottom attribute. */
    public static final String BOTTOM = "bottom";
    /** Constraint left attribute. */
    public static final String LEFT = "left";
    /** Constraint right attribute. */
    public static final String RIGHT = "right";

    /**
     * Create the collision constraint data from node.
     * 
     * @param node The node reference.
     * @return The collision constraint data.
     * @throws LionEngineException If error when reading node.
     */
    public static CollisionConstraint create(XmlNode node) throws LionEngineException
    {
        final String top = node.hasAttribute(TOP) ? node.readString(TOP) : null;
        final String bottom = node.hasAttribute(BOTTOM) ? node.readString(BOTTOM) : null;
        final String left = node.hasAttribute(LEFT) ? node.readString(LEFT) : null;
        final String right = node.hasAttribute(RIGHT) ? node.readString(RIGHT) : null;

        return new CollisionConstraint(top, bottom, left, right);
    }

    /**
     * Export the collision constraint as a node.
     * 
     * @param constraint The collision constraint to export.
     * @return The node reference.
     */
    public static XmlNode export(CollisionConstraint constraint)
    {
        final XmlNode node = Stream.createXmlNode(CONSTRAINT);
        writeNodeIfExists(node, TOP, constraint.getTop());
        writeNodeIfExists(node, BOTTOM, constraint.getBottom());
        writeNodeIfExists(node, LEFT, constraint.getLeft());
        writeNodeIfExists(node, RIGHT, constraint.getRight());
        return node;
    }

    /**
     * Write node attribute value if not <code>null</code>.
     * 
     * @param node The node reference.
     * @param attribute The attribute name.
     * @param value The attribute value.
     */
    private static void writeNodeIfExists(XmlNode node, String attribute, String value)
    {
        if (value != null)
        {
            node.writeString(attribute, value);
        }
    }
}
