package com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape;

/**
 * Types of foregrounds.
 */
public enum ForegroundType
{
    /** Water. */
    WATER("Water"),
    /** Lava. */
    LAVA("Lava");

    /** Theme name. */
    private final String theme;

    /**
     * Constructor.
     * 
     * @param theme The theme name.
     */
    private ForegroundType(String theme)
    {
        this.theme = theme;
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
}
