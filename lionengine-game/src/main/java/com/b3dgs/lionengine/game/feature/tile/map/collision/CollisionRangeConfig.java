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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;

/**
 * Represents the collision range.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see CollisionRange
 */
public final class CollisionRangeConfig
{
    /** The range node. */
    public static final String NODE_RANGE = Constant.XML_PREFIX + "range";
    /** Output axis attribute. */
    public static final String ATT_AXIS = "output";
    /** Input min X attribute. */
    public static final String ATT_MIN_X = "minX";
    /** Input max X attribute. */
    public static final String ATT_MAX_X = "maxX";
    /** Input min Y attribute. */
    public static final String ATT_MIN_Y = "minY";
    /** Input max Y attribute. */
    public static final String ATT_MAX_Y = "maxY";
    /** Axis type error. */
    static final String ERROR_TYPE = "Unknown axis: ";

    /**
     * Create the collision range data from a node.
     * 
     * @param node The node reference (must not be <code>null</code>).
     * @return The collision range data.
     * @throws LionEngineException If error when reading node.
     */
    public static CollisionRange imports(XmlReader node)
    {
        Check.notNull(node);

        final Axis axis = node.getEnum(Axis.class, ATT_AXIS);
        try
        {
            final int minX = node.getInteger(ATT_MIN_X);
            final int maxX = node.getInteger(ATT_MAX_X);
            final int minY = node.getInteger(ATT_MIN_Y);
            final int maxY = node.getInteger(ATT_MAX_Y);

            return new CollisionRange(axis, minX, maxX, minY, maxY);
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(exception, ERROR_TYPE + axis);
        }
    }

    /**
     * Export the collision range as a node.
     * 
     * @param root The node root (must not be <code>null</code>).
     * @param range The collision range to export (must not be <code>null</code>).
     * @throws LionEngineException If error on writing.
     */
    public static void exports(Xml root, CollisionRange range)
    {
        Check.notNull(root);
        Check.notNull(range);

        final Xml node = root.createChild(NODE_RANGE);
        node.writeEnum(ATT_AXIS, range.getOutput());
        node.writeInteger(ATT_MIN_X, range.getMinX());
        node.writeInteger(ATT_MIN_Y, range.getMinY());
        node.writeInteger(ATT_MAX_X, range.getMaxX());
        node.writeInteger(ATT_MAX_Y, range.getMaxY());
    }

    /**
     * Disabled constructor.
     */
    private CollisionRangeConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
