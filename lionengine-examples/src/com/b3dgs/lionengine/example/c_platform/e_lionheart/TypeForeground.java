package com.b3dgs.lionengine.example.c_platform.e_lionheart;

/**
 * Types of foregrounds.
 */
public enum TypeForeground
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
    private TypeForeground(String theme)
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
