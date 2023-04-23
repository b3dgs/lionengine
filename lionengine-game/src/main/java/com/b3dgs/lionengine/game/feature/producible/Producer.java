/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.producible;

import java.util.Iterator;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * Represents an ability of creating new object.
 */
@FeatureInterface
public interface Producer extends Feature, Updatable, Listenable<ProducerListener>, Iterable<Featurable>
{
    /**
     * Set the production checker.
     * 
     * @param checker The production checker reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void setChecker(ProducerChecker checker);

    /**
     * Add an element to the production queue. It works as a FIFO (First In, First Out). Production will be stopped when
     * the list is empty. In this case, getProductionProgress() will return -1. Production list stores only entity name.
     * 
     * @param featurable The element to produce (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void addToProductionQueue(Featurable featurable);

    /**
     * Skip current production.
     */
    void skipProduction();

    /**
     * Stop any production.
     */
    void stopProduction();

    /**
     * Set the production steps number per tick.
     * 
     * @param stepsPerTick The production steps number per tick (must be strictly positive).
     * @throws LionEngineException If invalid argument.
     */
    void setStepsSpeed(double stepsPerTick);

    /**
     * Get production progress. If it returns -1, it means that there are not any active production.
     * 
     * @return The progress value.
     */
    double getProgress();

    /**
     * Get production progress percent. If it returns -1, it means that there are not any active production.
     * 
     * @return The percent of progress.
     */
    int getProgressPercent();

    /**
     * Get current producing element.
     * 
     * @return The current producing element, <code>null</code> if none.
     */
    Featurable getProducingElement();

    /**
     * Get production iterator.
     * 
     * @return The list of production.
     */
    @Override
    Iterator<Featurable> iterator();

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
