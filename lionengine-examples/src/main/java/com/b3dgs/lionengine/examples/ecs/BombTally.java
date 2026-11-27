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

import com.b3dgs.lionengine.ecs.ObjectListener;
import com.b3dgs.lionengine.ecs.TraitListener;

/**
 * A game-defined listener: counts the fuses lit and the bombs that exploded, from the events of the flush
 * (REQ-ECS-007, ADR-0077).
 *
 * <p>
 * One trait listener receives the events of every trait type: it compares the type ID with the one of the fuse
 * storage. It only counts; a listener must not change the structure of the world.
 * </p>
 *
 * @since 11.0.0
 */
public final class BombTally implements ObjectListener, TraitListener
{
    /** The type ID of the fuse. */
    private final int fuseTypeId;
    /** Fuses lit. */
    private int lit;
    /** Bombs exploded. */
    private int exploded;

    /**
     * Creates the tally.
     *
     * @param fuseTypeId The type ID of the fuse storage.
     */
    public BombTally(int fuseTypeId)
    {
        this.fuseTypeId = fuseTypeId;
    }

    @Override
    public void onTraitAdded(long objectId, int traitTypeId)
    {
        if (traitTypeId == fuseTypeId)
        {
            lit++;
        }
    }

    @Override
    public void onDestroyed(long objectId)
    {
        exploded++;
    }

    /**
     * Returns the fuses lit.
     *
     * @return The fuse additions applied by the flushes.
     */
    public int getLit()
    {
        return lit;
    }

    /**
     * Returns the bombs exploded.
     *
     * @return The destructions applied by the flushes.
     */
    public int getExploded()
    {
        return exploded;
    }
}
