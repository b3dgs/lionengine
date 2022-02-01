/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.game.feature.tile.TilesExtractor;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * This class allows to convert a map image to a map level format.
 * The color [0-128-128] ({@link TilesExtractor#IGNORED_COLOR_VALUE}) is ignored (can be used to skip tile, in order to
 * improve performance).
 */
public final class LevelRipConverter
{
    /**
     * Run the converter.
     * 
     * @param levelrip The file containing the levelrip as an image.
     * @param map The destination map reference.
     * @return The total number of not found tiles.
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    public static int start(Media levelrip, MapTile map)
    {
        return start(levelrip, map, null, null);
    }

    /**
     * Run the converter.
     * 
     * @param levelrip The file containing the levelrip as an image.
     * @param map The destination map reference.
     * @param listener The progress listener.
     * @return The total number of not found tiles.
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    public static int start(Media levelrip, MapTile map, ProgressListener listener)
    {
        return start(levelrip, map, listener, null);
    }

    /**
     * Run the converter.
     * 
     * @param levelrip The file containing the levelrip as an image.
     * @param map The destination map reference.
     * @param listener The progress listener.
     * @param canceler The canceler reference.
     * @return The total number of not found tiles.
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    public static int start(Media levelrip, MapTile map, ProgressListener listener, Canceler canceler)
    {
        final Sprite imageMap = Drawable.loadSprite(levelrip);
        imageMap.load();
        imageMap.prepare();

        final int imageTilesInX = imageMap.getWidth() / map.getTileWidth();
        final int imageTilesInY = imageMap.getHeight() / map.getTileHeight();
        map.create(map.getTileWidth(), map.getTileHeight(), imageTilesInX, imageTilesInY);

        final double progressMax = imageTilesInX * (double) imageTilesInY;
        long progress = 0L;
        int lastPercent = 0;
        int errors = 0;

        final ImageBuffer tileRef = imageMap.getSurface();
        for (int progressTileY = 0; progressTileY < imageTilesInY; progressTileY++)
        {
            for (int progressTileX = 0; progressTileX < imageTilesInX; progressTileX++)
            {
                if (!checkPixel(map, tileRef, progressTileX, progressTileY))
                {
                    Verbose.warning("Tile missing at: " + progressTileX + " " + progressTileY);
                    errors++;
                }

                final int percent = (int) Math.round(progress / progressMax * 100);
                if (listener != null && percent != lastPercent)
                {
                    listener.notifyProgress(percent, progressTileX, progressTileY);
                }
                lastPercent = percent;
                progress++;

                if (canceler != null && canceler.isCanceled())
                {
                    tileRef.dispose();
                    return errors;
                }
            }
        }

        tileRef.dispose();
        return errors;
    }

    /**
     * Check the pixel by searching tile on sheet.
     * 
     * @param map The destination map reference.
     * @param tileRef The tile sheet.
     * @param progressTileX The progress on horizontal tiles.
     * @param progressTileY The progress on vertical tiles.
     * @return <code>true</code> if tile found, <code>false</code> else.
     */
    private static boolean checkPixel(MapTile map, ImageBuffer tileRef, int progressTileX, int progressTileY)
    {
        final int x = progressTileX * map.getTileWidth();
        final int y = progressTileY * map.getTileHeight();
        final int pixel = tileRef.getRgb(x, y);

        // Skip blank tile of image map
        if (TilesExtractor.IGNORED_COLOR_VALUE != pixel)
        {
            // Search if tile is on sheet and get it
            return searchForTile(map, tileRef, progressTileX, progressTileY);
        }
        return true;
    }

    /**
     * Search current tile of image map by checking all surfaces.
     * 
     * @param map The destination map reference.
     * @param tileSprite The tiled sprite
     * @param x The location x.
     * @param y The location y.
     * @return <code>true</code> if tile found, <code>false</code> else.
     */
    private static boolean searchForTile(MapTile map, ImageBuffer tileSprite, int x, int y)
    {
        // Check each tile on each sheet
        final int sheetsCount = map.getSheetsNumber();
        for (int sheetId = 0; sheetId < sheetsCount; sheetId++)
        {
            final boolean found = checkTile(map, tileSprite, sheetId, x, y);
            if (found)
            {
                return true;
            }
        }

        // No tile found
        return false;
    }

    /**
     * Check tile of sheet.
     * 
     * @param map The destination map reference.
     * @param tileSprite The tiled sprite
     * @param sheetId The sheet number.
     * @param x The location x.
     * @param y The location y.
     * @return <code>true</code> if tile found, <code>false</code> else.
     */
    private static boolean checkTile(MapTile map, ImageBuffer tileSprite, int sheetId, int x, int y)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final SpriteTiled tileSheet = map.getSheet(sheetId);
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

                if (TilesExtractor.compareTile(tw, th, tileSprite, xa, ya, sheetImage, xb, yb))
                {
                    map.setTile(x, map.getInTileHeight() - 1 - y, number);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Private constructor.
     */
    private LevelRipConverter()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
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
