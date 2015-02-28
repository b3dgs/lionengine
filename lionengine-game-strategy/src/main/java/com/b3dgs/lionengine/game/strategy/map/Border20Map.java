/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.strategy.map;

import com.b3dgs.lionengine.game.map.MapTile;

/**
 * Represents a 20 Axis map, designed to perform linking between two different areas. A good example is the fog of war,
 * or a tree area inside a grass area. It supports until 20 links (verticals, horizontal, corners in/out, middle...). It
 * has to be used with a tile based map.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Border20
 */
public class Border20Map
{
    /** Middle flag. */
    private final boolean middle;
    /** Safe array. */
    private boolean[][] safe;

    /**
     * Constructor.
     * 
     * @param middle The middle flag.
     */
    public Border20Map(boolean middle)
    {
        this.middle = middle;
        safe = null;
    }

    /**
     * Constructor.
     * 
     * @param map The map reference.
     */
    public void create(MapTile<?> map)
    {
        safe = new boolean[map.getInTileHeight()][map.getInTileWidth()];
    }

    /**
     * Special update routine for the inner area. The inner area can be used when a tile receive the CENTER value. The
     * adjacent tiles will be updated in order to match with the outer area.
     * 
     * @param map The axis20 map reference (map which will contain axis data).
     * @param tx The tile x to check.
     * @param ty The tile y to check.
     * @param tw The width in tile to check.
     * @param th The height in tile to check.
     * @param ray The ray to check (in tile).
     */
    public void updateInclude(Border20[][] map, int tx, int ty, int tw, int th, int ray)
    {
        for (int x = tx - ray + 1; x <= tx + ray + tw - 1; x++)
        {
            for (int y = ty - ray + 1; y <= ty + ray + th - 1; y++)
            {
                set(map, x, y, Border20.CENTER);
                setR(map, x, y + 1, Border20.DOWN);
                setR(map, x, y - 1, Border20.TOP);
                setR(map, x - 1, y, Border20.RIGHT);
                setR(map, x + 1, y, Border20.LEFT);
                setC(map, x - 1, y + 1, Border20.CORNER_DOWN_RIGHT);
                setC(map, x + 1, y + 1, Border20.CORNER_DOWN_LEFT);
                setC(map, x - 1, y - 1, Border20.CORNER_TOP_RIGHT);
                setC(map, x + 1, y - 1, Border20.CORNER_TOP_LEFT);
            }
        }
        checkAll(map, tx, ty, tw, th, ray);
    }

    /**
     * Special update routine for the outer area. The outer area can be used when a tile receive the NONE value. The
     * adjacent tiles will be updated in order to match with the outer area.
     * 
     * @param map The axis20 map reference (map which will contain axis data).
     * @param tx The tile x to check.
     * @param ty The tile y to check.
     */
    public void updateExclude(Border20[][] map, int tx, int ty)
    {
        Border20 l, r, t, d;
        int minY = -1, maxY = 1;
        int minX = -1, maxX = 1;

        if (get(map, tx, ty) == Border20.CORNER_DOWN_RIGHT)
        {
            minY = 0;
            minX = 0;
        }
        if (get(map, tx, ty) == Border20.CORNER_DOWN_LEFT)
        {
            minY = 0;
            maxX = 0;
        }
        if (get(map, tx, ty) == Border20.CORNER_TOP_RIGHT)
        {
            maxY = 0;
            minX = 0;
        }
        if (get(map, tx, ty) == Border20.CORNER_TOP_LEFT)
        {
            maxY = 0;
            maxX = 0;
        }

        set(map, tx, ty, Border20.NONE);

        for (int y = ty + minY; y <= ty + maxY; y++)
        {
            for (int x = tx + minX; x <= tx + maxX; x++)
            {
                if (y == ty && x == tx)
                {
                    continue;
                }
                l = left(map, x, y);
                r = right(map, x, y);
                t = top(map, x, y);
                d = down(map, x, y);

                if (l == Border20.NONE && r == Border20.NONE && t == Border20.NONE && d != Border20.NONE)
                {
                    set(map, x, y, Border20.DOWN_MIDDLE);
                }
                if (l == Border20.NONE && r == Border20.NONE && t != Border20.NONE && d == Border20.NONE)
                {
                    // set(map, v, h, Axis18.TOP_MIDDLE);
                }
                if (l != Border20.NONE && r != Border20.NONE && t != Border20.NONE && d == Border20.NONE)
                {
                    set(map, x, y, Border20.TOP);
                }
                if (l != Border20.NONE && r != Border20.NONE && t == Border20.NONE && d != Border20.NONE)
                {
                    set(map, x, y, Border20.DOWN);
                }
                if (l == Border20.NONE && r != Border20.NONE && t != Border20.NONE && d != Border20.NONE)
                {
                    set(map, x, y, Border20.RIGHT);
                }
                if (l != Border20.NONE && r == Border20.NONE && t != Border20.NONE && d != Border20.NONE)
                {
                    set(map, x, y, Border20.LEFT);
                }
                if (l == Border20.NONE && r != Border20.NONE && t == Border20.NONE && d != Border20.NONE)
                {
                    set(map, x, y, Border20.CORNER_DOWN_RIGHT);
                }
                if (l != Border20.NONE && r == Border20.NONE && t == Border20.NONE && d != Border20.NONE)
                {
                    set(map, x, y, Border20.CORNER_DOWN_LEFT);
                }
                if (l == Border20.NONE && r != Border20.NONE && t != Border20.NONE && d == Border20.NONE)
                {
                    set(map, x, y, Border20.CORNER_TOP_RIGHT);
                }
                if (l != Border20.NONE && r == Border20.NONE && t != Border20.NONE && d == Border20.NONE)
                {
                    set(map, x, y, Border20.CORNER_TOP_LEFT);
                }
            }
        }
        checkAll(map, tx, ty, 0, 0, 1);
    }

