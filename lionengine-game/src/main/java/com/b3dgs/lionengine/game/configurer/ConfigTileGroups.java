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

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.map.TileGroup;
import com.b3dgs.lionengine.game.map.TileRef;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the tile groups data from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see TileGroup
 */
public final class ConfigTileGroups
{
    /** Configuration file name. */
    public static final String FILENAME = "groups.xml";
    /** Group root node. */
    public static final String NODE_GROUPS = Configurer.PREFIX + "groups";
    /** Group node. */
    public static final String NODE_GROUP = Configurer.PREFIX + "group";
    /** Group name attribute. */
    public static final String ATTRIBUTE_GROUP_NAME = "name";
    /** Remove group. */
    public static final String REMOVE_GROUP_NAME = Constant.EMPTY_STRING;

    /**
     * Import the group data from configuration.
     * 
     * @param configGroups The groups descriptor.
     * @return The groups data.
     * @throws LionEngineException If unable to read data.
     */
    public static Collection<TileGroup> imports(Media configGroups)
    {
        final XmlNode nodeGroups = Stream.loadXml(configGroups);
        final Collection<TileGroup> groups = new ArrayList<TileGroup>();

        for (final XmlNode nodeGroup : nodeGroups.getChildren(NODE_GROUP))
        {
            final TileGroup group = importGroup(nodeGroup);
            groups.add(group);
        }

        return groups;
    }

    /**
     * Export groups to configuration file.
     * 
     * @param configGroups The export media.
     * @param groups The groups to export.
     * @throws LionEngineException If unable to write to media.
     */
    public static void exports(Media configGroups, Collection<TileGroup> groups)
    {
        final XmlNode nodeGroups = Stream.createXmlNode(NODE_GROUPS);
        nodeGroups.writeString(Configurer.HEADER, Engine.WEBSITE);

        for (final TileGroup group : groups)
        {
            exportGroup(nodeGroups, group);
        }

        Stream.saveXml(nodeGroups, configGroups);
    }

    /**
     * Import the group from its node.
     * 
     * @param nodeGroup The group node.
     * @return The imported group.
     */
    private static TileGroup importGroup(XmlNode nodeGroup)
    {
        final Collection<TileRef> tiles = new ArrayList<TileRef>();
        for (final XmlNode nodeTileRef : nodeGroup.getChildren(ConfigTile.NODE_TILE))
        {
            final TileRef tileRef = ConfigTile.create(nodeTileRef);
            tiles.add(tileRef);
        }

        final String groupName = nodeGroup.readString(ATTRIBUTE_GROUP_NAME);
        final TileGroup group = new TileGroup(groupName, tiles);

        return group;
    }

    /**
     * Export the group data as a node.
     * 
     * @param nodeGroups The root node.
     * @param group The group to export.
     */
    private static void exportGroup(XmlNode nodeGroups, TileGroup group)
    {
        final XmlNode nodeGroup = nodeGroups.createChild(NODE_GROUP);
        nodeGroup.writeString(ATTRIBUTE_GROUP_NAME, group.getName());

        for (final TileRef tileRef : group.getTiles())
        {
            final XmlNode nodeTileRef = ConfigTile.export(tileRef);
            nodeGroup.add(nodeTileRef);
        }
    }

    /**
     * Disabled constructor.
     */
    private ConfigTileGroups()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
