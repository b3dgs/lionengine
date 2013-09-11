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
    /** Potion little (item). */
    POTION_LITTLE(TypeEntityCategory.ITEM),
    /** Potion big (item). */
    POTION_BIG(TypeEntityCategory.ITEM),
    /** Life (item). */
    LIFE(TypeEntityCategory.ITEM),
    /** Sword1 (item). */
    SWORD1(TypeEntityCategory.ITEM),
    /** Sword2 (item). */
    SWORD2(TypeEntityCategory.ITEM),
    /** Sword3 (item). */
    SWORD3(TypeEntityCategory.ITEM),

    /*
     * Monsters
     */

    /** Crawling (monster). */
    CRAWLING(TypeEntityCategory.MONSTER),
    /** Dino (monster). */
    DINO(TypeEntityCategory.MONSTER),

    /*
     * Sceneries
     */

    /** Sheet (scenery). */
    SHEET(TypeEntityCategory.SCENERY),
    /** Turning auto (scenery). */
    TURNING_AUTO(TypeEntityCategory.SCENERY),
    /** Turning hit (scenery). */
    TURNING_HIT(TypeEntityCategory.SCENERY),

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
