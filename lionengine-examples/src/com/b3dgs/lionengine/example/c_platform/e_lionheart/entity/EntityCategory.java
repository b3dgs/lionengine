package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.util.Locale;

/**
 * List of entity categories.
 */
public enum EntityCategory
{
    /** Item (can be taken). */
    ITEM("items"),
    /** Monster (can be destroyed and attack the player). */
    MONSTER("monsters"),
    /** Scenery (other objects that are required to complete a level). */
    SCENERY("sceneries"),
    /** Valdyn (player). */
    PLAYER("players");

    /** Folder name. */
    private final String folder;
    /** Count number. */
    private int count;

    /**
     * Constructor.
     * 
     * @param folder The folder name.
     */
    private EntityCategory(String folder)
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
     * Get the count number.
     * 
     * @return The count number.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Increase the count number.
     */
    void increase()
    {
        count++;
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
