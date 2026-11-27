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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test {@link SheetGrid} (REQ-SHEET-001).
 */
final class SheetGridTest
{
    /**
     * The short constructor derives the columns and rows, without margin or spacing.
     */
    @Test
    void testCellSizeOnly()
    {
        final SheetGrid grid = new SheetGrid(16, 8);

        assertEquals(new SheetGrid(16, 8, SheetGrid.DERIVED, SheetGrid.DERIVED, 0, 0), grid);
        assertEquals(16, grid.cellWidth());
        assertEquals(8, grid.cellHeight());
        assertEquals(0, grid.columns());
        assertEquals(0, grid.rows());
        assertEquals(0, grid.margin());
        assertEquals(0, grid.spacing());
    }

    /**
     * Every value is kept.
     */
    @Test
    void testValues()
    {
        final SheetGrid grid = new SheetGrid(16, 8, 4, 3, 2, 1);

        assertEquals(4, grid.columns());
        assertEquals(3, grid.rows());
        assertEquals(2, grid.margin());
        assertEquals(1, grid.spacing());
    }

    /**
     * Invalid values are refused, with a message naming the value.
     *
     * @param cellWidth The cell width.
     * @param cellHeight The cell height.
     * @param columns The columns.
     * @param rows The rows.
     * @param margin The margin.
     * @param spacing The spacing.
     * @param name The name expected in the message.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "0, 8, 0, 0, 0, 0, Cell width", "16, -1, 0, 0, 0, 0, Cell height", "16, 8, -1, 0, 0, 0, Columns",
        "16, 8, 0, -2, 0, 0, Rows", "16, 8, 0, 0, -1, 0, Margin", "16, 8, 0, 0, 0, -1, Spacing"
    })
    void testInvalid(int cellWidth, int cellHeight, int columns, int rows, int margin, int spacing, String name)
    {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                                                () -> new SheetGrid(cellWidth,
                                                                                    cellHeight,
                                                                                    columns,
                                                                                    rows,
                                                                                    margin,
                                                                                    spacing));
        assertTrue(exception.getMessage().startsWith(name), exception.getMessage());
    }
}
