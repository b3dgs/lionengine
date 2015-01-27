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
package com.b3dgs.lionengine.example.game.collision_tile;

import com.b3dgs.lionengine.game.map.MapTileGame;

/**
 * Map implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.map
 */
class Map
        extends MapTileGame<Tile>
{
    /**
     * Constructor.
     */
    public Map()
    {
        super(16, 16);
    }

    /**
     * Create a block tile.
     * 
     * @param v The vertical location.
     * @param h The horizontal location.
     */
    public void createBlock(int v, int h)
    {
        final Tile tile = createTile();
        setTile(v, h, tile);
    }

    /*
     * MapTileGame
     */

    @Override
    public Tile createTile()
    {
        return new Tile(getTileWidth(), getTileHeight());
    }
}
