package com.b3dgs.lionengine.example.c_platform.e_lionheart;

/**
 * List of available musics.
 */
public enum Music
{
    /** Swamp music. */
    SWAMP("swamp.sc68");

    /** Music filename. */
    private final String filename;

    /**
     * Constructor.
     * 
     * @param filename The music filename.
     */
    private Music(String filename)
    {
        this.filename = filename;
    }

    /**
     * Get the music filename.
     * 
     * @return The music filename.
     */
    public String getFilename()
    {
        return filename;
    }
}
