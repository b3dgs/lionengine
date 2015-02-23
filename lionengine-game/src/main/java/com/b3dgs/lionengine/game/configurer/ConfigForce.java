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
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the force data from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Force
 */
public class ConfigForce
{
    /** Force node name. */
    public static final String FORCE = Configurer.PREFIX + "force";
    /** Force horizontal node name. */
    public static final String FORCE_VX = "vx";
    /** Force vertical node name. */
    public static final String FORCE_VY = "vy";
    /** Force velocity node name. */
    public static final String FORCE_VELOCITY = "velocity";
    /** Force sensibility node name. */
    public static final String FORCE_SENSIBILITY = "sensibility";

    /**
     * Create the force data from node.
     * 
     * @param root The root reference.
     * @return The force data.
     * @throws LionEngineException If unable to read node.
     */
    public static Force create(XmlNode root) throws LionEngineException
    {
        final XmlNode node = root.getChild(FORCE);
        final Force force = new Force(node.readDouble(FORCE_VX), node.readDouble(FORCE_VY));
        force.setVelocity(node.readDouble(FORCE_VELOCITY));
        force.setSensibility(node.readDouble(FORCE_SENSIBILITY));
        return force;
    }

    /**
     * Constructor.
     */
    private ConfigForce()
    {
        // Private constructor.
    }
}
