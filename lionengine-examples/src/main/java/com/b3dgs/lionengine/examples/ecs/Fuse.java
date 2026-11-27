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
 * A game-defined trait: the fuse of a bomb, data only (REQ-ECS-003, ADR-0011).
 *
 * <p>
 * Its logic lives in the code that burns it ({@link TraitExample#burn(com.b3dgs.lionengine.ecs.World,
 * com.b3dgs.lionengine.ecs.TraitStorage, long[])}). Its instances are pooled by the world: {@link TraitExample#FUSE}
 * creates them and resets them on removal.
 * </p>
 *
 * @since 11.0.0
 */
public final class Fuse
{
    /** Steps before the explosion, {@code 0} when reset. */
    private int stepsLeft;

    /**
     * Creates a fuse in its reset state.
     */
    public Fuse()
    {
        super();
    }

    /**
     * Returns the steps before the explosion.
     *
     * @return The steps left.
     */
    public int getStepsLeft()
    {
        return stepsLeft;
    }

    /**
     * Sets the steps before the explosion.
     *
     * @param stepsLeft The steps left.
     */
    public void setStepsLeft(int stepsLeft)
    {
        this.stepsLeft = stepsLeft;
    }
}
