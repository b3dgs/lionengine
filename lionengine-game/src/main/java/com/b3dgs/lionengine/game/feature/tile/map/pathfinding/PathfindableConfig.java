/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.io.Xml;

/**
 * Represents the pathfindable data from a configurer.
 */
public final class PathfindableConfig
{
    /** Pathfindable node name. */
    public static final String PATHFINDABLE = Constant.XML_PREFIX + "pathfindable";
    /** Path data node name. */
    public static final String PATH = Constant.XML_PREFIX + "path";
    /** Category attribute. */
    public static final String CATEGORY = "category";
    /** Cost attribute. */
    public static final String COST = "cost";
    /** Block attribute. */
    public static final String BLOCK = "block";
    /** Allowed movements node. */
    public static final String MOVEMENT = Constant.XML_PREFIX + "movement";

    /**
     * Import the pathfindable data from node.
     * 
     * @param configurer The configurer reference.
     * @return The pathfindable data.
     * @throws LionEngineException If unable to read node.
     */
    public static Map<String, PathData> imports(Configurer configurer)
    {
        final Xml root = configurer.getRoot();
        if (!root.hasChild(PATHFINDABLE))
        {
            return Collections.emptyMap();
        }
        final Map<String, PathData> categories = new HashMap<String, PathData>(0);
        final Xml nodePathfindable = root.getChild(PATHFINDABLE);
        for (final Xml nodePath : nodePathfindable.getChildren(PATH))
        {
            final PathData data = importPathData(nodePath);
            categories.put(data.getName(), data);
        }
        return categories;
    }

    /**
     * Export the pathfindable data to node.
     * 
     * @param pathData The pathfindable data.
     * @return The path data node.
     * @throws LionEngineException If unable to read node.
     */
    public static Xml exports(Map<String, PathData> pathData)
    {
        final Xml node = new Xml(PATHFINDABLE);
        for (final PathData data : pathData.values())
        {
            node.add(exportPathData(data));
        }
        return node;
    }

    /**
     * Create a path data from its node.
     * 
     * @param node The pathfinding node.
     * @return The path data instance.
     * @throws LionEngineException If error when reading path data.
     */
    public static PathData importPathData(Xml node)
    {
        final String category = node.readString(CATEGORY);
        final double cost = node.readDouble(0.0, COST);
        final boolean blocking = node.readBoolean(BLOCK);
        final Collection<MovementTile> movements = importAllowedMovements(node);

        return new PathData(category, cost, blocking, movements);
    }

    /**
     * Create a path data from its node.
     * 
     * @param data The path data.
     * @return The path data node.
     */
    public static Xml exportPathData(PathData data)
    {
        final Xml node = new Xml(PATH);
        node.writeString(CATEGORY, data.getName());
        node.writeDouble(COST, data.getCost());
        node.writeBoolean(BLOCK, data.isBlocking());
        exportAllowedMovements(node, data.getAllowedMovements());

        return node;
    }

    /**
     * Import the allowed movements.
     * 
     * @param node The root node.
     * @return The allowed movements.
     * @throws LionEngineException If malformed movement name.
     */
    private static Collection<MovementTile> importAllowedMovements(Xml node)
    {
        if (!node.hasChild(MOVEMENT))
        {
            return Collections.emptySet();
        }
        final Collection<MovementTile> movements = EnumSet.noneOf(MovementTile.class);
        for (final Xml movementNode : node.getChildren(MOVEMENT))
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
        return movements;
    }

    /**
     * Export the allowed movements.
     * 
     * @param root The root node.
     * @param movements The movements node.
     */
    private static void exportAllowedMovements(Xml root, Collection<MovementTile> movements)
    {
        for (final MovementTile movement : movements)
        {
            final Xml node = root.createChild(MOVEMENT);
            node.setText(movement.name());
        }
    }

    /**
     * Disabled constructor.
     */
    private PathfindableConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
