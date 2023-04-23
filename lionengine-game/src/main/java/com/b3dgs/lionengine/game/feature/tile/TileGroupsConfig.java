/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;

/**
 * Represents the tile groups data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see TileGroup
 */
public final class TileGroupsConfig
{
    /** Configuration file name. */
    public static final String FILENAME = "groups.xml";
    /** Group root node. */
    public static final String NODE_GROUPS = Constant.XML_PREFIX + "groups";
    /** Group node. */
    public static final String NODE_GROUP = Constant.XML_PREFIX + "group";
    /** Group name attribute. */
    public static final String ATT_GROUP_NAME = "name";
    /** Group type attribute. */
    public static final String ATT_GROUP_TYPE = "type";
    /** Remove group. */
    public static final String REMOVE_GROUP_NAME = Constant.EMPTY_STRING;

    /**
     * Import the group data from configuration.
     * 
     * @param groupsConfig The groups descriptor (must not be <code>null</code>).
     * @return The groups data.
     * @throws LionEngineException If unable to read data.
     */
    public static Collection<TileGroup> imports(Media groupsConfig)
    {
        Check.notNull(groupsConfig);

        final XmlReader nodeGroups = new XmlReader(groupsConfig);

        final Collection<XmlReader> children = nodeGroups.getChildren(NODE_GROUP);
        final Collection<TileGroup> groups = new ArrayList<>(children.size());

        for (final XmlReader nodeGroup : children)
        {
            final TileGroup group = importGroup(nodeGroup);
            groups.add(group);
        }
        children.clear();

        return groups;
    }

    /**
     * Export groups to configuration file.
     * 
     * @param groupsConfig The export media (must not be <code>null</code>).
     * @param groups The groups to export (must not be <code>null</code>).
     * @throws LionEngineException If unable to write to media.
     */
    public static void exports(Media groupsConfig, Iterable<TileGroup> groups)
    {
        Check.notNull(groupsConfig);
        Check.notNull(groups);

        final Xml nodeGroups = new Xml(NODE_GROUPS);
        nodeGroups.writeString(Constant.XML_HEADER, Constant.ENGINE_WEBSITE);

        for (final TileGroup group : groups)
        {
            exportGroup(nodeGroups, group);
        }

        nodeGroups.save(groupsConfig);
    }

    /**
     * Import the group from its node.
     * 
     * @param nodeGroup The group node (must not be <code>null</code>).
     * @return The imported group.
     */
    private static TileGroup importGroup(XmlReader nodeGroup)
    {
        final Collection<XmlReader> children = nodeGroup.getChildren(TileConfig.NODE_TILE);
        final Set<Integer> tiles = new HashSet<>(children.size());

        for (final XmlReader nodeTile : children)
        {
            final Integer tile = Integer.valueOf(TileConfig.imports(nodeTile));
            tiles.add(tile);
        }
        children.clear();

        final String groupName = nodeGroup.getString(ATT_GROUP_NAME);
        final TileGroupType groupType = nodeGroup.getEnum(TileGroupType.class, TileGroupType.NONE, ATT_GROUP_TYPE);
        return new TileGroup(groupName, groupType, tiles);
    }

    /**
     * Export the group data as a node.
     * 
     * @param nodeGroups The root node (must not be <code>null</code>).
     * @param group The group to export (must not be <code>null</code>).
     */
    private static void exportGroup(Xml nodeGroups, TileGroup group)
    {
        final Xml nodeGroup = nodeGroups.createChild(NODE_GROUP);
        nodeGroup.writeString(ATT_GROUP_NAME, group.getName());
        nodeGroup.writeEnum(ATT_GROUP_TYPE, group.getType());

        for (final Integer tile : group.getTiles())
        {
            final Xml nodeTileRef = TileConfig.exports(tile.intValue());
            nodeGroup.add(nodeTileRef);
        }
    }

    /**
     * Disabled constructor.
     */
    private TileGroupsConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
