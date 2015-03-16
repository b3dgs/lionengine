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

/**
 * Allows to check if production can be performed.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface ProducerChecker
{
    /**
     * Condition to start production.
     * <p>
     * Example:
     * </p>
     * <ul>
     * <li>Did the player have enough resources ?</li>
     * <li>Is the owner still alive ?</li>
     * </ul>
     * 
     * @param producible The producible to check.
     * @return <code>true</code> if able to produce, <code>false</code> else.
     */
    boolean checkProduction(Producible producible);
}
