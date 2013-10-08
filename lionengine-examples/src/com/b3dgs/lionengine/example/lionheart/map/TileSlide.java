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
 * Tile slide implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class TileSlide
        extends Tile
{
    /**
     * @see Tile#Tile(int, int, Integer, int, TileCollision)
     */
    public TileSlide(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        super(width, height, pattern, number, collision);
    }

    /**
     * Get the slide collision.
     * 
     * @param c The collision type.
     * @param localizable The localizable.
     * @param offset The offset.
     * @return The collision.
     */
    private Double getSlide(TileCollision c, Localizable localizable, int offset)
    {
        final int startY = isLeft(c.getGroup()) ? getBottom() : getTop();
        return getCollisionY(c.getGroup(), localizable, startY, offset, -28 - halfTileHeight, -14, -21);
    }

    /*
     * Tile
     */

    @Override
    public Double getCollisionY(Localizable localizable)
    {
        final double x = localizable.getLocationX() - getX() - getWidth();
        final TileCollision c = getCollision();
        switch (c)
        {
            case SLIDE_RIGHT_1:
                return getSlide(c, localizable, halfTileHeight);
            case SLIDE_RIGHT_2:
                return getSlide(c, localizable, 23);
            case SLIDE_RIGHT_3:
                return getSlide(c, localizable, -halfTileHeight);
            case SLIDE_RIGHT_GROUND_SLIDE:
                if (x > -halfTileHeight)
                {
                    return getSlide(c, localizable, halfTileHeight);
                }
                return getGround(localizable, 0);

            case SLIDE_LEFT_1:
                return getSlide(c, localizable, halfTileHeight);
            case SLIDE_LEFT_2:
                return getSlide(c, localizable, 23);
            case SLIDE_LEFT_3:
                return getSlide(c, localizable, -halfTileHeight);
            case SLIDE_LEFT_GROUND_SLIDE:
                if (x > -halfTileHeight)
                {
                    return getSlide(c, localizable, halfTileHeight);
                }
                return getGround(localizable, 0);

            default:
                return null;
        }
    }
}
