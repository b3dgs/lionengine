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
package com.b3dgs.lionengine.game.pathfinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A path determined by some path finding algorithm. A series of steps from the starting location to the target
 * location. This includes a step for the initial location.
 */
public final class Path
{
    /** List of steps. */
    private final List<Step> steps;

    /**
     * Constructor.
     */
    public Path()
    {
        steps = new ArrayList<Step>();
    }

    /**
     * Append a step to the path.
     * 
     * @param x The x coordinate of the new step.
     * @param y The y coordinate of the new step.
     */
    public void appendStep(int x, int y)
    {
        steps.add(new Step(x, y));
    }

    /**
     * Prepend a step to the path.
     * 
     * @param x The x coordinate of the new step.
     * @param y The y coordinate of the new step.
     */
    public void prependStep(int x, int y)
    {
        steps.add(0, new Step(x, y));
    }

    /**
     * Get the length of the path, i.e. the number of steps.
     * 
     * @return The number of steps in this path.
     */
    public int getLength()
    {
        return steps.size();
    }

    /**
     * Get the x coordinate for the step at the given index.
     * 
     * @param index The index of the step whose x coordinate should be retrieved.
     * @return The x coordinate at the step.
     */
    public int getX(int index)
    {
        return getStep(index).getX();
    }

    /**
     * Get the y coordinate for the step at the given index.
     * 
     * @param index The index of the step whose y coordinate should be retrieved.
     * @return The y coordinate at the step.
     */
    public int getY(int index)
    {
        return getStep(index).getY();
    }

    /**
     * Check if this path contains the given step.
     * 
     * @param x The x coordinate of the step to check for.
     * @param y The y coordinate of the step to check for.
     * @return <code>true</code> if the path contains the given step, <code>false</code> else.
     */
    public boolean contains(int x, int y)
    {
        return steps.contains(new Step(x, y));
    }

    /**
     * Get the step at a given index in the path.
     * 
     * @param index The index of the step to retrieve. Note this should be &lt;= 0 and &gt; getLength();
     * @return The step information, the position on the map.
     */
    private Step getStep(int index)
    {
        return steps.get(index);
    }
}
