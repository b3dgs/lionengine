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
package com.b3dgs.lionengine.game.rts;

import com.b3dgs.lionengine.Keyboard;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.purview.Localizable;
import com.b3dgs.lionengine.game.rts.entity.EntityRts;

/**
 * This camera should be used for a strategy oriented game. It allows free movement around the current map, using its
 * border as limit. It is also possible to define specific keys for camera handling. Don't forget give a call to
 * {@link CameraRts#setView(int, int, int, int)} and {@link CameraRts#setBorders(MapTile)} when the map is loaded.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CameraRts
        extends CameraGame
{
    /** Movement steps horizontal. */
    private final int hStep;
    /** Movement steps vertical. */
    private final int vStep;
    /** Movement key left. */
    private Integer left;
    /** Movement key right. */
    private Integer right;
    /** Movement key up. */
    private Integer up;
    /** Movement key down. */
    private Integer down;
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
    public CameraRts(MapTile<?, ?> map)
    {
        this(map.getTileWidth(), map.getTileHeight());
    }

    /**
     * Create a strategy oriented camera. Don't forget to call setBorders function when the map is loaded.
     * 
     * @param hStep The horizontal force move (usually the map tile width).
     * @param vStep The vertical force move (usually the map tile height).
     */
    public CameraRts(int hStep, int vStep)
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
     * @param keyboard The keyboard reference.
     */
    public void update(Keyboard keyboard)
    {
        final long time = UtilityMath.time();
        if (time - hTime > hSens)
        {
            hTime = time;
            if (keyboard.isPressed(left))
            {
                moveLocation(1.0, -hStep, 0);
            }
            if (keyboard.isPressed(right))
            {
                moveLocation(1.0, hStep, 0);
            }
        }
        if (time - vTime > vSens)
        {
            vTime = time;
            if (keyboard.isPressed(up))
            {
                moveLocation(1.0, 0, vStep);
            }
            if (keyboard.isPressed(down))
            {
                moveLocation(1.0, 0, -vStep);
            }
        }
        location.setLocationX(UtilityMath.fixBetween(location.getLocationX(), borderLeft, borderRight));
        location.setLocationY(UtilityMath.fixBetween(location.getLocationY(), borderTop, borderBottom));
    }

    /**
     * Set camera sensibility (the lower it is, the faster is the camera movement).
     * 
     * @param hSens The horizontal sensibility value (positive value).
     * @param vSens The vertical sensibility value (positive value).
     */
    public void setSensibility(int hSens, int vSens)
    {
        this.hSens = UtilityMath.fixBetween(hSens, 0, Integer.MAX_VALUE);
        this.vSens = UtilityMath.fixBetween(vSens, 0, Integer.MAX_VALUE);
    }

    /**
     * Set up camera limits depending of the map.
     * 
     * @param map The map reference.
     */
    public void setBorders(MapTile<?, ?> map)
    {
        borderLeft = 0;
        borderRight = map.getWidthInTile() * map.getTileWidth() - getViewWidth();
        borderTop = 0;
        borderBottom = map.getHeightInTile() * map.getTileHeight() - getViewHeight();
        setLocation(0, 0);
    }

    /**
     * Set specific camera keyboard controls.
     * 
     * @param left The left key.
     * @param right The right key.
     * @param up The up key.
     * @param down The down key.
     */
    public void setKeys(Integer left, Integer right, Integer up, Integer down)
    {
        this.left = left;
        this.right = right;
        this.up = up;
        this.down = down;
    }

    /**
     * Set the camera location at the specified tile location.
     * 
     * @param map The map reference.
     * @param tx The horizontal tile location.
     * @param ty The vertical tile location.
     */
    public void setLocation(MapTile<?, ?> map, int tx, int ty)
    {
        setLocation(tx * map.getTileWidth(), ty * map.getTileHeight());
    }

    /**
     * Get the horizontal location in tile.
     * 
     * @param map The map reference.
     * @return The horizontal location in tile.
     */
    public int getLocationInTileX(MapTile<?, ?> map)
    {
        return getLocationIntX() / map.getTileWidth();
    }

    /**
     * Get the vertical location in tile.
     * 
     * @param map The map reference.
     * @return The vertical location in tile.
     */
    public int getLocationInTileY(MapTile<?, ?> map)
    {
        return getLocationIntY() / map.getTileHeight();
    }

    /**
     * Check if cursor in inside camera view (outside panel).
     * 
     * @param cursor The cursor reference.
     * @return <code>true</code> if cursor in inside camera view, <code>false</code> else.
     */
    public boolean isInside(CursorRts cursor)
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
    public boolean canSee(EntityRts entity)
    {
        final double border = 0.1;
        return entity.getLocationX() + entity.getWidth() >= getLocationRealX() + border
                && entity.getLocationX() <= getLocationRealX() + getViewWidth() - border
                && entity.getLocationY() + entity.getHeight() >= getLocationRealY() + border
                && entity.getLocationY() <= getLocationRealY() + getViewHeight() - border;
    }

    /*
     * CameraGame
     */

    @Override
    public void moveLocation(double extrp, double vx, double vy)
    {
        super.moveLocation(extrp, vx, vy);
        location.setLocationX(UtilityMath.fixBetween(location.getLocationX(), borderLeft, borderRight));
        location.setLocationY(UtilityMath.fixBetween(location.getLocationY(), borderTop, borderBottom));
    }

    @Override
    public void follow(Localizable entity)
    {
        setLocation(entity.getLocationX() - getViewWidth() / 2.0, entity.getLocationY() - getViewHeight() / 2.0);
    }
}
