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
package com.b3dgs.lionengine.game.map;

import java.util.Collection;

/**
 * Collision tile representation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionTile
{
    /** Collision name. */
    private final String name;
    /** Input representation. */
    private final CollisionRange input;
    /** Output representation. */
    private final CollisionRange output;
    /** Formula used. */
    private final CollisionFormula formula;
    /** Constraints list. */
    private final Collection<String> constraints;

    /**
     * Constructor.
     * 
     * @param name The collision name.
     * @param input The input reference.
     * @param output The output reference.
     * @param formula The formula used.
     * @param constraints The constraints used.
     */
    public CollisionTile(String name, CollisionRange input, CollisionRange output, CollisionFormula formula,
            Collection<String> constraints)
    {
        this.name = name;
        this.input = input;
        this.output = output;
        this.formula = formula;
        this.constraints = constraints;
    }

    /**
     * Get the collision name.
     * 
     * @return The collision name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the input used.
     * 
     * @return The input used.
     */
    public CollisionRange getInput()
    {
        return input;
    }

    /**
     * Get the input used.
     * 
     * @return The input used.
     */
    public CollisionRange getOutput()
    {
        return output;
    }

    /**
     * Get the formula used.
     * 
     * @return The formula used.
     */
    public CollisionFormula getFormula()
    {
        return formula;
    }

    /**
     * Get the constraints list.
     * 
     * @return The constraints list.
     */
    public Collection<String> getConstraints()
    {
        return constraints;
    }
}
