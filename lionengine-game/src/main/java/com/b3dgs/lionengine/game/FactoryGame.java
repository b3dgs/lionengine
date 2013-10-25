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
package com.b3dgs.lionengine.game;

import java.util.EnumMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;

/**
 * It performs a list of {@link SetupGame} considering an input enumeration. This way it is possible to create new
 * instances of object related to their type by sharing the same data.
 * <p>
 * Sample implementation:
 * </p>
 * 
 * <pre>
 * public class Factory
 *         extends FactoryGame&lt;EntityType, SetupGame&gt;
 * {
 *     public Factory()
 *     {
 *         super(EntityType.class);
 *         load();
 *     }
 * 
 *     &#064;Override
 *     protected SetupGame createSetup(EntityType type)
 *     {
 *         return new SetupGame(Media.get(type.name() + &quot;.xml&quot;));
 *     }
 * }
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <T> The enum containing all types.
 * @param <S> The setup type used.
 */
public abstract class FactoryGame<T extends Enum<T>, S extends SetupGame>
{
    /** Enum type error. */
    private static final String ERROR_TYPE = "Enum type class must not be null !";

    /** Types list. */
    private final T[] types;
    /** Setups list. */
    private final Map<T, S> setups;

    /**
     * Constructor.
     * 
     * @param enumType The class of the enum type defined.
     */
    public FactoryGame(Class<T> enumType)
    {
        Check.notNull(enumType, FactoryGame.ERROR_TYPE);
        this.types = enumType.getEnumConstants();
        setups = new EnumMap<>(enumType);
    }

    /**
     * Get setup instance from the type.
     * 
     * @param type The enum type.
     * @return The setup instance.
     */
    protected abstract S createSetup(T type);

    /**
     * Load setup for each type.
     */
    public void load()
    {
        for (final T type : types)
        {
            addSetup(type, createSetup(type));
        }
    }

    /**
     * Get a setup reference from its type.
     * 
     * @param type The reference type.
     * @return The setup reference.
     */
    public S getSetup(T type)
    {
        return setups.get(type);
    }

    /**
     * Get the types list.
     * 
     * @return The types list.
     */
    public T[] getTypes()
    {
        return types;
    }

    /**
     * Add a setup reference for the specified type.
     * 
     * @param type The enum type.
     * @param setup The setup reference.
     */
    protected void addSetup(T type, S setup)
    {
        setups.put(type, setup);
    }
}
