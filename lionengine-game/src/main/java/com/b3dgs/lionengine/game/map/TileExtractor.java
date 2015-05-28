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

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;

/**
 * This class allows to extract unique tiles from a level rip.
 * The color [0-128-128] ({@link #IGNORED_COLOR}) is ignored (can be used to skip tile, in order to improve
 * performance).
 * <p>
 * Example (will scan level.png, using a 16*16 tile size, and store result in sheet.png 256*256):
 * </p>
 * 
 * <pre>
 * TileExtractor.start(Medias.create(&quot;level.png&quot;), Medias.create(&quot;sheet.png&quot;), 16, 16, 256, 256);
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class TileExtractor
{
    /** Ignored color. */
    public static final int IGNORED_COLOR = new ColorRgba(0, 128, 128).getRgba();

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
    static boolean compareTile(int tw, int th, ImageBuffer a, int xa, int ya, ImageBuffer b, int xb, int yb)
    {
        // Check tiles pixels
        for (int x = 0; x < tw; x++)
        {
            for (int y = 0; y < th; y++)
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

    /** Levels rip. */
    private final Collection<Media> rips = new ArrayList<>();
    /** Extracted tile sheets. */
    private final Collection<ImageBuffer> extractions = new ArrayList<>();
    /** Extraction folder. */
    private final Media folder;
    /** Extracted tile sheet prefix. */
    private final String prefix;
    /** Tile width. */
    private final int tileWidth;
    /** Tile height. */
    private final int tileHeight;
    /** Maximum tile sheet horizontal tiles. */
    private final int horizontal;
    /** Maximum tile sheet vertical tiles. */
    private final int vertical;
    /** Last sheet buffer. */
    private ImageBuffer sheet;
    /** Last sheet graphic. */
    private Graphic g;
    /** Last tile sheet extraction number. */
    private int lastIndex;
    /** Draw tile found location x. */
    private int cx;
    /** Draw tile found location y. */
    private int cy;

    /**
     * Create the extractor.
     * 
     * @param folder The generated levelrip folder.
     * @param tileWidth The level rip tile width.
     * @param tileHeight The level rip tile height.
     * @param horizontal The number of horizontal tiles on tile sheet.
     * @param vertical The number of vertical tiles on tile sheet.
     */
    public TileExtractor(Media folder, int tileWidth, int tileHeight, int horizontal, int vertical)
    {
        this(folder, "", tileWidth, tileHeight, horizontal, vertical);
    }

    /**
     * Create the extractor.
     * 
     * @param folder The generated levelrip folder.
     * @param prefix The extracted tile sheets prefix.
     * @param tileWidth The level rip tile width.
     * @param tileHeight The level rip tile height.
     * @param horizontal The number of horizontal tiles on tile sheet.
     * @param vertical The number of vertical tiles on tile sheet.
     */
    public TileExtractor(Media folder, String prefix, int tileWidth, int tileHeight, int horizontal, int vertical)
    {
        this.folder = folder;
        this.prefix = prefix;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    /**
     * Add a level rip to proceed during extraction.
     * 
     * @param rip The level rip media.
     */
    public void addRip(Media rip)
    {
        rips.add(rip);
    }

    /**
     * Start using specified output file.
     * 
     * @throws LionEngineException If an error occurred when saving the image.
     */
    public void start() throws LionEngineException
    {
        sheet = Graphics.createImageBuffer(horizontal * tileWidth, vertical * tileHeight, Transparency.BITMASK);
        extractions.add(sheet);
        g = sheet.createGraphic();

        for (final Media rip : rips)
        {
            proceed(rip);
        }
        g.dispose();
        saveExtraction();
    }

    /**
     * Proceed the specified level rip.
     * 
     * @param ripMedia The level rip.
     * @throws LionEngineException If an error occurred when proceeding the image.
     */
    private void proceed(Media ripMedia) throws LionEngineException
    {
        final SpriteTiled rip = Drawable.loadSpriteTiled(ripMedia, tileWidth, tileHeight);
        rip.load(false);

        final int ripHorizontalTiles = rip.getWidth() / tileWidth;
        final int ripVerticalTiles = rip.getHeight() / tileHeight;
        final ImageBuffer surface = rip.getSurface();

        for (int ripV = ripVerticalTiles - 1; ripV >= 0; ripV--)
        {
            for (int ripH = 0; ripH < ripHorizontalTiles; ripH++)
            {
                // Skip blank tile of image map (0, 128, 128)
                if (IGNORED_COLOR != surface.getRgb(ripH * tileWidth, ripV * tileHeight))
                {
                    if (!isExtracted(surface, ripH, ripV))
                    {
                        final int tileNumber = ripH + ripV * rip.getTilesHorizontal();
                        extract(rip, tileNumber);
                        checkSheetFilled();
                    }
                }
            }
        }
    }

    /**
     * Extract the tile number from the level rip to draw it on current tile sheet.
     * 
     * @param imageMap The level rip image.
     * @param tile The tile number on level rip.
     */
    private void extract(SpriteTiled imageMap, int tile)
    {
        imageMap.setLocation(cx, cy);
        imageMap.setTile(tile);
        imageMap.render(g);

        cx += tileWidth;
        if (cx > sheet.getWidth())
        {
            cx = 0;
            cy += tileHeight;
        }
    }

    /**
     * Check if current extracted sheet has been filled, and prepare a new one if needed.
     */
    private void checkSheetFilled()
    {
        if (cy >= sheet.getHeight())
        {
            saveExtraction();
            sheet = Graphics.createImageBuffer(horizontal * tileWidth, vertical * tileHeight, Transparency.BITMASK);
            extractions.add(sheet);

            g.dispose();
            g = sheet.createGraphic();

            cx = 0;
            cy = 0;
            lastIndex++;
        }
    }

    /**
     * Save the current extracted tile sheet.
     */
    private void saveExtraction()
    {
        Graphics.saveImage(sheet, getSheetMedia(lastIndex));
    }

    /**
     * Get the sheet media from its index.
     * 
     * @param index The sheet index.
     * @return The sheet media.
     */
    private Media getSheetMedia(int index)
    {
        return Medias.create(folder.getPath(), prefix + index + ".png");
    }

    /**
     * Check if tile has already been extracted at the current level rip location.
     * 
     * @param rip The the level rip.
     * @param ripH The tile horizontal index.
     * @param ripV The tile vertical index.
     * @return <code>true</code> if found, <code>false</code> else.
     */
    private boolean isExtracted(ImageBuffer rip, int ripH, int ripV)
    {
        for (final ImageBuffer sheet : extractions)
        {
            if (isExtracted(rip, sheet, ripH, ripV))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if tile has already been extracted at the current level rip location in the tile sheet.
     * 
     * @param rip The the level rip.
     * @param sheet The tile sheet to check.
     * @param ripH The tile horizontal index.
     * @param ripV The tile vertical index.
     * @return <code>true</code> if found, <code>false</code> else.
     */
    private boolean isExtracted(ImageBuffer rip, ImageBuffer sheet, int ripH, int ripV)
    {
        // Check each tile of the current tile sheet
        for (int sheetV = 0; sheetV < vertical; sheetV++)
        {
            for (int sheetH = 0; sheetH < horizontal; sheetH++)
            {
                // Compare tiles between sheet and level rip
                final int ripX = ripH * tileWidth;
                final int ripY = ripV * tileHeight;
                final int sheetX = sheetH * tileWidth;
                final int sheetY = sheetV * tileHeight;
                if (compareTile(tileWidth, tileHeight, rip, ripX, ripY, sheet, sheetX, sheetY))
                {
                    return true;
                }
            }
        }
        // No tile found
        return false;
    }
}
