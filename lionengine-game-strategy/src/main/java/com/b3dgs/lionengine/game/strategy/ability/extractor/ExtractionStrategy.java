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

import com.b3dgs.lionengine.game.Alterable;

/**
 * Extraction structure. It represents the extraction quantity and the time needed per extraction unit.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ExtractionStrategy
{
    /** Current extracted quantity data. */
    public final Alterable quantity;
    /** Resource unit extracted in one second. */
    private int extractionPerSecond;
    /** Resource unit dropped off in one second. */
    private int dropOffPerSecond;

    /**
     * Constructor.
     */
    public ExtractionStrategy()
    {
        quantity = new Alterable(0);
    }

    /**
     * Setup extraction.
     * 
     * @param quantityMax The maximum extraction quantity.
     * @param extractionPerSecond The time needed to extract one unit of quantity.
     * @param dropOffPerSecond The time needed to extract one unit.
     */
    public void initExtraction(int quantityMax, int extractionPerSecond, int dropOffPerSecond)
    {
        this.extractionPerSecond = extractionPerSecond;
        this.dropOffPerSecond = dropOffPerSecond;
        quantity.setMax(quantityMax);
    }

    /**
     * Get the amount of extracted resource unit per second.
     * 
     * @return The extracted resource unit per second.
     */
    public int getExtractionPerSecond()
    {
        return extractionPerSecond;
    }

    /**
     * Get the number of resource unit dropped off per second.
     * 
     * @return The number of resource unit dropped off per second.
     */
    public int getDropOffPerSecond()
    {
        return dropOffPerSecond;
    }
}
