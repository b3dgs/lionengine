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
package com.b3dgs.lionengine.example.warcraft.entity;

import com.b3dgs.lionengine.game.rts.ability.producer.ProductionCostRts;

/**
 * Production cost implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ProductionCost
        extends ProductionCostRts
{
    /** The needed amount of gold. */
    private final int gold;
    /** The needed amount of wood. */
    private final int wood;

    /**
     * Create a new cost.
     * 
     * @param steps The production steps number.
     * @param gold The needed amount of gold.
     * @param wood The needed amount of wood.
     */
    ProductionCost(int steps, int gold, int wood)
    {
        super(steps);
        this.gold = gold;
        this.wood = wood;
    }

    /**
     * Get the cost in gold.
     * 
     * @return The cost in gold.
     */
    public int getGold()
    {
        return gold;
    }

    /**
     * Get the cost in wood.
     * 
     * @return The cost in wood.
     */
    public int getWood()
    {
        return wood;
    }
}
