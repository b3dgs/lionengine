package com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.game.platform.background.BackgroundPlatform;

/**
 * Landscape factory.
 */
public final class FactoryLandscape
{
    /** Unknown entity error message. */
    private static final String UNKNOWN_LANDSCAPE_ERROR = "Unknown landscape: ";
    /** The resolution source reference. */
    private final Resolution source;
    /** The horizontal factor. */
    private final double scaleH;
    /** The vertical factor. */
    private final double scaleV;
    /** Background flickering flag. */
    private final boolean flicker;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param scaleH The horizontal factor.
     * @param scaleV The horizontal factor.
     * @param flicker The flicker flag.
     */
    public FactoryLandscape(Resolution source, double scaleH, double scaleV, boolean flicker)
    {
        this.source = source;
        this.scaleH = scaleH;
        this.scaleV = scaleV;
        this.flicker = flicker;
    }

    /**
     * Create a landscape from its type.
     * 
     * @param landscape The landscape type.
     * @return The landscape instance.
     */
    public Landscape createLandscape(LandscapeType landscape)
    {
        switch (landscape.getWorld())
        {
            case SWAMP:
            {
                final BackgroundPlatform background = new Swamp(source, scaleH, scaleV, landscape.getTheme(), flicker);
                final Foreground foreground = new Foreground(source, scaleH, scaleV, landscape.getForeground()
                        .getTheme());
                return new Landscape(landscape, background, foreground);
            }
            default:
                throw new LionEngineException(FactoryLandscape.UNKNOWN_LANDSCAPE_ERROR + landscape);
        }
    }
}
