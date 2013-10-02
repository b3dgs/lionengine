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
package com.b3dgs.lionengine.game.platform;

import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.platform.entity.EntityPlatform;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * More specific camera, platform game oriented. It supports map borders limitation (can't see outside the map).
 */
public class CameraPlatform
        extends CameraGame
{
    /** Screen width. */
    private final int screenWidth;
    /** Screen height. */
    private final int screenHeight;
    /** Map limits left. */
    private int mapLeftLimit;
    /** Map limits right. */
    private int mapRightLimit;
    /** Map limits up. */
    private int mapUpLimit;
    /** Map limits down. */
    private int mapDownLimit;

    /**
     * Constructor.
     * 
     * @param screenWidth The screen width.
     * @param screenHeight The screen height.
     */
    public CameraPlatform(int screenWidth, int screenHeight)
    {
        super();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        width = screenWidth;
        height = screenHeight;
        mapLeftLimit = -1;
        mapRightLimit = -1;
        mapUpLimit = -1;
        mapDownLimit = -1;
    }

    /**
     * Follow automatically the specified entity. The camera location will be adjusted to the followed entity.
     * 
     * @param entity The entity to follow.
     */
    public void follow(EntityPlatform entity)
    {
        setLocation(entity.getLocationX(), entity.getLocationY() + entity.getHeight() / 2);
    }

    /**
     * Define the map borders. This function will allow to let the camera know the map size, and so, know which part of
     * the map can be viewed without being outside the map extremity.
     * <p>
     * Note: Must be called after map loading (usually in {@link WorldGame#loading(FileReading)}).
     * </p>
     * 
     * @param map The map reference.
     */
    public void setLimits(MapTile<?, ?> map)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int top = UtilityMath.fixBetween(map.getHeightInTile() * th - screenHeight, 0, Integer.MAX_VALUE);
        final int right = map.getWidthInTile() * tw - screenWidth;
        mapUpLimit = top;
        mapLeftLimit = 0;
        mapRightLimit = right;
        mapDownLimit = 0;
        moveLocation(1.0, 0.0, 0.0);
    }

    /**
     * Get map left border.
     * 
     * @return The map left border.
     */
    public int getLimitMapLeft()
    {
        return mapLeftLimit;
    }

    /**
     * Get map right border.
     * 
     * @return The map right border.
     */
    public int getLimitMapRight()
    {
        return mapRightLimit;
    }

    /**
     * Get map up border.
     * 
     * @return The map up border.
     */
    public int getLimitMapUp()
    {
        return mapUpLimit;
    }

    /**
     * Get map down border.
     * 
     * @return The map down border.
     */
    public int getLimitMapDown()
    {
        return mapDownLimit;
    }

    /**
     * Check horizontal limit on move.
     * 
     * @param vx The horizontal movement.
     */
    private void checkHorizontalLimit(double vx)
    {
        // Middle
        if (offset.getLocationIntX() == -intervalHorizontal || offset.getLocationIntX() == intervalHorizontal)
        {
            location.moveLocation(1, vx, 0);
        }

        if (location.getLocationX() > getLimitMapLeft() && location.getLocationX() < getLimitMapRight())
        {
            offset.moveLocation(1, vx, 0);

            // Block offset on its limits
            if (offset.getLocationX() < -intervalHorizontal)
            {
                offset.setLocationX(-intervalHorizontal);
            }
            else if (offset.getLocationX() > intervalHorizontal)
            {
                offset.setLocationX(intervalHorizontal);
            }
            else
            {
                location.moveLocation(1, Force.ZERO);
            }
        }
        // Case of map extremity
        else
        {
            if (location.getLocationX() < getLimitMapLeft())
            {
                location.setLocationX(getLimitMapLeft());
            }
            else if (location.getLocationX() > getLimitMapRight())
            {
                location.setLocationX(getLimitMapRight());
            }
            else
            {
                location.moveLocation(1, Force.ZERO);
            }

            offset.moveLocation(1, vx, 0);

            if (location.getLocationIntX() == getLimitMapLeft() && offset.getLocationX() >= intervalHorizontal)
            {
                offset.setLocationX(intervalHorizontal);
                location.moveLocation(1, vx, 0);
            }
            if (location.getLocationIntX() == getLimitMapRight() && offset.getLocationX() <= -intervalHorizontal)
            {
                offset.setLocationX(-intervalHorizontal);
                location.moveLocation(1, vx, 0);
            }
        }
    }

    /*
     * CameraGame
     */

    @Override
    public void moveLocation(double extrp, double vx, double vy)
    {
        // Horizontal move
        // Can scroll only on offset interval
        checkHorizontalLimit(vx);

        // Vertical move
        if (offset.getLocationIntY() == 0)
        {
            location.moveLocation(extrp, 0, vy);
        }
        if (location.getLocationY() > getLimitMapDown() && location.getLocationY() < getLimitMapUp())
        {
            offset.setLocationY(0.0);
        }
        else
        {
            if (location.getLocationY() < getLimitMapDown())
            {
                location.setLocationY(getLimitMapDown());
            }
            if (location.getLocationY() > getLimitMapUp())
            {
                location.setLocationY(getLimitMapUp());
            }

            offset.moveLocation(extrp, 0, vy);

            if (location.getLocationIntY() == getLimitMapDown() && offset.getLocationY() >= 0)
            {
                location.moveLocation(extrp, 0, vy);
                offset.setLocationY(0.0);
            }
            if (location.getLocationIntY() == getLimitMapUp() && offset.getLocationY() <= 0)
            {
                location.moveLocation(extrp, 0, vy);
                offset.setLocationY(0.0);
            }
        }
    }

    @Override
    public void setLocation(double x, double y)
    {
        setLocationX(x - screenWidth / 2.0);
        setLocationY(y - screenHeight / 2.0);
    }

    @Override
    public void setLocationX(double x)
    {
        checkHorizontalLimit(x - (location.getLocationX() + offset.getLocationX()));
    }

    @Override
    public void setLocationY(double y)
    {
        // Middle
        if (y >= getLimitMapDown() && y < getLimitMapUp())
        {
            location.setLocationY(y);
            offset.setLocationY(0.0);
        }
        else
        {
            // Down limit
            if (y < getLimitMapDown())
            {
                location.setLocationY(getLimitMapDown());
                offset.setLocationY(y - location.getLocationY());
            }
            // Top limit
            if (y >= getLimitMapUp())
            {
                location.setLocationY(getLimitMapUp());
                offset.setLocationY(y - location.getLocationY());
            }
        }
    }
}
