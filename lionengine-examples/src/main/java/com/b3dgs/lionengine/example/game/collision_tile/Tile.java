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

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.CollisionTile;
import com.b3dgs.lionengine.game.map.TileGame;

/**
 * Tile implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Tile
        extends TileGame
{
    /**
     * @see TileGame#TileGame(int, int, Integer, int, CollisionTile)
     */
    public Tile(int width, int height, Integer pattern, int number, CollisionTile collision)
    {
        super(width, height, pattern, number, collision);
    }

    /**
     * Render the tile collision.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    public void renderCollision(Graphic g, Camera camera)
    {
        final int x = (int) camera.getViewpointX(getX());
        final int y = (int) camera.getViewpointY(getY() + getHeight());
        g.drawRect(x, y, getWidth(), getHeight(), true);
    }
}
