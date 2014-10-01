/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows to convert from object enum name to class reference.
 * 
 * @param <T> The primary object type used.
 * @param <E> The object enum type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ObjectTypeConverter<T, E extends Enum<E> & ObjectType<T>>
{
    /** Enum class reference. */
    private final Class<E> enumClass;
    /** Mapping between class reference and enum type. */
    private final Map<Class<? extends T>, E> map;

    /**
     * Constructor.
     * 
     * @param enumClass The enum class reference.
     */
    public ObjectTypeConverter(Class<E> enumClass)
    {
        this.enumClass = enumClass;
        map = new HashMap<>();
        for (final E enumType : enumClass.getEnumConstants())
        {
            map.put(enumType.getType(), enumType);
        }
    }

    /**
     * Get the class type from the enum type name.
     * 
     * @param type The enum type name.
     * @return The class type reference.
     */
    public Class<? extends T> getType(String type)
    {
        final E enumValue = Enum.valueOf(enumClass, type);
        return enumValue.getType();
    }

    /**
     * Get the corresponding enum from the object class type.
     * 
     * @param type The object class type.
     * @return The corresponding enum type.
     */
    public E get(Class<? extends T> type)
    {
        return map.get(type);
    }
}
