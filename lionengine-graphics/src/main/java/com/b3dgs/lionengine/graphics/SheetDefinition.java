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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Configuration;
import com.b3dgs.lionengine.DefinitionException;
import com.b3dgs.lionengine.JsonArray;
import com.b3dgs.lionengine.JsonObject;
import com.b3dgs.lionengine.JsonValue;
import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * Content of a sprite sheet definition file, read and checked by {@link SheetLoader}, and the check of the image of
 * the sheet once loaded: the grid must fit and every animation frame must be a cell (ADR-0032, ADR-0033,
 * REQ-SHEET-003).
 */
final class SheetDefinition
{
    /** Field of the image key. */
    static final String IMAGE = "image";
    /** Field of the cell width. */
    static final String CELL_WIDTH = "cellWidth";
    /** Field of the cell height. */
    static final String CELL_HEIGHT = "cellHeight";
    /** Field of the columns. */
    static final String COLUMNS = "columns";
    /** Field of the rows. */
    static final String ROWS = "rows";
    /** Field of the margin. */
    static final String MARGIN = "margin";
    /** Field of the spacing. */
    static final String SPACING = "spacing";
    /** Field of the horizontal pivot. */
    static final String PIVOT_X = "pivotX";
    /** Field of the vertical pivot. */
    static final String PIVOT_Y = "pivotY";

    /** Half of a cell, for the default pivot. */
    private static final double HALF = 2.0;
    /** Error message of a value that is not positive. */
    private static final String ERROR_POSITIVE = "Not positive: ";
    /** Error message of a negative value. */
    private static final String ERROR_NEGATIVE = "Negative: ";
    /** Error message of a grid outside its image. */
    private static final String ERROR_GRID = "Sheet grid outside its image of ";
    /** Error message of a frame outside the sheet. */
    private static final String ERROR_FRAME = "Frame outside the sheet of ";
    /** Unit of the messages. */
    private static final String PIXELS = " pixels";
    /** Unit of the messages. */
    private static final String CELLS = " cells: ";

    /** The configuration of the file, for the errors. */
    private final Configuration configuration;
    /** The image key. */
    private final ResourceKey imageKey;
    /** The grid. */
    private final SheetGrid grid;
    /** The horizontal pivot in pixels. */
    private final double pivotX;
    /** The vertical pivot in pixels. */
    private final double pivotY;
    /** The frames of every animation, checked against the cells of the sheet. */
    private final List<JsonArray> frames = new ArrayList<>();
    /** The animations by name, in the order of the file. */
    private final Map<String, Animation> animations;
    /** The templates by name, in the order of the file. */
    private final Map<String, ColorMapping> templates;

    /**
     * Reads and checks a definition, except what needs the image.
     *
     * @param root The root object of the file.
     * @throws DefinitionException If the definition is not valid.
     */
    SheetDefinition(JsonObject root)
    {
        super();

        configuration = new Configuration(root);
        configuration.checkFormat(SheetLoader.FORMAT, SheetLoader.VERSION);
        imageKey = readImage(configuration);
        final int cellWidth = readPositive(configuration, CELL_WIDTH);
        final int cellHeight = readPositive(configuration, CELL_HEIGHT);
        final int columns = readCount(configuration, root, COLUMNS);
        final int rows = readCount(configuration, root, ROWS);
        grid = new SheetGrid(cellWidth,
                             cellHeight,
                             columns,
                             rows,
                             readNotNegative(configuration, MARGIN),
                             readNotNegative(configuration, SPACING));
        pivotX = configuration.getDouble(PIVOT_X, cellWidth / HALF);
        pivotY = configuration.getDouble(PIVOT_Y, cellHeight / HALF);
        animations = SheetAnimations.read(configuration, root, frames);
        templates = SheetTemplates.read(configuration, root);
        configuration.checkAllRead();
    }

    /**
     * Reads the image key.
     *
     * @param configuration The configuration of the file.
     * @return The image key.
     * @throws DefinitionException If the field is missing or not a valid resource key.
     */
    private static ResourceKey readImage(Configuration configuration)
    {
        final String path = configuration.getString(IMAGE);
        try
        {
            return new ResourceKey(path);
        }
        catch (final IllegalArgumentException exception)
        {
            final DefinitionException error = configuration.createError(IMAGE, exception.getMessage());
            error.initCause(exception);
            throw error;
        }
    }

