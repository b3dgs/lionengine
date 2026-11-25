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
package com.b3dgs.lionengine.graphics;

import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * Image cut into a grid of cells, the frames of sprites and the tiles of a tileset (ADR-0003, ADR-0026, ADR-0033,
 * REQ-SHEET-001, REQ-SHEET-002).
 *
 * <p>
 * A sheet is created by a {@link SheetGroup}, which holds it under its id and holds its image: every cell is a region
 * of the same cached immutable image, drawn with its texture. Cells are numbered row by row from the top-left cell,
 * starting at {@code 0}; {@link #getCellX(int)} and {@link #getCellY(int)} give the top-left corner of a cell in pixels
 * of the image, and the cell size gives the rest of its source rectangle. These lookups allocate nothing.
 * </p>
 *
 * <p>
 * The grid never changes. Once the group is released, the geometry stays readable but the image is released.
 * Runtime thread only, like its group (ADR-0018).
 * </p>
 *
 * @since 11.0.0
 */
public final class SpriteSheet
{
    /** Error message of a cell outside the sheet. */
    private static final String ERROR_CELL = "Cell outside the sheet ";

    /**
     * Counts the cells along an axis.
     *
     * @param id The sheet id, for the messages.
     * @param size The size of the image along the axis, in pixels.
     * @param cell The size of a cell along the axis, in pixels.
     * @param count The number of cells requested, {@link SheetGrid#DERIVED} to derive it.
     * @param margin The margin in pixels.
     * @param spacing The spacing in pixels.
     * @return The number of cells.
     * @throws IllegalArgumentException If no cell fits, or the requested cells do not fit in the image.
     */
    private static int count(ResourceKey id, int size, int cell, int count, int margin, int spacing)
    {
        final long result;
        if (count == SheetGrid.DERIVED)
        {
            result = ((long) size - margin + spacing) / ((long) cell + spacing);
        }
        else
        {
            result = count;
        }
        if (result <= 0 || (long) margin + result * cell + (result - 1) * spacing > size)
        {
            throw new IllegalArgumentException("Sheet grid outside its image: "
                                               + id
                                               + ", "
                                               + result
                                               + " cells of "
                                               + cell
                                               + " pixels in "
                                               + size);
        }
        return (int) result;
    }

    /** The id of the sheet in its group. */
    private final ResourceKey id;
    /** The image, held by the group. */
    private final Image image;
    /** Width of a cell in pixels. */
    private final int cellWidth;
    /** Height of a cell in pixels. */
    private final int cellHeight;
    /** Number of columns. */
    private final int columns;
    /** Number of rows. */
    private final int rows;
    /** Margin in pixels. */
    private final int margin;
    /** Spacing in pixels. */
    private final int spacing;
    /** Number of cells. */
    private final int cellCount;

    /**
     * Creates a sheet over an image, for {@link SheetGroup} only.
     *
     * @param id The id of the sheet.
     * @param image The image, held by the group.
     * @param grid The grid; derived numbers of columns and rows are computed from the size of the image.
     * @throws IllegalArgumentException If no cell fits, or the requested cells do not fit in the image.
     */
    SpriteSheet(ResourceKey id, Image image, SheetGrid grid)
    {
        super();

        this.id = id;
        this.image = image;
        cellWidth = grid.cellWidth();
        cellHeight = grid.cellHeight();
        margin = grid.margin();
        spacing = grid.spacing();
        columns = count(id, image.getWidth(), cellWidth, grid.columns(), margin, spacing);
        rows = count(id, image.getHeight(), cellHeight, grid.rows(), margin, spacing);
        cellCount = columns * rows;
    }

    /**
     * Returns the id of the sheet.
     *
     * @return The id, unique in its group.
     */
    public ResourceKey getId()
    {
        return id;
    }

    /**
     * Returns the image of the sheet, to draw its cells with the texture of the image.
     *
     * @return The image handle held by the group: the group releases it, never the caller.
     */
    public Image getImage()
    {
        return image;
    }

    /**
     * Returns the width of a cell.
     *
     * @return The width in pixels.
     */
    public int getCellWidth()
    {
        return cellWidth;
    }

    /**
     * Returns the height of a cell.
     *
     * @return The height in pixels.
     */
    public int getCellHeight()
    {
        return cellHeight;
    }

    /**
     * Returns the number of columns.
     *
     * @return The number of columns, given or derived from the width of the image.
     */
    public int getColumns()
    {
        return columns;
    }

    /**
     * Returns the number of rows.
     *
     * @return The number of rows, given or derived from the height of the image.
     */
    public int getRows()
    {
        return rows;
    }

    /**
     * Returns the margin.
     *
     * @return The free space in pixels between the top and left edges of the image and the first cell.
     */
    public int getMargin()
    {
        return margin;
    }

    /**
     * Returns the spacing.
     *
     * @return The free space in pixels between two cells.
     */
    public int getSpacing()
    {
        return spacing;
    }

    /**
     * Returns the number of cells.
     *
     * @return The number of cells, columns times rows; valid indexes go from {@code 0} to this number minus one.
     */
    public int getCellCount()
    {
        return cellCount;
    }

    /**
     * Returns the left edge of a cell in the image.
     *
     * @param index The cell index, from {@code 0} at the top-left, row by row.
     * @return The horizontal position of the cell's top-left pixel in the image, in pixels.
     * @throws IndexOutOfBoundsException If the index is outside {@code [0, getCellCount())}.
     */
    public int getCellX(int index)
    {
        checkIndex(index);
        return margin + index % columns * (cellWidth + spacing);
    }

    /**
     * Returns the top edge of a cell in the image.
     *
     * @param index The cell index, from {@code 0} at the top-left, row by row.
     * @return The vertical position of the cell's top-left pixel in the image, in pixels.
     * @throws IndexOutOfBoundsException If the index is outside {@code [0, getCellCount())}.
     */
    public int getCellY(int index)
    {
        checkIndex(index);
        return margin + index / columns * (cellHeight + spacing);
    }

    /**
     * Checks a cell index.
     *
     * @param index The index.
     * @throws IndexOutOfBoundsException If the index is outside {@code [0, getCellCount())}.
     */
    private void checkIndex(int index)
    {
        if (index < 0 || index >= cellCount)
        {
            throw new IndexOutOfBoundsException(ERROR_CELL + id + ": " + index + " of " + cellCount);
        }
    }
}
