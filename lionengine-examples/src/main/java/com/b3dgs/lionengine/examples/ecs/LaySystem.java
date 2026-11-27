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

import com.b3dgs.lionengine.ecs.GameSystem;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * A game-defined system: lays a bomb with a lit {@link Fuse} every few steps, until a number of bombs is laid
 * (REQ-ECS-008, ADR-0077).
 *
 * <p>
 * The bomb it creates is pending until the flush that ends the step: the systems of later slots do not see it in this
 * step, and see it from the next one.
 * </p>
 *
 * @since 11.0.0
 */
public final class LaySystem implements GameSystem
{
    /** The world. */
    private final World world;
    /** The fuse storage of the world. */
    private final TraitStorage<Fuse> fuses;
    /** Steps between two bombs. */
    private final int period;
    /** Bombs to lay. */
    private final int bombs;
    /** Fuse of each bomb, in steps. */
    private final int fuseSteps;
    /** Steps run. */
    private int steps;
    /** Bombs laid. */
    private int laid;

    /**
     * Creates the system.
     *
     * @param world The world.
     * @param fuses The fuse storage of the world.
     * @param period Steps between two bombs, at least 1; the first bomb is laid at the first step.
     * @param bombs Bombs to lay.
     * @param fuseSteps Fuse of each bomb, in steps, at least 1.
     */
    public LaySystem(World world, TraitStorage<Fuse> fuses, int period, int bombs, int fuseSteps)
    {
        this.world = world;
        this.fuses = fuses;
        this.period = period;
        this.bombs = bombs;
        this.fuseSteps = fuseSteps;
    }

    @Override
    public void update(double stepSeconds)
    {
        if (laid < bombs && steps % period == 0)
        {
            fuses.add(world.create()).setStepsLeft(fuseSteps);
            laid++;
        }
        steps++;
    }

    /**
     * Returns the bombs laid.
     *
     * @return The bombs created so far, alive or pending.
     */
    public int getLaid()
    {
        return laid;
    }
}
