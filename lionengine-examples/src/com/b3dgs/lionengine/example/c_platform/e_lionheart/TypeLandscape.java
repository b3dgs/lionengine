package com.b3dgs.lionengine.example.c_platform.e_lionheart;

/**
 * Types of landscapes.
 */
public enum TypeLandscape
{
    /** Swamp dusk. */
    SWAMP_DUSK("Stage1", "raster1.xml", TypeForeground.WATER),
    /** Swamp dawn. */
    SWAMP_DAWN("Stage2", "raster2.xml", TypeForeground.WATER),
    /** Swamp day. */
    SWAMP_DAY("Stage3", "raster3.xml", TypeForeground.WATER);

    /** Theme name. */
    private final String theme;
    /** Raster name. */
    private final String raster;
    /** The foreground used. */
    private final TypeForeground foreground;

    /**
     * Constructor.
     * 
     * @param theme The theme name.
     * @param raster The raster name.
     * @param water The water type.
     */
    private TypeLandscape(String theme, String raster, TypeForeground water)
    {
        this.theme = theme;
        this.raster = raster;
        foreground = water;
    }

    /**
     * Get the theme name.
     * 
     * @return The theme name.
     */
    public String getTheme()
    {
        return theme;
    }

    /**
     * Get the raster filename.
     * 
     * @return The raster filename.
     */
    public String getRaster()
    {
        return raster;
    }

    /**
     * Get the foreground used.
     * 
     * @return The foreground used.
     */
    public TypeForeground getForeground()
    {
        return foreground;
    }
}
