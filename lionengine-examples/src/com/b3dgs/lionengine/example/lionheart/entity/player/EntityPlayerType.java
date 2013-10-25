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
package com.b3dgs.lionengine.example.lionheart.entity.player;

import java.io.IOException;

import com.b3dgs.lionengine.example.lionheart.entity.EntityCategory;
import com.b3dgs.lionengine.example.lionheart.entity.EntityType;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.ObjectTypeUtility;

/**
 * List of player types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum EntityPlayerType implements EntityType<EntityPlayerType>
{
    /** Valdyn (player). */
    VALDYN(Valdyn.class, EntityCategory.PLAYER);

    /**
     * Load type from its saved format.
     * 
     * @param file The file reading.
     * @return The loaded type.
     * @throws IOException If error.
     */
    public static EntityPlayerType load(FileReading file) throws IOException
    {
        return EntityPlayerType.valueOf(file.readString());
    }

    /** Class target. */
    private final Class<?> target;
    /** Path name. */
    private final String path;
    /** Type category. */
    private final EntityCategory category;

    /**
     * Constructor.
     * 
     * @param target The target class.
     * @param category The entity category.
     */
    private EntityPlayerType(Class<?> target, EntityCategory category)
    {
        this.target = target;
        path = ObjectTypeUtility.getPathName(this);
        this.category = category;
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
    public EntityPlayerType getType()
    {
        return this;
    }

    /*
     * ObjectType
     */

    @Override
    public Class<?> getTargetClass()
    {
        return target;
    }

    @Override
    public String getPathName()
    {
        return path;
    }
}
