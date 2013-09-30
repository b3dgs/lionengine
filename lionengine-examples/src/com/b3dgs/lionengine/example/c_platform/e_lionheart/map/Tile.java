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
package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

import com.b3dgs.lionengine.game.platform.map.TilePlatform;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Tile implementation.
 */
public class Tile
        extends TilePlatform<TileCollision>
{
    /** Half tile height, corresponding to the collision height location on tile. */
    protected final int halfTileHeight;

    /**
     * @see TilePlatform#TilePlatform(int, int, Integer, int, Enum)
     */
    public Tile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        super(width, height, pattern, number, collision);
        halfTileHeight = height / 2;
    }

    /**
     * Check if tile is from this group.
     * 
     * @param group The group to check to.
     * @return <code>true</code> if from this group, <code>false</code> else.
     */
    public boolean isGroup(TileCollisionGroup group)
    {
        return getCollision().getGroup() == group;
    }

    /**
     * Check if tile is a border.
     * 
     * @return <code>true</code> if border, <code>false</code> else.
     */
    public boolean isBorder()
    {
        return getCollision().isBorder();
    }

    /**
     * Check if tile if left part of this group.
     * 
     * @param group The group to check.
     * @return <code>true</code> if left.
     */
    public boolean isLeft(TileCollisionGroup group)
    {
        return isGroup(group) && getCollision().isLeft();
    }

    /**
     * Check if tile if right part of this group.
     * 
     * @param group The group to check.
     * @return <code>true</code> if right.
     */
    public boolean isRight(TileCollisionGroup group)
    {
        return isGroup(group) && !getCollision().isLeft();
    }

    /**
     * Get the y location on the tile in tilt case.
     * 
     * @param group The collision group.
     * @param localizable The localizable.
     * @param startY The starting location y.
     * @param offset The offset value.
     * @return The vertical relative location on tile.
     */
    protected double getOnTileTiltY(TileCollisionGroup group, Localizable localizable, int startY, int offset)
    {
        final boolean left = isLeft(group);
        final double x = localizable.getLocationX() - getX() - (left ? 0 : getWidth());
        return startY + x * group.getFactor() * (left ? 1 : -1) + offset;
    }

    /**
     * Get the collision y value (<code>null</code> if none).
     * 
     * @param group The collision group.
     * @param localizable The localizable.
     * @param startY The starting location y.
     * @param offset The offset value.
     * @param mOldY The old y offset test.
     * @param mY The y offset test.
     * @param cY The collision offset y.
     * @return The collision y value.
     */
    protected Double getCollisionY(TileCollisionGroup group, Localizable localizable, int startY, int offset,
            int mOldY, int mY, int cY)
    {
        final double y = getOnTileTiltY(group, localizable, startY, offset);
        if (localizable.getLocationOldY() >= y + mOldY && localizable.getLocationY() <= y + mY)
        {
            return Double.valueOf(y + cY);
        }
        return null;
    }

    /**
     * Get ground collision.
     * 
     * @param localizable The localizable.
     * @param offset The offset value.
     * @return The collision.
     */
    protected Double getGround(Localizable localizable, int offset)
    {
        final int top = getTop();
        final int bottom = top - 2;
        if (localizable.getLocationOldY() >= bottom && localizable.getLocationY() <= top)
        {
            return Double.valueOf(top + offset);
        }
        return null;
    }

    /**
     * Get the original top tile value.
     * 
     * @return THe original top tile value.
     */
    protected int getTopOriginal()
    {
        return super.getTop();
    }

    /*
     * TilePlatform
     */

    @Override
    public int getTop()
    {
        return super.getBottom() + 2;
    }

    @Override
    public Double getCollisionX(Localizable localizable)
    {
        return null;
    }

    @Override
    public Double getCollisionY(Localizable localizable)
    {
        return null;
    }
}
