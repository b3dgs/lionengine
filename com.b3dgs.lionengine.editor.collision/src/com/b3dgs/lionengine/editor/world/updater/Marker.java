/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.world.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.geom.Point;

/**
 * Represents a tile marker.
 */
public class Marker
{
    /** Point reference. */
    private final Point point;
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
        point = new Point(x, y);
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

    @Override
    public int hashCode()
    {
        return point.hashCode();
    }

    @Override
    public boolean equals(Object object)
    {
        return point.equals(object);
    }
}
