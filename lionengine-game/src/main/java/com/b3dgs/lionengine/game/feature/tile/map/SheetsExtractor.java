/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Surface;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Extract tile sheets from tiles list.
 */
public final class SheetsExtractor
{
    /**
     * Convert a set of tile to a set of tile sheets.
     * 
     * @param tiles The list of tiles.
     * @param horizontalTiles The number of horizontal tiles on sheet (inferior or equal to 0 to use automatic).
     * @return The list of tile sheets.
     */
    public static List<SpriteTiled> extract(Collection<ImageBuffer> tiles, int horizontalTiles)
    {
        final Surface surface = getSheetSize(tiles, horizontalTiles);
        final int horizontals = surface.getWidth();
        final int verticals = surface.getHeight();
        final int tilesPerSheet = Math.min(tiles.size(), horizontals * verticals);
        final List<SpriteTiled> sheets = new ArrayList<>();

        ImageBuffer sheet = null;
        Graphic g = null;
        int number = 0;
        int tw = 0;
        int th = 0;

        for (final ImageBuffer tile : tiles)
        {
            if (g == null)
            {
                tw = tile.getWidth();
                th = tile.getHeight();

                final int width = Math.max(tw, horizontals * tw);
                final int height = Math.max(th, verticals * th);
                sheet = Graphics.createImageBuffer(width, height, tile.getTransparentColor());
                g = sheet.createGraphic();
            }
            final int x = number % horizontals;
            final int y = (int) Math.floor(number / (double) horizontals);
            g.drawImage(tile, x * tw, y * th);
            number++;

            if (number >= tilesPerSheet)
            {
                g.dispose();
                sheets.add(Drawable.loadSpriteTiled(Graphics.getImageBuffer(sheet), tw, th));
                sheet = null;
                g = null;
                number = 0;
            }
        }

        return sheets;
    }

    /**
     * Get the sheet size depending of the number of horizontal tiles and total number of tiles.
     * 
     * @param tiles The tiles list.
     * @param horizontalTiles The horizontal tiles.
     * @return The horizontal and vertical tiles.
     */
    private static Surface getSheetSize(Collection<ImageBuffer> tiles, int horizontalTiles)
    {
        final int tilesNumber = tiles.size();
        final int horizontals;
        final int verticals;
        if (horizontalTiles > 0)
        {
            horizontals = horizontalTiles;
            verticals = (int) Math.ceil(tilesNumber / (double) horizontalTiles);
        }
        else
        {
            final int[] mult = UtilMath.getClosestSquareMult(tilesNumber, tilesNumber);
            horizontals = mult[0];
            verticals = mult[1];
        }
        return new Surface()
        {
            @Override
            public int getWidth()
            {
                return Math.max(1, horizontals);
            }

            @Override
            public int getHeight()
            {
                return Math.max(1, verticals);
            }
        };
    }

    /**
     * Private constructor.
     */
    private SheetsExtractor()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
