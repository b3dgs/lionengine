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

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;

/**
 * Extract tile sheets from tiles list.
 */
public final class SheetsExtractor
{
    /**
     * Convert a set of tile to a set of tile sheets.
     * 
     * @param tiles The list of tiles.
     * @param horizontalTiles The number of horizontal tiles on sheet.
     * @return The list of tile sheets.
     */
    public static Collection<SpriteTiled> extract(Collection<ImageBuffer> tiles, int horizontalTiles)
    {
        final int size = (int) Math.ceil(Math.sqrt(tiles.size()));
        final int tilesPerSheet = size * size;
        final int sheetsNumber = (int) Math.ceil(size / (double) horizontalTiles);
        final Collection<SpriteTiled> sheets = new ArrayList<SpriteTiled>(sheetsNumber);

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
                sheet = Graphics.createImageBuffer(size * tw, size * th, tile.getTransparency());
                g = sheet.createGraphic();
            }
            final int x = number % size;
            final int y = (int) Math.floor(number / (double) size);
            g.drawImage(tile, x * tw, y * th);
            number++;

            if (number >= tilesPerSheet || number >= tiles.size())
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
     * Private constructor.
     */
    private SheetsExtractor()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
