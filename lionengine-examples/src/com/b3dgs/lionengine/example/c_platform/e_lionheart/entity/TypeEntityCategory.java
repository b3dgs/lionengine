package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

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
    NEUTRAL("neutrals"),
    /** Player. */
    PLAYER("players");

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
}
