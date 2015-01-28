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
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.map.CollisionRange;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collision range from a configurer node.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurer
 */
public class ConfigCollisionRange
{
    /** Input axis attribute. */
    public static final String AXIS = "axis";
    /** Input min attribute. */
    public static final String MIN = "min";
    /** Input max attribute. */
    public static final String MAX = "max";

    /**
     * Create the collision input.
     * 
     * @param node The node reference.
     * @return The collision input instance.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public static CollisionRange create(XmlNode node) throws LionEngineException
    {
        return new CollisionRange(Axis.valueOf(node.readString(AXIS)), node.readInteger(MIN), node.readInteger(MAX));
    }
}
