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
package com.b3dgs.lionengine.game.map.feature.renderer;

import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Describe how the map tile rendering is performed. This will allow to customize map rendering.
 */
public interface MapTileRenderer
{
    /**
     * Render tile on its designed location.
     * 
     * @param g The graphic output.
     * @param map The map used.
     * @param tile The tile to render.
     * @param x The location x.
     * @param y The location y.
     */
    void renderTile(Graphic g, MapTile map, Tile tile, int x, int y);
}
