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
package com.b3dgs.lionengine.example.tilecollision;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.map.TilePlatform;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Tile implementation.
 */
final class Tile
        extends TilePlatform<TileCollision>
{
    /**
     * @see TilePlatform#TilePlatform(int, int, Integer, int, Enum)
     */
    public Tile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        super(width, height, pattern, number, collision);
    }

    /**
     * Render the tile collision.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    public void renderCollision(Graphic g, CameraPlatform camera)
    {
        final int x = camera.getViewpointX(getX());
        final int y = camera.getViewpointY(getY() + getHeight());
        g.drawRect(x, y, getWidth(), getHeight(), false);
    }

    /*
     * TilePlatform
     */

    @Override
    public Double getCollisionX(Localizable localizable)
    {
        // From left
        final int left = getX();
        final int half = getWidth() / 2;
        if (localizable.getLocationOldX() <= left + half && localizable.getLocationX() >= left)
        {
            return Double.valueOf(left);
        }
        // From right
        final int right = getX() + getWidth() - 1;
        if (localizable.getLocationX() <= right && localizable.getLocationOldX() >= right - half)
        {
            return Double.valueOf(right);
        }
        return null;
    }

    @Override
    public Double getCollisionY(Localizable localizable)
    {
        // From top
        final int top = getY() + getHeight() - 1;
        if (localizable.getLocationOldY() >= top && localizable.getLocationY() <= top)
        {
            return Double.valueOf(top);
        }
        return null;
    }
}
