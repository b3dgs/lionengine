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

import java.util.Collection;
import java.util.Map.Entry;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.collision.CollisionConstraint;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collisions constraint from a configurer.
 * 
 * @see CollisionConstraint
 */
public final class ConfigCollisionConstraint
{
    /** Constraint node. */
    public static final String CONSTRAINT = Configurer.PREFIX + "constraint";
    /** Orientation attribute. */
    public static final String ORIENTATION = "orientation";
    /** Group name attribute. */
    public static final String GROUP = "group";

    /**
     * Create the collision constraint data from node.
     * 
     * @param node The node reference.
     * @return The collision constraint data.
     * @throws LionEngineException If error when reading node.
     */
    public static CollisionConstraint create(XmlNode node)
    {
        final CollisionConstraint constraint = new CollisionConstraint();
        if (node.hasChild(CONSTRAINT))
        {
            for (final XmlNode current : node.getChildren(CONSTRAINT))
            {
                final Orientation orientation = Orientation.valueOf(current.readString(ORIENTATION));
                final String group = current.readString(GROUP);
                constraint.add(orientation, group);
            }
        }
        return constraint;
    }

    /**
     * Export the collision constraint as a node.
     * 
     * @param root The node root.
     * @param constraint The collision constraint to export.
     * @throws LionEngineException If error on writing.
     */
    public static void export(XmlNode root, CollisionConstraint constraint)
    {
        for (final Entry<Orientation, Collection<String>> entry : constraint.getConstraints().entrySet())
        {
            final Orientation orientation = entry.getKey();
            for (final String group : entry.getValue())
            {
                final XmlNode current = root.createChild(CONSTRAINT);
                current.writeString(ORIENTATION, orientation.name());
                current.writeString(GROUP, group);
            }
        }
    }

    /**
     * Disabled constructor.
     */
    private ConfigCollisionConstraint()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
