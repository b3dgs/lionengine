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
package com.b3dgs.lionengine.game.strategy.ability.producer;

import com.b3dgs.lionengine.game.Tiled;

/**
 * Represents a producible entity.
 * 
 * @param <T> The entity enum type used.
 * @param <C> The cost type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Producible<T extends Enum<T>, C extends ProductionCostStrategy>
        implements Tiled
{
    /** Entity id. */
    private final T id;
    /** Production cost. */
    private final C cost;
    /** Production width. */
    private final int tw;
    /** Production height. */
    private final int th;
    /** Production location x. */
    private int tx;
    /** Production location y. */
    private int ty;

    /**
     * Constructor.
     * 
     * @param id The entity id.
     * @param cost The production cost.
     * @param tw The production width.
     * @param th The production height.
     */
    public Producible(T id, C cost, int tw, int th)
    {
        this.id = id;
        this.cost = cost;
        this.tw = tw;
        this.th = th;
    }

    /**
     * Set the production location.
     * 
     * @param tx The horizontal location.
     * @param ty The vertical location.
     */
    public void setLocation(int tx, int ty)
    {
        this.tx = tx;
        this.ty = ty;
    }

    /**
     * Get the id.
     * 
     * @return The id.
     */
    public T getId()
    {
        return id;
    }

    /**
     * Get the cost.
     * 
     * @return The cost.
     */
    public C getCost()
    {
        return cost;
    }

    /*
     * Tiled
     */

    @Override
    public int getLocationInTileX()
    {
        return tx;
    }

    @Override
    public int getLocationInTileY()
    {
        return ty;
    }

    @Override
    public int getWidthInTile()
    {
        return tw;
    }

    @Override
    public int getHeightInTile()
    {
        return th;
    }
}
