package com.b3dgs.lionengine.utility;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.TileGame;

/**
 * This class allows to convert a map image to a map level format.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * private void ripLevel(Media levelrip, Media tilesheet, Media output)
 * {
 *     final LevelRipConverter&lt;Tile&gt; rip = new LevelRipConverter&lt;&gt;();
 *     rip.start(levelrip, map, tilesheet);
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
 * @param <T> Tile type used.
 */
public final class LevelRipConverter<T extends TileGame<?>>
        extends Thread
{
    /** Ignored color. */
    private static final int IGNORED_COLOR = new Color(0, 128, 128).getRGB();

    /** Map reference. */
    private MapTileGame<?, T> map;
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
    private LevelRipConverter(MapTileGame<?, T> map, Sprite imageMap, int numberOfThread, int id)
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
     */
    public void start(Media levelrip, MapTileGame<?, T> map, Media patternsDirectory)
    {
        final int threadsNum = Runtime.getRuntime().availableProcessors();
        final Sprite levelRip = Drawable.loadSprite(levelrip);
        levelRip.load(false);
        map.loadPatterns(patternsDirectory);
        map.create(levelRip.getWidthOriginal() / map.getTileWidth(), levelRip.getHeightOriginal() / map.getTileHeight());

        final LevelRipConverter<?>[] threads = new LevelRipConverter<?>[threadsNum];
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
    private T searchForTile(BufferedImage tileSprite, int x, int y)
    {
        // Check each tile on each pattern
        final Iterator<Integer> itr = map.getPatterns().iterator();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();

        while (itr.hasNext())
        {
            final Integer pattern = itr.next();
            final SpriteTiled tileSheet = map.getPattern(pattern);
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

                    if (compareTile(tileSprite, xa, ya, sheet, xb, yb))
                    {
                        final T tile = map.createTile(map.getTileWidth(), map.getTileHeight(), pattern, number, null);
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
        for (int x = 0; x < map.getTileWidth(); x++)
        {
            for (int y = 0; y < map.getTileHeight(); y++)
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

    /*
     * Thread
     */

    @Override
    public void run()
    {
        // Check all image tiles
        final BufferedImage tileRef = imageMap.getSurface();
        for (int imageMapCurrentTileY = 0; imageMapCurrentTileY < imageMapTilesInY; imageMapCurrentTileY++)
        {
            for (int imageMapCurrentTileX = startX; imageMapCurrentTileX < endX; imageMapCurrentTileX++)
            {
                // Skip blank tile of image map (0, 128, 128)
                final int imageColor = tileRef.getRGB(imageMapCurrentTileX * map.getTileWidth() + 1,
                        imageMapCurrentTileY * map.getTileHeight() + 1);
                if (LevelRipConverter.IGNORED_COLOR != imageColor)
                {
                    // Search if tile is on sheet and get it
                    final T tile = searchForTile(tileRef, imageMapCurrentTileX, imageMapCurrentTileY);

                    // A tile has been found
                    if (tile != null)
                    {
                        map.setTile(map.getHeightInTile() - 1 - imageMapCurrentTileY, imageMapCurrentTileX, tile);
                    }
                    else
                    {
                        errors++;
                    }
                }
            }
        }
    }
}
