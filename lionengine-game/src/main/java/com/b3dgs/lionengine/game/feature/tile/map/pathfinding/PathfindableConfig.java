/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the pathfindable data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class PathfindableConfig
{
    /** Pathfindable node name. */
    public static final String NODE_PATHFINDABLE = Constant.XML_PREFIX + "pathfindable";
    /** Path data node name. */
    public static final String NODE_PATH = Constant.XML_PREFIX + "path";
    /** Category attribute. */
    public static final String ATT_CATEGORY = "category";
    /** Cost attribute. */
    public static final String ATT_COST = "cost";
    /** Block attribute. */
    public static final String ATT_BLOCK = "block";
    /** Allowed movements node. */
    public static final String NODE_MOVEMENT = Constant.XML_PREFIX + "movement";

    /**
     * Import the pathfindable data from node.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The pathfindable data.
     * @throws LionEngineException If unable to read node.
     */
    public static Map<String, PathData> imports(Configurer configurer)
    {
        Check.notNull(configurer);

        final XmlReader root = configurer.getRoot();
        if (!root.hasNode(NODE_PATHFINDABLE))
        {
            return Collections.emptyMap();
        }

        final Map<String, PathData> categories = new HashMap<>(0);
        final XmlReader nodePathfindable = root.getChild(NODE_PATHFINDABLE);

        final Collection<XmlReader> children = nodePathfindable.getChildren(NODE_PATH);
        for (final XmlReader nodePath : children)
        {
            final PathData data = importPathData(nodePath);
            categories.put(data.getName(), data);
        }
        children.clear();

        return categories;
    }

    /**
     * Export the pathfindable data to node.
     * 
     * @param pathData The pathfindable data (must not be <code>null</code>).
     * @return The path data node.
     * @throws LionEngineException If unable to read node.
     */
    public static Xml exports(Map<String, PathData> pathData)
    {
        Check.notNull(pathData);

        final Xml node = new Xml(NODE_PATHFINDABLE);
        for (final PathData data : pathData.values())
        {
            node.add(exportPathData(data));
        }

        return node;
    }

    /**
     * Create a path data from its node.
     * 
     * @param node The pathfinding node (must not be <code>null</code>).
     * @return The path data instance.
     * @throws LionEngineException If error when reading path data.
     */
    public static PathData importPathData(XmlReader node)
    {
        Check.notNull(node);

        final String category = node.getString(ATT_CATEGORY);
        final double cost = node.getDouble(0.0, ATT_COST);
        final boolean blocking = node.getBoolean(ATT_BLOCK);
        final Collection<MovementTile> movements = importAllowedMovements(node);

        return new PathData(category, cost, blocking, movements);
    }

    /**
     * Create a path data from its node.
     * 
     * @param data The path data (must not be <code>null</code>).
     * @return The path data node.
     */
    public static Xml exportPathData(PathData data)
    {
        Check.notNull(data);

        final Xml node = new Xml(NODE_PATH);
        node.writeString(ATT_CATEGORY, data.getName());
        node.writeDouble(ATT_COST, data.getCost());
        node.writeBoolean(ATT_BLOCK, data.isBlocking());
        exportAllowedMovements(node, data.getAllowedMovements());

        return node;
    }

    /**
     * Import the allowed movements.
     * 
     * @param node The root node (must not be <code>null</code>).
     * @return The allowed movements.
     * @throws LionEngineException If malformed movement name.
     */
    private static Collection<MovementTile> importAllowedMovements(XmlReader node)
    {
        if (!node.hasNode(NODE_MOVEMENT))
        {
            return Collections.emptySet();
        }

        final Collection<MovementTile> movements = EnumSet.noneOf(MovementTile.class);

        final Collection<XmlReader> children = node.getChildren(NODE_MOVEMENT);
        for (final XmlReader movementNode : children)
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
        children.clear();

        return movements;
    }

    /**
     * Export the allowed movements.
     * 
     * @param root The root node (must not be <code>null</code>).
     * @param movements The movements node (must not be <code>null</code>).
     */
    private static void exportAllowedMovements(Xml root, Collection<MovementTile> movements)
    {
        for (final MovementTile movement : movements)
        {
            final Xml node = root.createChild(NODE_MOVEMENT);
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
