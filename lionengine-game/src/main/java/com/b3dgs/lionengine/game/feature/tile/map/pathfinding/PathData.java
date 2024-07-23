/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Nameable;

/**
 * Represents the data associated to a path.
 * 
 * @param category The category name.
 * @param cost The cost value.
 * @param blocking The blocking flag.
 * @param movements The allowed movements.
 */
public record PathData(String category, double cost, boolean blocking, Collection<MovementTile> movements)
                      implements Nameable<PathData>
{
    /**
     * Create a path data.
     * 
     * @param category The category name.
     * @param cost The cost value.
     * @param blocking The blocking flag.
     * @param movements The allowed movements.
     * @throws LionEngineException If invalid arguments.
     */
    public PathData
    {
        Check.notNull(category);
        Check.notNull(movements);

        if (movements.isEmpty())
        {
            movements = EnumSet.noneOf(MovementTile.class);
        }
        else
        {
            movements = EnumSet.copyOf(movements);
        }
    }

    /**
     * Get the cost value.
     * 
     * @return The cost value.
     */
    public double getCost()
    {
        return cost;
    }

    /**
     * Get the blocking state.
     * 
     * @return <code>true</code> if blocking, <code>false</code> else.
     */
    public boolean isBlocking()
    {
        return blocking;
    }

    /**
     * Check if the movement is allowed.
     * 
     * @param movement The movement to check.
     * @return <code>true</code> if allowed, <code>false</code> else.
     */
    public boolean isAllowedMovement(MovementTile movement)
    {
        return movements.contains(movement);
    }

    /**
     * Get the allowed movements as read only.
     * 
     * @return The allowed movements.
     */
    public Collection<MovementTile> getAllowedMovements()
    {
        return Collections.unmodifiableCollection(movements);
    }

    @Override
    public String getName()
    {
        return category;
    }
}
