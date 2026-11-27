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

/**
 * Grid of a sprite sheet: the size of its cells, their number, and the free space around and between them, all in
 * pixels of the image (ADR-0003, ADR-0033, REQ-SHEET-001).
 *
 * <p>
 * The first cell starts at ({@code margin}, {@code margin}) from the top-left corner of the image; cells are separated
 * by {@code spacing} pixels horizontally and vertically. A number of columns or rows equal to {@link #DERIVED} is
 * derived from the size of the image: as many cells as fit, as Tiled and Godot count them,
 * {@code (imageWidth - margin + spacing) / (cellWidth + spacing)} columns and likewise for the rows.
 * </p>
 *
 * <p>
 * Immutable and thread-safe.
 * </p>
 *
 * @param cellWidth The width of a cell in pixels (positive).
 * @param cellHeight The height of a cell in pixels (positive).
 * @param columns The number of columns (positive), or {@link #DERIVED} to derive it from the width of the image.
 * @param rows The number of rows (positive), or {@link #DERIVED} to derive it from the height of the image.
 * @param margin The free space in pixels between the top and left edges of the image and the first cell (zero or
 *            positive).
 * @param spacing The free space in pixels between two cells, horizontally and vertically (zero or positive).
 * @since 11.0.0
 */
public record SheetGrid(int cellWidth, int cellHeight, int columns, int rows, int margin, int spacing)
{
    /** Number of columns or rows derived from the size of the image. */
    public static final int DERIVED = 0;

    /** Error message of an invalid value: separator. */
    private static final String ERROR_SEPARATOR = ": ";

    /**
     * Checks that a value is positive.
     *
     * @param value The value.
     * @param name The name of the value, for the message.
     * @throws IllegalArgumentException If the value is not positive.
     */
    private static void checkPositive(int value, String name)
    {
        if (value <= 0)
        {
            throw new IllegalArgumentException(name + " not positive" + ERROR_SEPARATOR + value);
        }
    }

    /**
     * Checks that a value is not negative.
     *
     * @param value The value.
     * @param name The name of the value, for the message.
     * @throws IllegalArgumentException If the value is negative.
     */
    private static void checkNotNegative(int value, String name)
    {
        if (value < 0)
        {
            throw new IllegalArgumentException(name + " negative" + ERROR_SEPARATOR + value);
        }
    }

    /**
     * Creates a grid.
     *
     * @param cellWidth The width of a cell in pixels (positive).
     * @param cellHeight The height of a cell in pixels (positive).
     * @param columns The number of columns (positive), or {@link #DERIVED}.
     * @param rows The number of rows (positive), or {@link #DERIVED}.
     * @param margin The free space in pixels before the first cell (zero or positive).
     * @param spacing The free space in pixels between two cells (zero or positive).
     * @throws IllegalArgumentException If a cell size is not positive, or another value is negative.
     */
    public SheetGrid
    {
        checkPositive(cellWidth, "Cell width");
        checkPositive(cellHeight, "Cell height");
        checkNotNegative(columns, "Columns");
        checkNotNegative(rows, "Rows");
        checkNotNegative(margin, "Margin");
        checkNotNegative(spacing, "Spacing");
    }

    /**
     * Creates a grid of cells covering the image from its top-left corner, without margin or spacing, with the
     * numbers of columns and rows derived from the size of the image.
     *
     * @param cellWidth The width of a cell in pixels (positive).
     * @param cellHeight The height of a cell in pixels (positive).
     * @throws IllegalArgumentException If a cell size is not positive.
     */
    public SheetGrid(int cellWidth, int cellHeight)
    {
        this(cellWidth, cellHeight, DERIVED, DERIVED, 0, 0);
    }
}
