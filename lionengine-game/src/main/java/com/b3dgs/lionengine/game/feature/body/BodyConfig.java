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
package com.b3dgs.lionengine.game.feature.body;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the body data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class BodyConfig
{
    /** Body node. */
    public static final String NODE_BODY = Constant.XML_PREFIX + "body";
    /** Body group attribute name. */
    public static final String ATT_GRAVITY = "gravity";
    /** Body accepted groups attribute name. */
    public static final String ATT_GRAVITY_MAX = "gravityMax";
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 40;

    /**
     * Create the collidable data from node.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The config loaded.
     * @throws LionEngineException If unable to read node.
     */
    public static BodyConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        if (configurer.hasNode(NODE_BODY))
        {
            final double gravity = configurer.getDouble(ATT_GRAVITY, NODE_BODY);
            final double gravityMax = configurer.getDouble(ATT_GRAVITY_MAX, NODE_BODY);

            return new BodyConfig(gravity, gravityMax);
        }
        return new BodyConfig(0.0, 0.0);
    }

    /**
     * Create an XML node from a body.
     * 
     * @param root The node root (must not be <code>null</code>).
     * @param body The body reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public static void exports(Xml root, Body body)
    {
        Check.notNull(root);
        Check.notNull(body);

        final Xml node = root.createChild(NODE_BODY);
        node.writeDouble(ATT_GRAVITY, body.getGravity());
        node.writeDouble(ATT_GRAVITY_MAX, body.getGravityMax());
    }

    /** The gravity. */
    private final double gravity;
    /** The gravity max. */
    private final double gravityMax;

    /**
     * Private constructor.
     * 
     * @param gravity The gravity.
     * @param gravityMax The gravity max.
     */
    public BodyConfig(double gravity, double gravityMax)
    {
        super();

        this.gravity = gravity;
        this.gravityMax = gravityMax;
    }

    /**
     * Get the defined gravity.
     * 
     * @return The defined gravity.
     */
    public double getGravity()
    {
        return gravity;
    }

    /**
     * Get the gravity max.
     * 
     * @return The gravity max.
     */
    public double getGravityMax()
    {
        return gravityMax;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(gravity);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(gravityMax);
        result = prime * result + (int) (temp ^ temp >>> 32);
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
        final BodyConfig other = (BodyConfig) object;
        return Double.compare(getGravity(), other.getGravity()) == 0
               && Double.compare(getGravityMax(), other.getGravityMax()) == 0;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [gravity=")
                                            .append(gravity)
                                            .append(", gravityMax=")
                                            .append(gravityMax)
                                            .append("]")
                                            .toString();
    }
}
