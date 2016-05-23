/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the force data from a configurer.
 * 
 * @see Force
 */
public final class ForceConfig
{
    /** Force node name. */
    public static final String NODE_FORCE = Constant.XML_PREFIX + "force";
    /** Force horizontal node name. */
    public static final String ATT_VX = "vx";
    /** Force vertical node name. */
    public static final String ATT_VY = "vy";
    /** Force velocity node name. */
    public static final String ATT_VELOCITY = "velocity";
    /** Force sensibility node name. */
    public static final String ATT_SENSIBILITY = "sensibility";

    /**
     * Create the force data from node.
     * 
     * @param root The root reference.
     * @return The force data.
     * @throws LionEngineException If unable to read node.
     */
    public static Force imports(XmlNode root)
    {
        final XmlNode node = root.getChild(NODE_FORCE);

        final Force force = new Force(node.readDouble(ATT_VX), node.readDouble(ATT_VY));
        force.setVelocity(node.readDouble(ATT_VELOCITY));
        force.setSensibility(node.readDouble(ATT_SENSIBILITY));

        return force;
    }

    /**
     * Export the force node from data.
     * 
     * @param force The force reference.
     * @return The force data.
     * @throws LionEngineException If unable to read node.
     */
    public static XmlNode exports(Force force)
    {
        final XmlNode node = Xml.create(NODE_FORCE);
        node.writeDouble(ATT_VX, force.getDirectionHorizontal());
        node.writeDouble(ATT_VY, force.getDirectionVertical());
        node.writeDouble(ATT_VELOCITY, force.getVelocity());
        node.writeDouble(ATT_SENSIBILITY, force.getSensibility());

        return node;
    }

    /**
     * Private constructor.
     */
    private ForceConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
