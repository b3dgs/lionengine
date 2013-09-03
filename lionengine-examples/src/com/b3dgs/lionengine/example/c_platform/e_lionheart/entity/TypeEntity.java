package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.util.Locale;

/**
 * List of entity types.
 */
public enum TypeEntity
{
    /*
     * Items
     */

    /** Talisment (item). */
    TALISMENT(TypeEntityCategory.ITEM),

    /*
     * Monsters
     */

    /** Crawling (monster). */
    CRAWLING(TypeEntityCategory.MONSTER),

    /*
     * Sceneries
     */

    /** Sheet (scenery). */
    SHEET(TypeEntityCategory.SCENERY),

    /*
     * Valdyn
     */

    /** Valdyn (player). */
    VALDYN(TypeEntityCategory.PLAYER);

    /** Values. */
    private static final TypeEntity[] VALUES = TypeEntity.values();

    /**
     * Get the type from its index.
     * 
     * @param index The index.
     * @return The type.
     */
    public static TypeEntity get(int index)
    {
        return TypeEntity.VALUES[index];
    }

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
        category.increase();
    }

    /**
     * Get the index value.
     * 
     * @return The index value.
     */
    public int getIndex()
    {
        return ordinal();
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