    /**
     * Check all tiles around the specified location using the specified ray (area). All adjacent tiles will be adjusted
     * in order to make match two linked areas.
     * 
     * @param map The axis20 map reference (map which will contain axis data).
     * @param tx The tile x to check.
     * @param ty The tile y to check.
     * @param tw The width in tile to check.
     * @param th The height in tile to check.
     * @param ray The ray to check (in tile).
     */
    public void checkAll(Border20[][] map, int tx, int ty, int tw, int th, int ray)
    {
        for (int y = ty - ray - 2; y <= ty + ray + th + 2; y++)
        {
            for (int x = tx - ray - 2; x <= tx + ray + tw + 2; x++)
            {
                check(map, x, y);
            }
        }
        for (int y = ty - ray - 2; y <= ty + ray + th + 2; y++)
        {
            for (int x = tx - ray - 2; x <= tx + ray + tw + 2; x++)
            {
                check(map, x, y);
            }
        }
    }

    /**
     * Usually called in case of matching error. This function will perform some last checks.
     * 
     * @param map The axis20 map reference (map which will contain axis data).
     * @param tx The tile x to check.
     * @param ty The tile y to check.
     */
    public void finalCheck(Border20[][] map, int tx, int ty)
    {
        Border20 l, r, t, d;
        for (int y = ty - 1; y <= ty + 1; y++)
        {
            for (int x = tx - 1; x <= tx + 1; x++)
            {
                l = left(map, x, y);
                r = right(map, x, y);
                t = top(map, x, y);
                d = down(map, x, y);

                if (get(map, x, y) == Border20.CORNER_DOWN_LEFT || get(map, x, y) == Border20.CORNER_DOWN_RIGHT)
                {
                    if (l == Border20.NONE && r == Border20.NONE && t == Border20.NONE && d != Border20.NONE)
                    {
                        set(map, x, y, Border20.DOWN_MIDDLE);
                    }
                }
                if (get(map, x, y) == Border20.CORNER_TOP_LEFT || get(map, x, y) == Border20.CORNER_TOP_RIGHT)
                {
                    if (l == Border20.NONE && r == Border20.NONE && t != Border20.NONE && d == Border20.NONE)
                    {
                        set(map, x, y, Border20.TOP_MIDDLE);
                    }
                }
            }
        }
    }

    /**
     * Set an axis value.
     * 
     * @param map The map reference.
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @param axis The axis value.
     */
    public void set(Border20[][] map, int tx, int ty, Border20 axis)
    {
        try
        {
            map[ty][tx] = axis;
            safe[ty][tx] = true;
        }
        catch (final ArrayIndexOutOfBoundsException exception)
        {
            // Ignore
        }
    }

