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
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.ecs.WorldConfig;

/**
 * Runs game-defined systems in slot order and listens to object and trait changes (REQ-ECS-006, REQ-ECS-007,
 * REQ-ECS-008, REQ-ECS-012, REQ-ECS-013, ADR-0011, ADR-0030, ADR-0034, ADR-0077).
 *
 * <p>
 * The game registers its trait type ({@link Fuse}), its systems with an {@code int} slot each and its listener, once:
 * {@link LaySystem} at {@link #LAY_SLOT}, in the input and intent range below the standard {@code Jump} slot (1000),
 * lays bombs; {@link BurnSystem} at {@link #BURN_SLOT}, in the range of the reactions to collisions between
 * {@code Collision} (4000) and {@code Lifetime} (5000), burns their fuses through a query; {@link BombTally} counts the
 * fuses lit and the explosions. Each step is one {@link World#update(double)} with the constant step: the systems run
 * from the lowest slot to the highest, then the world flushes once, firing the events; the changes are visible from
 * the next step. Headless; {@link #run(Log)} logs the steps.
 * </p>
 *
 * @since 11.0.0
 */
public final class SystemExample
{
    /** Slot of {@link LaySystem}: input and intent range. */
    public static final int LAY_SLOT = 500;
    /** Slot of {@link BurnSystem}: reactions range. */
    public static final int BURN_SLOT = 4500;
    /** The constant step, in seconds. */
    public static final double STEP_SECONDS = 1.0 / 60.0;
    /** Bombs laid. */
    public static final int BOMBS = 3;
    /** Steps between two bombs. */
    public static final int LAY_PERIOD = 2;
    /** Fuse of every bomb, in steps. */
    public static final int FUSE_STEPS = 3;
    /** Steps played by {@link #run(Log)}: the last bomb is laid at step 4 and explodes at step 7. */
    public static final int STEPS = 8;

    /**
     * Private constructor.
     */
    private SystemExample()
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
     * Runs the example: registers the fuse, the systems and the tally, then plays {@link #STEPS} steps and logs the
     * bombs burning and the explosions after each one.
     *
     * @param log The log, at level {@link LogLevel#INFO} or finer to see the steps.
     * @return The tally, every bomb exploded.
     */
    public static BombTally run(Log log)
    {
        final World world = createWorld();
        final TraitStorage<Fuse> fuses = world.register(TraitExample.FUSE);
        final BurnSystem burn = new BurnSystem(world, fuses);
        world.addSystem(burn, BURN_SLOT);
        world.addSystem(new LaySystem(world, fuses, LAY_PERIOD, BOMBS, FUSE_STEPS), LAY_SLOT);
        final BombTally tally = new BombTally(fuses.getTypeId());
        world.addObjectListener(tally);
        world.addTraitListener(tally);

        for (int step = 0; step < STEPS; step++)
        {
            world.update(STEP_SECONDS);
            log.log(LogLevel.INFO, "Step:", step);
            log.log(LogLevel.INFO, "Bombs burning:", burn.getBurning());
            log.log(LogLevel.INFO, "Bombs exploded:", tally.getExploded());
        }
        return tally;
    }
}
