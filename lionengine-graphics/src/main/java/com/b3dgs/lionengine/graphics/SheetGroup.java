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
 * Sprite sheets grouped under unique ids, acquired and released together, for example by a scene and the tile map it
 * loads (ADR-0007, ADR-0019, ADR-0021, ADR-0026, ADR-0028, ADR-0055, REQ-SHEET-002, REQ-RT-005).
 *
 * <p>
 * A group is created empty with an {@link ImageCache}, and its creator holds its first handle. Each sheet is added
 * under an id in the resource-key form (ADR-0007), usually the key of its definition file or of its image, unique in
 * the group: an id already in the group is refused when the sheet is added, an id not in the group when it is looked
 * up, both with an {@link IllegalStateException} naming the id. Adding a sheet loads its image through the cache, so
 * sheets of several groups over the same image share its pixels and texture.
 * </p>
 *
 * <p>
 * A group is reference-counted, as the images of the cache are: {@link #acquire()} returns a new handle on the same
 * group, for example for a tile map that keeps its tileset (ADR-0028), and each handle is released by the code that
 * acquired it, normally the scene on exit. Releasing a handle is idempotent. The last release releases the images of
 * every sheet of the group together (scope release, ADR-0019); a released handle refuses every other call.
 * </p>
 *
 * <p>
 * Threads: runtime thread only (ADR-0018); every call but {@link #isReleased()} and {@link #release()} of a released
 * handle throws an {@link IllegalStateException} on another thread than the owner of the cache (ADR-0055).
 * </p>
 *
 * @since 11.0.0
 */
public final class SheetGroup
{
    /** Error message of a released handle. */
    private static final String ERROR_RELEASED = "Sheet group released";

    /**
     * Checks that the grid is not {@code null}.
     *
     * @param grid The grid.
     * @throws IllegalArgumentException If the grid is {@code null}.
     */
    private static void checkNotNull(SheetGrid grid)
    {
        if (grid == null)
        {
            throw new IllegalArgumentException("Grid null");
        }
    }

    /** The shared group. */
    private final SheetGroupEntry entry;
    /** {@code true} once released. */
    private boolean released;

    /**
     * Creates an empty group, whose first handle is this one.
     *
     * @param cache The cache that loads the images of the sheets (never {@code null}).
     * @throws IllegalArgumentException If the cache is {@code null}.
     */
    public SheetGroup(ImageCache cache)
    {
        super();

        if (cache == null)
        {
            throw new IllegalArgumentException("Cache null");
        }
        entry = new SheetGroupEntry(cache);
    }

    /**
     * Creates another handle on a group.
     *
     * @param entry The shared group, whose count includes this handle.
     */
    private SheetGroup(SheetGroupEntry entry)
    {
        super();

        this.entry = entry;
    }

    /**
     * Adds a sheet: loads its image through the cache and cuts it into the grid.
     *
     * @param id The id of the sheet, unique in the group (never {@code null}).
     * @param image The resource key of the image (never {@code null}).
     * @param grid The grid of the sheet (never {@code null}).
     * @return The sheet, also returned by {@link #get(ResourceKey)}.
     * @throws IllegalArgumentException If an argument is {@code null}, or the grid does not fit in the image: no cell
     *             fits, or the given columns or rows do not; nothing is added and the image is released.
     * @throws IllegalStateException If the id is already in the group, if the handle is released, if the image would
     *             exceed the limit of the cache, or if called on another thread than the owner of the cache.
     * @throws com.b3dgs.lionengine.resources.ResourceException If the image is missing or cannot be read.
     * @throws ImageDecodingException If the image cannot be decoded.
     */
    public SpriteSheet add(ResourceKey id, ResourceKey image, SheetGrid grid)
    {
        checkUsable();
        checkNotNull(grid);
        return entry.add(id, image, grid, grid.cellWidth() / 2.0, grid.cellHeight() / 2.0, null);
    }

    /**
     * Adds a sheet with the pivot of its cells: loads its image through the cache and cuts it into the grid.
     *
     * @param id The id of the sheet, unique in the group (never {@code null}).
     * @param image The resource key of the image (never {@code null}).
     * @param grid The grid of the sheet (never {@code null}).
     * @param pivotX The horizontal pivot of the cells, in pixels from their left edge (finite; may be outside the
     *            cell).
     * @param pivotY The vertical pivot of the cells, in pixels from their top edge (finite; may be outside the cell).
     * @return The sheet, also returned by {@link #get(ResourceKey)}.
     * @throws IllegalArgumentException If an argument is {@code null}, a pivot is not finite, or the grid does not
     *             fit in the image: no cell fits, or the given columns or rows do not; nothing is added and the image
     *             is released.
     * @throws IllegalStateException If the id is already in the group, if the handle is released, if the image would
     *             exceed the limit of the cache, or if called on another thread than the owner of the cache.
     * @throws com.b3dgs.lionengine.resources.ResourceException If the image is missing or cannot be read.
     * @throws ImageDecodingException If the image cannot be decoded.
     */
    public SpriteSheet add(ResourceKey id, ResourceKey image, SheetGrid grid, double pivotX, double pivotY)
    {
        checkUsable();
        return entry.add(id, image, grid, pivotX, pivotY, null);
    }

    /**
     * Adds a sheet read from a definition, after a check of the image size, for {@link SheetLoader}.
     *
     * @param id The id of the sheet.
     * @param image The resource key of the image.
     * @param grid The grid of the sheet.
     * @param pivotX The horizontal pivot of the cells, in pixels.
     * @param pivotY The vertical pivot of the cells, in pixels.
     * @param check The check of the image size, which may throw before the sheet is created.
     * @return The sheet.
     */
    SpriteSheet add(ResourceKey id, ResourceKey image, SheetGrid grid, double pivotX, double pivotY, SheetCheck check)
    {
        checkUsable();
        return entry.add(id, image, grid, pivotX, pivotY, check);
    }

    /**
     * Returns a sheet of the group.
     *
     * @param id The id of the sheet (never {@code null}).
     * @return The sheet.
     * @throws IllegalArgumentException If the id is {@code null}.
     * @throws IllegalStateException If the id is not in the group (see {@link #has(ResourceKey)}), if the handle is
     *             released, or if called on another thread than the owner of the cache.
     */
    public SpriteSheet get(ResourceKey id)
    {
        checkUsable();
        return entry.get(id);
    }

    /**
     * Tells whether a sheet is in the group.
     *
     * @param id The id of the sheet (never {@code null}).
     * @return {@code true} if the group holds a sheet under this id.
     * @throws IllegalArgumentException If the id is {@code null}.
     * @throws IllegalStateException If the handle is released, or if called on another thread than the owner of the
     *             cache.
     */
    public boolean has(ResourceKey id)
    {
        checkUsable();
        return entry.has(id);
    }

    /**
     * Returns the number of sheets of the group.
     *
     * @return The number of sheets.
     * @throws IllegalStateException If the handle is released, or if called on another thread than the owner of the
     *             cache.
     */
    public int size()
    {
        checkUsable();
        return entry.size();
    }

    /**
     * Acquires another handle on the group, for another user that releases it on its own.
     *
     * @return A new handle on the same group.
     * @throws IllegalStateException If this handle is released, or if called on another thread than the owner of the
     *             cache.
     */
    public SheetGroup acquire()
    {
        checkUsable();
        entry.acquire();
        return new SheetGroup(entry);
    }

    /**
     * Tells whether the handle is released.
     *
     * @return {@code true} once {@link #release()} was called.
     */
    public boolean isReleased()
    {
        return released;
    }

    /**
     * Releases this handle. Idempotent: a second call has no effect. The last release of the group releases the image
     * of every sheet.
     *
     * @throws IllegalStateException If called on another thread than the owner of the cache; the handle stays
     *             acquired then.
     */
    public void release()
    {
        if (!released)
        {
            entry.checkThread();
            entry.release();
            released = true;
        }
    }

    /**
     * Checks the thread, then that the handle is not released.
     *
     * @throws IllegalStateException If called on another thread than the owner of the cache, or if the handle is
     *             released.
     */
    void checkUsable()
    {
        entry.checkThread();
        if (released)
        {
            throw new IllegalStateException(ERROR_RELEASED);
        }
    }
}
