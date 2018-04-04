/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.ImageHeader;
import com.b3dgs.lionengine.graphic.drawable.ImageInfo;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * This class allows to extract unique tiles from a level rip.
 * The color [0-128-128] ({@link #IGNORED_COLOR_VALUE}) is ignored (can be used to skip tile, in order to improve
 * performance).
 */
public final class TilesExtractor
{
    /** Ignored color. */
    public static final ColorRgba IGNORED_COLOR = new ColorRgba(0, 128, 128);
    /** Ignored color. */
    public static final int IGNORED_COLOR_VALUE = IGNORED_COLOR.getRgba();

    /**
     * Compare two tiles by checking all pixels.
     * 
     * @param tw The tile width.
     * @param th The tile height.
     * @param a The first tile image.
     * @param xa The location x.
     * @param ya The location y.
     * @param b The second tile image.
     * @param xb The location x.
     * @param yb The location y.
     * @return <code>true</code> if equals, <code>false</code> else.
     */
    public static boolean compareTile(int tw, int th, ImageBuffer a, int xa, int ya, ImageBuffer b, int xb, int yb)
    {
        for (int x = 0; x < tw; x++)
        {
            for (int y = 0; y < th; y++)
            {
                final int colorA = a.getRgb(x + xa, y + ya);
                final int colorB = b.getRgb(x + xb, y + yb);

                if (colorA != colorB)
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if tile has already been extracted regarding the current tile on level rip.
     * 
     * @param level The level rip reference.
     * @param x The current level rip horizontal location.
     * @param y The current level rip vertical location.
     * @param tiles The current extracted tiles.
     * @return <code>true</code> if already extracted, <code>false</code> else.
     */
    private static boolean isExtracted(SpriteTiled level, int x, int y, Collection<ImageBuffer> tiles)
    {
        final int tw = level.getTileWidth();
        final int th = level.getTileHeight();
        final ImageBuffer surface = level.getSurface();

        for (final ImageBuffer tile : tiles)
        {
            if (compareTile(tw, th, surface, x, y, tile, 0, 0))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Extract the tile from level.
     * 
     * @param level The level image reference.
     * @param number The tile number on level.
     * @return The extracted tile image from level.
     */
    private static ImageBuffer extract(SpriteTiled level, int number)
    {
        final ColorRgba transparency = level.getSurface().getTransparentColor();
        final ImageBuffer tile = Graphics.createImageBuffer(level.getTileWidth(), level.getTileHeight(), transparency);
        final Graphic g = tile.createGraphic();
        level.setTile(number);
        level.render(g);
        g.dispose();

        final ImageBuffer copy = Graphics.getImageBuffer(tile);
        tile.dispose();
        return copy;
    }

    /**
     * Get the total number of tiles.
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @param levelRips The levels rip used.
     * @return The total number of tiles.
     */
    private static int getTilesNumber(int tileWidth, int tileHeight, Collection<Media> levelRips)
    {
        int tiles = 0;
        for (final Media levelRip : levelRips)
        {
            final ImageHeader info = ImageInfo.get(levelRip);
            final int horizontalTiles = info.getWidth() / tileWidth;
            final int verticalTiles = info.getHeight() / tileHeight;
            tiles += horizontalTiles * verticalTiles;
        }
        return tiles;
    }

    /**
     * Get progress percent.
     * 
     * @param progress The current progress.
     * @param progressMax The maximum progress.
     * @return The progress percent.
     */
    private static int getProgressPercent(long progress, int progressMax)
    {
        return (int) Math.round(progress / (double) progressMax * 100);
    }

    /** Progress listener. */
    private final Collection<ProgressListener> listeners = new HashSet<>();

    /**
     * Create the extractor.
     */
    public TilesExtractor()
    {
        super();
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
     * Start using specified output file. Listeners are cleared once ended.
     * 
     * @param tw The tile width.
     * @param th The tile height.
     * @param levelRips The levels rip used.
     * @return The extracted tile sheets.
     * @throws LionEngineException If an error occurred when computing sheet.
     */
    public Collection<ImageBuffer> extract(int tw, int th, Collection<Media> levelRips)
    {
        return extract(null, tw, th, levelRips);
    }

    /**
     * Start using specified output file. Listeners are cleared once ended.
     * 
     * @param canceler The canceler reference (can be <code>null</code>).
     * @param tw The tile width.
     * @param th The tile height.
     * @param levelRips The levels rip used.
     * @return The extracted tile sheets.
     * @throws LionEngineException If an error occurred when computing sheet.
     */
    public Collection<ImageBuffer> extract(Canceler canceler, int tw, int th, Collection<Media> levelRips)
    {
        final int tilesNumber = getTilesNumber(tw, th, levelRips);
        final Collection<ImageBuffer> tiles = new ArrayList<>();
        int checkedTiles = 0;
        for (final Media levelRip : levelRips)
        {
            final SpriteTiled level = Drawable.loadSpriteTiled(levelRip, tw, th);
            level.load();
            level.prepare();

            checkedTiles = extract(canceler, level, tilesNumber, tiles, checkedTiles);
            level.getSurface().dispose();
            if (checkedTiles < 0)
            {
                break;
            }
        }

        listeners.clear();
        return tiles;
    }

    /**
     * Proceed the specified level rip.
     * 
     * @param canceler The canceler reference (can be <code>null</code>).
     * @param level The level rip.
     * @param tilesNumber The total tiles number to extract.
     * @param tiles The current extracted tiles.
     * @param checkedTiles The last number of checked tiles.
     * @return The current number of checked tiles, or -1 if canceled.
     * @throws LionEngineException If an error occurred when proceeding the image.
     */
    private int extract(Canceler canceler,
                        SpriteTiled level,
                        int tilesNumber,
                        Collection<ImageBuffer> tiles,
                        int checkedTiles)
    {
        final int horizontalTiles = level.getTilesHorizontal();
        final int verticalTiles = level.getTilesVertical();
        final ImageBuffer surface = level.getSurface();

        final int tw = level.getTileWidth();
        final int th = level.getTileHeight();
        int checked = checkedTiles;
        int oldPercent = 0;
        for (int v = 0; v < verticalTiles; v++)
        {
            for (int h = 0; h < horizontalTiles; h++)
            {
                final int x = h * tw;
                final int y = v * th;
                if (IGNORED_COLOR_VALUE != surface.getRgb(x, y) && !isExtracted(level, x, y, tiles))
                {
                    final ImageBuffer tile = extract(level, h + v * horizontalTiles);
                    tiles.add(tile);
                }
                checked++;
                oldPercent = updateProgress(checked, tilesNumber, oldPercent, tiles);
                if (canceler != null && canceler.isCanceled())
                {
                    return -1;
                }
            }
        }
        return checked;
    }

    /**
     * Update progress and notify if needed.
     * 
     * @param checkedTiles The current number of checked tiles.
     * @param tilesNumber The total number of tile to extract.
     * @param oldPercent The old progress value.
     * @param tiles The extracted tiles image.
     * @return The last progress percent value.
     */
    private int updateProgress(int checkedTiles, int tilesNumber, int oldPercent, Collection<ImageBuffer> tiles)
    {
        final int percent = getProgressPercent(checkedTiles, tilesNumber);
        if (percent != oldPercent)
        {
            for (final ProgressListener listener : listeners)
            {
                listener.notifyProgress(percent, tiles);
            }
        }
        return percent;
    }

    /**
     * Listen to extraction progress.
     */
    public interface ProgressListener
    {
        /**
         * Called once progress detected.
         * 
         * @param percent Progress percent.
         * @param tiles The extracted tiles.
         */
        void notifyProgress(int percent, Collection<ImageBuffer> tiles);
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
