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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the pathfinding data from a configurer.
 * 
 * @see com.b3dgs.lionengine.game.map.MapTilePath
 */
public final class ConfigPathfinding
{
    /** Pathfinding root node. */
    public static final String PATHFINDING = Configurer.PREFIX + "pathfinding";
    /** Tile path node. */
    public static final String TILE_PATH = Configurer.PREFIX + "tilepath";
    /** Tile path category name attribute. */
    public static final String CATEGORY = "category";

    /**
     * Create the pathfinding data from node.
     * 
     * @param root The node root reference.
     * @return The collisions group data.
     * @throws LionEngineException If unable to read node.
     */
    public static ConfigPathfinding create(XmlNode root)
    {
        final ConfigPathfinding config = new ConfigPathfinding();
        for (final XmlNode node : root.getChildren(TILE_PATH))
        {
            final String category = node.readString(CATEGORY);
            for (final XmlNode group : node.getChildren(ConfigTileGroups.NODE_GROUP))
            {
                config.addGroup(category, group.getText());
            }
        }
        return config;
    }

    /** Pathfinding data. */
    private final Map<String, Collection<String>> categories;

    /**
     * Constructor.
     */
    private ConfigPathfinding()
    {
        categories = new HashMap<String, Collection<String>>();
    }

    /**
     * Get the group category.
     * 
     * @param group The group name.
     * @return The category name (<code>null</code> if undefined).
     */
    public String getCategory(String group)
    {
        for (final Map.Entry<String, Collection<String>> category : categories.entrySet())
        {
            if (category.getValue().contains(group))
            {
                return category.getKey();
            }
        }
        return null;
    }

    /**
     * Clear all data.
     */
    public void clear()
    {
        categories.clear();
    }

    /**
     * Add a group to its category.
     * 
     * @param categoryName The category name.
     * @param groupName The group name.
     */
    private void addGroup(String categoryName, String groupName)
    {
        final Collection<String> category;
        if (categories.containsKey(categoryName))
        {
            category = categories.get(categoryName);
        }
        else
        {
            category = new ArrayList<String>();
            categories.put(categoryName, category);
        }
        category.add(groupName);
    }
}
