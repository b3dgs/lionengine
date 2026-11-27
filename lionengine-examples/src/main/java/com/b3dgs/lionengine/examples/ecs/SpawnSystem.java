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

import com.b3dgs.lionengine.RandomSource;
import com.b3dgs.lionengine.ecs.GameSystem;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * A game-defined system: spawns one object per step at a random {@link Spot} of the arena, drawn from the world's
 * random source, until a number of objects is spawned (REQ-DET-001, REQ-DET-003, ADR-0078).
 *
 * <p>
 * The generator is the world's, kept from the construction: {@link World#getRandomSource()} returns the same instance
 * for the life of the world, also after {@link World#reseed(long)}. Never {@code Math.random} or
 * {@code java.util.Random}: the same seed and the same steps give the same spawns on every platform.
 * </p>
 *
 * @since 11.0.0
 */
public final class SpawnSystem implements GameSystem
{
    /** The world. */
    private final World world;
    /** The spot storage of the world. */
    private final TraitStorage<Spot> spots;
    /** The random source of the world. */
    private final RandomSource random;
    /** Objects to spawn. */
    private final int count;
    /** Width of the arena, in meters. */
    private final double width;
    /** Height of the arena, in meters. */
    private final double height;
    /** Objects spawned. */
    private int spawned;

    /**
     * Creates the system.
     *
     * @param world The world.
     * @param spots The spot storage of the world.
     * @param count Objects to spawn, one per step.
     * @param width Width of the arena, in meters: x is drawn in [0, width).
     * @param height Height of the arena, in meters: y is drawn in [0, height).
     */
    public SpawnSystem(World world, TraitStorage<Spot> spots, int count, double width, double height)
    {
        this.world = world;
        this.spots = spots;
        random = world.getRandomSource();
        this.count = count;
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(double stepSeconds)
    {
        if (spawned < count)
        {
            final double x = random.nextDouble() * width;
            final double y = random.nextDouble() * height;
            spots.add(world.create()).set(x, y);
            spawned++;
        }
    }

    /**
     * Returns the objects spawned.
     *
     * @return The number of objects created so far.
     */
    public int getSpawned()
    {
        return spawned;
    }

    /**
     * Starts a new round: the next steps spawn the configured number of objects again. Called between two updates.
     */
    public void restart()
    {
        spawned = 0;
    }
}
