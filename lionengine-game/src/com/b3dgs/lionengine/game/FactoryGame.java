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

/**
 * Abstract entity factory. It performs a list of available entities from a directory considering an input enumeration.
 * Data are stored with an enumeration as key.
 * <p>
 * Sample implementation:
 * </p>
 * 
 * <pre>
 * public class Factory
 *         extends FactoryGame&lt;TypeEntity, SetupGame&gt;
 * {
 *     public Factory()
 *     {
 *         super(TypeEntity.class);
 *         loadAll(TypeEntity.values());
 *     }
 * 
 *     &#064;Override
 *     protected SetupGame createSetup(TypeEntity id)
 *     {
 *         return new SetupGame(Media.get(&quot;directory&quot;, type + &quot;.xml&quot;));
 *     }
 * }
 * </pre>
 * 
 * @param <T> The enum containing all type.
 * @param <S> The setup entity type used.
 */
public abstract class FactoryGame<T extends Enum<T>, S extends SetupGame>
{
    /** Setups list. */
    private final Map<T, S> setups;

    /**
     * Create a new factory.
     * 
     * @param keyType The class of the enum type defined.
     */
    public FactoryGame(Class<T> keyType)
    {
        setups = new EnumMap<>(keyType);
    }

    /**
     * Get setup instance.
     * 
     * @param type The entity type (as enumeration).
     * @return The setup instance.
     */
    protected abstract S createSetup(T type);

    /**
     * Load all setup from their list, considering an additional list of arguments for specific cases.
     * 
     * @param list The entities list from enumeration.
     */
    public void loadAll(T[] list)
    {
        for (final T type : list)
        {
            addSetup(type, createSetup(type));
        }
    }

    /**
     * Add a setup reference at the specified type.
     * 
     * @param type The reference type.
     * @param setup The setup reference.
     */
    public void addSetup(T type, S setup)
    {
        setups.put(type, setup);
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
}