    /**
     * Get axis value of the specified location.
     * 
     * @param map The map reference.
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @return The axis value.
     */
    public Border20 get(Border20[][] map, int tx, int ty)
    {
        try
        {
            return map[ty][tx];
        }
        catch (final ArrayIndexOutOfBoundsException ex)
        {
            return Border20.UNKNOWN;
        }
    }

    /**
     * Check the fog around this tile.
     * 
     * @param map The fog map.
     * @param tx The horizontal location.
     * @param ty The vertical location.
     */
    private void check(Border20[][] map, int tx, int ty)
    {
        final Border20 l = left(map, tx, ty);
        final Border20 r = right(map, tx, ty);
        final Border20 t = top(map, tx, ty);
        final Border20 d = down(map, tx, ty);

        // Center
        if (l == Border20.CENTER && r == Border20.CENTER && t != Border20.NONE && d != Border20.NONE
                || l != Border20.NONE && r != Border20.NONE && t == Border20.CENTER && d == Border20.CENTER)
        {
            set(map, tx, ty, Border20.CENTER);
        }

        // Top
        if ((l == Border20.TOP || l == Border20.CORNER_TOP_RIGHT)
                && (r == Border20.TOP || r == Border20.CORNER_TOP_LEFT)
                && (t == Border20.CENTER || t == Border20.DOWN)
                && (d == Border20.NONE || d == Border20.DOWN || d == Border20.CORNER_DOWN_RIGHT || d == Border20.CORNER_DOWN_LEFT))
        {
            set(map, tx, ty, Border20.TOP);
        }

        // Right
        if ((l == Border20.NONE || l == Border20.LEFT || l == Border20.CORNER_DOWN_LEFT || l == Border20.CORNER_TOP_LEFT)
                && (r == Border20.CENTER || r == Border20.LEFT)
                && (t == Border20.RIGHT || t == Border20.CORNER_DOWN_RIGHT)
                && (d == Border20.RIGHT || d == Border20.CORNER_TOP_RIGHT))
        {
            set(map, tx, ty, Border20.RIGHT);
        }

        // Down
        if ((l == Border20.DOWN || l == Border20.CORNER_DOWN_RIGHT)
                && (r == Border20.DOWN || r == Border20.CORNER_DOWN_LEFT)
                && (t == Border20.NONE || t == Border20.TOP || t == Border20.CORNER_TOP_RIGHT || t == Border20.CORNER_TOP_LEFT)
                && (d == Border20.CENTER || d == Border20.TOP))
        {
            set(map, tx, ty, Border20.DOWN);
        }

        // Left
        if ((l == Border20.CENTER || l == Border20.RIGHT)
                && (r == Border20.NONE || r == Border20.RIGHT || r == Border20.CORNER_DOWN_RIGHT || r == Border20.CORNER_TOP_RIGHT)
                && (t == Border20.LEFT || t == Border20.CORNER_DOWN_LEFT)
                && (d == Border20.LEFT || d == Border20.CORNER_TOP_LEFT))
        {
            set(map, tx, ty, Border20.LEFT);
        }

        // Top-Left
        if ((l == Border20.CENTER || l == Border20.DOWN_RIGHT || l == Border20.TOP_RIGHT || l == Border20.RIGHT)
                && (r == Border20.TOP_RIGHT || r == Border20.CORNER_IN_TOP_DOWN || r == Border20.CORNER_OUT_TOP_DOWN
                        || r == Border20.TOP || r == Border20.CORNER_TOP_LEFT)
                && (t == Border20.CENTER || t == Border20.DOWN_RIGHT || t == Border20.DOWN_LEFT || t == Border20.DOWN)
                && (d == Border20.DOWN_LEFT || d == Border20.CORNER_IN_TOP_DOWN || d == Border20.CORNER_OUT_TOP_DOWN
                        || d == Border20.LEFT || d == Border20.CORNER_TOP_LEFT))
        {
            set(map, tx, ty, Border20.TOP_LEFT);
        }

        // Top-Right
        if ((l == Border20.TOP_LEFT || l == Border20.CORNER_IN_DOWN_TOP || l == Border20.CORNER_OUT_DOWN_TOP
                || l == Border20.TOP || l == Border20.CORNER_TOP_RIGHT)
                && (r == Border20.CENTER || r == Border20.DOWN_LEFT || r == Border20.TOP_LEFT || r == Border20.LEFT)
                && (t == Border20.CENTER || t == Border20.DOWN_LEFT || t == Border20.DOWN_RIGHT || t == Border20.DOWN)
                && (d == Border20.DOWN_RIGHT || d == Border20.CORNER_IN_DOWN_TOP || d == Border20.CORNER_OUT_DOWN_TOP
                        || d == Border20.RIGHT || d == Border20.CORNER_TOP_RIGHT))
        {
            set(map, tx, ty, Border20.TOP_RIGHT);
        }

        // Down-Right
        if ((l == Border20.DOWN_LEFT || l == Border20.CORNER_IN_TOP_DOWN || l == Border20.CORNER_OUT_TOP_DOWN
                || l == Border20.DOWN || l == Border20.CORNER_DOWN_RIGHT)
                && (r == Border20.CENTER || r == Border20.TOP_LEFT || r == Border20.DOWN_LEFT || r == Border20.LEFT)
                && (t == Border20.TOP_RIGHT || t == Border20.CORNER_IN_TOP_DOWN || t == Border20.CORNER_OUT_TOP_DOWN
                        || t == Border20.RIGHT || t == Border20.CORNER_DOWN_RIGHT)
                && (d == Border20.CENTER || d == Border20.TOP_LEFT || d == Border20.TOP_RIGHT || d == Border20.TOP))
        {
            set(map, tx, ty, Border20.DOWN_RIGHT);
        }

        // Down-Left
        if ((l == Border20.CENTER || l == Border20.TOP_RIGHT || l == Border20.DOWN_RIGHT || l == Border20.RIGHT)
                && (r == Border20.DOWN_RIGHT || r == Border20.CORNER_IN_DOWN_TOP || r == Border20.CORNER_OUT_DOWN_TOP
                        || r == Border20.DOWN || r == Border20.CORNER_DOWN_LEFT)
                && (t == Border20.TOP_LEFT || t == Border20.CORNER_IN_DOWN_TOP || t == Border20.CORNER_OUT_DOWN_TOP
                        || t == Border20.LEFT || t == Border20.CORNER_DOWN_LEFT)
                && (d == Border20.CENTER || d == Border20.TOP_RIGHT || d == Border20.TOP_LEFT || d == Border20.TOP))
        {
            set(map, tx, ty, Border20.DOWN_LEFT);
        }

        // Corner-Top-Left
        if ((l == Border20.TOP_LEFT || l == Border20.CORNER_IN_DOWN_TOP || l == Border20.TOP || l == Border20.CORNER_TOP_RIGHT)
                && (r == Border20.NONE || r == Border20.RIGHT || r == Border20.CORNER_TOP_RIGHT || r == Border20.CORNER_DOWN_RIGHT)
                && (t == Border20.TOP_LEFT || t == Border20.CORNER_IN_DOWN_TOP || t == Border20.LEFT || t == Border20.CORNER_DOWN_LEFT)
                && (d == Border20.NONE || d == Border20.DOWN || d == Border20.CORNER_DOWN_LEFT || d == Border20.CORNER_DOWN_RIGHT))
        {
            set(map, tx, ty, Border20.CORNER_TOP_LEFT);
        }

        // Corner-Top-Right
        if ((l == Border20.NONE || l == Border20.LEFT || l == Border20.CORNER_DOWN_LEFT || l == Border20.CORNER_TOP_LEFT)
                && (r == Border20.TOP_RIGHT || r == Border20.CORNER_IN_TOP_DOWN || r == Border20.TOP || r == Border20.CORNER_TOP_LEFT)
                && (t == Border20.TOP_RIGHT || t == Border20.CORNER_IN_TOP_DOWN || t == Border20.RIGHT || t == Border20.CORNER_DOWN_RIGHT)
                && (d == Border20.NONE || d == Border20.DOWN || d == Border20.CORNER_DOWN_LEFT || d == Border20.CORNER_DOWN_RIGHT))
        {
            set(map, tx, ty, Border20.CORNER_TOP_RIGHT);
        }

        // Corner-Down-Right
        if ((l == Border20.NONE || l == Border20.LEFT || l == Border20.CORNER_TOP_LEFT || l == Border20.CORNER_DOWN_LEFT)
                && (r == Border20.DOWN_RIGHT || r == Border20.CORNER_IN_DOWN_TOP || r == Border20.DOWN || r == Border20.CORNER_DOWN_LEFT)
                && (t == Border20.NONE || t == Border20.TOP || t == Border20.CORNER_TOP_LEFT || t == Border20.CORNER_TOP_RIGHT)
                && (d == Border20.DOWN_RIGHT || d == Border20.CORNER_IN_DOWN_TOP || d == Border20.RIGHT || d == Border20.CORNER_TOP_RIGHT))
        {
            set(map, tx, ty, Border20.CORNER_DOWN_RIGHT);
        }

        // Corner-Down-Left
        if ((l == Border20.DOWN_LEFT || l == Border20.CORNER_IN_TOP_DOWN || l == Border20.DOWN || l == Border20.CORNER_DOWN_RIGHT)
                && (r == Border20.NONE || r == Border20.RIGHT || r == Border20.TOP_RIGHT || r == Border20.DOWN_RIGHT)
                && (t == Border20.NONE || t == Border20.TOP || t == Border20.CORNER_TOP_LEFT || t == Border20.CORNER_TOP_RIGHT)
                && (d == Border20.DOWN_LEFT || d == Border20.CORNER_IN_TOP_DOWN || d == Border20.LEFT || d == Border20.CORNER_TOP_LEFT))
        {
            set(map, tx, ty, Border20.CORNER_DOWN_LEFT);
        }

        // Corner-In-Top-Down
        if ((l == Border20.TOP_LEFT || l == Border20.CORNER_OUT_DOWN_TOP || l == Border20.TOP || l == Border20.CORNER_TOP_RIGHT)
                && (r == Border20.DOWN_RIGHT || r == Border20.CORNER_OUT_DOWN_TOP || r == Border20.DOWN || r == Border20.CORNER_DOWN_LEFT)
                && (t == Border20.TOP_LEFT || t == Border20.CORNER_OUT_DOWN_TOP || t == Border20.LEFT || t == Border20.CORNER_DOWN_LEFT)
                && (d == Border20.DOWN_RIGHT || d == Border20.CORNER_OUT_DOWN_TOP || d == Border20.RIGHT || d == Border20.CORNER_TOP_RIGHT))
        {
            set(map, tx, ty, Border20.CORNER_IN_TOP_DOWN);
        }

        // Corner-In-Down-Top
        if ((l == Border20.DOWN_LEFT || l == Border20.CORNER_OUT_TOP_DOWN || l == Border20.DOWN || l == Border20.CORNER_DOWN_RIGHT)
                && (r == Border20.TOP_RIGHT || r == Border20.CORNER_OUT_TOP_DOWN || r == Border20.TOP || r == Border20.CORNER_TOP_LEFT)
                && (t == Border20.TOP_RIGHT || t == Border20.CORNER_OUT_TOP_DOWN || t == Border20.RIGHT || t == Border20.CORNER_DOWN_RIGHT)
                && (d == Border20.DOWN_LEFT || d == Border20.CORNER_OUT_TOP_DOWN || d == Border20.LEFT || d == Border20.CORNER_TOP_LEFT))
        {
            set(map, tx, ty, Border20.CORNER_IN_DOWN_TOP);
        }

        // Corner-Out-Top-Down
        if ((l == Border20.CORNER_IN_DOWN_TOP || l == Border20.TOP || l == Border20.CORNER_TOP_RIGHT)
                && (r == Border20.CORNER_IN_DOWN_TOP || r == Border20.DOWN_RIGHT || r == Border20.DOWN || r == Border20.CORNER_DOWN_LEFT)
                && (t == Border20.CORNER_IN_DOWN_TOP || t == Border20.LEFT || t == Border20.CORNER_DOWN_LEFT)
                && (d == Border20.CORNER_IN_DOWN_TOP || d == Border20.DOWN_RIGHT || d == Border20.RIGHT || d == Border20.CORNER_TOP_RIGHT))
        {
            set(map, tx, ty, Border20.CORNER_OUT_TOP_DOWN);
        }

        // Corner-Out-Down-Top
        if ((l == Border20.CORNER_IN_TOP_DOWN || l == Border20.DOWN_LEFT || l == Border20.DOWN || l == Border20.CORNER_DOWN_RIGHT)
                && (r == Border20.CORNER_IN_TOP_DOWN || r == Border20.TOP || r == Border20.CORNER_TOP_LEFT)
                && (t == Border20.CORNER_IN_TOP_DOWN || t == Border20.RIGHT || t == Border20.CORNER_DOWN_RIGHT)
                && (d == Border20.CORNER_IN_TOP_DOWN || d == Border20.DOWN_LEFT || d == Border20.LEFT || d == Border20.CORNER_TOP_LEFT))
        {
            set(map, tx, ty, Border20.CORNER_OUT_DOWN_TOP);
        }

        if (middle)
        {
            // Right-Middle
            if ((l == Border20.NONE || l == Border20.LEFT || l == Border20.CORNER_DOWN_LEFT || l == Border20.CORNER_TOP_LEFT)
                    && (r == Border20.CENTER || r == Border20.LEFT || r == Border20.TOP)
                    && (t == Border20.DOWN_MIDDLE || t == Border20.RIGHT || t == Border20.CORNER_DOWN_RIGHT)
                    && (d == Border20.TOP_MIDDLE || d == Border20.RIGHT || d == Border20.CORNER_TOP_RIGHT))
            {
                set(map, tx, ty, Border20.RIGHT_MIDDLE);
            }

            // Left-Middle
            if ((l == Border20.CENTER || l == Border20.RIGHT)
                    && (r == Border20.NONE || r == Border20.RIGHT || r == Border20.CORNER_DOWN_RIGHT || r == Border20.CORNER_TOP_RIGHT)
                    && (t == Border20.DOWN_MIDDLE || t == Border20.LEFT || t == Border20.CORNER_DOWN_LEFT)
                    && (d == Border20.TOP_MIDDLE || d == Border20.LEFT || d == Border20.CORNER_TOP_LEFT))
            {
                set(map, tx, ty, Border20.LEFT_MIDDLE);
            }

            // Top-Middle
            if (l == Border20.NONE && r == Border20.NONE
                    && (t == Border20.DOWN_MIDDLE || t == Border20.LEFT_MIDDLE || t == Border20.RIGHT_MIDDLE)
                    && d == Border20.NONE)
            {
                set(map, tx, ty, Border20.TOP_MIDDLE);
            }

            // Down-Middle
            if (l == Border20.NONE && r == Border20.NONE && t == Border20.NONE
                    && (d == Border20.TOP_MIDDLE || d == Border20.LEFT_MIDDLE || t == Border20.RIGHT_MIDDLE))
            {
                set(map, tx, ty, Border20.DOWN_MIDDLE);
            }
        }
    }

