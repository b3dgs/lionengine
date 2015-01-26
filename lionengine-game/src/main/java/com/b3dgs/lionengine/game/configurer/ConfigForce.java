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
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the force data from a configurer node.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurer
 * @see XmlNode
 */
public class ConfigForce
{
    /** Frames node name. */
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
     * Create the frames node.
     * 
     * @param root The root reference.
     * @return The frames node value.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public static ConfigForce create(XmlNode root) throws LionEngineException
    {
        final XmlNode node = root.getChild(FORCE);
        return new ConfigForce(node.readDouble(ConfigForce.FORCE_VX), node.readDouble(ConfigForce.FORCE_VY),
                node.readDouble(ConfigForce.FORCE_VELOCITY), node.readDouble(ConfigForce.FORCE_SENSIBILITY));
    }

    /** The horizontal force. */
    private final double vx;
    /** The vertical force. */
    private final double vy;
    /** The velocity. */
    private final double velocity;
    /** The sensibility. */
    private final double sensibility;

    /**
     * Constructor.
     * 
     * @param vx The horizontal force.
     * @param vy The vertical force.
     * @param velocity The velocity value.
     * @param sensibility The sensibility value.
     */
    public ConfigForce(double vx, double vy, double velocity, double sensibility)
    {
        this.vx = vx;
        this.vy = vy;
        this.velocity = velocity;
        this.sensibility = sensibility;
    }

    /**
     * Get the configured force.
     * 
     * @return The configured force.
     */
    public Force getForce()
    {
        final Force force = new Force(vx, vy);
        force.setVelocity(velocity);
        force.setSensibility(sensibility);
        return force;
    }
}
