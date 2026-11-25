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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.DefinitionException;
import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * Sheets of a group and the number of its handles, shared by every {@link SheetGroup} handle acquired on it (ADR-0019,
 * ADR-0026, REQ-SHEET-002).
 *
 * <p>
 * Each sheet holds one handle on its image, acquired from the image cache when the sheet is added; the last release
 * of the group releases them, in the order the sheets were added. Runtime thread only, checked through the cache.
 * </p>
 */
final class SheetGroupEntry
{
    /** Error message of a sheet already in the group. */
    private static final String ERROR_DUPLICATE = "Sheet id already in the group: ";
    /** Error message of a sheet not in the group. */
    private static final String ERROR_UNKNOWN = "Sheet id not in the group: ";
    /** Name of the id argument, for the messages. */
    private static final String NAME_ID = "Id";

    /** The cache of the images. */
    private final ImageCache cache;
    /** The sheets by id. */
    private final Map<ResourceKey, SpriteSheet> sheets = new HashMap<>();
    /** The sheets in the order they were added. */
    private final List<SpriteSheet> order = new ArrayList<>();
    /** Number of handles not released. */
    private int references = 1;

    /**
     * Creates an empty group with one handle.
     *
     * @param cache The cache of the images.
     */
    SheetGroupEntry(ImageCache cache)
    {
        super();

        this.cache = cache;
    }

    /**
     * Checks that the calling thread owns the cache.
     *
     * @throws IllegalStateException If called on another thread than the owner of the cache.
     */
    void checkThread()
    {
        cache.checkThread();
    }

    /**
     * Adds a sheet.
     *
     * @param id The id of the sheet.
     * @param image The resource key of its image.
     * @param grid The grid.
     * @param pivotX The horizontal pivot in pixels, finite.
     * @param pivotY The vertical pivot in pixels, finite.
     * @param check The check of the image size before the sheet is created, {@code null} if none.
     * @return The sheet.
     * @throws IllegalArgumentException If an argument is {@code null}, a pivot is not finite, or the grid does not fit
     *             in the image.
     * @throws IllegalStateException If the id is already in the group.
     */
    SpriteSheet add(ResourceKey id, ResourceKey image, SheetGrid grid, double pivotX, double pivotY, SheetCheck check)
    {
        ImageCache.checkNotNull(id, NAME_ID);
        ImageCache.checkNotNull(image, "Image");
        ImageCache.checkNotNull(grid, "Grid");
        if (!Double.isFinite(pivotX) || !Double.isFinite(pivotY))
        {
            throw new IllegalArgumentException("Pivot not finite: " + pivotX + ", " + pivotY);
        }
        if (sheets.containsKey(id))
        {
            throw new IllegalStateException(ERROR_DUPLICATE + id);
        }
        final Image handle = cache.load(image);
        final SpriteSheet sheet;
        try
        {
            if (check != null)
            {
                check.check(handle.getWidth(), handle.getHeight());
            }
            sheet = new SpriteSheet(id, handle, grid, pivotX, pivotY);
        }
        catch (final IllegalArgumentException | DefinitionException exception)
        {
            handle.release();
            throw exception;
        }
        sheets.put(id, sheet);
        order.add(sheet);
        return sheet;
    }

    /**
     * Returns a sheet.
     *
     * @param id The id of the sheet.
     * @return The sheet.
     * @throws IllegalArgumentException If the id is {@code null}.
     * @throws IllegalStateException If the id is not in the group.
     */
    SpriteSheet get(ResourceKey id)
    {
        ImageCache.checkNotNull(id, NAME_ID);
        final SpriteSheet sheet = sheets.get(id);
        if (sheet == null)
        {
            throw new IllegalStateException(ERROR_UNKNOWN + id);
        }
        return sheet;
    }

    /**
     * Tells whether a sheet is in the group.
     *
     * @param id The id of the sheet.
     * @return {@code true} if the group holds a sheet under this id.
     * @throws IllegalArgumentException If the id is {@code null}.
     */
    boolean has(ResourceKey id)
    {
        ImageCache.checkNotNull(id, NAME_ID);
        return sheets.containsKey(id);
    }

    /**
     * Returns the number of sheets.
     *
     * @return The number of sheets in the group.
     */
    int size()
    {
        return order.size();
    }

    /**
     * Adds a handle.
     */
    void acquire()
    {
        references++;
    }

    /**
     * Releases a handle; the last one releases the image of every sheet.
     */
    void release()
    {
        references--;
        if (references == 0)
        {
            final int count = order.size();
            for (int i = 0; i < count; i++)
            {
                order.get(i).getImage().release();
            }
            order.clear();
            sheets.clear();
        }
    }
}
