package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;

/**
 * Define a structure used to create configurable objects.
 */
public class SetupGame
{
    /** Configurable reference. */
    public final Configurable configurable;
    /** Config file name. */
    public final Media configFile;

    /**
     * Create a new entity setup.
     * 
     * @param config The config media.
     */
    public SetupGame(Media config)
    {
        this(new ConfigurableModel(), config);
    }

    /**
     * Create a new entity setup.
     * 
     * @param configurable The configurable reference.
     * @param config The config media.
     */
    public SetupGame(Configurable configurable, Media config)
    {
        Check.notNull(configurable, "The configurable must bot be null !");
        Media.exist(config);
        this.configurable = configurable;
        this.configurable.loadData(config);
        configFile = config;
    }
}
