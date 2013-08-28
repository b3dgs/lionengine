package com.b3dgs.lionengine.game.platform;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.entity.SetupEntityGame;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.utility.UtilityImage;

/**
 * Define a structure used to create multiple rastered platform entry, sharing the same data.
 */
public class SetupEntityPlatformRastered
        extends SetupEntityGame
{
    /** Maximum rasters. */
    public static final int MAX_RASTERS = 15;
    /** Maximum rasters R. */
    public static final int MAX_RASTERS_R = SetupEntityPlatformRastered.MAX_RASTERS * 2;
    /** Maximum rasters M. */
    public static final int MAX_RASTERS_M = SetupEntityPlatformRastered.MAX_RASTERS - 1;
    /** List of rasters animation. */
    public final List<SpriteAnimated> rastersAnim;
    /** Raster filename. */
    public final Media rasterFile;
    /** Raster smooth flag. */
    public final boolean smoothRaster;
    /** Vertical frames. */
    private final int vf;
    /** Horizontal frames. */
    private final int hf;
    /** frame height. */
    private final int frameHeight;

    /**
     * Create a new rastered platform entry setup.
     * 
     * @param configurable The configurable reference.
     * @param config The config media.
     * @param alpha The alpha use flag.
     * @param rasterFile The raster media.
     * @param smoothRaster The raster smooth flag.
     */
    public SetupEntityPlatformRastered(Configurable configurable, Media config, boolean alpha, Media rasterFile,
            boolean smoothRaster)
    {
        super(configurable, config, alpha);
        this.rasterFile = rasterFile;
        this.smoothRaster = smoothRaster;
        if (rasterFile != null)
        {
            rastersAnim = new ArrayList<>(SetupEntityPlatformRastered.MAX_RASTERS);
            hf = configurable.getDataInteger("horizontal", "frames");
            vf = configurable.getDataInteger("vertical", "frames");
            frameHeight = surface.getHeight() / vf;
            loadRasters();
        }
        else
        {
            rastersAnim = null;
            hf = 0;
            vf = 0;
            frameHeight = 0;
        }
    }

    /**
     * Load rasters.
     */
    private void loadRasters()
    {
        final int[][] rasters = UtilityImage.loadRaster(rasterFile);
        final int[] color = new int[rasters.length];
        final int[] colorNext = new int[rasters.length];
        final int max = smoothRaster ? 2 : 1;

        for (int m = 0; m < max; m++)
        {
            for (int i = 1; i <= SetupEntityPlatformRastered.MAX_RASTERS; i++)
            {
                for (int c = 0; c < rasters.length; c++)
                {
                    if (smoothRaster)
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
                            colorNext[c] = UtilityImage.getRasterColor(SetupEntityPlatformRastered.MAX_RASTERS - i - 1,
                                    rasters[c], SetupEntityPlatformRastered.MAX_RASTERS);
                        }
                    }
                    else
                    {
                        color[c] = UtilityImage.getRasterColor(i, rasters[c], SetupEntityPlatformRastered.MAX_RASTERS);
                        colorNext[c] = color[c];
                    }
                }
                addRaster(color[0], color[1], color[2], colorNext[0], colorNext[1], colorNext[2]);
            }
        }
    }

    /**
     * Add raster.
     * 
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @param er The end red.
     * @param eg The end green.
     * @param eb The end blue.
     */
    private void addRaster(int fr, int fg, int fb, int er, int eg, int eb)
    {
        final BufferedImage rasterBuf = UtilityImage.getRasterBuffer(surface, fr, fg, fb, er, eg, eb, frameHeight);
        final SpriteAnimated raster = Drawable.loadSpriteAnimated(rasterBuf, hf, vf);
        rastersAnim.add(raster);
    }
}
