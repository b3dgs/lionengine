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
import java.util.HashSet;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.io.Xml;

/**
 * Represents the pathfinding data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see MapTilePath
 */
public final class PathfindingConfig
{
    /** Default filename. */
    public static final String FILENAME = "pathfinding.xml";
    /** Pathfinding root node. */
    public static final String PATHFINDING = Constant.XML_PREFIX + "pathfinding";
    /** Tile path node. */
    public static final String TILE_PATH = Constant.XML_PREFIX + "tilepath";
    /** Tile path category name attribute. */
    public static final String CATEGORY = "category";

    /**
     * Import the category data from configuration.
     * 
     * @param configPathfinding The pathfinding descriptor (must not be <code>null</code>).
     * @return The categories data.
     * @throws LionEngineException If unable to read data.
     */
    public static Collection<PathCategory> imports(Media configPathfinding)
    {
        Check.notNull(configPathfinding);

        final Xml nodeCategories = new Xml(configPathfinding);
        final Collection<Xml> childrenTile = nodeCategories.getChildren(TILE_PATH);
        final Collection<PathCategory> categories = new HashSet<>(childrenTile.size());

        for (final Xml node : childrenTile)
        {
            final String name = node.readString(CATEGORY);
            final Collection<Xml> childrenGroup = node.getChildren(TileGroupsConfig.NODE_GROUP);
            final Collection<String> groups = new HashSet<>(childrenGroup.size());

            for (final Xml groupNode : childrenGroup)
            {
                groups.add(groupNode.getText());
            }

            final PathCategory category = new PathCategory(name, groups);
            categories.add(category);
        }

        return categories;
    }

    /**
     * Disabled constructor.
     */
    private PathfindingConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
