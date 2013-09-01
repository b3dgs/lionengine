package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.util.Locale;

/**
 * List of world types.
 */
public enum TypeWorld
{
    /** Swamp world. */
    SWAMP;

    /** Values. */
    private static final TypeWorld[] VALUES = TypeWorld.values();

    /**
     * Get the type from its index.
     * 
     * @param index The index.
     * @return The type.
     */
    public static TypeWorld get(int index)
    {
        return TypeWorld.VALUES[index];
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
