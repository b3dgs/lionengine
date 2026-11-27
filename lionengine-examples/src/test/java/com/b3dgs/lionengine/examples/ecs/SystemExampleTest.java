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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * Tests of {@link SystemExample}, {@link LaySystem}, {@link BurnSystem} and {@link BombTally}.
 */
final class SystemExampleTest
{
    /**
     * Step by step: a bomb laid by the early system is burnt by the late one from the next step only; an explosion
     * recorded by the burn system applies at the flush that ends its step and fires its event; the world never grows.
     */
    @Test
    void testStepByStep()
    {
        final World world = SystemExample.createWorld();
        final TraitStorage<Fuse> fuses = world.register(TraitExample.FUSE);
        final BurnSystem burn = new BurnSystem(world, fuses);
        final LaySystem lay = new LaySystem(world,
                                            fuses,
                                            SystemExample.LAY_PERIOD,
                                            SystemExample.BOMBS,
                                            SystemExample.FUSE_STEPS);
        world.addSystem(burn, SystemExample.BURN_SLOT);
        world.addSystem(lay, SystemExample.LAY_SLOT);
        final BombTally tally = new BombTally(fuses.getTypeId());
        world.addObjectListener(tally);
        world.addTraitListener(tally);

        final int[] burning = {1, 1, 2, 1, 2, 1, 1, 0};
        final int[] exploded = {0, 0, 0, 1, 1, 2, 2, 3};
        for (int step = 0; step < SystemExample.STEPS; step++)
        {
            world.update(SystemExample.STEP_SECONDS);
            assertEquals(burning[step], burn.getBurning(), "Burning at step " + step);
            assertEquals(exploded[step], tally.getExploded(), "Exploded at step " + step);
        }
        assertEquals(SystemExample.BOMBS, lay.getLaid());
        assertEquals(SystemExample.BOMBS, tally.getLit());
        assertEquals(0, world.getObjectCount());
        assertEquals(SystemExample.BOMBS, world.getObjectCapacity());
        assertEquals(SystemExample.BOMBS, fuses.getCapacity());
    }

    /**
     * Disabling the burn system stops the fuses from the next step.
     */
    @Test
    void testDisabledSystem()
    {
        final World world = SystemExample.createWorld();
        final TraitStorage<Fuse> fuses = world.register(TraitExample.FUSE);
        final BurnSystem burn = new BurnSystem(world, fuses);
        world.addSystem(burn, SystemExample.BURN_SLOT);
        world.addSystem(new LaySystem(world, fuses, 1, 1, 1), SystemExample.LAY_SLOT);
        world.update(SystemExample.STEP_SECONDS);
        world.setSystemEnabled(burn, false);
        world.update(SystemExample.STEP_SECONDS);

        assertEquals(1, burn.getBurning());
        world.setSystemEnabled(burn, true);
        world.update(SystemExample.STEP_SECONDS);
        assertEquals(0, burn.getBurning());
    }

    /**
     * The example runs: every bomb is lit and explodes.
     */
    @Test
    void testRun()
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);
        final BombTally tally = SystemExample.run(log);

        assertEquals(SystemExample.BOMBS, tally.getLit());
        assertEquals(SystemExample.BOMBS, tally.getExploded());
    }
}
