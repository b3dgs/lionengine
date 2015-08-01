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
import com.b3dgs.lionengine.game.collision.TileGroup;
import com.b3dgs.lionengine.game.collision.TileGroup.TileRef;
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
    /** Tile node. */
    public static final String TILE = Configurer.PREFIX + "tile";
    /** Group name attribute. */
    public static final String NAME = "name";
    /** Tile sheet attribute. */
    public static final String SHEET = "sheet";
    /** Tile number attribute. */
    public static final String NUMBER = "number";
    /** Remove group. */
    public static final String REMOVE_GROUP_NAME = Constant.EMPTY_STRING;

    /**
     * Create the group data from node.
     * 
     * @param root The node root reference.
     * @return The group data.
     * @throws LionEngineException If unable to read node.
     */
    public static Collection<TileGroup> create(XmlNode root) throws LionEngineException
    {
        final Collection<TileGroup> groups = new ArrayList<TileGroup>();
        for (final XmlNode node : root.getChildren(GROUP))
        {
            final Collection<TileRef> tiles = new ArrayList<TileRef>();
            for (final XmlNode ref : node.getChildren(TILE))
            {
                tiles.add(new TileRef(ref.readInteger(SHEET), ref.readInteger(NUMBER)));
            }

            final TileGroup group = new TileGroup(node.readString(NAME), tiles);
            groups.add(group);
        }
        return groups;
    }

    /**
     * Export the group data as a node.
     * 
     * @param root The root node.
     * @param group The group to export.
     */
    public static void export(XmlNode root, TileGroup group)
    {
        final XmlNode node = root.createChild(GROUP);
        node.writeString(NAME, group.getName());

        for (final TileRef ref : group.getTiles())
        {
            final XmlNode tileRef = node.createChild(TILE);
            tileRef.writeInteger(SHEET, ref.getSheet());
            tileRef.writeInteger(NUMBER, ref.getNumber());
        }
    }

    /**
     * Disabled constructor.
     */
    private ConfigTileGroup()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
