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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import com.b3dgs.lionengine.game.Tiled;

/**
 * Extractor events listener.
 */
public interface ExtractorListener
{
    /**
     * Notify listener when extractor will start to move to resources (called once).
     * 
     * @param type The resource type.
     * @param resourceLocation The resource location.
     */
    void notifyStartGoToRessources(String type, Tiled resourceLocation);

    /**
     * Notify listener when extractor is going to start extraction (called once).
     * 
     * @param type The resource type.
     * @param resourceLocation The resource location.
     */
    void notifyStartExtraction(String type, Tiled resourceLocation);

    /**
     * Notify listener when extractor extracted one unit (called once when one quantity is extracted).
     * 
     * @param type The resource type.
     * @param currentQuantity The current quantity of extracted resources.
     */
    void notifyExtracted(String type, int currentQuantity);

    /**
     * Notify listener when extractor is going to start carry (called once).
     * 
     * @param type The resource type.
     * @param totalQuantity The total resource quantity to carry.
     */
    void notifyStartCarry(String type, int totalQuantity);

    /**
     * Notify listener when extractor carried and will start to drop off resources (called once).
     * 
     * @param type The resource type.
     * @param totalQuantity The total resource quantity to drop.
     */
    void notifyStartDropOff(String type, int totalQuantity);

    /**
     * Notify listener when extractor has dropped resources to warehouse.
     * 
     * @param type The resource type.
     * @param droppedQuantity The total resource quantity dropped off.
     */
    void notifyDroppedOff(String type, int droppedQuantity);

    /**
     * Notify listener when extractor stopped extraction.
     */
    void notifyStopped();
}
