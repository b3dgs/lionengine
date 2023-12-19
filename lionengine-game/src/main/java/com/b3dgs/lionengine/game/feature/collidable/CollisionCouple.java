/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents a collision couple.
 * 
 * @param with The collision with.
 * @param by The collision by.
 */
public record CollisionCouple(Collision with, Collision by)
{
    /**
     * Create couple.
     * 
     * @param with The collision with (must not be <code>null</code>).
     * @param by The collision by (must not be <code>null</code>).
     * @throws LionEngineException If <code>null</code> arguments.
     */
    public CollisionCouple
    {
        Check.notNull(with);
        Check.notNull(by);
    }

    /**
     * Get the collision with.
     * 
     * @return the collision with.
     */
    public Collision getWith()
    {
        return with;
    }

    /**
     * Get the collision by.
     * 
     * @return the collision by.
     */
    public Collision getBy()
    {
        return by;
    }
}
