package com.b3dgs.lionengine.game.effect;

import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;
import com.b3dgs.lionengine.utility.UtilityImage;

/**
 * Define a structure used to create multiple effect, sharing the same data.
 */
public class SetupEffectGame
        extends SetupGame
{
    /** Surface reference. */
    public final BufferedImage surface;
    /** Surface file name. */
    public final Media surfaceFile;

    /**
     * Create a new effect setup.
     * 
     * @param config The config media.
     */
    public SetupEffectGame(Media config)
    {
        this(new ConfigurableModel(), config, false);
    }

    /**
     * Create a new effect setup.
     * 
     * @param config The config media.
     * @param alpha The alpha use flag.
     */
    public SetupEffectGame(Media config, boolean alpha)
    {
        this(new ConfigurableModel(), config, alpha);
    }

    /**
     * Create a new effect setup.
     * 
     * @param configurable The configurable reference.
     * @param config The config media.
     * @param alpha The alpha use flag.
     */
    public SetupEffectGame(Configurable configurable, Media config, boolean alpha)
    {
        super(configurable, config);
        final String conf = config.getPath();
        surfaceFile = new Media(conf.substring(0, conf.lastIndexOf(Media.getSeparator()) + 1)
                + this.configurable.getDataString("surface"));
        surface = UtilityImage.getBufferedImage(surfaceFile, alpha);
    }
}
