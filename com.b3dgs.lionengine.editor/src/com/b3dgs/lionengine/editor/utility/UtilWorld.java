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
package com.b3dgs.lionengine.editor.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroup;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.feature.group.MapTileGroup;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileConfig;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Point;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Series of tool functions around the editor world.
 */
public final class UtilWorld
{
    /**
     * Get the location over the mouse.
     * 
     * @param camera The camera reference.
     * @param mx The mouse X.
     * @param my The mouse Y.
     * @return The location found.
     */
    public static Point getPoint(Camera camera, int mx, int my)
    {
        final int x = (int) camera.getX() + mx;
        final int y = (int) camera.getY() - my + camera.getHeight();
        return Geom.createPoint(x, y);
    }

    /**
     * Get the tile location over the mouse.
     * 
     * @param map The map reference.
     * @param camera The camera reference.
     * @param mx The mouse X.
     * @param my The mouse Y.
     * @return The tile found, <code>null</code> if none.
     */
    public static Tile getTile(MapTile map, Camera camera, int mx, int my)
    {
        final int x = (int) camera.getX() + mx;
        final int y = (int) camera.getY() - my + camera.getHeight();
        return map.getTileAt(x, y);
    }

    /**
     * Change tile group.
     * 
     * @param map The map reference.
     * @param oldGroup The old group name.
     * @param newGroup The new group name (empty to remove it).
     * @param tile The tile reference.
     */
    public static void changeTileGroup(MapTileGroup map, String oldGroup, String newGroup, Tile tile)
    {
        final Media config = map.getGroupsConfig();
        final XmlNode root = Xml.load(config);
        changeTileGroup(root, oldGroup, newGroup, tile);
        Xml.save(root, config);
        map.loadGroups(config);
    }

    /**
     * Change tile group.
     * 
     * @param root The root reference.
     * @param oldGroup The old group name.
     * @param newGroup The new group name (empty to remove it).
     * @param tile The tile reference.
     */
    public static void changeTileGroup(XmlNode root, String oldGroup, String newGroup, Tile tile)
    {
        final Collection<Point> toAdd = new HashSet<>();
        for (final XmlNode nodeGroup : root.getChildren(TileGroupsConfig.NODE_GROUP))
        {
            removeOldGroup(nodeGroup, oldGroup, tile);
            if (CollisionGroup.same(nodeGroup.readString(TileGroupsConfig.ATTRIBUTE_GROUP_NAME), newGroup))
            {
                final Point point = Geom.createPoint(tile.getSheet().intValue(), tile.getNumber());
                if (!toAdd.contains(point))
                {
                    toAdd.add(point);
                }
            }

        }
        if (!TileGroupsConfig.REMOVE_GROUP_NAME.equals(newGroup))
        {
            final XmlNode newNode = getNewNode(root, newGroup);
            for (final Point current : toAdd)
            {
                final XmlNode node = newNode.createChild(TileConfig.NODE_TILE);
                node.writeInteger(TileConfig.ATT_TILE_SHEET, current.getX());
                node.writeInteger(TileConfig.ATT_TILE_NUMBER, current.getY());
            }
        }
        toAdd.clear();
    }

    /**
     * Remove old tile group.
     * 
     * @param nodeGroup The current node group.
     * @param oldGroup The old group name.
     * @param tile The current tile.
     */
    private static void removeOldGroup(XmlNode nodeGroup, String oldGroup, Tile tile)
    {
        final Collection<XmlNode> toRemove = new ArrayList<>();
        if (CollisionGroup.same(nodeGroup.readString(TileGroupsConfig.ATTRIBUTE_GROUP_NAME), oldGroup))
        {
            for (final XmlNode nodeTile : nodeGroup.getChildren(TileConfig.NODE_TILE))
            {
                if (nodeTile.readInteger(TileConfig.ATT_TILE_SHEET) == tile.getSheet().intValue()
                    && nodeTile.readInteger(TileConfig.ATT_TILE_NUMBER) == tile.getNumber())
                {
                    toRemove.add(nodeTile);
                }
            }
            for (final XmlNode remove : toRemove)
            {
                nodeGroup.removeChild(remove);
            }
        }
        toRemove.clear();
    }

    /**
     * Get the new node group.
     * 
     * @param node The node root.
     * @param newGroup The new group name.
     * @return The node found or created.
     */
    private static XmlNode getNewNode(XmlNode node, String newGroup)
    {
        for (final XmlNode nodeGroup : node.getChildren(TileGroupsConfig.NODE_GROUP))
        {
            if (newGroup.equals(nodeGroup.readString(TileGroupsConfig.ATTRIBUTE_GROUP_NAME)))
            {
                return nodeGroup;
            }
        }
        final XmlNode newGroupNode = node.createChild(TileGroupsConfig.NODE_GROUP);
        newGroupNode.writeString(TileGroupsConfig.ATTRIBUTE_GROUP_NAME, newGroup);

        return newGroupNode;
    }

    /**
     * Private constructor.
     */
    private UtilWorld()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
