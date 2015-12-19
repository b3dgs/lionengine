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
package com.b3dgs.lionengine.game.tile;

/**
 * Represents something that can have a tile representation (in location and in size).
 */
public interface Tiled
{
    /**
     * Get horizontal tile location.
     * 
     * @return The horizontal tile location.
     */
    int getInTileX();

    /**
     * Get vertical tile location.
     * 
     * @return The vertical tile location.
     */
    int getInTileY();

    /**
     * Get the width in tile.
     * 
     * @return The width in tile.
     */
    int getInTileWidth();

    /**
     * Get the height in tile.
     * 
     * @return The height in tile.
     */
    int getInTileHeight();
}
