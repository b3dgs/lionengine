package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

/**
 * List of selection types.
 */
public enum TypeSelection
{
    /** Select an entity. */
    SELECT("Selecting"),
    /** Place an entity. */
    PLACE("Placing"),
    /** Delete an entity. */
    DELETE("Delete"),
    /** Place player related markers. */
    PLAYER("Selecting");

    /** The description. */
    private final String description;

    /**
     * Constructor.
     * 
     * @param description The description.
     */
    private TypeSelection(String description)
    {
        this.description = description;
    }

    /**
     * Get the description.
     * 
     * @return The description.
     */
    public String getDescription()
    {
        return description;
    }
}
