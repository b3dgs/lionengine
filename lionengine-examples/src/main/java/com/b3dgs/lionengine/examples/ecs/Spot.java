/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.examples.ecs;

/**
 * A game-defined trait: the place of an object in the arena, in meters, data only (REQ-ECS-003, ADR-0011).
 *
 * <p>
 * Its instances are pooled by the world: {@link RandomSpawnExample#SPOT} creates them and resets them on removal.
 * </p>
 *
 * @since 11.0.0
 */
public final class Spot
{
    /** Horizontal position, in meters, {@code 0} when reset. */
    private double x;
    /** Vertical position, in meters (Y up), {@code 0} when reset. */
    private double y;

    /**
     * Creates a spot in its reset state, at the origin.
     */
    public Spot()
    {
        super();
    }

    /**
     * Returns the horizontal position.
     *
     * @return The position, in meters.
     */
    public double getX()
    {
        return x;
    }

    /**
     * Returns the vertical position.
     *
     * @return The position, in meters (Y up).
     */
    public double getY()
    {
        return y;
    }

    /**
     * Sets the position.
     *
     * @param x The horizontal position, in meters.
     * @param y The vertical position, in meters (Y up).
     */
    public void set(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
}
