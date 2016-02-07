/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

/**
 * Represent a resource, such as gold, wood... It is possible to increase the resource amount, or spend it. It is also
 * possible to check if there are enough resource before spending it.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Resource gold = new Resource();
 * gold.add(100);
 * gold.get(); // returns 100
 * gold.canSpend(25); // returns true
 * gold.spend(25); // returns 75
 * gold.canSpend(100); // returns false
 * </pre>
 */
public class Resource
{
    /** Resource value. */
    private final Alterable ressource;

    /**
     * Create a resource without maximum value.
     */
    public Resource()
    {
        ressource = new Alterable(Integer.MAX_VALUE);
    }

    /**
     * Create a resource.
     * 
     * @param amount The starting amount.
     */
    public Resource(int amount)
    {
        ressource = new Alterable(amount);
    }

    /**
     * Increase resource stock with a specified amount.
     * 
     * @param amount The amount of new resource.
     * @return The amount added.
     */
    public int add(int amount)
    {
        return ressource.increase(amount);
    }

    /**
     * Decrease resource stock with a specified amount. Caution, it is possible to be negative. Ensure to call first
     * {@link Resource#canSpend(int)} if you expect only positive values.
     * 
     * @param amount The amount of resource to spend.
     */
    public void spend(int amount)
    {
        ressource.decrease(amount);
    }

    /**
     * Check if the specified amount of resource can be spent.
     * 
     * @param amount The amount to check.
     * @return <code>true</code> if current stock - amount &gt; 0, <code>false</code> else.
     */
    public boolean canSpend(int amount)
    {
        return ressource.isEnough(amount);
    }

    /**
     * Get current amount of resource.
     * 
     * @return The current stock.
     */
    public int get()
    {
        return ressource.getCurrent();
    }
}
