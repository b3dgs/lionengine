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

import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.graphics.SheetGrid;
import com.b3dgs.lionengine.graphics.SheetGroup;
import com.b3dgs.lionengine.graphics.SpriteSheet;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.TextureStore;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Cuts two images into sprite sheets grouped by id, shares the group with a tile map, draws one cell of each sheet
 * with render commands, and releases the group (REQ-SHEET-001, REQ-SHEET-002, REQ-RT-005).
 *
 * <p>
 * A scene creates a {@link SheetGroup} over the {@link ImageCache} of the game and adds each sheet under an id in the
 * resource-key form, with the key of its image and its {@link SheetGrid}: here the hero, 16 by 16 pixel frames, and a
 * tileset of 8 by 8 pixel tiles with a margin of 1 pixel and a spacing of 2 pixels, whose numbers of columns and rows
 * come from the size of its image. Cells are numbered row by row from the top-left; a cell is drawn by giving its
 * position and the cell size as the source rectangle of a render command on the texture of the sheet's image. The tile
 * map the scene loads keeps its own handle on the group ({@link SheetGroup#acquire()}); the images leave the cache when
 * the scene and the map have both released their handles.
 * </p>
 *
 * @since 11.0.0
 */
public final class SpriteSheetExample
{
    /** Key of the hero image, in the game data: 64 by 32 pixels. */
    public static final ResourceKey HERO_IMAGE = new ResourceKey("sprites/hero.png");
    /** Key of the tileset image, in the game data: 31 by 21 pixels. */
    public static final ResourceKey TILES_IMAGE = new ResourceKey("tiles/forest.png");
    /** Id of the hero sheet. */
    public static final ResourceKey HERO = new ResourceKey("sheets/hero");
    /** Id of the tileset sheet. */
    public static final ResourceKey TILES = new ResourceKey("sheets/forest");
    /** Frame of the hero drawn: the second frame of the second row. */
    public static final int HERO_FRAME = 5;
    /** Tile drawn: the last tile of the tileset. */
    public static final int TILE = 5;

    /** Size of a hero frame in pixels. */
    private static final int HERO_CELL = 16;
    /** Size of a tile in pixels. */
    private static final int TILE_CELL = 8;
    /** Margin of the tileset in pixels. */
    private static final int TILE_MARGIN = 1;
    /** Spacing of the tileset in pixels. */
    private static final int TILE_SPACING = 2;
    /** Horizontal position of the hero on screen, in pixels of the logical frame. */
    private static final float HERO_X = 40.0F;
    /** Vertical position of the hero on screen, in pixels of the logical frame. */
    private static final float HERO_Y = 24.0F;

    /**
     * Private constructor.
     */
    private SpriteSheetExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Groups the two sheets, shares the group with a tile map, draws one cell of each, and releases the group.
     *
     * @param resources The resources holding {@link #HERO_IMAGE} and {@link #TILES_IMAGE}.
     * @param textures The texture store of the render backend.
     * @param commands The command buffer of the frame.
     * @return The grid of the tileset and the number of cached images during the scene, after the scene and at the end.
     */
    public static Result run(Resources resources, TextureStore textures, CommandBuffer commands)
    {
        final ImageCache cache = new ImageCache(resources, textures);

        final SheetGroup scene = new SheetGroup(cache);
        final SpriteSheet hero = scene.add(HERO, HERO_IMAGE, new SheetGrid(HERO_CELL, HERO_CELL));
        scene.add(TILES,
                  TILES_IMAGE,
                  new SheetGrid(TILE_CELL,
                                TILE_CELL,
                                SheetGrid.DERIVED,
                                SheetGrid.DERIVED,
                                TILE_MARGIN,
                                TILE_SPACING));
        final SheetGroup map = scene.acquire();

        final SpriteSheet tiles = map.get(TILES);
        commands.add(tiles.getImage().getTexture(),
                     tiles.getCellX(TILE),
                     tiles.getCellY(TILE),
                     tiles.getCellWidth(),
                     tiles.getCellHeight());
        commands.add(hero.getImage().getTexture(),
                     hero.getCellX(HERO_FRAME),
                     hero.getCellY(HERO_FRAME),
                     hero.getCellWidth(),
                     hero.getCellHeight())
                .position(HERO_X, HERO_Y)
                .layer(1);
        final int cachedInScene = cache.size();

        scene.release();
        final int cachedAfterScene = cache.size();
        map.release();
        return new Result(tiles.getColumns(), tiles.getRows(), cachedInScene, cachedAfterScene, cache.size());
    }

    /**
     * Result of the example.
     *
     * @param tileColumns The columns of the tileset, derived from its image.
     * @param tileRows The rows of the tileset, derived from its image.
     * @param cachedInScene The number of cached images while the scene uses the group.
     * @param cachedAfterScene The number of cached images once the scene released its handle, the map still holding
     *            its own.
     * @param cachedAtEnd The number of cached images once every handle was released.
     * @since 11.0.0
     */
    public record Result(int tileColumns, int tileRows, int cachedInScene, int cachedAfterScene, int cachedAtEnd)
    {
        // Value type of the example.
    }
}
