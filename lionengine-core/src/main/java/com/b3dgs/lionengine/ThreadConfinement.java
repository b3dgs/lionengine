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
package com.b3dgs.lionengine;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Check that an object is used on one thread only, the runtime thread, for the caches of the consuming modules
 * (ADR-0018, ADR-0055, REQ-RT-003).
 *
 * <p>
 * The owner is the thread of the first {@link #check()}, not the thread that created the object: the application
 * creates its caches while wiring the game (ADR-0040), then the platform runs the loop on the runtime thread, where
 * the first cache operation happens. Every later check on another thread throws an {@link IllegalStateException}
 * naming both threads, so that a cache touched by a loading worker or a platform thread fails at once instead of
 * corrupting its state. The owner never changes.
 * </p>
 *
 * <p>
 * Threads: {@link #check()} may be called from any thread, that is its purpose. Once the owner is known, a check
 * costs one volatile read and a comparison and allocates nothing.
 * </p>
 *
 * @since 11.0.0
 */
public final class ThreadConfinement
{
    /** Error message of a use on another thread: object name. */
    private static final String ERROR_THREAD = " used on the thread ";
    /** Error message of a use on another thread: owner. */
    private static final String ERROR_OWNER = ", but it belongs to the thread ";
    /** Error message of a use on another thread: rule. */
    private static final String ERROR_RULE = " (runtime thread only, ADR-0018)";

    /** Name of the confined object, for the messages. */
    private final String name;
    /** The owner thread, {@code null} until the first check. */
    private final AtomicReference<Thread> owner = new AtomicReference<>();

    /**
     * Creates the check of an object; the owner is the thread of the first {@link #check()}.
     *
     * @param name The name of the confined object, for the messages, for example {@code "ImageCache"} (never
     *            {@code null}).
     * @throws IllegalArgumentException If the name is {@code null}.
     */
    public ThreadConfinement(String name)
    {
        super();

        if (name == null)
        {
            throw new IllegalArgumentException("Name must not be null");
        }
        this.name = name;
    }

    /**
     * Checks that the calling thread owns the object; the first call makes the calling thread the owner.
     *
     * @throws IllegalStateException If another thread owns the object.
     */
    public void check()
    {
        final Thread current = Thread.currentThread();
        final Thread bound = owner.get();
        if (bound != current && (bound != null || !owner.compareAndSet(null, current)))
        {
            throw new IllegalStateException(name
                                            + ERROR_THREAD
                                            + current.getName()
                                            + ERROR_OWNER
                                            + owner.get().getName()
                                            + ERROR_RULE);
        }
    }
}
