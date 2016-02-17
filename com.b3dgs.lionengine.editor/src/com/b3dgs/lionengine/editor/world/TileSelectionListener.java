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
package com.b3dgs.lionengine.editor.world;

import com.b3dgs.lionengine.game.tile.Tile;

/**
 * Listen to tiles selection on map.
 */
public interface TileSelectionListener
{
    /**
     * Called when a tile is selected.
     * 
     * @param click The associated mouse click.
     * @param tile The selected tile from map.
     */
    void notifyTileSelected(int click, Tile tile);

    /**
     * Called when selected tile group changed.
     * 
     * @param group The new tile group selection.
     */
    void notifyTileGroupSelected(String group);
}
