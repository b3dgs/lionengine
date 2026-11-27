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
package com.b3dgs.lionengine.android.verification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.ecs.WorldConfig;

/**
 * Runs {@code ecs} on Android API 21 (ADR-0044): object creation and removal by ID, pending and stale IDs, the flush
 * and the capacity growth. Expected values are the ones of the JVM tests of {@code lionengine-ecs} (WorldTest,
 * WorldFlushOrderTest).
 */
@RunWith(AndroidJUnit4.class)
public final class EcsTest
{
    /**
     * Creates the test.
     */
    public EcsTest()
    {
        super();
    }

    /**
     * A created object is pending until the flush; a removed one is stale after the flush; a reused index has a new
     * generation.
     */
    @Test
    public void testLifecycle()
    {
        final World world = new World(new WorldConfig(1));
        final long id = world.create();

        assertFalse(world.isAlive(id));
        world.flush();
        assertTrue(world.isAlive(id));

        world.remove(id);
        assertTrue(world.isAlive(id));
        world.flush();
        assertFalse(world.isAlive(id));
        assertThrows(IllegalArgumentException.class, () -> world.remove(id));

        final long reused = world.create();
        world.flush();
        assertTrue(world.isAlive(reused));
        assertTrue(reused > id);
        assertEquals(1, world.getObjectCount());
        assertFalse(world.isAlive(World.NO_OBJECT));
    }

    /**
     * Creating more objects than the capacity doubles the table at the flush.
     */
    @Test
    public void testGrowth()
    {
        final World world = new World(new WorldConfig(2));
        for (int i = 0; i < 5; i++)
        {
            world.create();
        }
        assertEquals(2, world.getObjectCapacity());
        world.flush();
        assertEquals(8, world.getObjectCapacity());
        assertEquals(5, world.getObjectCount());
    }
}
