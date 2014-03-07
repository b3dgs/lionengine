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
package com.b3dgs.lionengine.example.game.strategy.skills;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Mouse;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.strategy.CameraStrategy;
import com.b3dgs.lionengine.game.strategy.CursorStrategy;

/**
 * Cursor implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.strategy.cursor
 */
public final class Cursor
        extends CursorStrategy
{
    /** Current cursor type. */
    private CursorType type;
    /** Box width. */
    private int boxWidth;
    /** Box height. */
    private int boxHeight;
    /** Box color. */
    private ColorRgba boxColor;

    /**
     * {@link CursorStrategy#CursorStrategy(Mouse, CameraStrategy, Resolution, MapTile, Media...)}
     */
    Cursor(Mouse mouse, CameraStrategy camera, Resolution source, MapTile<?, ?> map, Media... cursor)
    {
        super(mouse, camera, source, map, cursor);
        type = CursorType.POINTER;
    }

    /**
     * Set the cursor type.
     * 
     * @param type The cursor type.
     */
    public void setType(CursorType type)
    {
        this.type = type;
        switch (type)
        {
            case POINTER:
                setSurfaceId(0);
                setRenderingOffset(0, 0);
                break;
            case WEN:
                setRenderingOffset(-5, -5);
                setSurfaceId(1);
                break;
            case CROSS:
                setSurfaceId(2);
                setRenderingOffset(-7, -5);
                break;
            default:
                break;
        }
    }

    /**
     * Set the box size.
     * 
     * @param width The grid width.
     * @param height The grid height.
     */
    public void setBoxSize(int width, int height)
    {
        boxWidth = width * getGridWidth();
        boxHeight = height * getGridHeight();
    }

    /**
     * Set the grid color.
     * 
     * @param color The grid color.
     */
    public void setBoxColor(ColorRgba color)
    {
        boxColor = color;
    }

    /**
     * Get the cursor type.
     * 
     * @return The cursor type.
     */
    public CursorType getType()
    {
        return type;
    }

    /**
     * Render box cursor in has.
     * 
     * @param g The graphics output.
     */
    public void renderBox(Graphic g)
    {
        if (CursorType.BOX == type)
        {
            g.setColor(boxColor);
            g.drawRect((getScreenX() + 8) / getGridWidth() * getGridWidth() - 8,
                    ((getScreenY() + 4) / getGridHeight() + 1) * getGridHeight() - 4 - boxHeight, boxWidth, boxHeight,
                    false);
        }
    }

    /*
     * CursorRts
     */

    @Override
    public void render(Graphic g)
    {
        if (CursorType.BOX != type)
        {
            super.render(g);
        }
    }
}
