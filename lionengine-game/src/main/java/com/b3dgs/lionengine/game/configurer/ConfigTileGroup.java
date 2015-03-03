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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.collision.TileGroup;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the tile group data from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see TileGroup
 */
public final class ConfigTileGroup
{
    /** Group root node. */
    public static final String GROUPS = Configurer.PREFIX + "groups";
    /** Group node. */
    public static final String GROUP = Configurer.PREFIX + "group";
    /** Group name attribute. */
    public static final String NAME = "name";
    /** Tile sheet attribute. */
    public static final String SHEET = "sheet";
    /** Starting tile number attribute. */
    public static final String START = "start";
    /** Ending tile number attribute. */
    public static final String END = "end";

    /**
     * Create the group data from node.
     * 
     * @param root The node root reference.
     * @param map The map reference.
     * @return The group data.
     * @throws LionEngineException If unable to read node.
     */
    public static Collection<TileGroup> create(XmlNode root, MapTile map) throws LionEngineException
    {
        final Collection<TileGroup> groups = new ArrayList<>();
        for (final XmlNode node : root.getChildren(GROUP))
        {
            final TileGroup group = new TileGroup(node.readString(NAME), node.readInteger(SHEET),
                    node.readInteger(START), node.readInteger(END));
            groups.add(group);
        }
        return groups;
    }

    /**
     * Export the group data as a node.
     * 
     * @param group The group to export.
     * @return The node reference.
     */
    public static XmlNode export(TileGroup group)
    {
        final XmlNode node = Stream.createXmlNode(GROUPS);
        node.writeString(NAME, group.getName());
        node.writeInteger(SHEET, group.getSheet());
        node.writeInteger(START, group.getStart());
        node.writeInteger(END, group.getEnd());
        return node;
    }

    /**
     * Constructor.
     */
    private ConfigTileGroup()
    {
        // Private constructor
    }
}
