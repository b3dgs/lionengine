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
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.ecs.WorldConfig;

/**
 * Creates and removes objects by ID in a world: a turret fires volleys of bullets that expire after a few steps
 * (REQ-ECS-001, REQ-ECS-002, REQ-ECS-011, ADR-0030, ADR-0074).
 *
 * <p>
 * {@link World#create()} returns the ID of a bullet at once, but the bullet is pending until the flush at the end of
 * the step: {@link World#isAlive(long)} is {@code false} until then. {@link World#remove(long)} also applies at the
 * flush. The game loop of a scene calls the flush once per step, after its systems; here {@link #step(World, long[],
 * int)} plays one step by hand. A removed bullet's ID becomes stale: {@code isAlive} stays {@code false} even when the
 * world reuses its index for a new bullet, so a turret that keeps an old ID never touches the wrong bullet. The world
 * is sized for the busiest step, so steps allocate nothing. Headless; {@link #main(String[])} logs the steps.
 * </p>
 *
 * @since 11.0.0
 */
public final class ObjectLifecycleExample
{
    /** Bullets per volley. */
    public static final int VOLLEY = 4;
    /** Lifetime of a bullet, in steps. */
    public static final int LIFETIME_STEPS = 3;
    /** Object capacity of the world: every bullet alive at once, plus a volley pending. */
    public static final int CAPACITY = VOLLEY * (LIFETIME_STEPS + 1);

    /** Steps played by {@link #main(String[])}. */
    private static final int STEPS = 8;

    /**
     * Private constructor.
     */
    private ObjectLifecycleExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates the world of the example.
     *
     * @return A world holding {@link #CAPACITY} objects without growing.
     */
    public static World createWorld()
    {
        return new World(new WorldConfig(CAPACITY));
    }

    /**
     * Plays one step: removes the bullets fired {@link #LIFETIME_STEPS} steps earlier, fires a volley, then flushes.
     *
     * @param world The world.
     * @param bullets The bullets fired by the last {@link #LIFETIME_STEPS} steps, {@link #VOLLEY} per step, indexed by
     *            step modulo {@link #LIFETIME_STEPS}; {@link World#NO_OBJECT} where nothing was fired yet. Updated with
     *            the new volley.
     * @param step The number of the step, from {@code 0}.
     * @return The number of bullets alive after the step.
     */
    public static int step(World world, long[] bullets, int step)
    {
        final int first = step % LIFETIME_STEPS * VOLLEY;
        for (int i = first; i < first + VOLLEY; i++)
        {
            if (bullets[i] != World.NO_OBJECT)
            {
                world.remove(bullets[i]);
            }
            bullets[i] = world.create();
        }
        world.flush();
        return world.getObjectCount();
    }

    /**
     * Runs the example: logs the bullets alive after each step, and the liveness of a stale ID.
     *
     * @param args Unused.
     */
    // CHECKSTYLE IGNORE LINE: UncommentedMain
    public static void main(String[] args)
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);

        final World world = createWorld();
        final long[] bullets = new long[VOLLEY * LIFETIME_STEPS];
        final long pending = world.create();
        log.log(LogLevel.INFO, "Alive before the flush (0 = no):", world.isAlive(pending) ? 1 : 0);
        world.flush();
        log.log(LogLevel.INFO, "Alive after the flush (1 = yes):", world.isAlive(pending) ? 1 : 0);
        world.remove(pending);
        world.flush();

        for (int step = 0; step < STEPS; step++)
        {
            log.log(LogLevel.INFO, "Bullets alive:", step(world, bullets, step));
        }
        log.log(LogLevel.INFO, "Stale ID alive (0 = no):", world.isAlive(pending) ? 1 : 0);
    }
}
