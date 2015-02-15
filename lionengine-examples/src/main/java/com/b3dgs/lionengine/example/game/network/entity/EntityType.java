/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.network.entity;

import java.util.HashMap;

/**
 * List of entity types shared via the network.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum EntityType
{
    /** Mario. */
    MARIO(Mario.class),
    /** Goomba. */
    GOOMBA(Goomba.class);

    /** Values. */
    private static final EntityType[] VALUES = EntityType.values();

    /**
     * Get the type from its id.
     * 
     * @param id The entity type id.
     * @return The entity id.
     */
    public static EntityType fromId(byte id)
    {
        return EntityType.VALUES[id];
    }

    /**
     * Get the entity type from its class.
     * 
     * @param clazz The entity class.
     * @return The entity type.
     */
    public static EntityType fromClass(Class<? extends Entity> clazz)
    {
        return EntityTypeMap.fromClass(clazz);
    }

    /** Entity class. */
    private final Class<? extends Entity> clazz;

    /**
     * Constructor.
     * 
     * @param clazz The entity class.
     */
    private EntityType(Class<? extends Entity> clazz)
    {
        this.clazz = clazz;
        EntityTypeMap.link(clazz, this);
    }

    /**
     * Get the entity class.
     * 
     * @return The entity class.
     */
    public Class<? extends Entity> getEntityClass()
    {
        return clazz;
    }

    /**
     * Represents the link between entity class and their enum type.
     */
    private static final class EntityTypeMap
    {
        /** Type class map. */
        private static final HashMap<Class<? extends Entity>, EntityType> MAP = new HashMap<>(4);

        /**
         * Link the class and the type.
         * 
         * @param clazz The class to link to the type.
         * @param type The entity type.
         */
        static void link(Class<? extends Entity> clazz, EntityType type)
        {
            EntityTypeMap.MAP.put(clazz, type);
        }

        /**
         * Get the entity type from its class.
         * 
         * @param clazz The entity class.
         * @return The entity type.
         */
        static EntityType fromClass(Class<? extends Entity> clazz)
        {
            return EntityTypeMap.MAP.get(clazz);
        }
    }
}
