/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteTiled;

/**
 * This class allows to convert a map image to a map level format.
 * The color [0-128-128] ({@link TileExtractor#IGNORED_COLOR}) is ignored (can be used to skip tile, in order to improve
 * performance).
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final LevelRipConverter rip = new LevelRipConverter(levelrip, tilesheet, map);
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
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class LevelRipConverter
{
    /** Map reference. */
    private final MapTile map;
    /** Level rip image. */
    private final Sprite imageMap;
    /** Level rip width in tile. */
    private final int imageMapTilesInX;
    /** Level rip height in tile. */
    private final int imageMapTilesInY;
    /** Number of errors. */
    private int errors;

    /**
     * Must be called to start conversion.
     * 
     * @param levelrip The file containing the levelrip as an image.
     * @param sheetsConfig The file that define the sheets configuration.
     * @param map The destination map reference.
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    public LevelRipConverter(Media levelrip, Media sheetsConfig, MapTile map)
    {
        this.map = map;

        imageMap = Drawable.loadSprite(levelrip);
        imageMap.load(false);

        map.loadSheets(sheetsConfig);
        imageMapTilesInX = imageMap.getWidth() / map.getTileWidth();
        imageMapTilesInY = imageMap.getHeight() / map.getTileHeight();
        map.create(imageMapTilesInX, imageMapTilesInY);

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
            for (int imageMapCurrentTileX = 0; imageMapCurrentTileX < imageMapTilesInX; imageMapCurrentTileX++)
            {
                final int imageColor = tileRef.getRgb(imageMapCurrentTileX * map.getTileWidth(), imageMapCurrentTileY
                        * map.getTileHeight());
                // Skip blank tile of image map
                if (TileExtractor.IGNORED_COLOR != imageColor)
                {
                    // Search if tile is on sheet and get it
                    final Tile tile = searchForTile(tileRef, imageMapCurrentTileX, imageMapCurrentTileY);
                    if (tile != null)
                    {
                        map.setTile(imageMapCurrentTileX, map.getInTileHeight() - 1 - imageMapCurrentTileY, tile);
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
    private Tile searchForTile(ImageBuffer tileSprite, int x, int y)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();

        // Check each tile on each sheet
        for (final Integer sheet : map.getSheets())
        {
            final SpriteTiled tileSheet = map.getSheet(sheet);
            final ImageBuffer sheetImage = tileSheet.getSurface();
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

                    if (TileExtractor.compareTile(tw, th, tileSprite, xa, ya, sheetImage, xb, yb))
                    {
                        final Tile tile = map.createTile();
                        tile.setSheet(sheet);
                        tile.setNumber(number);

                        return tile;
                    }
                }
            }
        }

        // No tile found
        return null;
    }
}
