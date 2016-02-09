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

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the pathfindable data from a configurer.
 */
public final class PathfindableConfig
{
    /** Pathfindable node name. */
    public static final String PATHFINDABLE = Configurer.PREFIX + "pathfindable";
    /** Category attribute. */
    public static final String CATEGORY = "category";
    /** Cost attribute. */
    public static final String COST = "cost";
    /** Block attribute. */
    public static final String BLOCK = "block";
    /** Diagonal attribute. */
    public static final String DIAGONAL = "diagonal";

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
        final double cost = node.readDouble(COST);
        final boolean blocking = node.readBoolean(BLOCK);
        final boolean diagonal = node.readBoolean(DIAGONAL);

        return new PathData(category, cost, blocking, diagonal);
    }

    /**
     * Disabled constructor.
     */
    private PathfindableConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
