package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.util.Locale;

/**
 * List of world types.
 */
public enum WorldType
{
    /** Swamp world. */
    SWAMP(0, Music.SWAMP);

    /** Values. */
    private static final WorldType[] VALUES = WorldType.values();

    /**
     * Get the type from its index.
     * 
     * @param index The index.
     * @return The type.
     */
    public static WorldType get(int index)
    {
        return WorldType.VALUES[index];
    }

    /** Index. */
    private final int index;
    /** World music. */
    private Music music;

    /**
     * Constructor.
     * 
     * @param index The index number.
     * @param music The music type.
     */
    private WorldType(int index, Music music)
    {
        this.index = index;
        this.music = music;
    }

    /**
     * Get the index value.
     * 
     * @return The index value.
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Get the music type.
     * 
     * @return The music type.
     */
    public Music getMusic()
    {
        return music;
    }

    /**
     * Get the name as a path (lower case).
     * 
     * @return The name.
     */
    public String asPathName()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Get the title name (first letter as upper).
     * 
     * @return The title name.
     */
    @Override
    public String toString()
    {
        final String string = asPathName();
        return Character.toString(string.charAt(0)).toUpperCase(Locale.ENGLISH) + string.substring(1);
    }
}
