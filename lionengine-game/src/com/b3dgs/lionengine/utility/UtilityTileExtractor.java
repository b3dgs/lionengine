package com.b3dgs.lionengine.utility;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.Media;
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
 * UtilityTileExtractor.start(Media.get(&quot;level.png&quot;), Media.get(&quot;sheet.png&quot;), 16, 16, 256, 256);
 * </pre>
 */
public final class UtilityTileExtractor
{
    /** Ignored color. */
    private static final int IGNORED_COLOR = new Color(0, 128, 128).getRGB();
    /** Image map reference. */
    private SpriteTiled imageMap;
    /** Built pattern from map. */
    private BufferedImage pattern;
    /** Graphics. */
    private Graphics2D g;
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
    private UtilityTileExtractor()
    {
        throw new RuntimeException();
    }

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
        final UtilityTileExtractor extractor = new UtilityTileExtractor(levelrip, tilew, tileh, destW, destH);
        extractor.start(out);
    }

    /**
     * Private constructor.
     * 
     * @param media The levelrip media path.
     * @param tilew The tile width.
     * @param tileh The tile height.
     * @param destW The tilesheet width.
     * @param destH The tilesheet height.
     */
    private UtilityTileExtractor(Media media, int tilew, int tileh, int destW, int destH)
    {
        this.tilew = tilew;
        this.tileh = tileh;
        imageMap = Drawable.loadSpriteTiled(media, tilew, tileh);
        imageMap.load(false);
        imageMapTilesInY = imageMap.getHeightOriginal() / this.tileh;
        imageMapTilesInX = imageMap.getWidthOriginal() / this.tilew;
        startX = 0;
        endX = imageMapTilesInX;
        pattern = UtilityImage.createBufferedImage(destW, destH, Transparency.BITMASK);
        g = pattern.createGraphics();
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
        final BufferedImage tileRef = imageMap.getSurface();
        for (int imageMapCurrentTileY = imageMapTilesInY - 1; imageMapCurrentTileY >= 0; imageMapCurrentTileY--)
        {
            for (int imageMapCurrentTileX = startX; imageMapCurrentTileX < endX; imageMapCurrentTileX++)
            {
                // Skip blank tile of image map (0, 128, 128)
                final int imageColor = tileRef.getRGB(imageMapCurrentTileX * tilew + 1,
                        (imageMapTilesInY - 1 - imageMapCurrentTileY) * tileh + 1);
                if (imageColor != UtilityTileExtractor.IGNORED_COLOR)
                {
                    // Search if tile is on sheet and get it
                    final boolean found = searchForTile(tileRef, imageMapCurrentTileX, imageMapTilesInY - 1
                            - imageMapCurrentTileY);
                    final int n = imageMapCurrentTileX + (imageMapTilesInY - 1 - imageMapCurrentTileY)
                            * imageMap.getTilesHorizontal();

                    if (!found)
                    {
                        g.drawImage(imageMap.getTile(n), cx, cy, null);
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
    private boolean searchForTile(BufferedImage tileSprite, int x, int y)
    {
        final BufferedImage sheet = pattern;
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
    private boolean compareTile(BufferedImage a, int xa, int ya, BufferedImage b, int xb, int yb)
    {
        // Check tiles pixels
        for (int x = 0; x < tilew; x++)
        {
            for (int y = 0; y < tileh; y++)
            {
                // Compare color
                if (a.getRGB(x + xa, y + ya) != b.getRGB(x + xb, y + yb))
                {
                    return false;
                }
            }
        }
        // Tiles are equal
        return true;
    }
}
