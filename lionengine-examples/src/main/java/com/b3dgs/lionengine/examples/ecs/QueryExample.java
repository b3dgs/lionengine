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
import com.b3dgs.lionengine.ecs.Query;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.TraitType;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.ecs.WorldConfig;

/**
 * Processes the objects having given traits with queries: bombs whose {@link Fuse} burns unless they are
 * {@link Frozen} (REQ-ECS-005, REQ-ECS-006, REQ-ECS-012, REQ-ECS-013, ADR-0011, ADR-0030, ADR-0034, ADR-0076).
 *
 * <p>
 * The game creates its queries once, after registering its trait types: {@link World#createQuery(TraitStorage...)}
 * for the frozen bombs, {@link World#createQuery(TraitStorage[], TraitStorage[])} for the bombs with a fuse and not
 * frozen. Each step iterates them with an index loop over {@link Query#getCount()} and {@link Query#getId(int)},
 * without allocating. Removing a bomb or a trait inside the loop is recorded and applied at the flush, so the loop
 * stays valid; the flush then updates every query. The iteration order is not the creation order:
 * {@link Query#copySortedIds(long[])} gives the IDs in ascending order into a buffer of the game. Headless;
 * {@link #run(Log)} logs the steps.
 * </p>
 *
 * @since 11.0.0
 */
public final class QueryExample
{
    /** Bombs laid. */
    public static final int BOMBS = 6;
    /** Fuse of every bomb, in steps. */
    public static final int FUSE_STEPS = 2;
    /** Steps an odd-ranked bomb stays frozen. */
    public static final int FROZEN_STEPS = 3;
    /** The frozen trait type: a new state has no step left, a removed one is reset to none. */
    public static final TraitType<Frozen> FROZEN = new TraitType<>()
    {
        @Override
        public Frozen create()
        {
            return new Frozen();
        }

        @Override
        public void reset(Frozen trait)
        {
            trait.setStepsLeft(0);
        }
    };

    /** Steps played by {@link #run(Log)}. */
    private static final int STEPS = 6;

    /**
     * Private constructor.
     */
    private QueryExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates the world of the example: room for every bomb and every trait, so steps never grow it.
     *
     * @return The world.
     */
    public static World createWorld()
    {
        return new World(new WorldConfig(BOMBS, BOMBS));
    }

    /**
     * Lays the bombs: creates them, each with a fuse of {@link #FUSE_STEPS} steps, the odd-ranked ones also frozen for
     * {@link #FROZEN_STEPS} steps, then flushes.
     *
     * @param world The world.
     * @param fuses The fuse storage of the world.
     * @param frozen The frozen storage of the world.
     * @return The bombs, alive with their traits.
     */
    public static long[] lay(World world, TraitStorage<Fuse> fuses, TraitStorage<Frozen> frozen)
    {
        final long[] bombs = new long[BOMBS];
        for (int i = 0; i < BOMBS; i++)
        {
            bombs[i] = world.create();
            fuses.add(bombs[i]).setStepsLeft(FUSE_STEPS);
            if (i % 2 == 1)
            {
                frozen.add(bombs[i]).setStepsLeft(FROZEN_STEPS);
            }
        }
        world.flush();
        return bombs;
    }

    /**
     * Thaws the frozen bombs by one step: a bomb whose frozen state reaches zero loses it at the flush.
     *
     * @param frozen The frozen storage of the world.
     * @param thawing The query of the frozen bombs.
     */
    public static void thaw(TraitStorage<Frozen> frozen, Query thawing)
    {
        for (int i = 0; i < thawing.getCount(); i++)
        {
            final long bomb = thawing.getId(i);
            final Frozen state = frozen.get(bomb);
            state.setStepsLeft(state.getStepsLeft() - 1);
            if (state.getStepsLeft() == 0)
            {
                frozen.remove(bomb);
            }
        }
    }

    /**
     * Burns the fuses of the bombs that are not frozen by one step: a bomb whose fuse reaches zero explodes, removed
     * at the flush.
     *
     * @param world The world.
     * @param fuses The fuse storage of the world.
     * @param burning The query of the bombs with a fuse and not frozen.
     * @return The bombs that explode at the next flush.
     */
    public static int burn(World world, TraitStorage<Fuse> fuses, Query burning)
    {
        int exploded = 0;
        for (int i = 0; i < burning.getCount(); i++)
        {
            final long bomb = burning.getId(i);
            final Fuse fuse = fuses.get(bomb);
            fuse.setStepsLeft(fuse.getStepsLeft() - 1);
            if (fuse.getStepsLeft() == 0)
            {
                world.remove(bomb);
                exploded++;
            }
        }
        return exploded;
    }

    /**
     * Runs the example: each step thaws, burns and flushes, and logs the explosions and the bombs left in ascending ID
     * order.
     *
     * @param log The log, at level {@link LogLevel#INFO} or finer to see the steps.
     * @return The bombs that exploded.
     */
    public static int run(Log log)
    {
        final World world = createWorld();
        final TraitStorage<Fuse> fuses = world.register(TraitExample.FUSE);
        final TraitStorage<Frozen> frozen = world.register(FROZEN);
        final Query thawing = world.createQuery(frozen);
        final Query burning = world.createQuery(new TraitStorage<?>[] {fuses}, new TraitStorage<?>[] {frozen});
        lay(world, fuses, frozen);
        final long[] left = new long[BOMBS];

        int exploded = 0;
        for (int step = 0; step < STEPS; step++)
        {
            thaw(frozen, thawing);
            final int now = burn(world, fuses, burning);
            world.flush();
            log.log(LogLevel.INFO, "Bombs exploded in this step:", now);
            exploded += now;
            final int count = burning.copySortedIds(left);
            for (int i = 0; i < count; i++)
            {
                log.log(LogLevel.INFO, "Bomb burning:", left[i]);
            }
        }
        return exploded;
    }
}
