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
 * Game type factory. It performs a list of available types from a directory considering an input enumeration.
 * Data are stored with an enumeration as key.
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
 *         super(EntityType.class, EntityType.values());
 *         load();
 *     }
 * 
 *     &#064;Override
 *     protected SetupGame createSetup(EntityType id)
 *     {
 *         return new SetupGame(Media.get(&quot;directory&quot;, id + &quot;.xml&quot;));
 *     }
 * }
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <T> The enum containing all types.
 * @param <S> The setup object type used.
 */
public abstract class FactoryGame<T extends Enum<T>, S extends SetupGame>
{
    /** Types list. */
    private final T[] types;
    /** Setups list. */
    private final Map<T, S> setups;

    /**
     * Constructor.
     * 
     * @param keyType The class of the enum type defined.
     * @param types The types list (use method <code>values</code> of enum).
     */
    public FactoryGame(Class<T> keyType, T[] types)
    {
        this.types = types;
        setups = new EnumMap<>(keyType);
    }

    /**
     * Get setup instance.
     * 
     * @param type The object type.
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

    /**
     * Get the types list.
     * 
     * @return The types list.
     */
    public T[] getTypes()
    {
        return types;
    }
}
