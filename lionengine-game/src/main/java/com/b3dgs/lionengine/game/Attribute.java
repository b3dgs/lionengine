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
package com.b3dgs.lionengine.game;

/**
 * Can describe an attribute (vitality, agility...).
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Attribute vitality = new Attribute();
 * vitality.set(1);
 * vitality.increase(2);
 * System.out.println(vitality.get()); // print 3
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Attribute
{
    /** Current attribute value. */
    private int value;

    /**
     * Constructor. Initial value is set to 0 by default.
     */
    public Attribute()
    {
        value = 0;
    }

    /**
     * Increase attribute with the specified step.
     * 
     * @param step The increase value.
     */
    public void increase(int step)
    {
        value += step;
    }

    /**
     * Set attribute value.
     * 
     * @param value The value.
     */
    public void set(int value)
    {
        this.value = value;
    }

    /**
     * Get attribute value.
     * 
     * @return The attribute value.
     */
    public int get()
    {
        return value;
    }
}
