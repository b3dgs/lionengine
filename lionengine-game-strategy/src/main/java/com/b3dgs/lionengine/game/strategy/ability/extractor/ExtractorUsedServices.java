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
package com.b3dgs.lionengine.game.strategy.ability.extractor;

/**
 * List of services needed by the extractor.
 * 
 * @param <R> The resource enum type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface ExtractorUsedServices<R extends Enum<R>>
        extends ExtractorListener<R>
{
    /**
     * Check if extractor can extract (called while going to resources location).
     * <p>
     * For example:
     * <ul>
     * <li>The owner is close enough to the resource location</li>
     * </ul>
     * </p>
     * 
     * @return The extraction check condition result.
     */
    boolean canExtract();

    /**
     * Check if extractor can bring back its extraction (called while bring back resources).
     * 
     * @return The carry condition result.
     */
    boolean canCarry();

    /**
     * Get the extraction capacity in unit (the maximum number of unit extractible per extraction).
     * 
     * @return The extraction capacity.
     */
    int getExtractionCapacity();

    /**
     * Get the extraction speed.
     * 
     * @return The extraction speed.
     */
    int getExtractionSpeed();

    /**
     * Get the drop off speed.
     * 
     * @return The drop off speed.
     */
    int getDropOffSpeed();
}
