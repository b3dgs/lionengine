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
package com.b3dgs.lionengine.game.pathfinding;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.object.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the pathfindable data from a configurer.
 */
public final class PathfindableConfig
{
    /** Pathfindable node name. */
    public static final String PATHFINDABLE = Constant.XML_PREFIX + "pathfindable";
    /** Category attribute. */
    public static final String CATEGORY = "category";
    /** Cost attribute. */
    public static final String COST = "cost";
    /** Block attribute. */
    public static final String BLOCK = "block";
    /** Allowed movement node. */
    public static final String MOVEMENT = Constant.XML_PREFIX + "movement";

    /**
     * Create the pathfindable data from node.
     * 
     * @param configurer The configurer reference.
     * @return The pathfindable data.
     * @throws LionEngineException If unable to read node.
     */
    public static Map<String, PathData> create(Configurer configurer)
    {
        final Map<String, PathData> categories = new HashMap<String, PathData>(0);
        for (final XmlNode node : configurer.getRoot().getChildren(PATHFINDABLE))
        {
            final PathData data = createPathData(node);
            categories.put(data.getName(), data);
        }
        return categories;
    }

    /**
     * Create a path data from its node.
     * 
     * @param node The pathfinding node.
     * @return The path data instance.
     * @throws LionEngineException If error when reading path data.
     */
    public static PathData createPathData(XmlNode node)
    {
        final String category = node.readString(CATEGORY);
        final double cost;
        if (node.hasAttribute(COST))
        {
            cost = node.readDouble(COST);
        }
        else
        {
            cost = 0.0;
        }
        final boolean blocking = node.readBoolean(BLOCK);
        final Collection<MovementTile> movements = getAllowedMovements(node);

        return new PathData(category, cost, blocking, movements);
    }

    /**
     * Read the allowed movements.
     * 
     * @param node The root node.
     * @return The allowed movements.
     * @throws LionEngineException If malformed movement name.
     */
    private static Collection<MovementTile> getAllowedMovements(XmlNode node)
    {
        if (!node.hasChild(MOVEMENT))
        {
            return Collections.emptySet();
        }
        final Collection<MovementTile> movements = new HashSet<MovementTile>();
        for (final XmlNode movementNode : node.getChildren(MOVEMENT))
        {
            try
            {
                movements.add(MovementTile.valueOf(movementNode.getText()));
            }
            catch (final IllegalArgumentException exception)
            {
                throw new LionEngineException(exception);
            }
        }
        return EnumSet.copyOf(movements);
    }

    /**
     * Disabled constructor.
     */
    private PathfindableConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
