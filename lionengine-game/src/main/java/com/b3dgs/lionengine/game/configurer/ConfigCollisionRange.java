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
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.CollisionRange;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collision range from a configurer node.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see CollisionRange
 */
public final class ConfigCollisionRange
{
    /** The range node. */
    public static final String RANGE = Configurer.PREFIX + "range";
    /** Output axis attribute. */
    public static final String AXIS = "output";
    /** Input min X attribute. */
    public static final String MIN_X = "minX";
    /** Input max X attribute. */
    public static final String MAX_X = "maxX";
    /** Input min Y attribute. */
    public static final String MIN_Y = "minY";
    /** Input max Y attribute. */
    public static final String MAX_Y = "maxY";
    /** Axis type error. */
    private static final String ERROR_TYPE = "Unknown axis: ";

    /**
     * Create the collision range data from a node.
     * 
     * @param node The node reference.
     * @return The collision range data.
     * @throws LionEngineException If error when reading node.
     */
    public static CollisionRange create(XmlNode node) throws LionEngineException
    {
        final String axisName = node.readString(AXIS);
        try
        {
            final Axis axis = Axis.valueOf(axisName);
            final int minX = node.readInteger(MIN_X);
            final int maxX = node.readInteger(MAX_X);
            final int minY = node.readInteger(MIN_Y);
            final int maxY = node.readInteger(MAX_Y);

            return new CollisionRange(axis, minX, maxX, minY, maxY);
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(ERROR_TYPE, axisName);
        }
    }

    /**
     * Export the collision range as a node.
     * 
     * @param range The collision range to export.
     * @return The node reference.
     * @throws LionEngineException If error on writing.
     */
    public static XmlNode export(CollisionRange range) throws LionEngineException
    {
        final XmlNode node = Stream.createXmlNode(RANGE);

        node.writeString(AXIS, range.getOutput().name());
        node.writeInteger(MIN_X, range.getMinX());
        node.writeInteger(MIN_Y, range.getMinY());
        node.writeInteger(MAX_X, range.getMaxX());
        node.writeInteger(MAX_Y, range.getMaxY());

        return node;
    }

    /**
     * Disabled constructor.
     */
    private ConfigCollisionRange()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
