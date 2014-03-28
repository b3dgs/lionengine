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
package com.b3dgs.lionengine.game.platform;

/**
 * Describe the collision function used.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionFunction
{
    /** The name. */
    private String name;
    /** The input used. */
    private CollisionInput input;
    /** Value. */
    private double value;
    /** Offset value. */
    private int offset;

    /**
     * Constructor.
     */
    public CollisionFunction()
    {
        input = CollisionInput.X;
        value = 0;
        offset = 0;
    }

    /**
     * Set the name.
     * 
     * @param name The name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Set the input type used.
     * 
     * @param input The input to set
     */
    public void setInput(CollisionInput input)
    {
        this.input = input;
    }

    /**
     * Set the value.
     * 
     * @param value The value to set
     */
    public void setValue(double value)
    {
        this.value = value;
    }

    /**
     * Set the offset value.
     * 
     * @param offset The offset to set
     */
    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    /**
     * Get the name.
     * 
     * @return The name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the input.
     * 
     * @return the input
     */
    public CollisionInput getInput()
    {
        return input;
    }

    /**
     * Get the value.
     * 
     * @return the value
     */
    public double getValue()
    {
        return value;
    }

    /**
     * Get the offset.
     * 
     * @return the offset
     */
    public int getOffset()
    {
        return offset;
    }
}
