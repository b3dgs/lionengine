/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.trait.producible;

import java.util.Iterator;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.game.trait.Trait;

/**
 * Represents an ability of creating new object.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Producer extends Trait, Updatable
{
    /**
     * Add a producer listener.
     * 
     * @param listener The producer listener to add.
     */
    void addListener(ProducerListener listener);

    /**
     * Add an element to the production queue. It works as a FIFO (First In, First Out). Production will be stopped when
     * the list is empty. In this case, getProductionProgress() will return -1. Production list stores only entity name.
     * 
     * @param producible The element to produce.
     */
    void addToProductionQueue(Producible producible);

    /**
     * Skip current production.
     */
    void skipProduction();

    /**
     * Stop any production.
     */
    void stopProduction();

    /**
     * Set the production steps number per second.
     * 
     * @param stepsPerSecond The production steps number per second.
     */
    void setStepsPerSecond(double stepsPerSecond);

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
     * Get media of current producing element.
     * 
     * @return The media of current producing element.
     */
    Media getProducingElement();

    /**
     * Get production iterator.
     * 
     * @return The list of production.
     */
    Iterator<Producible> iterator();

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
