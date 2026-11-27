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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * Image cut into a grid of cells, the frames of sprites and the tiles of a tileset (ADR-0003, ADR-0026, ADR-0033,
 * ADR-0057, REQ-SHEET-001, REQ-SHEET-002, REQ-SPRITE-005, REQ-ANIM-001).
 *
 * <p>
 * A sheet is created by a {@link SheetGroup}, which holds it under its id and holds its image: every cell is a region
 * of the same cached immutable image, drawn with its texture. Cells are numbered row by row from the top-left cell,
 * starting at {@code 0}; {@link #getCellX(int)} and {@link #getCellY(int)} give the top-left corner of a cell in pixels
 * of the image, and the cell size gives the rest of its source rectangle. These lookups allocate nothing.
 * </p>
 *
 * <p>
 * The pivot of the cells, the point a sprite is placed, scaled and rotated about, is in pixels from the top-left
 * corner of a cell, the cell center by default (ADR-0026, ADR-0033).
 * </p>
 *
 * <p>
 * A sheet read from a definition file keeps the {@code animations} of the file: named {@link Animation} values whose
 * frames are cells of the sheet ({@link #getAnimation(String)}). A sheet added in code has no animation: its
 * animations are built in code.
 * </p>
 *
 * <p>
 * A sheet read from a definition file also keeps the {@code templates} of the file: named {@link ColorMapping} values,
 * such as team colors, to bake into variants of the image with {@link ImageCache#bake(Image, ColorTransform)} and
 * {@link ColorTransform#withMapping(ColorMapping)}, then draw with {@link Sprite#Sprite(SpriteSheet, Image)}. A sheet
 * added in code has no template: its colors are mapped in code the same way.
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
    /** Error message of an animation not in the sheet. */
    private static final String ERROR_ANIMATION = "Animation not in the sheet ";
    /** Name of the animation argument, for the messages. */
    private static final String NAME_ANIMATION = "Animation name";
    /** Error message of a template not in the sheet. */
    private static final String ERROR_TEMPLATE = "Template not in the sheet ";
    /** Separator of the messages. */
    private static final String COLON = ": ";
    /** Name of the template argument, for the messages. */
    private static final String NAME_TEMPLATE = "Template name";

    /**
     * Counts the cells along an axis.
     *
     * @param size The size of the image along the axis, in pixels.
     * @param cell The size of a cell along the axis, in pixels.
     * @param count The number of cells requested, {@link SheetGrid#DERIVED} to derive it.
     * @param margin The margin in pixels.
     * @param spacing The spacing in pixels.
     * @return The number of cells, {@code 0} if no cell fits or the requested cells do not fit in the image.
     */
    static int count(int size, int cell, int count, int margin, int spacing)
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
        final int fitting;
        if (result <= 0 || margin + result * cell + (result - 1) * spacing > size)
        {
            fitting = 0;
        }
        else
        {
            fitting = (int) result;
        }
        return fitting;
    }

    /**
     * Counts the cells along an axis, or fails.
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
    private static int countOrFail(ResourceKey id, int size, int cell, int count, int margin, int spacing)
    {
        final int result = count(size, cell, count, margin, spacing);
        if (result == 0)
        {
            throw new IllegalArgumentException("Sheet grid outside its image: "
                                               + id
                                               + ", "
                                               + count
                                               + " cells of "
                                               + cell
                                               + " pixels in "
                                               + size);
        }
        return result;
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
    /** Horizontal pivot in pixels from the left edge of a cell. */
    private final double pivotX;
    /** Vertical pivot in pixels from the top edge of a cell. */
    private final double pivotY;
    /** The animations by name, in the order of the definition, unmodifiable. */
    private final Map<String, Animation> animations;
    /** The names of the animations, in the order of the definition, unmodifiable. */
    private final List<String> animationNames;
    /** The templates by name, in the order of the definition, unmodifiable. */
    private final Map<String, ColorMapping> templates;
    /** The names of the templates, in the order of the definition, unmodifiable. */
    private final List<String> templateNames;

    /**
     * Creates a sheet over an image, for {@link SheetGroup} only.
     *
     * @param id The id of the sheet.
     * @param image The image, held by the group.
     * @param grid The grid; derived numbers of columns and rows are computed from the size of the image.
     * @param pivotX The horizontal pivot in pixels from the left edge of a cell.
     * @param pivotY The vertical pivot in pixels from the top edge of a cell.
     * @throws IllegalArgumentException If no cell fits, or the requested cells do not fit in the image.
     */
    SpriteSheet(ResourceKey id, Image image, SheetGrid grid, double pivotX, double pivotY)
    {
        this(id, image, grid, pivotX, pivotY, Collections.emptyMap(), Collections.emptyMap());
    }

    /**
     * Creates a sheet over an image with the animations and templates of its definition, for {@link SheetGroup}
     * only.
     *
     * @param id The id of the sheet.
     * @param image The image, held by the group.
     * @param grid The grid; derived numbers of columns and rows are computed from the size of the image.
     * @param pivotX The horizontal pivot in pixels from the left edge of a cell.
     * @param pivotY The vertical pivot in pixels from the top edge of a cell.
     * @param animations The animations by name, in the order of the definition, unmodifiable, their frames inside the
     *            sheet.
     * @param templates The templates by name, in the order of the definition, unmodifiable.
     * @throws IllegalArgumentException If no cell fits, or the requested cells do not fit in the image.
     */
    SpriteSheet(ResourceKey id,
                Image image,
                SheetGrid grid,
                double pivotX,
                double pivotY,
                Map<String, Animation> animations,
                Map<String, ColorMapping> templates)
    {
        super();

        this.id = id;
        this.image = image;
        cellWidth = grid.cellWidth();
        cellHeight = grid.cellHeight();
        margin = grid.margin();
        spacing = grid.spacing();
        columns = countOrFail(id, image.getWidth(), cellWidth, grid.columns(), margin, spacing);
        rows = countOrFail(id, image.getHeight(), cellHeight, grid.rows(), margin, spacing);
        cellCount = columns * rows;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.animations = animations;
        animationNames = List.copyOf(animations.keySet());
        this.templates = templates;
        templateNames = List.copyOf(templates.keySet());
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
     * Returns the horizontal pivot of the cells.
     *
     * @return The pivot in pixels from the left edge of a cell, half the cell width by default.
     */
    public double getPivotX()
    {
        return pivotX;
    }

    /**
     * Returns the vertical pivot of the cells.
     *
     * @return The pivot in pixels from the top edge of a cell, half the cell height by default.
     */
    public double getPivotY()
    {
        return pivotY;
    }

    /**
     * Returns the names of the animations.
     *
     * @return The names of the animations of the definition, in the order of the file, unmodifiable; empty for a sheet
     *         added in code or a definition without animation.
     */
    public List<String> getAnimationNames()
    {
        return animationNames;
    }

    /**
     * Tells whether the sheet has an animation.
     *
     * @param name The name of the animation (never {@code null}).
     * @return {@code true} if its definition has an animation of this name.
     * @throws IllegalArgumentException If the name is {@code null}.
     */
    public boolean hasAnimation(String name)
    {
        ImageCache.checkNotNull(name, NAME_ANIMATION);
        return animations.containsKey(name);
    }

    /**
     * Returns an animation of the definition, whose frames are cells of this sheet.
     *
     * @param name The name of the animation (never {@code null}).
     * @return The animation.
     * @throws IllegalArgumentException If the name is {@code null}.
     * @throws IllegalStateException If the sheet has no animation of this name (see {@link #hasAnimation(String)}).
     */
    public Animation getAnimation(String name)
    {
        ImageCache.checkNotNull(name, NAME_ANIMATION);
        final Animation animation = animations.get(name);
        if (animation == null)
        {
            throw new IllegalStateException(ERROR_ANIMATION + id + COLON + name);
        }
        return animation;
    }

    /**
     * Returns the names of the templates.
     *
     * @return The names of the templates of the definition, in the order of the file, unmodifiable; empty for a sheet
     *         added in code or a definition without template.
     */
    public List<String> getTemplateNames()
    {
        return templateNames;
    }

    /**
     * Tells whether the sheet has a template.
     *
     * @param name The name of the template (never {@code null}).
     * @return {@code true} if its definition has a template of this name.
     * @throws IllegalArgumentException If the name is {@code null}.
     */
    public boolean hasTemplate(String name)
    {
        ImageCache.checkNotNull(name, NAME_TEMPLATE);
        return templates.containsKey(name);
    }

    /**
     * Returns a template: the color mapping of its definition, such as team colors, to bake into a variant of the
     * image with {@link ColorTransform#withMapping(ColorMapping)} and {@link ImageCache#bake(Image, ColorTransform)}.
     *
     * @param name The name of the template (never {@code null}).
     * @return The color mapping, {@link ColorMapping#isIdentity()} for an empty template.
     * @throws IllegalArgumentException If the name is {@code null}.
     * @throws IllegalStateException If the sheet has no template of this name (see {@link #hasTemplate(String)}).
     */
    public ColorMapping getTemplate(String name)
    {
        ImageCache.checkNotNull(name, NAME_TEMPLATE);
        final ColorMapping mapping = templates.get(name);
        if (mapping == null)
        {
            throw new IllegalStateException(ERROR_TEMPLATE + id + COLON + name);
        }
        return mapping;
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
            throw new IndexOutOfBoundsException(ERROR_CELL + id + COLON + index + " of " + cellCount);
        }
    }
}
