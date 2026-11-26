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

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.graphics.ColorMapping;
import com.b3dgs.lionengine.graphics.ColorTransform;
import com.b3dgs.lionengine.graphics.Image;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.graphics.SheetGroup;
import com.b3dgs.lionengine.graphics.SheetLoader;
import com.b3dgs.lionengine.graphics.Sprite;
import com.b3dgs.lionengine.graphics.SpriteSheet;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Draws one soldier per team, each in its team colors, from one sprite sheet and the color templates of its
 * definition file, without an image file per team (REQ-SPRITE-005, ADR-0057).
 *
 * <p>
 * The definition {@code sheets/soldier.json} maps the key colors of the image, painted in magenta, to the colors of
 * each team:
 * </p>
 *
 * <pre>
 * {
 *   "format": "lionengine.sheet",
 *   "version": 1,
 *   "image": "sprites/soldier.png",
 *   "cellWidth": 16,
 *   "cellHeight": 16,
 *   "templates": {
 *     "red": {"#FF00FF": "#FF0000", "#800080": "#800000"},
 *     "blue": {"#FF00FF": "#0000FF", "#800080": "#000080"}
 *   }
 * }
 * </pre>
 *
 * <p>
 * The {@link SheetLoader} reads each template as a {@link ColorMapping}. For each team, the scene bakes the mapping
 * into a variant of the sheet image with {@link ImageCache#bake(Image, ColorTransform)}: the variant is computed once
 * on the CPU and cached under the source image and the mapping, so every scene drawing the same team shares it. A
 * {@link Sprite} over the sheet and the variant draws the frames of the sheet in the team colors. The mapping is the
 * first part of a {@link ColorTransform}, so a team color can also be flashed, grayed, tinted or faded. The scene
 * releases the variants, then the group, which releases the sheet image.
 * </p>
 *
 * @since 11.0.0
 */
public final class TeamColorExample
{
    /** Position of the cape in the first cell of the soldier image, in pixels. */
    public static final int CAPE_X = 0;
    /** Key of the soldier sheet definition. */
    public static final ResourceKey SOLDIER = new ResourceKey("sheets/soldier.json");
    /** Logical frame width in pixels. */
    public static final int WIDTH = 160;
    /** Logical frame height in pixels. */
    public static final int HEIGHT = 120;

    /** Initial capacity of the command buffer: one sprite per team. */
    private static final int CAPACITY = 4;
    /** Distance between two soldiers in pixels. */
    private static final double STEP = 24.0;
    /** Vertical position of the soldiers in pixels. */
    private static final double ROW = 60.0;

    /**
     * Private constructor.
     */
    private TeamColorExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Draws a frame with one soldier per template of the sheet, then releases the images.
     *
     * @param resources The resources holding {@link #SOLDIER} and its image.
     * @param backend The render backend, which presents the frame.
     * @return The teams drawn, the color of their cape, and the number of cached images during the frame and at the
     *         end.
     */
    public static Result run(Resources resources, RenderBackend backend)
    {
        final ImageCache cache = new ImageCache(resources, backend.getTextureStore());
        final SheetGroup group = new SheetGroup(cache);
        final SpriteSheet sheet = new SheetLoader(resources).load(group, SOLDIER);
        final List<String> teams = sheet.getTemplateNames();
        final CommandBuffer commands = new CommandBuffer(WIDTH, HEIGHT, CAPACITY);
        final List<Image> variants = new ArrayList<>();
        final List<Integer> capes = new ArrayList<>();

        for (int i = 0; i < teams.size(); i++)
        {
            final ColorMapping colors = sheet.getTemplate(teams.get(i));
            final Image variant = cache.bake(sheet.getImage(), ColorTransform.NONE.withMapping(colors));
            variants.add(variant);
            final Sprite soldier = new Sprite(sheet, variant);
            soldier.setPosition(STEP * (i + 1), ROW);
            soldier.render(commands);
            capes.add(Integer.valueOf(variant.getPixel(CAPE_X, 0)));
        }
        backend.present(commands);
        final int cachedInScene = cache.size();

        for (final Image variant : variants)
        {
            variant.release();
        }
        group.release();
        return new Result(teams, capes, cachedInScene, cache.size());
    }

    /**
     * Result of the example.
     *
     * @param teams The names of the templates drawn, in the order of the definition.
     * @param capes The color of the cape pixel ({@link #CAPE_X}, {@code 0}) of each team, packed {@code 0xAARRGGBB}.
     * @param cachedInScene The number of cached images during the frame: the sheet image and one variant per team.
     * @param cachedAtEnd The number of cached images once every handle was released.
     * @since 11.0.0
     */
    public record Result(List<String> teams, List<Integer> capes, int cachedInScene, int cachedAtEnd)
    {
        // Value type of the example.
    }
}
