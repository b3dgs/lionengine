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
package com.b3dgs.lionengine.example.lionheart.entity.swamp;

import java.io.IOException;

import com.b3dgs.lionengine.example.lionheart.entity.EntityCategory;
import com.b3dgs.lionengine.example.lionheart.entity.EntityType;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.ObjectTypeUtility;

/**
 * List of entity types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum EntitySwampType implements EntityType<EntitySwampType>
{
    /*
     * Item
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
     * Monster
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
     * Scenery
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
    BEETLE_VERTICAL(EntityCategory.SCENERY);

    /**
     * Load type from its saved format.
     * 
     * @param file The file reading.
     * @return The loaded type.
     * @throws IOException If error.
     */
    public static EntitySwampType load(FileReading file) throws IOException
    {
        return EntitySwampType.valueOf(file.readString());
    }

    /** Type category. */
    private final EntityCategory category;

    /**
     * Constructor.
     * 
     * @param category The category type.
     */
    private EntitySwampType(EntityCategory category)
    {
        this.category = category;
        category.increase();
    }

    /*
     * EntityType
     */

    @Override
    public void save(FileWriting file) throws IOException
    {
        file.writeString(name());
    }

    @Override
    public EntityCategory getCategory()
    {
        return category;
    }

    @Override
    public EntitySwampType getType()
    {
        return this;
    }

    /*
     * ObjectType
     */

    @Override
    public String asPathName()
    {
        return ObjectTypeUtility.asPathName(this);
    }

    @Override
    public String asClassName()
    {
        return ObjectTypeUtility.asClassName(this);
    }

    @Override
    public String toString()
    {
        return ObjectTypeUtility.toString(this);
    }
}
