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

import java.util.Iterator;

/**
 * Represents an ability of creating new entity.
 * 
 * @param <T> The entity enum type used.
 * @param <C> The cost type used.
 * @param <P> The producible type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface ProducerServices<T extends Enum<T>, C extends ProductionCostRts, P extends Producible<T, C>>
{
    /**
     * Add an element to the production queue. It works as a FIFO (First In, First Out). Production will be stopped when
     * the list is empty. In this case, getProductionProgress() will return -1. Production list stores only entity name.
     * You have to return the corresponding instance by overriding this function: getUnitToProduce(Enum<?> id) from the
     * ProducerModel.
     * 
     * @param producible The element to produce.
     */
    void addToProductionQueue(P producible);

    /**
     * Update production routine.
     * 
     * @param extrp The extrapolation value.
     */
    void updateProduction(double extrp);

    /**
     * Skip current production.
     */
    void skipProduction();

    /**
     * Stop any production.
     */
    void stopProduction();

    /**
     * Get production progress. If it returns -1, it means that there are not any active production.
     * 
     * @return The progress value.
     */
    double getProductionProgress();

    /**
     * Get production progress percent. If it returns -1, it means that there are not any active production.
     * 
     * @return The percent of progress.
     */
    int getProductionProgressPercent();

    /**
     * Get name of current producing element.
     * 
     * @return The id of current producing element.
     */
    T getProducingElement();

    /**
     * Get production iterator.
     * 
     * @return The list of production.
     */
    Iterator<P> getProductionIterator();

    /**
     * Get size of production queue.
     * 
     * @return The number of remaining products.
     */
    int getQueueLength();

    /**
     * Return true if currently producing.
     * 
     * @return <code>true</code> if producing, <code>false</code> else.
     */
    boolean isProducing();
}
