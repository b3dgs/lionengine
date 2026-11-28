/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.traits;

import com.b3dgs.lionengine.physics.Box;
import com.b3dgs.lionengine.physics.ShapeQuery;

/**
 * A grid of solid tiles of 1 m, origin {@code (0, 0)}, as a {@link ShapeQuery}: the stand-in of the tile map of
 * {@code tilemap} for the tests of the tile push-out (ADR-0083).
 *
 * <p>
 * Cell {@code (column, row)} covers {@code [column, column + 1] x [row, row + 1]}; the found tiles are written row by
 * row, then column by column, bottom-left first; allocates nothing per query.
 * </p>
 */
final class TileGrid implements ShapeQuery
{
    /** Columns. */
    private final int columns;
    /** Rows. */
    private final int rows;
    /** Solid cells, row-major. */
    private final boolean[] solids;

    /**
     * Creates an empty grid.
     *
     * @param columns The columns.
     * @param rows The rows.
     */
    TileGrid(int columns, int rows)
    {
        this.columns = columns;
        this.rows = rows;
        solids = new boolean[columns * rows];
    }

    /**
     * Sets a cell solid or empty.
     *
     * @param column The column.
     * @param row The row.
     * @param solid {@code true} for solid.
     * @return This grid.
     */
    TileGrid set(int column, int row, boolean solid)
    {
        solids[row * columns + column] = solid;
        return this;
    }

    /**
     * Returns whether a cell is solid.
     *
     * @param column The column.
     * @param row The row.
     * @return {@code true} if solid.
     */
    boolean isSolid(int column, int row)
    {
        return solids[row * columns + column];
    }

    /**
     * Fills a rectangle of cells solid.
     *
     * @param column The first column.
     * @param row The first row.
     * @param width The columns.
     * @param height The rows.
     * @return This grid.
     */
    TileGrid fill(int column, int row, int width, int height)
    {
        for (int r = row; r < row + height; r++)
        {
            for (int c = column; c < column + width; c++)
            {
                set(c, r, true);
            }
        }
        return this;
    }

    @Override
    public int query(Box area, Box[] results)
    {
        if (area == null || results == null)
        {
            throw new IllegalArgumentException("Area or results are null");
        }
        final int firstColumn = Math.max(0, (int) Math.floor(area.getMinX()));
        final int lastColumn = Math.min(columns - 1, (int) Math.ceil(area.getMaxX()) - 1);
        final int firstRow = Math.max(0, (int) Math.floor(area.getMinY()));
        final int lastRow = Math.min(rows - 1, (int) Math.ceil(area.getMaxY()) - 1);
        int count = 0;
        for (int row = firstRow; row <= lastRow; row++)
        {
            for (int column = firstColumn; column <= lastColumn; column++)
            {
                if (solids[row * columns + column]
                    && Box.overlaps(column, column + 1.0, area.getMinX(), area.getMaxX())
                    && Box.overlaps(row, row + 1.0, area.getMinY(), area.getMaxY()))
                {
                    if (count < results.length)
                    {
                        results[count].set(column + 0.5, row + 0.5, 1.0, 1.0);
                    }
                    count++;
                }
            }
        }
        return count;
    }
}
