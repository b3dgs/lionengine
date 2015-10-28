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

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteTiled;

/**
 * This class allows to convert a map image to a map level format.
 * The color [0-128-128] ({@link TileExtractor#IGNORED_COLOR_VALUE}) is ignored (can be used to skip tile, in order to
 * improve performance).
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
    /** Progress listener. */
    private final Collection<ProgressListener> listeners = new HashSet<ProgressListener>();
    /** Map reference. */
    private final MapTile map;
    /** Level rip image. */
    private final Sprite imageMap;
    /** Level rip width in tile. */
    private int imageMapTilesInX;
    /** Level rip height in tile. */
    private int imageMapTilesInY;
    /** Number of errors. */
    private int errors;
    /** Progress on horizontal tile. */
    private int progressTileX;
    /** Progress on vertical tile. */
    private int progressTileY;
    /** Progress max. */
    private double progressMax;
    /** Old progress. */
    private long progressPercentOld;
    /** Current progress. */
    private long progress;

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
        map.loadSheets(sheetsConfig);

        imageMap = Drawable.loadSprite(levelrip);
        errors = 0;
    }

    /**
     * Add a listener.
     * 
     * @param listener The listener to add.
     */
    public void addListener(ProgressListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Run the converter.
     */
    public void start()
    {
        start(null);
    }

    /**
     * Run the converter.
     * 
     * @param canceler The canceler reference.
     */
    public void start(Canceler canceler)
    {
        imageMap.load();
        imageMap.prepare();

        imageMapTilesInX = imageMap.getWidth() / map.getTileWidth();
        imageMapTilesInY = imageMap.getHeight() / map.getTileHeight();
        map.create(imageMapTilesInX, imageMapTilesInY);

        progressMax = imageMapTilesInX * imageMapTilesInY;
        progress = 0L;
        progressPercentOld = -1L;

        // Check all image tiles
        final ImageBuffer tileRef = imageMap.getSurface();
        for (progressTileY = 0; progressTileY < imageMapTilesInY; progressTileY++)
        {
            for (progressTileX = 0; progressTileX < imageMapTilesInX; progressTileX++)
            {
                final int x = progressTileX * map.getTileWidth();
                final int y = progressTileY * map.getTileHeight();
                final int imageColor = tileRef.getRgb(x, y);

                // Skip blank tile of image map
                if (TileExtractor.IGNORED_COLOR_VALUE != imageColor)
                {
                    // Search if tile is on sheet and get it
                    final Tile tile = searchForTile(tileRef, progressTileX, progressTileY);
                    if (tile != null)
                    {
                        map.setTile(progressTileX, map.getInTileHeight() - 1 - progressTileY, tile);
                    }
                    else
                    {
                        errors++;
                    }
                }
                updateProgress();
                if (canceler != null && canceler.isCanceled())
                {
                    return;
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
        // Check each tile on each sheet
        for (final Integer sheet : map.getSheets())
        {
            final Tile tile = checkTile(tileSprite, sheet, x, y);
            if (tile != null)
            {
                return tile;
            }
        }

        // No tile found
        return null;
    }

    /**
     * Check tile of sheet.
     * 
     * @param tileSprite The tiled sprite
     * @param sheet The sheet number.
     * @param x The location x.
     * @param y The location y.
     * @return The tile found.
     */
    private Tile checkTile(ImageBuffer tileSprite, Integer sheet, int x, int y)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final SpriteTiled tileSheet = map.getSheet(sheet);
        final ImageBuffer sheetImage = tileSheet.getSurface();
        final int tilesInX = tileSheet.getWidth() / tw;
        final int tilesInY = tileSheet.getHeight() / th;

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
        return null;
    }

    /**
     * Update progress and notify if needed.
     */
    private void updateProgress()
    {
        progress++;
        final int percent = getProgressPercent();
        if (percent != progressPercentOld)
        {
            for (final ProgressListener listener : listeners)
            {
                listener.notifyProgress(percent, progressTileX, progressTileY);
            }
            progressPercentOld = percent;
        }
    }

    /**
     * Get percent progress.
     * 
     * @return Progress percent.
     */
    private int getProgressPercent()
    {
        return (int) Math.round(progress / progressMax * 100);
    }

    /**
     * Listen to import progress.
     */
    public interface ProgressListener
    {
        /**
         * Called once progress detected.
         * 
         * @param percent Progress percent.
         * @param progressTileX Current progress on horizontal tile.
         * @param progressTileY Current progress on vertical tile.
         */
        void notifyProgress(int percent, int progressTileX, int progressTileY);
    }

    /**
     * Cancel controller.
     */
    public interface Canceler
    {
        /**
         * Check if operation is canceled.
         * 
         * @return <code>true</code> if canceled, <code>false</code> else.
         */
        boolean isCanceled();
    }
}
