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
package com.b3dgs.lionengine.examples.graphics;

import com.b3dgs.lionengine.DefinitionException;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.graphics.SheetGroup;
import com.b3dgs.lionengine.graphics.SheetLoader;
import com.b3dgs.lionengine.graphics.SpriteSheet;
import com.b3dgs.lionengine.render.TextureStore;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Loads a sprite sheet from its definition file into a group, and reports the error of a broken definition with its
 * file, line, column and field (REQ-SHEET-003, REQ-DEF-002).
 *
 * <p>
 * A definition is a JSON file of format {@code lionengine.sheet}, version 1, for example {@code sheets/hero.json}:
 * </p>
 *
 * <pre>
 * {
 *   "format": "lionengine.sheet",
 *   "version": 1,
 *   "image": "sprites/hero.png",
 *   "cellWidth": 16,
 *   "cellHeight": 16,
 *   "pivotY": 15,
 *   "animations": {
 *     "walk": {"frames": [0, 1, 2, 3], "frameDuration": 0.1, "loop": "loop"}
 *   },
 *   "templates": {
 *     "red": {"#FF00FF": "#FF0000"}
 *   }
 * }
 * </pre>
 *
 * <p>
 * The {@link SheetLoader} reads it through {@link Resources}, checks every field, the grid against the image and the
 * animation frames against the cells, and adds the sheet to the {@link SheetGroup} under the key of the file. A
 * mistake in the file is a {@link DefinitionException} naming the file, the line, the column and the JSON path of the
 * faulty value, and the group stays unchanged.
 * </p>
 *
 * @since 11.0.0
 */
public final class SheetLoaderExample
{
    /** Key of the hero sheet definition. */
    public static final ResourceKey HERO = new ResourceKey("sheets/hero.json");
    /** Key of a definition with a mistake. */
    public static final ResourceKey BROKEN = new ResourceKey("sheets/broken.json");
    /** Frame whose position is read: the first frame of the second row. */
    public static final int FRAME = 4;

    /**
     * Private constructor.
     */
    private SheetLoaderExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Loads {@link #HERO}, reads its grid, pivot and one frame, tries {@link #BROKEN}, and releases the group.
     *
     * @param resources The resources holding the definitions and the image.
     * @param textures The texture store of the render backend.
     * @return What was read from the hero sheet, and the error of the broken one.
     */
    public static Result run(Resources resources, TextureStore textures)
    {
        final ImageCache cache = new ImageCache(resources, textures);
        final SheetGroup group = new SheetGroup(cache);
        final SheetLoader loader = new SheetLoader(resources);

        final SpriteSheet hero = loader.load(group, HERO);
        String error = null;
        try
        {
            loader.load(group, BROKEN);
        }
        catch (final DefinitionException exception)
        {
            error = exception.getMessage();
        }
        final Result result = new Result(hero.getColumns(),
                                         hero.getRows(),
                                         hero.getPivotY(),
                                         hero.getCellY(FRAME),
                                         group.size(),
                                         error);
        group.release();
        return result;
    }

    /**
     * Result of the example.
     *
     * @param columns The columns of the hero sheet, derived from its image.
     * @param rows The rows of the hero sheet, derived from its image.
     * @param pivotY The vertical pivot of the hero sheet, from its definition, in pixels.
     * @param frameY The top edge of {@link #FRAME} in the image, in pixels.
     * @param sheets The number of sheets in the group after both loads.
     * @param error The message of the error of the broken definition.
     * @since 11.0.0
     */
    public record Result(int columns, int rows, double pivotY, int frameY, int sheets, String error)
    {
        // Value type of the example.
    }
}
