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
package com.b3dgs.lionengine.game.rts.ability.producer;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.purview.Configurable;

/**
 * Represents the production factory. Designed to return a producible instance from its type.
 * 
 * @param <T> The entity enum type used.
 * @param <C> The cost type used.
 * @param <P> The producible type used.
 */
public abstract class FactoryProductionRts<T extends Enum<T> & ObjectType, C extends ProductionCostRts, P extends Producible<T, C>>
        extends FactoryGame<T, SetupGame>
{
    /** Productions folder. */
    private final String folder;

    /**
     * Constructor.
     * 
     * @param keyType The class of the enum type defined.
     * @param types The production types.
     * @param folder The productions folder.
     */
    public FactoryProductionRts(Class<T> keyType, T[] types, String folder)
    {
        super(keyType, types);
        this.folder = folder;
    }

    /**
     * Create a new producible from the entity type.
     * 
     * @param type The entity type.
     * @return The producible instance.
     */
    public abstract P createProducible(T type);

    /**
     * Create a new producible from the entity type.
     * 
     * @param type The entity type.
     * @param tx The producible horizontal tile.
     * @param ty The producible vertical tile.
     * @return The producible instance.
     */
    public abstract P createProducible(T type, int tx, int ty);

    /**
     * Get a configurable reference from its type.
     * 
     * @param type The reference type.
     * @return The configurable reference.
     */
    public Configurable getConfig(T type)
    {
        return getSetup(type).configurable;
    }

    /**
     * Create a setup from its media.
     * 
     * @param type The production type.
     * @param config The setup media config file.
     * @return The setup instance.
     */
    protected abstract SetupGame createSetup(T type, Media config);

    /*
     * FactoryGame
     */

    @Override
    protected SetupGame createSetup(T type)
    {
        final Media config = Media.get(folder, type.asPathName() + ".xml");
        return createSetup(type, config);
    }
}
