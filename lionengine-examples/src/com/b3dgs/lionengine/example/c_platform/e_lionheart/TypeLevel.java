package com.b3dgs.lionengine.example.c_platform.e_lionheart;

/**
 * List of game levels.
 */
public enum TypeLevel
{
    /** Stage 1. */
    SWAMP_1("level1.lrm");

    /** Total number of levels. */
    public static final int LEVELS_NUMBER = TypeLevel.values().length;
    /** Level filename. */
    private final String filename;

    /**
     * Constructor.
     * 
     * @param filename The level filename.
     */
    private TypeLevel(String filename)
    {
        this.filename = filename;
    }

    /**
     * Get the level filename.
     * 
     * @return The level filename.
     */
    public String getFilename()
    {
        return filename;
    }
}
