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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.InputDevicePointer;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * This class can be used to handle easily a strategy cursor, designed to select and give order to any kind of entity.
 * The cursor can be asynchronous (compared to the system pointer).
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CursorStrategy
        extends Cursor
{
    /** Camera reference. */
    private final CameraStrategy camera;
    /** Grid width. */
    private final int gridWidth;
    /** Grid height. */
    private final int gridHeight;
    /** Grid rectangle buffer. */
    private final Rectangle grid;
    /** Screen width. */
    private final int width;
    /** Screen height. */
    private final int height;
    /** Offset x. */
    private int offX;
    /** Offset y. */
    private int offY;
    /** Old location x. */
    private int oldX;
    /** Old location y. */
    private int oldY;

    /**
     * Constructor.
     * 
     * @param pointer The pointer reference (must not be <code>null</code>).
     * @param camera The camera reference (must not be <code>null</code>).
     * @param source The source display.
     * @param cursor The cursor images media.
     * @param map The map reference.
     */
    public CursorStrategy(InputDevicePointer pointer, CameraStrategy camera, Resolution source, MapTile<?, ?> map,
            Media... cursor)
    {
        this(pointer, camera, source, map.getTileWidth(), map.getTileHeight(), cursor);
    }

    /**
     * Constructor.
     * 
     * @param pointer The pointer reference (must not be <code>null</code>).
     * @param camera The camera reference (must not be <code>null</code>).
     * @param source The source display.
     * @param cursor The cursor images media.
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     */
    public CursorStrategy(InputDevicePointer pointer, CameraStrategy camera, Resolution source, int tileWidth,
            int tileHeight, Media... cursor)
    {
        super(pointer, 0, 0, source.getWidth(), source.getHeight(), cursor);
        Check.notNull(camera, "The camera must not be null !");
        this.camera = camera;
        setLocation(source.getWidth() / 2, source.getHeight() / 2);
        gridWidth = tileWidth;
        gridHeight = tileHeight;
        grid = Geom.createRectangle();
        width = source.getWidth();
        height = source.getHeight();
    }

    /**
     * Get a rectangle describing a grid (placed on the cursor, depending of map tile size).
     * 
     * @param size The size (in tile square).
     * @return rectangle The rectangle reference.
     */
    public Rectangle getGrid(int size)
    {
        final int x = getLocationX() / gridWidth * gridWidth;
        final int y = getLocationY() / gridHeight * gridHeight;
        grid.set(x, y, gridWidth * size, gridHeight * size);
        return grid;
    }

    /**
     * Get cursor location x on screen (not sync to any camera).
     * 
     * @return The cursor location x on screen.
     */
    public int getScreenX()
    {
        return super.getLocationX();
    }

    /**
     * Get cursor location y on screen (not sync to any camera).
     * 
     * @return The cursor location y on screen.
     */
    public int getScreenY()
    {
        return super.getLocationY();
    }

    /**
     * Get the horizontal tile pointed by the cursor.
     * 
     * @return The horizontal tile pointed by the cursor.
     */
    public int getLocationInTileX()
    {
        return (super.getLocationX() + offX) / gridWidth;
    }

    /**
     * Get the vertical tile pointed by the cursor.
     * 
     * @return The vertical tile pointed by the cursor.
     */
    public int getLocationInTileY()
    {
        return (height - super.getLocationY() + offY) / gridHeight;
    }

    /**
     * Get cursor horizontal move.
     * 
     * @return The horizontal move.
     */
    public int getMoveX()
    {
        return super.getLocationX() - oldX;
    }

    /**
     * Get cursor vertical move.
     * 
     * @return The vertical move.
     */
    public int getMoveY()
    {
        return super.getLocationY() - oldY;
    }

    /**
     * Get the grid width.
     * 
     * @return The grid width.
     */
    public int getGridWidth()
    {
        return gridWidth;
    }

    /**
     * Get the grid height.
     * 
     * @return The grid height.
     */
    public int getGridHeight()
    {
        return gridHeight;
    }

    /**
     * Check if the cursor can be over the entity, depending of the camera view.
     * 
     * @param tiled The tiled reference.
     * @param camera The camera reference.
     * @return <code>true</code> if can click over, <code>false</code> else.
     */
    public boolean isOver(Tiled tiled, CameraStrategy camera)
    {
        return getScreenX() >= camera.getViewX() && getScreenX() < camera.getViewX() + camera.getViewWidth()
                && getScreenY() >= camera.getViewY() && getScreenY() < camera.getViewY() + camera.getViewHeight()
                && getLocationInTileX() >= tiled.getLocationInTileX()
                && getLocationInTileX() < tiled.getLocationInTileX() + tiled.getWidthInTile()
                && getLocationInTileY() >= tiled.getLocationInTileY()
                && getLocationInTileY() < tiled.getLocationInTileY() + tiled.getHeightInTile();
    }

    /*
     * Cursor
     */

    @Override
    public void update(double extrp)
    {
        oldX = super.getLocationX();
        oldY = super.getLocationY() - camera.getViewY();
        super.update(extrp);
        setArea(0, 0, width, height);
        offX = camera.getLocationIntX();
        offY = camera.getLocationIntY() - camera.getViewY() * 2;
    }

    /**
     * Get cursor location x on map (sync to camera).
     * 
     * @return The cursor on map location x.
     */
    @Override
    public int getLocationX()
    {
        return super.getLocationX() + offX;
    }

    /**
     * Get cursor location y on map (sync to camera).
     * 
     * @return The cursor on map location y.
     */
    @Override
    public int getLocationY()
    {
        return height - super.getLocationY() + offY;
    }
}
