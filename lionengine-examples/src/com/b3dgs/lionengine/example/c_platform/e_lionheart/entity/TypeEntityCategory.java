package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.util.Locale;

/**
 * List of entity categories.
 */
public enum TypeEntityCategory
{
    /** Item. */
    ITEM("items"),
    /** Monster. */
    MONSTER("monsters"),
    /** Neutral. */
    NEUTRAL("neutrals");

    /** Folder name. */
    private final String folder;

    /**
     * Constructor.
     * 
     * @param folder The folder name.
     */
    private TypeEntityCategory(String folder)
    {
        this.folder = folder;
    }

    /**
     * Get the folder name.
     * 
     * @return The folder name.
     */
    public String getFolder()
    {
        return folder;
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
