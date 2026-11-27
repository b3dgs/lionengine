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

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.TraitType;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.ecs.WorldConfig;

/**
 * Adds, replaces and removes a game-defined trait at runtime: bombs with a {@link Fuse} that burns down and explodes
 * (REQ-ECS-003, REQ-ECS-004, REQ-ECS-006, REQ-ECS-012, ADR-0011, ADR-0030, ADR-0075).
 *
 * <p>
 * The game describes its trait type once with a {@link TraitType} ({@link #FUSE}: how a fuse is created and reset) and
 * registers it in its world with {@link World#register(TraitType)}, which returns the {@link TraitStorage} used for
 * every access. {@link TraitStorage#add(long)} returns the fuse at once to be filled, but attaches it at the flush,
 * like {@link TraitStorage#remove(long)}: within a step, {@link TraitStorage#has(long)} and
 * {@link TraitStorage#get(long)} still read the state of the last flush. Adding a fuse to a bomb that has one replaces
 * it (relighting); removing it (defusing) leaves the bomb alive without a fuse; removing the bomb removes its fuse.
 * Removed fuses are reset and reused by the world, so a fuse must not be kept after its removal. Headless;
 * {@link #run(Log)} logs the steps.
 * </p>
 *
 * @since 11.0.0
 */
public final class TraitExample
{
    /** Bombs laid. */
    public static final int BOMBS = 4;
    /** Fuse of the first bomb, in steps; each next bomb has one more step. */
    public static final int FUSE_STEPS = 3;
    /** The fuse trait type: a new fuse has no step left, a removed fuse is reset to none. */
    public static final TraitType<Fuse> FUSE = new TraitType<>()
    {
        @Override
        public Fuse create()
        {
            return new Fuse();
        }

        @Override
        public void reset(Fuse trait)
        {
            trait.setStepsLeft(0);
        }
    };

    /** Steps played by {@link #run(Log)}. */
    private static final int STEPS = 8;

    /**
     * Private constructor.
     */
    private TraitExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates the world of the example: room for every bomb and every fuse, so steps never grow it.
     *
     * @return The world.
     */
    public static World createWorld()
    {
        return new World(new WorldConfig(BOMBS, BOMBS));
    }

    /**
     * Lays the bombs: creates them, each with a fuse of {@link #FUSE_STEPS} steps plus its rank, then flushes.
     *
     * @param world The world.
     * @param fuses The fuse storage of the world.
     * @return The bombs, alive with their fuse.
     */
    public static long[] lay(World world, TraitStorage<Fuse> fuses)
    {
        final long[] bombs = new long[BOMBS];
        for (int i = 0; i < BOMBS; i++)
        {
            bombs[i] = world.create();
            fuses.add(bombs[i]).setStepsLeft(FUSE_STEPS + i);
        }
        world.flush();
        return bombs;
    }

    /**
     * Plays one step: burns each fuse by one step; a bomb whose fuse reaches zero explodes and is removed with its
     * fuse. Then flushes.
     *
     * @param world The world.
     * @param fuses The fuse storage of the world.
     * @param bombs The bombs, alive or exploded.
     * @return The bombs that exploded in this step.
     */
    public static int burn(World world, TraitStorage<Fuse> fuses, long[] bombs)
    {
        int exploded = 0;
        for (final long bomb : bombs)
        {
            if (world.isAlive(bomb) && fuses.has(bomb))
            {
                final Fuse fuse = fuses.get(bomb);
                fuse.setStepsLeft(fuse.getStepsLeft() - 1);
                if (fuse.getStepsLeft() == 0)
                {
                    world.remove(bomb);
                    exploded++;
                }
            }
        }
        world.flush();
        return exploded;
    }

    /**
     * Runs the example: defuses the first bomb and relights the last one with a one-step fuse before the first step,
     * then logs the explosions of eight steps and what is left.
     *
     * @param log The log, at level {@link LogLevel#INFO} or finer to see the steps.
     * @return The bombs that exploded.
     */
    public static int run(Log log)
    {
        final World world = createWorld();
        final TraitStorage<Fuse> fuses = world.register(FUSE);
        final long[] bombs = lay(world, fuses);

        fuses.remove(bombs[0]);
        fuses.add(bombs[BOMBS - 1]).setStepsLeft(1);
        log.log(LogLevel.INFO, "Fuse of the last bomb until the flush:", fuses.get(bombs[BOMBS - 1]).getStepsLeft());

        int exploded = 0;
        for (int step = 0; step < STEPS; step++)
        {
            final int now = burn(world, fuses, bombs);
            log.log(LogLevel.INFO, "Bombs exploded in this step:", now);
            exploded += now;
        }
        log.log(LogLevel.INFO, "Defused bomb alive (1 = yes):", world.isAlive(bombs[0]) ? 1 : 0);
        log.log(LogLevel.INFO, "Fuses left:", fuses.getCount());
        return exploded;
    }
}
