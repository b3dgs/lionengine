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
    /** Sword2 (item). */
    SWORD2(TypeEntityCategory.ITEM),
    /** Sword3 (item). */
    SWORD3(TypeEntityCategory.ITEM),
    /** Sword4 (item). */
    SWORD4(TypeEntityCategory.ITEM),

    /*
     * Monsters
     */

    /** Crawling (monster). */
    CRAWLING(TypeEntityCategory.MONSTER),
    /** Dino (monster). */
    DINO(TypeEntityCategory.MONSTER),
    /** Bee (monster). */
    BEE(TypeEntityCategory.MONSTER),
    /** BeeLittle (monster). */
    BEE_LITTLE(TypeEntityCategory.MONSTER),
    /** BumbleBee (monster). */
    BUMBLE_BEE(TypeEntityCategory.MONSTER),

    /*
     * Sceneries
     */

    /** Sheet (scenery). */
    SHEET(TypeEntityCategory.SCENERY),
    /** Turning auto (scenery). */
    TURNING_AUTO(TypeEntityCategory.SCENERY),
    /** Turning hit (scenery). */
    TURNING_HIT(TypeEntityCategory.SCENERY),
    /** Beetle horizontal (scenery). */
    BEETLE_HORIZONTAL(TypeEntityCategory.SCENERY),
    /** Beetle vertical (scenery). */
    BEETLE_VERTICAL(TypeEntityCategory.SCENERY),

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
     * Get the class name equivalence.
     * 
     * @return The class name equivalence.
     */
    public String asClassName()
    {
        final char[] name = toString().toCharArray();
        for (int i = 0; i < name.length; i++)
        {
            if (name[i] == '_')
            {
                name[i + 1] = Character.toUpperCase(name[i + 1]);
            }
        }
        return String.valueOf(name).replace("_", "");
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
