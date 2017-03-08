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
package com.b3dgs.lionengine.editor.world.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.b3dgs.lionengine.game.feature.tile.Tile;

/**
 * Represents a tile marker.
 */
public class Marker
{
    /** Horizontal. */
    private final int x;
    /** Vertical. */
    private final int y;
    /** Associated tile. */
    private Collection<Tile> tiles;

    /**
     * Create a marker.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public Marker(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Add a tile.
     * 
     * @param tile The tile reference.
     */
    public void addTile(Tile tile)
    {
        if (tiles == null)
        {
            tiles = new ArrayList<>();
        }
        tiles.add(tile);
    }

    /**
     * Get the associated tiles.
     * 
     * @return The marked tiles.
     */
    public Collection<Tile> getTiles()
    {
        return Collections.unmodifiableCollection(tiles);
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || getClass() != object.getClass())
        {
            return false;
        }
        final Marker other = (Marker) object;
        return x == other.x && y == other.y;
    }
}
