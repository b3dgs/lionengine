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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;

/**
 * Represents the force data.
 * <p>
 * This class is Thread-Safe.
 * </p>
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
     * Create the force data from setup.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The force data.
     * @throws LionEngineException If unable to read node.
     */
    public static Force imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Create the force data from node.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The force data.
     * @throws LionEngineException If unable to read node.
     */
    public static Force imports(Xml root)
    {
        Check.notNull(root);

        final Xml node = root.getChild(NODE_FORCE);

        final Force force = new Force(node.readDouble(ATT_VX), node.readDouble(ATT_VY));
        force.setVelocity(node.readDouble(0.0, ATT_VELOCITY));
        force.setSensibility(node.readDouble(0.0, ATT_SENSIBILITY));

        return force;
    }

    /**
     * Export the force node from data.
     * 
     * @param force The force reference (must not be <code>null</code>).
     * @return The force data.
     * @throws LionEngineException If unable to read node.
     */
    public static Xml exports(Force force)
    {
        Check.notNull(force);

        final Xml node = new Xml(NODE_FORCE);
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