    /**
     * Reads a required positive integer.
     *
     * @param configuration The configuration of the file.
     * @param name The field.
     * @return The value.
     * @throws DefinitionException If the field is missing, not an {@code int}, or not positive.
     */
    private static int readPositive(Configuration configuration, String name)
    {
        final int value = configuration.getInt(name);
        if (value <= 0)
        {
            throw configuration.createError(name, ERROR_POSITIVE + value);
        }
        return value;
    }

    /**
     * Reads an optional number of cells, positive when present.
     *
     * @param configuration The configuration of the file.
     * @param root The root object of the file, to know whether the field is present.
     * @param name The field.
     * @return The value, {@link SheetGrid#DERIVED} if the field is absent.
     * @throws DefinitionException If the field is not an {@code int}, or not positive.
     */
    private static int readCount(Configuration configuration, JsonObject root, String name)
    {
        int value = SheetGrid.DERIVED;
        if (root.has(name))
        {
            value = readPositive(configuration, name);
        }
        return value;
    }

    /**
     * Reads an optional integer, zero or positive, {@code 0} by default.
     *
     * @param configuration The configuration of the file.
     * @param name The field.
     * @return The value.
     * @throws DefinitionException If the field is not an {@code int} or is negative.
     */
    private static int readNotNegative(Configuration configuration, String name)
    {
        final int value = configuration.getInt(name, 0);
        if (value < 0)
        {
            throw configuration.createError(name, ERROR_NEGATIVE + value);
        }
        return value;
    }

    /**
     * Returns the image key.
     *
     * @return The resource key of the image.
     */
    ResourceKey getImage()
    {
        return imageKey;
    }

    /**
     * Returns the grid.
     *
     * @return The grid.
     */
    SheetGrid getGrid()
    {
        return grid;
    }

    /**
     * Returns the horizontal pivot.
     *
     * @return The pivot in pixels from the left edge of a cell.
     */
    double getPivotX()
    {
        return pivotX;
    }

    /**
     * Returns the vertical pivot.
     *
     * @return The pivot in pixels from the top edge of a cell.
     */
    double getPivotY()
    {
        return pivotY;
    }

    /**
     * Returns the animations.
     *
     * @return The animations by name, in the order of the file, unmodifiable; empty if none.
     */
    Map<String, Animation> getAnimations()
    {
        return animations;
    }

    /**
     * Returns the templates.
     *
     * @return The color mappings by name, in the order of the file, unmodifiable; empty if none.
     */
    Map<String, ColorMapping> getTemplates()
    {
        return templates;
    }

    /**
     * Checks the image of the sheet, after it is loaded and before the sheet is created: the grid must fit and every
     * animation frame must be a cell.
     *
     * @param width The width of the image in pixels.
     * @param height The height of the image in pixels.
     * @throws DefinitionException If the image does not suit the definition.
     */
    void check(int width, int height)
    {
        final int columns = SpriteSheet.count(width,
                                              grid.cellWidth(),
                                              grid.columns(),
                                              grid.margin(),
                                              grid.spacing());
        checkFits(columns, grid.columns(), COLUMNS, CELL_WIDTH, width);
        final int rows = SpriteSheet.count(height,
                                           grid.cellHeight(),
                                           grid.rows(),
                                           grid.margin(),
                                           grid.spacing());
        checkFits(rows, grid.rows(), ROWS, CELL_HEIGHT, height);
        final int cells = columns * rows;
        final int count = frames.size();
        for (int i = 0; i < count; i++)
        {
            final JsonArray list = frames.get(i);
            final int size = list.size();
            for (int j = 0; j < size; j++)
            {
                final JsonValue frame = list.get(j);
                if (frame.asInt() >= cells)
                {
                    throw new DefinitionException(ERROR_FRAME + cells + CELLS + frame.asInt(),
                                                  frame,
                                                  null,
                                                  SheetAnimations.FRAMES);
                }
            }
        }
    }

    /**
     * Checks that the cells of an axis fit in the image.
     *
     * @param count The number of cells that fit, {@code 0} if none.
     * @param given The number given by the file, {@link SheetGrid#DERIVED} if absent.
     * @param countField The field of the number of cells.
     * @param cellField The field of the cell size.
     * @param size The size of the image along the axis.
     * @throws DefinitionException If the cells do not fit, located at the number of cells if given, otherwise at the
     *             cell size.
     */
    private void checkFits(int count, int given, String countField, String cellField, int size)
    {
        if (count == 0)
        {
            final String field;
            if (given == SheetGrid.DERIVED)
            {
                field = cellField;
            }
            else
            {
                field = countField;
            }
            throw configuration.createError(field, ERROR_GRID + size + PIXELS);
        }
    }
}
