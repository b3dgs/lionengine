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

import com.b3dgs.lionengine.game.rts.entity.EntityRts;

/**
 * List of event linked to the production.
 * 
 * @param <T> The entity enum type used.
 * @param <C> The cost type used.
 * @param <P> The producible type used.
 * @param <E> The entity type used.
 */
public interface ProducerListener<T extends Enum<T>, C extends ProductionCostRts, P extends Producible<T, C>, E extends EntityRts>
{
    /**
     * Notify listener that current element can not be produced.
     * 
     * @param producible The element that would have been under production.
     */
    void notifyCanNotProduce(P producible);

    /**
     * Notify listener that production is starting for this element.
     * 
     * @param producible The element going to be produced
     * @param entity The entity instance from element.
     */
    void notifyStartProduction(P producible, E entity);

    /**
     * Notify listener that this element is currently under production.
     * 
     * @param producible The element under production.
     * @param entity The entity instance from element.
     */
    void notifyProducing(P producible, E entity);

    /**
     * Notify listener that this element has been produced.
     * 
     * @param producible The element produced.
     * @param entity The entity instance from element.
     */
    void notifyProduced(P producible, E entity);
}
