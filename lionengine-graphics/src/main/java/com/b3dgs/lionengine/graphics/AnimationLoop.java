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
package com.b3dgs.lionengine.graphics;

/**
 * Loop mode of an {@link Animation}: what follows its last frame (ADR-0026, ADR-0033, REQ-ANIM-001).
 *
 * @since 11.0.0
 */
public enum AnimationLoop
{
    /** Plays the frames once and stops on the last one; written {@code once} in a sheet definition. */
    ONCE("once"),
    /** Restarts at the first frame after the last one; written {@code loop} in a sheet definition. */
    LOOP("loop"),
    /**
     * Plays the frames forward, then backward, and again, the end frames shown once per turn; written
     * {@code ping-pong} in a sheet definition.
     */
    PING_PONG("ping-pong");

    /**
     * Returns the loop mode written in a sheet definition.
     *
     * @param name The name, as in a definition file (never {@code null}).
     * @return The loop mode, {@code null} if the name is not one.
     * @throws IllegalArgumentException If the name is {@code null}.
     */
    static AnimationLoop fromName(String name)
    {
        ImageCache.checkNotNull(name, "Loop name");
        AnimationLoop result = null;
        for (final AnimationLoop loop : values())
        {
            if (loop.name.equals(name))
            {
                result = loop;
            }
        }
        return result;
    }

    /** The name in a sheet definition. */
    private final String name;

    /**
     * Creates a loop mode.
     *
     * @param name The name in a sheet definition.
     */
    AnimationLoop(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the loop mode in a sheet definition.
     *
     * @return The name: {@code once}, {@code loop} or {@code ping-pong}.
     */
    public String getName()
    {
        return name;
    }
}
