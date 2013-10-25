/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.warcraft.map;

import java.io.IOException;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.example.warcraft.ResourceType;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.rts.map.Border20;
import com.b3dgs.lionengine.game.rts.map.Border20Map;
import com.b3dgs.lionengine.game.rts.map.MapTileRts;

/**
 * Map implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Map
        extends MapTileRts<TileCollision, Tile>
{
    /** Tree map layer. */
    public final Border20Map treeMap;
    /** The map theme id. */
    private int id;
    /** Tree map states. */
    private Border20[][] trees;
    /** Cut tree time number. */
    private int treeCut;

    /**
     * Constructor.
     */
    public Map()
    {
        super(16, 16);
        treeMap = new Border20Map(true);
    }

    /**
     * Cut the tree and update trees around.
     * 
     * @param tree The tree to cut.
     */
    public void cutTree(Tiled tree)
    {
        final Tile tile = getTile(tree);
        tile.setNumber(treeCut);
        tile.setResourceType(ResourceType.NONE);
        tile.setCollision(TileCollision.GROUND9);
        updateTree(tile, true);
    }

    /**
     * Get the tree cut tile number.
     * 
     * @return The tree cut tile number.
     */
    public int getTreeCut()
    {
        return treeCut;
    }

    /**
     * Update the tree tile.
     * 
     * @param tile Tree tile.
     * @param check <code>true</code> to check all around, <code>false</code> else.
     */
    public void updateTree(Tile tile, boolean check)
    {
        final int tx = tile.getX() / getTileWidth();
        final int ty = tile.getY() / getTileHeight() + 1;
        if (check)
        {
            treeMap.checkAll(trees, tx, ty, 0, 0, 1);
        }
        else
        {
            treeMap.updateExclude(trees, tx, ty);
        }
        treeMap.finalCheck(trees, tx, ty);
        for (int v = ty - 1; v <= ty + 1; v++)
        {
            for (int h = tx - 1; h <= tx + 1; h++)
            {
                final Tile t = getTile(h, v);
                if (t != null)
                {
                    if (t.getId() != Border20.NONE)
                    {
                        final Border20 a = treeMap.get(trees, h, v);
                        t.setNumber(a);
                    }
                    else
                    {
                        treeMap.set(trees, h, v, Border20.NONE);
                    }
                }
            }
        }
    }

    /**
     * Set the tile tree id.
     * 
     * @param tile The tile reference.
     * @param id The tree id.
     */
    public void setTree(Tile tile, Border20 id)
    {
        treeMap.set(trees, tile.getX() / getTileWidth(), -tile.getY() / getTileHeight(), id);
    }

    /*
     * MapTileRts
     */

    @Override
    public void create(int widthInTile, int heightInTile)
    {
        super.create(widthInTile, heightInTile);
        trees = new Border20[heightInTile][widthInTile];
        treeMap.create(this);
        treeCut = 124;
        if (1 == id)
        {
            treeCut = 143;
        }
    }

    @Override
    public Tile createTile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        return new Tile(width, height, pattern, number, collision, id);
    }

    @Override
    public void load(FileReading file) throws IOException
    {
        super.load(file);
        for (int v = 0; v < heightInTile; v++)
        {
            for (int h = 0; h < widthInTile; h++)
            {
                final Tile tile = getTile(h, v);
                trees[v][h] = tile.getId();
            }
        }
    }

    @Override
    public TileCollision getCollisionFrom(String collision)
    {
        try
        {
            return TileCollision.valueOf(collision);
        }
        catch (IllegalArgumentException
               | NullPointerException exception)
        {
            return TileCollision.NONE;
        }
    }

    @Override
    protected ColorRgba getTilePixelColor(Tile tile)
    {
        return tile.getColor().getTheme(id);
    }
}
