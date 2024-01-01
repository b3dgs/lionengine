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
import java.util.HashSet;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;

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
    public static final String NODE_PATHFINDING = Constant.XML_PREFIX + "pathfinding";
    /** Tile path node. */
    public static final String NODE_TILE_PATH = Constant.XML_PREFIX + "tilePath";
    /** Tile path category name attribute. */
    public static final String ATT_CATEGORY = "category";

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

        final XmlReader nodeCategories = new XmlReader(configPathfinding);
        final Collection<XmlReader> childrenTile = nodeCategories.getChildren(NODE_TILE_PATH);
        final Collection<PathCategory> categories = new HashSet<>(childrenTile.size());

        for (final XmlReader node : childrenTile)
        {
            final String name = node.getString(ATT_CATEGORY);
            final Collection<XmlReader> childrenGroup = node.getChildren(TileGroupsConfig.NODE_GROUP);
            final Collection<String> groups = new HashSet<>(childrenGroup.size());

            for (final XmlReader groupNode : childrenGroup)
            {
                groups.add(groupNode.getText());
            }
            childrenGroup.clear();

            final PathCategory category = new PathCategory(name, groups);
            categories.add(category);
        }
        childrenTile.clear();

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
