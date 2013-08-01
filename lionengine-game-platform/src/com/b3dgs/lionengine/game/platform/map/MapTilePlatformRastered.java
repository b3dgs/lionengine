package com.b3dgs.lionengine.game.platform.map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.platform.SetupEntityPlatformRastered;
import com.b3dgs.lionengine.utility.UtilityFile;
import com.b3dgs.lionengine.utility.UtilityImage;

/**
 * Rastered version of a platform tile map.
 * 
 * @param <C> Collision type.
 * @param <T> Tile type used.
 */
public abstract class MapTilePlatformRastered<C extends Enum<C>, T extends TilePlatform<C>>
        extends MapTilePlatform<C, T>
{
    /** File describing the raster. */
    private Media rasterFile;
    /** List of rastered patterns. */
    private final TreeMap<Integer, List<SpriteTiled>> rasterPatterns;
    /** Rasters smooth flag. */
    private boolean smooth;
    /** Rasters cache use flag. */
    private boolean cache;
    /** Loaded state. */
    private boolean rasterLoaded;

    /**
     * Create a new rastered tile map.
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     */
    public MapTilePlatformRastered(int tileWidth, int tileHeight)
    {
        super(tileWidth, tileHeight);
        rasterPatterns = new TreeMap<>();
        rasterLoaded = false;
    }

    /**
     * Set raster file and smoothed flag.
     * 
     * @param raster The raster media.
     * @param smooth <code>true</code> for a smoothed raster (may be slower), <code>false</code> else.
     * @param cache <code>true</code> to cache data on hard drive (it is highly recommended to set it to
     *            <code>false</code> !)
     */
    public void setRaster(Media raster, boolean smooth, boolean cache)
    {
        this.rasterFile = raster;
        this.smooth = smooth;
        this.cache = cache;
    }

    /**
     * Get raster index from input tile (depending of its height).
     * 
     * @param tile The input tile.
     * @return The raster index.
     */
    public int getRasterIndex(T tile)
    {
        final int value = tile.getY() / getTileHeight();
        int index = value % SetupEntityPlatformRastered.MAX_RASTERS_R;
        if (!smooth && index > SetupEntityPlatformRastered.MAX_RASTERS_M)
        {
            index = SetupEntityPlatformRastered.MAX_RASTERS_M - (index - SetupEntityPlatformRastered.MAX_RASTERS);
        }
        return index;
    }

    /**
     * Get a tilesheet from its pattern and raster id.
     * 
     * @param pattern The pattern number
     * @param rasterID The raster id.
     * @return The tilesheet reference.
     */
    public SpriteTiled getRasterPattern(Integer pattern, int rasterID)
    {
        return rasterPatterns.get(pattern).get(rasterID);
    }

    /**
     * Load raster from data.
     * 
     * @param directory The current tile directory.
     * @param pattern The current pattern.
     * @param rasters The rasters data.
     */
    private void loadRaster(String directory, Integer pattern, int[][] rasters)
    {
        final int[] color = new int[rasters.length];
        final int[] colorNext = new int[rasters.length];
        final int max = smooth ? 2 : 1;

        for (int m = 0; m < max; m++)
        {
            for (int i = 1; i <= SetupEntityPlatformRastered.MAX_RASTERS; i++)
            {
                String rasFile = null;
                if (this.cache)
                {
                    rasFile = Media.getPath(Media.getTempDir(), directory, pattern + "_" + i + ".png");
                }
                if (this.cache && UtilityFile.exists(rasFile))
                {
                    final BufferedImage rasterBuf = UtilityImage.getBufferedImage(new Media(rasFile), false);
                    addRasterPattern(pattern, rasterBuf, getTileWidth(), getTileHeight());
                }
                else
                {
                    for (int c = 0; c < rasters.length; c++)
                    {
                        if (this.smooth)
                        {
                            if (m == 0)
                            {
                                color[c] = UtilityImage.getRasterColor(i, rasters[c],
                                        SetupEntityPlatformRastered.MAX_RASTERS);
                                colorNext[c] = UtilityImage.getRasterColor(i + 1, rasters[c],
                                        SetupEntityPlatformRastered.MAX_RASTERS);
                            }
                            else
                            {
                                color[c] = UtilityImage.getRasterColor(SetupEntityPlatformRastered.MAX_RASTERS - i,
                                        rasters[c], SetupEntityPlatformRastered.MAX_RASTERS);
                                colorNext[c] = UtilityImage.getRasterColor(SetupEntityPlatformRastered.MAX_RASTERS - i
                                        - 1, rasters[c], SetupEntityPlatformRastered.MAX_RASTERS);
                            }
                        }
                        else
                        {
                            color[c] = UtilityImage.getRasterColor(i, rasters[c],
                                    SetupEntityPlatformRastered.MAX_RASTERS);
                            colorNext[c] = color[c];
                        }
                    }
                    addRasterPattern(directory, pattern, i, color[0], color[1], color[2], colorNext[0], colorNext[1],
                            colorNext[2]);
                }
            }
        }
    }

    /**
     * Add a raster pattern.
     * 
     * @param directory The current tiles directory.
     * @param pattern The current pattern.
     * @param rasterID The raster id.
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @param er The end red.
     * @param eg The end green.
     * @param eb The end blue.
     */
    private void addRasterPattern(String directory, Integer pattern, int rasterID, int fr, int fg, int fb, int er,
            int eg, int eb)
    {
        final SpriteTiled original = super.getPattern(pattern);
        final BufferedImage buf = original.getSurface();
        final BufferedImage rasterBuf = UtilityImage.getRasterBuffer(buf, fr, fg, fb, er, eg, eb, getTileHeight());
        final String rasFile = Media.getPath(Media.getTempDir(), directory, pattern + "_" + rasterID + ".png");
        final File file = new File(Media.getPath(Media.getTempDir(), directory));

        if (!file.exists())
        {
            if (!file.mkdir())
            {
                Verbose.warning(MapTilePlatformRastered.class, "addRasterPattern", "Directory has not been created: "
                        + file.getPath());
            }
        }
        if (cache)
        {
            UtilityImage.saveImage(rasterBuf, new Media(rasFile));
        }
        addRasterPattern(pattern, rasterBuf, getTileWidth(), getTileHeight());
    }

    /**
     * Add a raster pattern.
     * 
     * @param pattern The current pattern.
     * @param surface The surface reference.
     * @param tw The tile width.
     * @param th The tile height.
     */
    private void addRasterPattern(Integer pattern, BufferedImage surface, int tw, int th)
    {
        List<SpriteTiled> rasters = rasterPatterns.get(pattern);
        if (rasters == null)
        {
            rasters = new ArrayList<>(SetupEntityPlatformRastered.MAX_RASTERS);
            rasterPatterns.put(pattern, rasters);
        }
        final SpriteTiled raster = Drawable.loadSpriteTiled(surface, tw, th);
        rasters.add(raster);
    }

    /*
     * MapTile
     */

    @Override
    public void loadPatterns(Media directory)
    {
        super.loadPatterns(directory);
        final String path = directory.getPath();
        if (!rasterLoaded)
        {
            if (rasterFile != null)
            {
                final Set<Integer> patterns = getPatterns();
                final Iterator<Integer> itr = patterns.iterator();
                final int[][] rasters = UtilityImage.loadRaster(rasterFile);

                while (itr.hasNext())
                {
                    final Integer pattern = itr.next();
                    loadRaster(path, pattern, rasters);
                }
            }
            rasterLoaded = true;
        }
    }

    @Override
    protected void renderingTile(Graphic g, T tile, int x, int y, int screenHeight)
    {
        SpriteTiled ts;
        if (rasterFile != null)
        {
            ts = getRasterPattern(tile.getPattern(), this.getRasterIndex(tile));
        }
        else
        {
            ts = super.getPattern(tile.getPattern());
        }
        ts.render(g, tile.getNumber(), x, y + screenHeight);
    }
}
