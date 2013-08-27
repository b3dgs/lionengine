package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

/**
 * Types of entities. Lower case is preferred, as the name has to be the same with its corresponding files.
 */
public enum TypeEntity
{
    /** Talisment (item). */
    talisment(TypeEntityCategory.ITEM),
    /** Valdyn (player). */
    valdyn(TypeEntityCategory.PLAYER);

    /** Type category. */
    private final TypeEntityCategory category;

    /**
     * Constructor.
     * 
     * @param category The category type.
     */
    private TypeEntity(TypeEntityCategory category)
    {
        this.category = category;
    }

    /**
     * Get the entity category type.
     * 
     * @return The entity category type.
     */
    public TypeEntityCategory getCategory()
    {
        return category;
    }
}
