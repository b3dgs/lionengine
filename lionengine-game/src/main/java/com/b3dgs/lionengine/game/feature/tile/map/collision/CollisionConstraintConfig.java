/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import java.util.Collection;
import java.util.Map.Entry;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Orientation;

/**
 * Represents the collisions constraint.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see CollisionConstraint
 */
public final class CollisionConstraintConfig
{
    /** Constraint node. */
    public static final String NODE_CONSTRAINT = Constant.XML_PREFIX + "constraint";
    /** Orientation attribute. */
    public static final String ATT_ORIENTATION = "orientation";
    /** Group name attribute. */
    public static final String ATT_GROUP = "group";

    /**
     * Create the collision constraint data from node.
     * 
     * @param node The node reference (must not be <code>null</code>).
     * @return The collision constraint data.
     * @throws LionEngineException If error when reading node.
     */
    public static CollisionConstraint imports(Xml node)
    {
        Check.notNull(node);

        final CollisionConstraint constraint = new CollisionConstraint();

        if (node.hasChild(NODE_CONSTRAINT))
        {
            final Collection<Xml> children = node.getChildren(NODE_CONSTRAINT);
            for (final Xml current : children)
            {
                final Orientation orientation = Orientation.valueOf(current.readString(ATT_ORIENTATION));
                final String group = current.readString(ATT_GROUP);
                constraint.add(orientation, group);
            }
            children.clear();
        }

        return constraint;
    }

    /**
     * Export the collision constraint as a node.
     * 
     * @param root The node root (must not be <code>null</code>).
     * @param constraint The collision constraint to export (must not be <code>null</code>).
     * @throws LionEngineException If error on writing.
     */
    public static void exports(Xml root, CollisionConstraint constraint)
    {
        Check.notNull(root);
        Check.notNull(constraint);

        for (final Entry<Orientation, Collection<String>> entry : constraint.getConstraints().entrySet())
        {
            final Orientation orientation = entry.getKey();

            for (final String group : entry.getValue())
            {
                final Xml current = root.createChild(NODE_CONSTRAINT);
                current.writeString(ATT_ORIENTATION, orientation.name());
                current.writeString(ATT_GROUP, group);
            }
        }
    }

    /**
     * Disabled constructor.
     */
    private CollisionConstraintConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
