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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.collision.CollisionConstraint;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collisions constraint from a configurer node.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
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
     * Create the formula config.
     * 
     * @param node The node reference.
     * @return The collision constraint instance.
     * @throws LionEngineException If error when reading data.
     */
    public static CollisionConstraint create(XmlNode node) throws LionEngineException
    {
        final String top = node.hasAttribute(TOP) ? node.readString(TOP) : null;
        final String bottom = node.hasAttribute(BOTTOM) ? node.readString(BOTTOM) : null;
        final String left = node.hasAttribute(LEFT) ? node.readString(LEFT) : null;
        final String right = node.hasAttribute(RIGHT) ? node.readString(RIGHT) : null;

        return new CollisionConstraint(top, bottom, left, right);
    }
}
