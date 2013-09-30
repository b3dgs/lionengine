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
package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import java.awt.Color;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeCursor;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Cursor implementation.
 */
public final class Cursor
        extends CursorRts
{
    /** Current cursor type. */
    private TypeCursor type;
    /** Box width. */
    private int boxWidth;
    /** Box height. */
    private int boxHeight;
    /** Box color. */
    private Color boxColor;

    /**
     * Create a new rts cursor.
     * 
     * @param source The resolution source reference.
     * @param map The map reference.
     * @param cursor The cursor media.
     */
    Cursor(Resolution source, MapTile<?, ?> map, Media... cursor)
    {
        super(source, map, cursor);
        type = TypeCursor.POINTER;
    }

    /**
     * Set the cursor type.
     * 
     * @param type The cursor type.
     */
    public void setType(TypeCursor type)
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
    public void setBoxColor(Color color)
    {
        boxColor = color;
    }

    /**
     * Get the cursor type.
     * 
     * @return The cursor type.
     */
    public TypeCursor getType()
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
        if (TypeCursor.BOX == type)
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
        if (TypeCursor.BOX != type)
        {
            super.render(g);
        }
    }
}
