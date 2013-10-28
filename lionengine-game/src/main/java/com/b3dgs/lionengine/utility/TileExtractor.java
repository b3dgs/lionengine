/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.utility;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;

/**
 * This class allows to extract unique tiles from a level rip. The color [0-128-128] is ignored (can be used to skip
 * tile, in order to improve performance).
 * <p>
 * Example (will scan level.png, using a 16*16 tile size, and store result in sheet.png 256*256):
 * </p>
 * 
 * <pre>
 * TileExtractor.start(Media.get(&quot;level.png&quot;), Media.get(&quot;sheet.png&quot;), 16, 16, 256, 256);
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class TileExtractor
{
    /** Ignored color. */
    private static final int IGNORED_COLOR = new ColorRgba(0, 128, 128).getRgba();

    /**
     * Start tiles extraction.
     * 
     * @param levelrip The input media, containing level rip as an image.
     * @param out The output media, which will contain unique tiles.
     * @param tilew The level tile width.
     * @param tileh The level tile height.
     * @param destW The output image width.
     * @param destH The output image height.
     */
    public static void start(Media levelrip, Media out, int tilew, int tileh, int destW, int destH)
    {
        final TileExtractor extractor = new TileExtractor(levelrip, tilew, tileh, destW, destH);
        extractor.start(out);
    }

    /** Image map reference. */
    private SpriteTiled imageMap;
    /** Built pattern from map. */
    private ImageBuffer pattern;
    /** Graphics. */
    private Graphic g;
    /** Image map height. */
    private int imageMapTilesInY;
    /** Image map width. */
    private int imageMapTilesInX;
    /** Exploring area start x. */
    private int startX;
    /** Exploring area start y. */
    private int endX;
    /** Tile width. */
    private int tilew;
    /** Tile height. */
    private int tileh;
    /** Draw tile found location x. */
    private int cx;
    /** Draw tile found location y. */
    private int cy;

    /**
     * Private constructor.
     */
    private TileExtractor()
    {
        throw new RuntimeException();
    }

    /**
     * Constructor.
     * 
     * @param media The levelrip media path.
     * @param tilew The tile width.
     * @param tileh The tile height.
     * @param destW The tilesheet width.
     * @param destH The tilesheet height.
     */
    private TileExtractor(Media media, int tilew, int tileh, int destW, int destH)
    {
        this.tilew = tilew;
        this.tileh = tileh;
        imageMap = Drawable.loadSpriteTiled(media, tilew, tileh);
        imageMap.load(false);
        imageMapTilesInY = imageMap.getHeightOriginal() / this.tileh;
        imageMapTilesInX = imageMap.getWidthOriginal() / this.tilew;
        startX = 0;
        endX = imageMapTilesInX;
        pattern = UtilityImage.createImageBuffer(destW, destH, Transparency.BITMASK);
        g = pattern.createGraphic();
        cx = 0;
        cy = 0;
    }

    /**
     * Start using specified output file.
     * 
     * @param fileout The output file.
     */
    private void start(Media fileout)
    {
        // Check all image tiles
        final ImageBuffer tileRef = imageMap.getSurface();
        for (int imageMapCurrentTileY = imageMapTilesInY - 1; imageMapCurrentTileY >= 0; imageMapCurrentTileY--)
        {
            for (int imageMapCurrentTileX = startX; imageMapCurrentTileX < endX; imageMapCurrentTileX++)
            {
                // Skip blank tile of image map (0, 128, 128)
                final int imageColor = tileRef.getRgb(imageMapCurrentTileX * tilew + 1,
                        (imageMapTilesInY - 1 - imageMapCurrentTileY) * tileh + 1);
                if (imageColor != TileExtractor.IGNORED_COLOR)
                {
                    // Search if tile is on sheet and get it
                    final boolean found = searchForTile(tileRef, imageMapCurrentTileX, imageMapTilesInY - 1
                            - imageMapCurrentTileY);
                    final int n = imageMapCurrentTileX + (imageMapTilesInY - 1 - imageMapCurrentTileY)
                            * imageMap.getTilesHorizontal();

                    if (!found)
                    {
                        g.drawImage(imageMap.getTile(n), cx, cy);
                        cx += tilew;
                        if (cx > pattern.getWidth())
                        {
                            cx = 0;
                            cy += tileh;
                        }
                    }
                }
            }
        }
        g.dispose();
        UtilityImage.saveImage(pattern, fileout);
    }

    /**
     * Search current tile of image map by checking all surfaces.
     * 
     * @param tileSprite The the tile
     * @param x The location x.
     * @param y The location y.
     * @return <code>true</code> if found, <code>false</code> else.
     */
    private boolean searchForTile(ImageBuffer tileSprite, int x, int y)
    {
        final ImageBuffer sheet = pattern;
        final int tilesInX = pattern.getWidth() / tilew;
        final int tilesInY = pattern.getHeight() / tileh;

        // Check each tile of the tile sheet
        for (int surfaceCurrentTileY = 0; surfaceCurrentTileY < tilesInY; surfaceCurrentTileY++)
        {
            for (int surfaceCurrentTileX = 0; surfaceCurrentTileX < tilesInX; surfaceCurrentTileX++)
            {
                // Compare tiles between sheet and image map
                final int xa = x * tilew;
                final int ya = y * tileh;
                final int xb = surfaceCurrentTileX * tilew;
                final int yb = surfaceCurrentTileY * tileh;
                if (compareTile(tileSprite, xa, ya, sheet, xb, yb))
                {
                    return true;
                }
            }
        }
        // No tile found
        return false;
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
        for (int x = 0; x < tilew; x++)
        {
            for (int y = 0; y < tileh; y++)
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
