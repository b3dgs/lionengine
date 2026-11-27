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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * Tests of {@link TraitExample}.
 */
final class TraitExampleTest
{
    /**
     * Each bomb explodes when its fuse reaches zero, one step later per rank; its fuse goes with it; the world never
     * grows.
     */
    @Test
    void testBurn()
    {
        final World world = TraitExample.createWorld();
        final TraitStorage<Fuse> fuses = world.register(TraitExample.FUSE);
        final long[] bombs = TraitExample.lay(world, fuses);
        assertEquals(TraitExample.BOMBS, fuses.getCount());
        assertEquals(TraitExample.FUSE_STEPS, fuses.get(bombs[0]).getStepsLeft());

        for (int step = 1; step < TraitExample.FUSE_STEPS; step++)
        {
            assertEquals(0, TraitExample.burn(world, fuses, bombs));
        }
        for (int i = 0; i < TraitExample.BOMBS; i++)
        {
            assertEquals(1, TraitExample.burn(world, fuses, bombs));
            assertFalse(world.isAlive(bombs[i]));
            assertThrows(IllegalArgumentException.class, () -> fuses.has(bombs[0]));
        }
        assertEquals(0, fuses.getCount());
        assertEquals(TraitExample.BOMBS, fuses.getCapacity());
        assertEquals(TraitExample.BOMBS, world.getObjectCapacity());
    }

    /**
     * Defusing removes the fuse at the flush and leaves the bomb alive; relighting replaces the fuse at the flush.
     */
    @Test
    void testDefuseAndRelight()
    {
        final World world = TraitExample.createWorld();
        final TraitStorage<Fuse> fuses = world.register(TraitExample.FUSE);
        final long[] bombs = TraitExample.lay(world, fuses);
        final Fuse old = fuses.get(bombs[1]);

        fuses.remove(bombs[0]);
        final Fuse relit = fuses.add(bombs[1]);
        relit.setStepsLeft(1);
        assertTrue(fuses.has(bombs[0]));
        assertSame(old, fuses.get(bombs[1]));
        world.flush();

        assertFalse(fuses.has(bombs[0]));
        assertNotSame(old, fuses.get(bombs[1]));
        assertSame(relit, fuses.get(bombs[1]));
        assertEquals(1, TraitExample.burn(world, fuses, bombs));
        assertFalse(world.isAlive(bombs[1]));
        assertTrue(world.isAlive(bombs[0]));
    }

    /**
     * The example runs: every bomb but the defused one explodes.
     */
    @Test
    void testRun()
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);

        assertEquals(TraitExample.BOMBS - 1, TraitExample.run(log));
    }
}
