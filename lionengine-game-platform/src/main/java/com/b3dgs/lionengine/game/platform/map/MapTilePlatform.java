/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.platform.map;

import java.util.EnumSet;
import java.util.List;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.file.XmlNodeNotFoundException;
import com.b3dgs.lionengine.file.XmlParser;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.platform.CollisionFunction;
import com.b3dgs.lionengine.game.platform.CollisionInput;
import com.b3dgs.lionengine.game.platform.CollisionOperation;
import com.b3dgs.lionengine.game.platform.entity.EntityPlatform;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Default platform map implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <C> Collision type used.
 * @param <T> Tile type used.
 */
public abstract class MapTilePlatform<C extends Enum<C>, T extends TilePlatform<C>>
        extends MapTileGame<C, T>
{
    /**
     * Get the tile search speed value.
     * 
     * @param d The distance value.
     * @return The speed value.
     */
    private static double getTileSearchSpeed(int d)
    {
        if (d < 0)
        {
            return -1.0;
        }
        else if (d > 0)
        {
            return 1.0;
        }
        return 0.0;
    }

    /**
     * Get the tile search speed value.
     * 
     * @param dsup The distance superior value.
     * @param dinf The distance inferior value.
     * @return The speed value.
     */
    private static double getTileSearchSpeed(int dsup, int dinf)
    {
        if (0 == dsup)
        {
            return MapTilePlatform.getTileSearchSpeed(dinf);
        }
        return dinf / (double) dsup;
    }

    /**
     * Constructor.
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     */
    public MapTilePlatform(int tileWidth, int tileHeight)
    {
        super(tileWidth, tileHeight);
    }

    /**
     * Get the tile at the entity location.
     * 
     * @param entity The entity.
     * @param offsetX The horizontal offset search.
     * @param offsetY The vertical offset search.
     * @return The tile found at the entity.
     */
    public T getTile(EntityPlatform entity, int offsetX, int offsetY)
    {
        final int tx = (entity.getLocationIntX() + offsetX) / getTileWidth();
        final int ty = (entity.getLocationIntY() + offsetY) / getTileHeight();
        return getTile(tx, ty);
    }

    /**
     * Get the first tile hit by the localizable that contains collision, applying a ray tracing from its old location
     * to its current. This way, the localizable can not pass through a collidable tile.
     * 
     * @param localizable The localizable reference.
     * @param collisions Collisions list to search for.
     * @return The first tile hit, <code>null</code> if none found.
     */
    public T getFirstTileHit(Localizable localizable, EnumSet<C> collisions)
    {
        // Starting location
        final int sv = (int) Math.floor(localizable.getLocationOldY());
        final int sh = (int) Math.floor(localizable.getLocationOldX());

        // Ending location
        final int ev = (int) Math.floor(localizable.getLocationY());
        final int eh = (int) Math.floor(localizable.getLocationX());

        // Distance calculation
        final int dv = ev - sv;
        final int dh = sh - eh;

        // Search vector and number of search steps
        final double sx, sy;
        final int stepMax;
        if (Math.abs(dv) >= Math.abs(dh))
        {
            sy = MapTilePlatform.getTileSearchSpeed(dv);
            sx = MapTilePlatform.getTileSearchSpeed(dv, dh);
            stepMax = Math.abs(dv);
        }
        else
        {
            sx = MapTilePlatform.getTileSearchSpeed(dh);
            sy = MapTilePlatform.getTileSearchSpeed(dh, dv);
            stepMax = Math.abs(dh);
        }

        int step = 0;
        for (double v = sv, h = sh; step <= stepMax; v += sy, h -= sx)
        {
            final T tile = getTile((int) Math.floor(h / getTileWidth()), (int) Math.floor(v / getTileHeight()));
            if (tile != null && collisions.contains(tile.getCollision()))
            {
                return tile;
            }
            step++;
        }
        return null;
    }

    /**
     * Get location x relative to map referential as tile.
     * 
     * @param localizable The localizable reference.
     * @return The location x relative to map referential as tile.
     */
    public int getInTileX(Localizable localizable)
    {
        return (int) Math.floor(localizable.getLocationX() / getTileWidth());
    }

    /**
     * Get location y relative to map referential as tile.
     * 
     * @param localizable The localizable reference.
     * @return The location y relative to map referential as tile.
     */
    public int getInTileY(Localizable localizable)
    {
        return (int) Math.floor(localizable.getLocationY() / getTileHeight());
    }

    /*
     * MapTileGame
     */

    @Override
    public void loadCollisions(Media media)
    {
        super.loadCollisions(media);

        final XmlParser xml = File.createXmlParser();
        final XmlNode root = xml.load(media);
        final List<XmlNode> collisions = root.getChildren();
        for (XmlNode node : collisions)
        {
            try
            {
                final XmlNode functionNode = node.getChild("function");
                final C collision = getCollisionFrom(node.readString("name"));
                loadCollisionFunction(collision, functionNode);
            }
            catch (XmlNodeNotFoundException e)
            {
                Verbose.exception(MapTilePlatform.class, "loadCollisions", e);
            }
        }
    }

    /**
     * Load the collision function from the node.
     * 
     * @param collision The current collision enum.
     * @param functionNode The function node reference.
     */
    private void loadCollisionFunction(C collision, XmlNode functionNode)
    {
        final CollisionFunction function = new CollisionFunction();
        function.setInput(CollisionInput.valueOf(functionNode.readString("input")));
        function.setOperation(CollisionOperation.valueOf(functionNode.readString("operation")));
        function.setValue(functionNode.readInteger("value"));
        function.setOperationOffset(CollisionOperation.valueOf(functionNode.readString("operationOffset")));
        function.setOffset(functionNode.readInteger("offset"));

        assignCollisionFunction(collision, function);
    }

    /**
     * Assign the collision function to all tiles with the same collision.
     * 
     * @param collision The current collision enum.
     * @param function The function reference.
     */
    private void assignCollisionFunction(C collision, CollisionFunction function)
    {
        for (int i = 0; i < heightInTile; i++)
        {
            for (int j = 0; j < widthInTile; j++)
            {
                final T tile = getTile(j, i);
                if (tile != null && tile.getCollision() == collision)
                {
                    tile.setCollisionFunction(function);
                }
            }
        }
    }
}
