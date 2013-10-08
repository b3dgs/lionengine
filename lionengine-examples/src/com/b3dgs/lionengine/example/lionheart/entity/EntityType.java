/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.example.lionheart.entity;

import java.io.IOException;
import java.util.Locale;

import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;

/**
 * List of entity types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum EntityType
{
    /*
     * Items
     */

    /** Talisment (item). */
    TALISMENT(EntityCategory.ITEM),
    /** Potion little (item). */
    POTION_LITTLE(EntityCategory.ITEM),
    /** Potion big (item). */
    POTION_BIG(EntityCategory.ITEM),
    /** Life (item). */
    LIFE(EntityCategory.ITEM),
    /** Sword2 (item). */
    SWORD2(EntityCategory.ITEM),
    /** Sword3 (item). */
    SWORD3(EntityCategory.ITEM),
    /** Sword4 (item). */
    SWORD4(EntityCategory.ITEM),

    /*
     * Monsters
     */

    /** Crawling (monster). */
    CRAWLING(EntityCategory.MONSTER),
    /** Dino (monster). */
    DINO(EntityCategory.MONSTER),
    /** Bee (monster). */
    BEE(EntityCategory.MONSTER),
    /** BeeLittle (monster). */
    BEE_LITTLE(EntityCategory.MONSTER),
    /** BumbleBee (monster). */
    BUMBLE_BEE(EntityCategory.MONSTER),

    /*
     * Sceneries
     */

    /** Sheet (scenery). */
    SHEET(EntityCategory.SCENERY),
    /** Turning auto (scenery). */
    TURNING_AUTO(EntityCategory.SCENERY),
    /** Turning hit (scenery). */
    TURNING_HIT(EntityCategory.SCENERY),
    /** Beetle horizontal (scenery). */
    BEETLE_HORIZONTAL(EntityCategory.SCENERY),
    /** Beetle vertical (scenery). */
    BEETLE_VERTICAL(EntityCategory.SCENERY),

    /*
     * Valdyn
     */

    /** Valdyn (player). */
    VALDYN(EntityCategory.PLAYER);

    /**
     * Load type from its saved format.
     * 
     * @param file The file reading.
     * @return The loaded type.
     * @throws IOException If error.
     */
    public static EntityType load(FileReading file) throws IOException
    {
        return EntityType.valueOf(file.readString());
    }

    /** Type category. */
    private final EntityCategory category;

    /**
     * Constructor.
     * 
     * @param category The category type.
     */
    private EntityType(EntityCategory category)
    {
        this.category = category;
        category.increase();
    }

    /**
     * Save the entity type.
     * 
     * @param file The file writing.
     * @throws IOException If error.
     */
    public void save(FileWriting file) throws IOException
    {
        file.writeString(name());
    }

    /**
     * Get the entity category type.
     * 
     * @return The entity category type.
     */
    public EntityCategory getCategory()
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
