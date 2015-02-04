/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.utility;

import java.util.Iterator;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.TileGame;

/**
 * This class allows to convert a map image to a map level format.
 * The color [0-128-128] is ignored (can be used to skip tile, in order to improve performance).
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final LevelRipConverter&lt;TileGame&gt; rip = new LevelRipConverter&lt;&gt;(levelrip, tilesheet, map);
 * rip.start();
 * try (FileWriting file = Stream.createFileWriting(output))
 * {
 *     map.save(file);
 * }
 * catch (final IOException exception)
 * {
 *     // Error
 * }
 * </pre>
 * 
 * @param <T> Tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class LevelRipConverter<T extends TileGame>
{
    /** Ignored color. */
    private static final int IGNORED_COLOR = new ColorRgba(0, 128, 128).getRgba();

    /** Map reference. */
    private final MapTile<T> map;
    /** Level rip image. */
    private final Sprite imageMap;
    /** Level rip height in tile. */
    private final int imageMapTilesInY;
    /** Level rip width in tile. */
    private final int imageMapTilesInX;
    /** Thread computation range start. */
    private final int startX;
    /** Thread computation range end. */
    private final int endX;
    /** Number of errors. */
    private int errors;

    /**
     * Must be called to start conversion.
     * 
     * @param levelrip The file containing the levelrip as an image.
     * @param patternsDirectory The directory containing tiles themes.
     * @param map The destination map reference.
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    public LevelRipConverter(Media levelrip, Media patternsDirectory, MapTile<T> map)
    {
        this.map = map;

        imageMap = Drawable.loadSprite(levelrip);
        imageMap.load(false);

        map.loadPatterns(patternsDirectory);
        map.create(imageMap.getWidth() / map.getTileWidth(), imageMap.getHeight() / map.getTileHeight());

        imageMapTilesInY = imageMap.getHeight() / map.getTileHeight();
        imageMapTilesInX = imageMap.getWidth() / map.getTileWidth();
        startX = 0;
        endX = imageMapTilesInX;

        errors = 0;
    }

    /**
     * Run the converter.
     */
    public void start()
    {
        // Check all image tiles
        final ImageBuffer tileRef = imageMap.getSurface();
        for (int imageMapCurrentTileY = 0; imageMapCurrentTileY < imageMapTilesInY; imageMapCurrentTileY++)
        {
            for (int imageMapCurrentTileX = startX; imageMapCurrentTileX < endX; imageMapCurrentTileX++)
            {
                // Skip blank tile of image map (0, 128, 128)
                final int imageColor = tileRef.getRgb(imageMapCurrentTileX * map.getTileWidth() + 1,
                        imageMapCurrentTileY * map.getTileHeight() + 1);
                if (LevelRipConverter.IGNORED_COLOR != imageColor)
                {
                    // Search if tile is on sheet and get it
                    final T tile = searchForTile(tileRef, imageMapCurrentTileX, imageMapCurrentTileY);

                    // A tile has been found
                    if (tile != null)
                    {
                        map.setTile(map.getHeightInTile() - 1 - imageMapCurrentTileY, imageMapCurrentTileX, tile);
                    }
                    else
                    {
                        errors++;
                    }
                }
            }
        }
    }

    /**
     * Get the error counter (number of not found tiles).
     * 
     * @return The total number of not found tiles.
     */
    public int getErrors()
    {
        return errors;
    }

    /**
     * Search current tile of image map by checking all surfaces.
     * 
     * @param tileSprite The tiled sprite
     * @param x The location x.
     * @param y The location y.
     * @return The tile found.
     */
    private T searchForTile(ImageBuffer tileSprite, int x, int y)
    {
        // Check each tile on each pattern
        final Iterator<Integer> itr = map.getPatterns().iterator();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();

        while (itr.hasNext())
        {
            final Integer pattern = itr.next();
            final SpriteTiled tileSheet = map.getPattern(pattern);
            final ImageBuffer sheet = tileSheet.getSurface();
            final int tilesInX = tileSheet.getWidth() / tw;
            final int tilesInY = tileSheet.getHeight() / th;

            // Check each tile of the tile sheet
            for (int surfaceCurrentTileY = 0; surfaceCurrentTileY < tilesInY; surfaceCurrentTileY++)
            {
                for (int surfaceCurrentTileX = 0; surfaceCurrentTileX < tilesInX; surfaceCurrentTileX++)
                {
                    // Tile number on tile sheet
                    final int number = surfaceCurrentTileX + surfaceCurrentTileY * tilesInX;

                    // Compare tiles between sheet and image map
                    final int xa = x * tw;
                    final int ya = y * th;
                    final int xb = surfaceCurrentTileX * tw;
                    final int yb = surfaceCurrentTileY * th;

                    if (compareTile(tileSprite, xa, ya, sheet, xb, yb))
                    {
                        final T tile = map.createTile();
                        tile.setPattern(pattern);
                        tile.setNumber(number);

                        return tile;
                    }
                }
            }
        }

        // No tile found
        return null;
    }

    /**
     * Compare two tiles by checking all pixels.
     * 
     * @param a The first tile image.
     * @param xa The location x.
     * @param ya The location y.
     * @param b The second tile image.
     * @param xb The location x.
     * @param yb The location y.
     * @return <code>true</code> if equals, <code>false</code> else.
     */
    private boolean compareTile(ImageBuffer a, int xa, int ya, ImageBuffer b, int xb, int yb)
    {
        // Check tiles pixels
        for (int x = 0; x < map.getTileWidth(); x++)
        {
            for (int y = 0; y < map.getTileHeight(); y++)
            {
                // Compare color
                if (a.getRgb(x + xa, y + ya) != b.getRgb(x + xb, y + yb))
                {
                    return false;
                }
            }
        }
        // Tiles are equal
        return true;
    }
}
