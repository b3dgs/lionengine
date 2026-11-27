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
 * A game-defined trait: a bomb frozen for some steps, whose fuse does not burn meanwhile; data only (REQ-ECS-003,
 * ADR-0011).
 *
 * <p>
 * Its logic lives in {@link QueryExample}, which thaws it and excludes frozen bombs from burning. Its instances are
 * pooled by the world: {@link QueryExample#FROZEN} creates them and resets them on removal.
 * </p>
 *
 * @since 11.0.0
 */
public final class Frozen
{
    /** Steps before thawing, {@code 0} when reset. */
    private int stepsLeft;

    /**
     * Creates a frozen state in its reset state.
     */
    public Frozen()
    {
        super();
    }

    /**
     * Returns the steps before thawing.
     *
     * @return The steps left.
     */
    public int getStepsLeft()
    {
        return stepsLeft;
    }

    /**
     * Sets the steps before thawing.
     *
     * @param stepsLeft The steps left.
     */
    public void setStepsLeft(int stepsLeft)
    {
        this.stepsLeft = stepsLeft;
    }
}
