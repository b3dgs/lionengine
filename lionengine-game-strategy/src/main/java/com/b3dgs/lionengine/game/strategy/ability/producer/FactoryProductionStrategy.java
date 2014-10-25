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
package com.b3dgs.lionengine.game.strategy.ability.producer;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.strategy.entity.EntityStrategy;

/**
 * Represents the production factory. Designed to return a producible instance from its type.
 * 
 * @param <E> The entity enum type used.
 * @param <C> The cost type used.
 * @param <P> The producible type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class FactoryProductionStrategy<E extends EntityStrategy, C extends ProductionCostStrategy, P extends Producible<E, C>>
        extends FactoryGame<SetupGame>
{
    /**
     * Constructor base.
     */
    public FactoryProductionStrategy()
    {
        super();
    }

    /**
     * Create a new producible from the entity media.
     * 
     * @param media The entity media.
     * @return The producible instance.
     */
    public abstract P create(Media media);

    /**
     * Create a new producible from the entity media.
     * 
     * @param media The entity media.
     * @param tx The producible horizontal tile.
     * @param ty The producible vertical tile.
     * @return The producible instance.
     */
    public abstract P create(Media media, int tx, int ty);
}
