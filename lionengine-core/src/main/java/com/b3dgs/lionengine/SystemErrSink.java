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

import java.io.PrintStream;

/**
 * Default sink of {@link Log}: writes each message on a line of {@code System.err} (ADR-0031, REQ-CORE-006).
 *
 * <p>
 * Line format: the level name, {@code ": "} and the message; the stack trace of the error, if any, follows. The
 * stream is read at each call, so a redirection with {@link System#setErr(PrintStream)} applies. Standard error is the
 * output required by REQ-CORE-006, hence the suppression of Sonar rule S106 on this class only.
 * </p>
 */
@SuppressWarnings("java:S106")
final class SystemErrSink implements LogSink
{
    /** Separator between the level name and the message. */
    private static final String LEVEL_SEPARATOR = ": ";

    /**
     * Creates the sink.
     */
    SystemErrSink()
    {
        super();
    }

    @Override
    public void write(LogLevel level, CharSequence message, Throwable error)
    {
        final PrintStream out = System.err;
        out.append(level.name()).append(LEVEL_SEPARATOR).append(message);
        out.println();
        if (error != null)
        {
            error.printStackTrace(out);
        }
    }
}
