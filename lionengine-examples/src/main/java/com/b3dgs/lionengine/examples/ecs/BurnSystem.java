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
import com.b3dgs.lionengine.ecs.Query;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * A game-defined system: burns the {@link Fuse} of every bomb by one step and removes the bombs whose fuse reaches zero
 * (REQ-ECS-006, REQ-ECS-008, ADR-0077).
 *
 * <p>
 * It creates its query once, in its constructor, and iterates it with an index loop in {@link #update(double)}; the
 * removals it records apply at the flush that ends the step, so the loop stays valid.
 * </p>
 *
 * @since 11.0.0
 */
public final class BurnSystem implements GameSystem
{
    /** The world. */
    private final World world;
    /** The fuse storage of the world. */
    private final TraitStorage<Fuse> fuses;
    /** The bombs with a fuse. */
    private final Query burning;

    /**
     * Creates the system and its query.
     *
     * @param world The world.
     * @param fuses The fuse storage of the world.
     */
    public BurnSystem(World world, TraitStorage<Fuse> fuses)
    {
        this.world = world;
        this.fuses = fuses;
        burning = world.createQuery(fuses);
    }

    @Override
    public void update(double stepSeconds)
    {
        for (int i = 0; i < burning.getCount(); i++)
        {
            final long bomb = burning.getId(i);
            final Fuse fuse = fuses.get(bomb);
            fuse.setStepsLeft(fuse.getStepsLeft() - 1);
            if (fuse.getStepsLeft() == 0)
            {
                world.remove(bomb);
            }
        }
    }

    /**
     * Returns the bombs burning, as of the last flush.
     *
     * @return The number of bombs with a fuse.
     */
    public int getBurning()
    {
        return burning.getCount();
    }
}
