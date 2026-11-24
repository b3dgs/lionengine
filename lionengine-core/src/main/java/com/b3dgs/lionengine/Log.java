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
 * Logging facade of the engine (ADR-0031, REQ-CORE-006).
 *
 * <p>
 * A message is a constant string, optionally followed by primitive arguments that the facade appends after a space:
 * {@code log.log(LogLevel.WARNING, "Steps dropped:", 3)} writes {@code Steps dropped: 3}. A disabled level returns
 * before any formatting, so it allocates nothing. Code that would build a message itself checks
 * {@link #isEnabled(LogLevel)} first.
 * </p>
 *
 * <p>
 * Defaults: the level is {@link #DEFAULT_LEVEL}, so {@link LogLevel#ERROR} and {@link LogLevel#WARNING} are written
 * and the other levels dropped, and the sink writes each message on a line of {@code System.err} ({@code LEVEL: text},
 * then the stack trace of the error if any). A platform installs its own sink with {@link #setSink(LogSink)}.
 * </p>
 *
 * <p>
 * An instance is passed explicitly or held by the platform object; there is no static logger. Not thread-safe: the
 * formatting buffer is reused, so a loading worker logs through its own instance (ADR-0019).
 * </p>
 *
 * @since 11.0.0
 */
public final class Log
{
    /** Default level: {@link LogLevel#WARNING}, so errors and warnings are enabled. */
    public static final LogLevel DEFAULT_LEVEL = LogLevel.WARNING;

    /** Initial capacity of the formatting buffer, in characters. */
    private static final int BUFFER_CAPACITY = 256;
    /** Separator between the message and its arguments. */
    private static final char ARGUMENT_SEPARATOR = ' ';
    /** Error message of a {@code null} level. */
    private static final String ERROR_LEVEL = "Level must not be null";

    /** Formatting buffer, reused by every message with arguments. */
    private final StringBuilder buffer = new StringBuilder(BUFFER_CAPACITY);
    /** Destination of the enabled messages. */
    private LogSink sink;
    /** Least severe enabled level. */
    private LogLevel threshold = DEFAULT_LEVEL;

    /**
     * Creates a log with the default level and the default {@code System.err} sink.
     */
    public Log()
    {
        this(new SystemErrSink());
    }

    /**
     * Creates a log with the default level and the given sink.
     *
     * @param sink The destination of the enabled messages (never {@code null}).
     * @throws IllegalArgumentException If {@code sink} is {@code null}.
     */
    public Log(LogSink sink)
    {
        setSink(sink);
    }

    /**
     * Replaces the sink, for example by the platform's own log.
     *
     * @param sink The destination of the enabled messages (never {@code null}).
     * @throws IllegalArgumentException If {@code sink} is {@code null}.
     */
    public void setSink(LogSink sink)
    {
        if (sink == null)
        {
            throw new IllegalArgumentException("Sink must not be null");
        }
        this.sink = sink;
    }

    /**
     * Returns the sink.
     *
     * @return The destination of the enabled messages (never {@code null}).
     */
    public LogSink getSink()
    {
        return sink;
    }

    /**
     * Sets the least severe enabled level: that level and the more severe ones are written.
     *
     * @param level The least severe enabled level (never {@code null}); {@link LogLevel#DEBUG} enables every level.
     * @throws IllegalArgumentException If {@code level} is {@code null}.
     */
    public void setLevel(LogLevel level)
    {
        if (level == null)
        {
            throw new IllegalArgumentException(ERROR_LEVEL);
        }
        threshold = level;
    }

    /**
     * Returns the least severe enabled level.
     *
     * @return The level (never {@code null}), {@link #DEFAULT_LEVEL} unless changed.
     */
    public LogLevel getLevel()
    {
        return threshold;
    }

    /**
     * Tells whether a level is enabled. Allocates nothing.
     *
     * @param level The level to check (never {@code null}).
     * @return {@code true} if messages of this level are written, {@code false} if they are dropped.
     * @throws IllegalArgumentException If {@code level} is {@code null}.
     */
    public boolean isEnabled(LogLevel level)
    {
        if (level == null)
        {
            throw new IllegalArgumentException(ERROR_LEVEL);
        }
        return level.compareTo(threshold) <= 0;
    }

    /**
     * Logs a message.
     *
     * @param level The message level (never {@code null}).
     * @param message The constant message (never {@code null}).
     * @throws IllegalArgumentException If {@code level} is {@code null}.
     */
    public void log(LogLevel level, String message)
    {
        if (isEnabled(level))
        {
            sink.write(level, message, null);
        }
    }

    /**
     * Logs a message followed by one integer argument.
     *
     * @param level The message level (never {@code null}).
     * @param message The constant message (never {@code null}).
     * @param value The argument, appended after a space.
     * @throws IllegalArgumentException If {@code level} is {@code null}.
     */
    public void log(LogLevel level, String message, long value)
    {
        if (isEnabled(level))
        {
            start(message).append(value);
            sink.write(level, buffer, null);
        }
    }

    /**
     * Logs a message followed by two integer arguments.
     *
     * @param level The message level (never {@code null}).
     * @param message The constant message (never {@code null}).
     * @param first The first argument, appended after a space.
     * @param second The second argument, appended after a space.
     * @throws IllegalArgumentException If {@code level} is {@code null}.
     */
    public void log(LogLevel level, String message, long first, long second)
    {
        if (isEnabled(level))
        {
            start(message).append(first).append(ARGUMENT_SEPARATOR).append(second);
            sink.write(level, buffer, null);
        }
    }

    /**
     * Logs a message followed by one real argument.
     *
     * @param level The message level (never {@code null}).
     * @param message The constant message (never {@code null}).
     * @param value The argument, appended after a space as {@link Double#toString(double)} writes it.
     * @throws IllegalArgumentException If {@code level} is {@code null}.
     */
    public void log(LogLevel level, String message, double value)
    {
        if (isEnabled(level))
        {
            start(message).append(value);
            sink.write(level, buffer, null);
        }
    }

    /**
     * Logs a message with the error that caused it.
     *
     * @param level The message level (never {@code null}).
     * @param message The constant message (never {@code null}).
     * @param error The error, given to the sink (may be {@code null}).
     * @throws IllegalArgumentException If {@code level} is {@code null}.
     */
    public void log(LogLevel level, String message, Throwable error)
    {
        if (isEnabled(level))
        {
            sink.write(level, message, error);
        }
    }

    /**
     * Clears the buffer and writes the message followed by the argument separator.
     *
     * @param message The constant message.
     * @return The buffer, ready for the arguments.
     */
    private StringBuilder start(String message)
    {
        buffer.setLength(0);
        return buffer.append(message).append(ARGUMENT_SEPARATOR);
    }
}
