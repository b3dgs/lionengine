package com.b3dgs.lionengine.utility;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.maptile.MapTileGame;
import com.b3dgs.lionengine.game.maptile.TileGame;

/**
 * This class allows to convert a map image to a map level format.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * private void ripLevel(Media levelrip, Media tilesheet, Media output)
 * {
 *     final LevelRipConverter&lt;TypeCollision, Tile&gt; rip = new LevelRipConverter&lt;&gt;();
 *     rip.start(levelrip, map, tilesheet, false);
 *     try (FileWriting file = File.createFileWriting(output);)
 *     {
 *         map.save(file);
 *     }
 *     catch (final IOException exception)
 *     {
 *         Verbose.exception(World.class, &quot;constructor&quot;, exception, &quot;Error on saving map !&quot;);
 *     }
 * }
 * </pre>
 * 
 * @param <C> Collision type used.
 * @param <T> Tile type used.
 */
public final class LevelRipConverter<C extends Enum<C>, T extends TileGame<C>>
        extends Thread
{
    /** Ignored color. */
    private static final int IGNORED_COLOR = new Color(0, 128, 128).getRGB();
    /** Map reference. */
    private MapTileGame<C, T> map;
    /** Level rip image. */
    private Sprite imageMap;
    /** Level rip height in tile. */
    private int imageMapTilesInY;
    /** Level rip width in tile. */
    private int imageMapTilesInX;
    /** Thread computation range start. */
    private int startX;
    /** Thread computation range end. */
    private int endX;
    /** Number of errors. */
    private int errors;

    /**
     * Create a new level rip converter.
     */
    public LevelRipConverter()
    {
        // Nothing to do
    }

    /**
     * Create a new level rip converter.
     * 
     * @param map The map reference (to store read data).
     * @param imageMap The level rip image name.
     * @param numberOfThread The number of used thread.
     * @param id The current thread id.
     */
    private LevelRipConverter(MapTileGame<C, T> map, Sprite imageMap, int numberOfThread, int id)
    {
        super("LevelRip Converter");
        this.map = map;
        this.imageMap = imageMap;
        imageMapTilesInY = imageMap.getHeightOriginal() / map.getTileHeight();
        imageMapTilesInX = imageMap.getWidthOriginal() / map.getTileWidth();
        startX = (int) Math.floor(imageMapTilesInX / numberOfThread * id);
        endX = (int) Math.ceil(imageMapTilesInX / (double) numberOfThread * (id + 1));
        if (id + 1 == numberOfThread)
        {
            endX += imageMapTilesInX - endX;
        }
        errors = 0;
    }

    /**
     * Must be called to start conversion.
     * 
     * @param levelrip The file containing the levelrip as an image.
     * @param map The destination map reference.
     * @param patternsDirectory The directory containing tiles themes.
     * @param display <code>true</code> to show display box at the end, <code>false</code> else.
     */
    public void start(Media levelrip, MapTileGame<?, ?> map, Media patternsDirectory, boolean display)
    {
        final int threadsNum = Runtime.getRuntime().availableProcessors();
        final Sprite levelRip = Drawable.loadSprite(levelrip);
        levelRip.load(false);
        map.loadPatterns(patternsDirectory);
        map.create(levelRip.getWidthOriginal() / map.getTileWidth(), levelRip.getHeightOriginal() / map.getTileHeight());

        final LevelRipConverter<?, ?>[] threads = new LevelRipConverter<?, ?>[threadsNum];
        errors = 0;
        try
        {
            for (int i = 0; i < threadsNum; i++)
            {
                threads[i] = new LevelRipConverter<>(map, levelRip, threadsNum, i);
                threads[i].start();
            }
            for (int i = 0; i < threadsNum; i++)
            {
                threads[i].join();
                errors += threads[i].errors;
            }
        }
        catch (final InterruptedException exception)
        {
            Verbose.critical(LevelRipConverter.class, "An error occured: ", levelrip.getPath());
        }
        if (display)
        {
            if (errors > 0)
            {
                UtilityMessageBox.error("LevelRipConverter", "Convertion finished with " + errors + " error(s) !");
            }
            else
            {
                UtilityMessageBox
                        .information("LevelRipConverter", "Convertion finished with " + errors + " error(s) !");
            }
        }
    }

    @Override
    public void run()
    {
        // Check all image tiles
        final BufferedImage tileRef = this.imageMap.getSurface();
        for (int imageMapCurrentTileY = 0; imageMapCurrentTileY < this.imageMapTilesInY; imageMapCurrentTileY++)
        {
            for (int imageMapCurrentTileX = this.startX; imageMapCurrentTileX < this.endX; imageMapCurrentTileX++)
            {
                // Skip blank tile of image map (0, 128, 128)
                final int imageColor = tileRef.getRGB(imageMapCurrentTileX * this.map.getTileWidth() + 1,
                        imageMapCurrentTileY * this.map.getTileHeight() + 1);
                if (LevelRipConverter.IGNORED_COLOR != imageColor)
                {
                    // Search if tile is on sheet and get it
                    final T tile = this.searchForTile(tileRef, imageMapCurrentTileX, imageMapCurrentTileY);

                    // A tile has been found
                    if (tile != null)
                    {
                        this.map.setTile(this.map.getHeightInTile() - 1 - imageMapCurrentTileY, imageMapCurrentTileX,
                                tile);
                    }
                    else
                    {
                        this.errors++;
                    }
                }
            }
        }
    }

    /**
     * Search current tile of image map by checking all surfaces.
     * 
     * @param tileSprite The tiled sprite
     * @param x The location x.
     * @param y The location y.
     * @return The tile found.
     */
    private T searchForTile(BufferedImage tileSprite, int x, int y)
    {
        // Check each tile on each pattern
        final Iterator<Integer> itr = this.map.getPatterns().iterator();
        final int tw = this.map.getTileWidth();
        final int th = this.map.getTileHeight();

        while (itr.hasNext())
        {
            final Integer pattern = itr.next();
            final SpriteTiled tileSheet = this.map.getPattern(pattern);
            final BufferedImage sheet = tileSheet.getSurface();
            final int tilesInX = tileSheet.getWidthOriginal() / tw;
            final int tilesInY = tileSheet.getHeightOriginal() / th;

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

                    if (this.compareTile(tileSprite, xa, ya, sheet, xb, yb))
                    {
                        final T tile = this.map.createTile(this.map.getTileWidth(), this.map.getTileHeight());
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
    private boolean compareTile(BufferedImage a, int xa, int ya, BufferedImage b, int xb, int yb)
    {
        // Check tiles pixels
        for (int x = 0; x < this.map.getTileWidth(); x++)
        {
            for (int y = 0; y < this.map.getTileHeight(); y++)
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
