package com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.platform.background.BackgroundPlatform;

/**
 * Landscape factory.
 */
public final class FactoryLandscape
{
    /** Unknown entity error message. */
    private static final String UNKNOWN_LANDSCAPE_ERROR = "Unknown landscape: ";
    /** The config reference. */
    private final Config config;
    /** The wide flag. */
    private final boolean wide;
    /** Background flickering flag. */
    private final boolean flicker;

    /**
     * Constructor.
     * 
     * @param config The config reference.
     * @param wide The wide state.
     * @param flicker The flicker flag.
     */
    public FactoryLandscape(Config config, boolean wide, boolean flicker)
    {
        this.config = config;
        this.wide = wide;
        this.flicker = flicker;
    }

    /**
     * Create a landscape from its type.
     * 
     * @param landscape The landscape type.
     * @return The landscape instance.
     */
    public Landscape createLandscape(TypeLandscape landscape)
    {
        switch (landscape)
        {
            case SWAMP_DUSK:
            case SWAMP_DAWN:
            case SWAMP_DAY:
            {
                final BackgroundPlatform background = new Swamp(config, wide, landscape.getTheme(), flicker);
                final Water foreground = new Water(config, wide, landscape.getForeground().getTheme());
                return new Landscape(landscape, background, foreground);
            }
            default:
                throw new LionEngineException(FactoryLandscape.UNKNOWN_LANDSCAPE_ERROR + landscape);
        }
    }
}
