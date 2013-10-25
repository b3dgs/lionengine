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
package com.b3dgs.lionengine.game.rts.ability.extractor;

import com.b3dgs.lionengine.game.Tiled;

/**
 * Extractor events listener.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <R> The resource enum type used.
 */
public interface ExtractorListener<R extends Enum<R>>
{
    /**
     * Notify listener when extractor will start to move to resources (called once).
     * 
     * @param type The resource type.
     * @param resourceLocation The resource location.
     */
    void notifyStartGoToRessources(R type, Tiled resourceLocation);

    /**
     * Notify listener when extractor is going to start extraction (called once).
     * 
     * @param type The resource type.
     * @param resourceLocation The resource location.
     */
    void notifyStartExtraction(R type, Tiled resourceLocation);

    /**
     * Notify listener when extractor extracted one unit (called once when one quantity is extracted).
     * 
     * @param type The resource type.
     * @param currentQuantity The current quantity of extracted resources.
     */
    void notifyExtracted(R type, int currentQuantity);

    /**
     * Notify listener when extractor is going to start carry (called once).
     * 
     * @param type The resource type.
     * @param totalQuantity The total resource quantity to carry.
     */
    void notifyStartCarry(R type, int totalQuantity);

    /**
     * Notify listener when extractor carried and will start to drop off resources (called once).
     * 
     * @param type The resource type.
     * @param totalQuantity The total resource quantity to drop.
     */
    void notifyStartDropOff(R type, int totalQuantity);

    /**
     * Notify listener when extractor has dropped resources to warehouse.
     * 
     * @param type The resource type.
     * @param droppedQuantity The total resource quantity dropped off.
     */
    void notifyDroppedOff(R type, int droppedQuantity);
}
