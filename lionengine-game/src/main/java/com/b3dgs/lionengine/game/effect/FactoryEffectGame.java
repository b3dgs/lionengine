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
package com.b3dgs.lionengine.game.effect;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Abstract effect factory. It performs a list of available effects from a directory considering an input enumeration.
 * Data are stored with an enumeration as key.
 * 
 * @param <T> The enum containing all type.
 * @param <S> The setup type.
 * @param <E> The effect type.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class FactoryEffectGame<T extends Enum<T> & ObjectType, S extends SetupSurfaceGame, E extends EffectGame>
        extends FactoryGame<T, S>
{
    /** Entities folder. */
    private final String folder;

    /**
     * Constructor.
     * 
     * @param keyType The class of the enum type defined.
     * @param types The effect types.
     * @param folder The effects folder.
     */
    public FactoryEffectGame(Class<T> keyType, T[] types, String folder)
    {
        super(keyType, types);
        this.folder = folder;
    }

    /**
     * Get the effect instance from its key. It is recommended to use a switch on the key, and throw an exception for
     * the default case (instead of returning a <code>null</code> value).
     * 
     * @param type The effect type.
     * @return The effect instance.
     */
    public abstract E createEffect(T type);

    /**
     * Create a setup from its media.
     * 
     * @param type The effect type.
     * @param config The setup media config file.
     * @return The setup instance.
     */
    protected abstract S createSetup(T type, Media config);

    /*
     * FactoryGame
     */

    @Override
    protected S createSetup(T type)
    {
        final Media config = Media.get(folder, type.asPathName() + ".xml");
        return createSetup(type, config);
    }
}
