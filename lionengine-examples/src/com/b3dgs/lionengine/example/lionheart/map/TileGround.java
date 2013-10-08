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
package com.b3dgs.lionengine.example.lionheart.map;

import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Tile ground implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class TileGround
        extends Tile
{
    /**
     * @see Tile#Tile(int, int, Integer, int, TileCollision)
     */
    public TileGround(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        super(width, height, pattern, number, collision);
    }

    /*
     * TilePlatform
     */

    @Override
    public Double getCollisionX(Localizable localizable)
    {
        final double x = localizable.getLocationX() - getX() - getWidth();
        final TileCollision c = getCollision();
        switch (c)
        {
            case GROUND_SPIKE:
                if (x > -16 && x < -13)
                {
                    return Double.valueOf(getX());
                }
                else if (x > -3 && x < 1)
                {
                    return Double.valueOf(getX() + getWidth());
                }
                return null;

            default:
                return null;
        }
    }

    @Override
    public Double getCollisionY(Localizable localizable)
    {
        final TileCollision c = getCollision();
        switch (c)
        {
            case GROUND:
            case GROUND_SPIKE:
            case BORDER_LEFT:
            case BORDER_CENTER:
            case BORDER_RIGHT:
                return getGround(localizable, 0);

            default:
                return null;
        }
    }
}
