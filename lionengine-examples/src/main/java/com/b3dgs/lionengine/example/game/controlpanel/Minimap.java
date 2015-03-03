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
package com.b3dgs.lionengine.example.game.controlpanel;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.map.TileCollision;

/**
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Minimap
        extends com.b3dgs.lionengine.game.map.Minimap
{
    /**
     * {@link com.b3dgs.lionengine.game.map.Minimap#com.b3dgs.lionengine.game.map.Minimap(MapTile)}
     */
    public Minimap(MapTile map)
    {
        super(map);
    }

    /*
     * Minimap
     */

    @Override
    protected ColorRgba getTilePixelColor(Tile tile)
    {
        for (final TileColorType type : TileColorType.values())
        {
            if (type.name().equals(tile.getFeature(TileCollision.class).getGroup()))
            {
                return type.get();
            }
        }
        return super.getTilePixelColor(tile);
    }
}
