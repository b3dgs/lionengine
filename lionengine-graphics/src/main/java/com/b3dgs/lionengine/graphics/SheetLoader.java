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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.b3dgs.lionengine.DefinitionException;
import com.b3dgs.lionengine.JsonObject;
import com.b3dgs.lionengine.JsonReader;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Loads sprite sheets from their definition files into a {@link SheetGroup} (ADR-0028, ADR-0032, ADR-0033,
 * ADR-0057, REQ-SHEET-003, REQ-DEF-001, REQ-DEF-002, REQ-SPRITE-005, REQ-ANIM-001).
 *
 * <p>
 * A definition is a strict JSON file of format {@value #FORMAT}, version {@value #VERSION}:
 * </p>
 *
 * <ul>
 * <li>{@code image}: the resource key of the image (required);</li>
 * <li>{@code cellWidth}, {@code cellHeight}: the cell size in pixels (required, positive);</li>
 * <li>{@code columns}, {@code rows}: the numbers of cells (optional, positive), derived from the image when absent
 * ({@link SheetGrid#DERIVED});</li>
 * <li>{@code margin}, {@code spacing}: in pixels (optional, zero or positive, {@code 0} by default);</li>
 * <li>{@code pivotX}, {@code pivotY}: the pivot of the cells in pixels from their top-left corner (optional numbers,
 * the cell center by default);</li>
 * <li>{@code animations}: an object of named animations, each with {@code frames} (cell indices, at least one, inside
 * the sheet), {@code frameDuration} (seconds, positive), optional {@code frameDurations} (seconds, positive, one per
 * frame, overriding {@code frameDuration}) and {@code loop} ({@code once}, {@code loop} or {@code ping-pong})
 * (optional); kept by the sheet as {@link Animation} values ({@link SpriteSheet#getAnimation(String)});</li>
 * <li>{@code templates}: an object of named color mappings, each mapping key colors to target colors, written
 * {@code #RRGGBB} (opaque) or {@code #AARRGGBB}, a key color at most once (optional); kept by the sheet as
 * {@link ColorMapping} values ({@link SpriteSheet#getTemplate(String)});</li>
 * <li>{@code comment}: documentation, at any level (optional); so no animation, template or key color can be named
 * {@code comment}.</li>
 * </ul>
 *
 * <p>
 * Any other field is an error. Every error of the file is a {@link DefinitionException} giving the key of the file,
 * the line, the column and the JSON path of the faulty value; the whole file is checked, the frame indices and the
 * grid against the image too, before the sheet enters the group, so a failed load adds nothing and caches nothing.
 * The sheet keeps the animations as {@link Animation} values ({@link SpriteSheet#getAnimation(String)}) and the
 * templates as {@link ColorMapping} values ({@link SpriteSheet#getTemplate(String)}).
 * </p>
 *
 * <p>
 * The sheet is added to the group under the key of its definition file, the id a tile map or a game uses to find it.
 * Threads: {@link #load(SheetGroup, ResourceKey)} runs on the runtime thread, like the group; the loader keeps no
 * state between loads.
 * </p>
 *
 * @since 11.0.0
 */
public final class SheetLoader
{
    /** Format of a sprite sheet definition. */
    public static final String FORMAT = "lionengine.sheet";
    /** Newest version of the format this loader reads. */
    public static final int VERSION = 1;

    /** Size of the read buffer in bytes. */
    private static final int BUFFER_SIZE = 4096;

    /** The resources of the definitions. */
    private final Resources resources;
    /** The JSON reader, with its limits. */
    private final JsonReader reader;

    /**
     * Creates a loader with the default limits of the JSON reader (ADR-0032).
     *
     * @param resources The resources the definitions are read from (never {@code null}).
     * @throws IllegalArgumentException If the resources are {@code null}.
     */
    public SheetLoader(Resources resources)
    {
        this(resources, new JsonReader());
    }

    /**
     * Creates a loader with a JSON reader of chosen limits.
     *
     * @param resources The resources the definitions are read from (never {@code null}).
     * @param reader The JSON reader (never {@code null}).
     * @throws IllegalArgumentException If an argument is {@code null}.
     */
    public SheetLoader(Resources resources, JsonReader reader)
    {
        super();

        ImageCache.checkNotNull(resources, "Resources");
        ImageCache.checkNotNull(reader, "Reader");
        this.resources = resources;
        this.reader = reader;
    }

    /**
     * Loads a sheet from its definition file and adds it to a group, under the key of the file.
     *
     * @param group The group receiving the sheet (never {@code null}).
     * @param key The resource key of the definition file, also the id of the sheet (never {@code null}).
     * @return The sheet, also returned by {@code group.get(key)}.
     * @throws IllegalArgumentException If an argument is {@code null}.
     * @throws IllegalStateException If the key is already an id of the group, if the group handle is released, if the
     *             image would exceed the limit of the cache, or if called on another thread than the owner of the
     *             cache.
     * @throws ResourceException If the definition or its image is missing ({@code MISSING}) or cannot be read
     *             ({@code INACCESSIBLE}).
     * @throws DefinitionException If the definition is not valid: JSON, format, version, a missing, invalid or unknown
     *             field, a frame outside the sheet, or a grid outside the image.
     * @throws ImageDecodingException If the image cannot be decoded.
     */
    public SpriteSheet load(SheetGroup group, ResourceKey key)
    {
        ImageCache.checkNotNull(group, "Group");
        ImageCache.checkNotNull(key, "Key");
        group.checkUsable();
        final JsonObject root = reader.read(read(key), key.path()).asObject();
        final SheetDefinition definition = new SheetDefinition(root);
        return group.add(key,
                         definition.getImage(),
                         definition.getGrid(),
                         definition.getPivotX(),
                         definition.getPivotY(),
                         definition);
    }

    /**
     * Reads the bytes of a definition, at most one more than the document limit of the reader, which then refuses
     * the document; closes the stream.
     *
     * @param key The resource key.
     * @return The bytes read.
     * @throws ResourceException If the resource is missing or cannot be read.
     */
    private byte[] read(ResourceKey key)
    {
        final long limit = reader.getMaxDocumentSize() + 1L;
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (InputStream input = resources.open(key))
        {
            final byte[] buffer = new byte[BUFFER_SIZE];
            int count = input.read(buffer);
            while (count >= 0 && output.size() < limit)
            {
                output.write(buffer, 0, count);
                count = input.read(buffer);
            }
        }
        catch (final IOException exception)
        {
            throw new ResourceException(key, ResourceException.Reason.INACCESSIBLE, exception);
        }
        return output.toByteArray();
    }
}
