package com.b3dgs.lionengine.game;

import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;
import com.b3dgs.lionengine.utility.UtilityImage;

/**
 * Define a structure used to create multiple entity, sharing the same data.
 */
public class SetupSurfaceGame
        extends SetupGame
{
    /** Surface reference. */
    public final BufferedImage surface;
    /** Surface file name. */
    public final Media surfaceFile;

    /**
     * Create a new entity setup.
     * 
     * @param config The config media.
     */
    public SetupSurfaceGame(Media config)
    {
        this(new ConfigurableModel(), config, false);
    }

    /**
     * Create a new entity setup.
     * 
     * @param config The config media.
     * @param alpha The alpha use flag.
     */
    public SetupSurfaceGame(Media config, boolean alpha)
    {
        this(new ConfigurableModel(), config, alpha);
    }

    /**
     * Create a new entity setup.
     * 
     * @param configurable The configurable reference.
     * @param config The config media.
     * @param alpha The alpha use flag.
     */
    public SetupSurfaceGame(Configurable configurable, Media config, boolean alpha)
    {
        super(configurable, config);
        final String conf = config.getPath();
        surfaceFile = new Media(conf.substring(0, conf.lastIndexOf(Media.getSeparator()) + 1)
                + this.configurable.getDataString("surface"));
        surface = UtilityImage.getBufferedImage(surfaceFile, alpha);
    }
}
