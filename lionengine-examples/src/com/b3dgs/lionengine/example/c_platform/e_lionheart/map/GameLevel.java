package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

/**
 * List of game levels.
 */
public enum GameLevel
{
    /** Stage 1. */
    SWAMP_1("level1.lrm");

    /** Levels list. */
    public static final GameLevel[] LEVELS = GameLevel.values();
    /** Level filename. */
    private final String filename;

    /**
     * Constructor.
     * 
     * @param filename The level filename.
     */
    private GameLevel(String filename)
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