    /**
     * Set the main fog.
     * 
     * @param map The map reference.
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @param fog The fog value.
     */
    private void setR(Border20[][] map, int tx, int ty, Border20 fog)
    {
        try
        {
            if (get(map, tx, ty) != Border20.CENTER)
            {
                map[ty][tx] = fog;
                safe[ty][tx] = true;
            }
        }
        catch (final ArrayIndexOutOfBoundsException ex)
        {
            // Ignore
        }
    }

    /**
     * Set the border fog.
     * 
     * @param map The map reference.
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @param fog The fog value.
     */
    private void setC(Border20[][] map, int tx, int ty, Border20 fog)
    {
        try
        {
            if (!safe[ty][tx])
            {
                map[ty][tx] = fog;
            }
        }
        catch (final ArrayIndexOutOfBoundsException ex)
        {
            // Ignore
        }
    }

    /**
     * Get the top from this location.
     * 
     * @param map The map reference.
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @return The fog value.
     */
    private Border20 top(Border20[][] map, int tx, int ty)
    {
        return get(map, tx, ty + 1);
    }

    /**
     * Get the right from this location.
     * 
     * @param map The map reference.
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @return The fog value.
     */
    private Border20 right(Border20[][] map, int tx, int ty)
    {
        return get(map, tx + 1, ty);
    }

    /**
     * Get the down from this location.
     * 
     * @param map The map reference.
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @return The fog value.
     */
    private Border20 down(Border20[][] map, int tx, int ty)
    {
        return get(map, tx, ty - 1);
    }

    /**
     * Get the left from this location.
     * 
     * @param map The map reference.
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @return The fog value.
     */
    private Border20 left(Border20[][] map, int tx, int ty)
    {
        return get(map, tx - 1, ty);
    }
}
