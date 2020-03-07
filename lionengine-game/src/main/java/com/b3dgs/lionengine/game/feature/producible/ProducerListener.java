/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.game.feature.Featurable;

/**
 * List of event linked to the production.
 */
public interface ProducerListener
{
    /**
     * Notify listener that current element can not be produced.
     * 
     * @param featurable The element that would have been under production.
     */
    void notifyCanNotProduce(Featurable featurable);

    /**
     * Notify listener that production is starting for this element.
     * 
     * @param featurable The featurable instance from element.
     */
    void notifyStartProduction(Featurable featurable);

    /**
     * Notify listener that this element is currently under production.
     * 
     * @param featurable The featurable instance from element.
     */
    void notifyProducing(Featurable featurable);

    /**
     * Notify listener that this element has been produced.
     * 
     * @param featurable The featurable instance from element.
     */
    void notifyProduced(Featurable featurable);
}
