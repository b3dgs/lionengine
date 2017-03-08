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
package com.b3dgs.lionengine.game.feature.tile;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.io.Xml;

/**
 * Represents the tile groups data from a configurer.
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
    public static final String ATTRIBUTE_GROUP_NAME = "name";
    /** Group type attribute. */
    public static final String ATTRIBUTE_GROUP_TYPE = "type";
    /** Remove group. */
    public static final String REMOVE_GROUP_NAME = Constant.EMPTY_STRING;

    /**
     * Import the group data from configuration.
     * 
     * @param groupsConfig The groups descriptor.
     * @return The groups data.
     * @throws LionEngineException If unable to read data.
     */
    public static Collection<TileGroup> imports(Media groupsConfig)
    {
        final Xml nodeGroups = new Xml(groupsConfig);
        final Collection<TileGroup> groups = new ArrayList<TileGroup>();

        for (final Xml nodeGroup : nodeGroups.getChildren(NODE_GROUP))
        {
            final TileGroup group = importGroup(nodeGroup);
            groups.add(group);
        }

        return groups;
    }

    /**
     * Export groups to configuration file.
     * 
     * @param groupsConfig The export media.
     * @param groups The groups to export.
     * @throws LionEngineException If unable to write to media.
     */
    public static void exports(Media groupsConfig, Collection<TileGroup> groups)
    {
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
     * @param nodeGroup The group node.
     * @return The imported group.
     */
    private static TileGroup importGroup(Xml nodeGroup)
    {
        final Collection<TileRef> tiles = new ArrayList<TileRef>();
        for (final Xml nodeTileRef : nodeGroup.getChildren(TileConfig.NODE_TILE))
        {
            final TileRef tileRef = TileConfig.create(nodeTileRef);
            tiles.add(tileRef);
        }

        final String groupName = nodeGroup.readString(ATTRIBUTE_GROUP_NAME);
        final TileGroupType groupType = TileGroupType.from(nodeGroup.readString(ATTRIBUTE_GROUP_TYPE));
        return new TileGroup(groupName, groupType, tiles);
    }

    /**
     * Export the group data as a node.
     * 
     * @param nodeGroups The root node.
     * @param group The group to export.
     */
    private static void exportGroup(Xml nodeGroups, TileGroup group)
    {
        final Xml nodeGroup = nodeGroups.createChild(NODE_GROUP);
        nodeGroup.writeString(ATTRIBUTE_GROUP_NAME, group.getName());
        nodeGroup.writeString(ATTRIBUTE_GROUP_TYPE, group.getType().name());

        for (final TileRef tileRef : group.getTiles())
        {
            final Xml nodeTileRef = TileConfig.export(tileRef);
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
