package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.io.IOException;
import java.util.Locale;

import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;

/**
 * List of world types.
 */
public enum WorldType
{
    /** Swamp world. */
    SWAMP(Music.SWAMP);

    /**
     * Load type from its saved format.
     * 
     * @param file The file reading.
     * @return The loaded type.
     * @throws IOException If error.
     */
    public static WorldType load(FileReading file) throws IOException
    {
        return WorldType.valueOf(file.readString());
    }

    /** World music. */
    private Music music;

    /**
     * Constructor.
     * 
     * @param music The music type.
     */
    private WorldType(Music music)
    {
        this.music = music;
    }

    /**
     * Save the world type.
     * 
     * @param file The file writing.
     * @throws IOException If error.
     */
    public void save(FileWriting file) throws IOException
    {
        file.writeString(name());
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
