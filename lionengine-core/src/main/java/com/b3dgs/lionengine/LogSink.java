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

/**
 * Destination of the messages of the logging facade (ADR-0031, REQ-CORE-006).
 *
 * <p>
 * The facade checks the level before formatting, so a sink only receives enabled messages and nothing is allocated
 * for a disabled level. The default sink writes {@link LogLevel#ERROR} and {@link LogLevel#WARNING} to
 * {@code System.err}; a platform may install its own (for example the Android log).
 * </p>
 *
 * <p>
 * Thread: called on the thread that logs, the runtime thread by default (ADR-0018). A sink used by several threads
 * documents its own synchronization.
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface LogSink
{
    /**
     * Writes one formatted message.
     *
     * @param level The message level (never {@code null}).
     * @param message The formatted message (never {@code null}). It is only valid during the call because the facade
     *            reuses its buffer: a sink that keeps it copies it.
     * @param error The error reported with the message, {@code null} if none.
     */
    void write(LogLevel level, CharSequence message, Throwable error);
}
