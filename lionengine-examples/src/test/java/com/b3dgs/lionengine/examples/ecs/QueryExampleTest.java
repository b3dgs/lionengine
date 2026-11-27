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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.ecs.Query;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * Tests of {@link QueryExample}.
 */
final class QueryExampleTest
{
    /**
     * Frozen bombs are excluded from burning until they thaw; explosions requested inside the iteration apply at the
     * flush; the queries follow every flush and the world never grows.
     */
    @Test
    void testThawAndBurn()
    {
        final World world = QueryExample.createWorld();
        final TraitStorage<Fuse> fuses = world.register(TraitExample.FUSE);
        final TraitStorage<Frozen> frozen = world.register(QueryExample.FROZEN);
        final Query thawing = world.createQuery(frozen);
        final Query burning = world.createQuery(new TraitStorage<?>[] {fuses}, new TraitStorage<?>[] {frozen});
        final long[] bombs = QueryExample.lay(world, fuses, frozen);
        final long[] ids = new long[QueryExample.BOMBS];
        assertEquals(QueryExample.BOMBS / 2, thawing.getCount());
        assertEquals(QueryExample.BOMBS / 2, burning.copySortedIds(ids));
        assertArrayEquals(new long[] {bombs[0], bombs[2], bombs[4]}, Arrays.copyOf(ids, 3));

        for (int step = 1; step < QueryExample.FUSE_STEPS; step++)
        {
            QueryExample.thaw(frozen, thawing);
            assertEquals(0, QueryExample.burn(world, fuses, burning));
            world.flush();
        }
        QueryExample.thaw(frozen, thawing);
        assertEquals(QueryExample.BOMBS / 2, QueryExample.burn(world, fuses, burning));
        assertEquals(QueryExample.BOMBS / 2, burning.getCount());
        assertTrue(world.isAlive(bombs[0]));
        world.flush();
        assertFalse(world.isAlive(bombs[0]));
        assertEquals(0, burning.getCount());

        for (int step = QueryExample.FUSE_STEPS; step < QueryExample.FROZEN_STEPS; step++)
        {
            QueryExample.thaw(frozen, thawing);
            world.flush();
        }
        assertEquals(0, thawing.getCount());
        assertEquals(QueryExample.BOMBS / 2, burning.copySortedIds(ids));
        assertArrayEquals(new long[] {bombs[1], bombs[3], bombs[5]}, Arrays.copyOf(ids, 3));
        assertEquals(QueryExample.BOMBS, world.getObjectCapacity());
        assertEquals(QueryExample.BOMBS, fuses.getCapacity());
    }

    /**
     * The example runs: every bomb explodes, the frozen ones later.
     */
    @Test
    void testRun()
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);

        assertEquals(QueryExample.BOMBS, QueryExample.run(log));
    }
}
