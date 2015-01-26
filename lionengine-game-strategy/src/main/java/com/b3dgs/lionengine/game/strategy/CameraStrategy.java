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
package com.b3dgs.lionengine.game.strategy;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.strategy.entity.EntityStrategy;

/**
 * This camera should be used for a strategy oriented game. It allows free movement around the current map, using its
 * border as limit. It is also possible to define specific keys for camera handling. Don't forget give a call to
 * {@link CameraStrategy#setView(int, int, int, int)} and {@link CameraStrategy#setBorders(MapTile)} when the map is
 * loaded.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CameraStrategy
        extends Camera
{
    /** Movement steps horizontal. */
    private final int hStep;
    /** Movement steps vertical. */
    private final int vStep;
    /** Movement border left. */
    private int borderLeft;
    /** Movement border right. */
    private int borderRight;
    /** Movement border top. */
    private int borderTop;
    /** Movement border bottom. */
    private int borderBottom;
    /** Movement sensibility horizontal. */
    private long hSens;
    /** Movement sensibility vertical. */
    private long vSens;
    /** Movement horizontal time related to sensibility. */
    private long hTime;
    /** Movement vertical time related to sensibility. */
    private long vTime;

    /**
     * Create a strategy oriented camera. Don't forget to call {@link #setBorders(MapTile)} function when the map is
     * loaded.
     * 
     * @param map The map reference (hStep and vStep will use the map tile size).
     */
    public CameraStrategy(MapTile<?> map)
    {
        this(map.getTileWidth(), map.getTileHeight());
    }

    /**
     * Create a strategy oriented camera. Don't forget to call setBorders function when the map is loaded.
     * 
     * @param hStep The horizontal force move (usually the map tile width).
     * @param vStep The vertical force move (usually the map tile height).
     */
    public CameraStrategy(int hStep, int vStep)
    {
        super();
        this.hStep = hStep;
        this.vStep = vStep;
        borderLeft = Integer.MIN_VALUE;
        borderRight = Integer.MAX_VALUE;
        borderTop = Integer.MIN_VALUE;
        borderBottom = Integer.MAX_VALUE;
        final int defaultSensibility = 25;
        hSens = defaultSensibility;
        vSens = defaultSensibility;
    }

    /**
     * Update camera by handling its movements.
     * 
     * @param inputDevice The input device reference.
     */
    public void update(InputDeviceDirectional inputDevice)
    {
        final long time = UtilMath.time();
        final int x = inputDevice.getHorizontalDirection();
        final int y = inputDevice.getVerticalDirection();
        if (time - hTime > hSens)
        {
            hTime = time;
            moveLocation(1.0, hStep * x, 0);
        }
        if (time - vTime > vSens)
        {
            vTime = time;
            moveLocation(1.0, 0, vStep * y);
        }
        movable.setLocationX(UtilMath.fixBetween(movable.getX(), borderLeft, borderRight));
        movable.setLocationY(UtilMath.fixBetween(movable.getY(), borderTop, borderBottom));
    }

    /**
     * Set camera sensibility (the lower it is, the faster is the camera movement).
     * 
     * @param hSens The horizontal sensibility value (positive value).
     * @param vSens The vertical sensibility value (positive value).
     */
    public void setSensibility(int hSens, int vSens)
    {
        this.hSens = UtilMath.fixBetween(hSens, 0, Integer.MAX_VALUE);
        this.vSens = UtilMath.fixBetween(vSens, 0, Integer.MAX_VALUE);
    }

    /**
     * Set up camera limits depending of the map.
     * 
     * @param map The map reference.
     */
    public void setBorders(MapTile<?> map)
    {
        borderLeft = 0;
        borderRight = map.getWidthInTile() * map.getTileWidth() - getViewWidth();
        borderTop = 0;
        borderBottom = map.getHeightInTile() * map.getTileHeight() - getViewHeight();
        setLocation(0, 0);
    }

    /**
     * Set the camera location at the specified tile location.
     * 
     * @param map The map reference.
     * @param tx The horizontal tile location.
     * @param ty The vertical tile location.
     */
    public void setLocation(MapTile<?> map, int tx, int ty)
    {
        setLocation(tx * map.getTileWidth(), ty * map.getTileHeight());
    }

    /**
     * Get the horizontal location in tile.
     * 
     * @param map The map reference.
     * @return The horizontal location in tile.
     */
    public int getLocationInTileX(MapTile<?> map)
    {
        return (int) getX() / map.getTileWidth();
    }

    /**
     * Get the vertical location in tile.
     * 
     * @param map The map reference.
     * @return The vertical location in tile.
     */
    public int getLocationInTileY(MapTile<?> map)
    {
        return (int) getY() / map.getTileHeight();
    }

    /**
     * Check if cursor in inside camera view (outside panel).
     * 
     * @param cursor The cursor reference.
     * @return <code>true</code> if cursor in inside camera view, <code>false</code> else.
     */
    public boolean isInside(CursorStrategy cursor)
    {
        return cursor.getScreenX() >= getViewX() && cursor.getScreenX() <= getViewX() + getViewWidth()
                && cursor.getScreenY() >= getViewY() && cursor.getScreenY() <= getViewY() + getViewHeight();
    }

    /**
     * Check if camera can see the entity.
     * 
     * @param entity The entity to check.
     * @return <code>true</code> if can see the entity, <code>false</code> else.
     */
    public boolean canSee(EntityStrategy entity)
    {
        final double border = 0.1;
        return entity.getX() + entity.getWidth() >= getLocationRealX() + border
                && entity.getX() <= getLocationRealX() + getViewWidth() - border
                && entity.getY() + entity.getHeight() >= getLocationRealY() + border
                && entity.getY() <= getLocationRealY() + getViewHeight() - border;
    }

    /*
     * CameraGame
     */

    @Override
    public void moveLocation(double extrp, double vx, double vy)
    {
        super.moveLocation(extrp, vx, vy);
        movable.setLocationX(UtilMath.fixBetween(movable.getX(), borderLeft, borderRight));
        movable.setLocationY(UtilMath.fixBetween(movable.getY(), borderTop, borderBottom));
    }

    @Override
    public void follow(Localizable entity)
    {
        setLocation(entity.getX() - getViewWidth() / 2.0, entity.getY() - getViewHeight() / 2.0);
    }
}
