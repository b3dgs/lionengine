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

import com.b3dgs.lionengine.game.object.ObjectGame;

/**
 * List of event linked to the production.
 */
public interface ProducerListener
{
    /**
     * Notify listener that current element can not be produced.
     * 
     * @param producible The element that would have been under production.
     */
    void notifyCanNotProduce(Producible producible);

    /**
     * Notify listener that production is starting for this element.
     * 
     * @param producible The element going to be produced
     * @param object The object instance from element.
     */
    void notifyStartProduction(Producible producible, ObjectGame object);

    /**
     * Notify listener that this element is currently under production.
     * 
     * @param producible The element under production.
     * @param object The object instance from element.
     */
    void notifyProducing(Producible producible, ObjectGame object);

    /**
     * Notify listener that this element has been produced.
     * 
     * @param producible The element produced.
     * @param object The object instance from element.
     */
    void notifyProduced(Producible producible, ObjectGame object);
}
