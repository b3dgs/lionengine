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
package com.b3dgs.lionengine.example.d_rts.d_ability;

import com.b3dgs.lionengine.game.rts.ability.producer.ProductionCostRts;

/**
 * The production cost representation.
 */
public final class ProductionCost
        extends ProductionCostRts
{
    /** The gold cost. */
    private final int gold;
    /** The wood cost. */
    private final int wood;

    /**
     * Constructor.
     * 
     * @param steps The steps number.
     * @param gold The gold number.
     * @param wood The wood number.
     */
    ProductionCost(int steps, int gold, int wood)
    {
        super(steps);
        this.gold = gold;
        this.wood = wood;
    }

    /**
     * Get required gold for this production.
     * 
     * @return The production gold cost.
     */
    public int getGold()
    {
        return gold;
    }

    /**
     * Get required wood for this production.
     * 
     * @return The production wood cost.
     */
    public int getWood()
    {
        return wood;
    }
}
