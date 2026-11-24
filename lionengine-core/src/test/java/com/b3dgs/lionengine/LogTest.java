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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.sun.management.ThreadMXBean;

/**
 * Test {@link Log} (REQ-CORE-006).
 */
final class LogTest
{
    /** Messages logged per allocation measurement. */
    private static final int ITERATIONS = 100_000;
    /** Bytes tolerated for the measurement itself: far below one allocation per call. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /**
     * Records copies of the messages it receives.
     */
    private static final class RecordingSink implements LogSink
    {
        /** Levels received. */
        private final List<LogLevel> levels = new ArrayList<>();
        /** Message copies received. */
        private final List<String> messages = new ArrayList<>();
        /** Errors received. */
        private final List<Throwable> errors = new ArrayList<>();

        @Override
        public void write(LogLevel level, CharSequence message, Throwable error)
        {
            levels.add(level);
            messages.add(message.toString());
            errors.add(error);
        }
    }

    /**
     * Defaults: warning level and the {@code System.err} sink.
     */
    @Test
    void testDefaults()
    {
        final Log log = new Log();

        assertSame(LogLevel.WARNING, Log.DEFAULT_LEVEL);
        assertSame(Log.DEFAULT_LEVEL, log.getLevel());
        assertInstanceOf(SystemErrSink.class, log.getSink());
    }

    /**
     * A level is enabled when it is at least as severe as the threshold.
     *
     * @param threshold The least severe enabled level.
     * @param level The checked level.
     * @param expected The expected result.
     */
    @ParameterizedTest
    @CsvSource({"ERROR, ERROR, true",
                "ERROR, WARNING, false",
                "WARNING, ERROR, true",
                "WARNING, WARNING, true",
                "WARNING, INFO, false",
                "INFO, DEBUG, false",
                "DEBUG, DEBUG, true",
                "DEBUG, ERROR, true"})
    void testIsEnabled(LogLevel threshold, LogLevel level, boolean expected)
    {
        final Log log = new Log(new RecordingSink());
        log.setLevel(threshold);

        assertSame(threshold, log.getLevel());
        assertEquals(expected, log.isEnabled(level));
    }

    /**
     * A message without argument is given as is.
     */
    @Test
    void testMessage()
    {
        final RecordingSink sink = new RecordingSink();
        final Log log = new Log(sink);

        log.log(LogLevel.ERROR, "Failure");

        assertEquals(List.of(LogLevel.ERROR), sink.levels);
        assertEquals(List.of("Failure"), sink.messages);
        assertNull(sink.errors.get(0));
    }

    /**
     * Arguments are appended after a space, and the reused buffer keeps nothing from a previous message.
     */
    @Test
    void testArguments()
    {
        final RecordingSink sink = new RecordingSink();
        final Log log = new Log(sink);

        log.log(LogLevel.WARNING, "Frame size:", 320, 240);
        log.log(LogLevel.WARNING, "Steps dropped:", 3);
        log.log(LogLevel.WARNING, "Scale:", 1.5);

        assertEquals(List.of("Frame size: 320 240", "Steps dropped: 3", "Scale: 1.5"), sink.messages);
    }

    /**
     * The error is given to the sink with its message.
     */
    @Test
    void testError()
    {
        final RecordingSink sink = new RecordingSink();
        final Log log = new Log(sink);
        final IllegalStateException error = new IllegalStateException("cause");

        log.log(LogLevel.ERROR, "Loop stopped", error);

        assertEquals(List.of("Loop stopped"), sink.messages);
        assertSame(error, sink.errors.get(0));
    }

    /**
     * A disabled level reaches no sink, whatever the overload.
     */
    @Test
    void testDisabledLevel()
    {
        final RecordingSink sink = new RecordingSink();
        final Log log = new Log(sink);

        log.log(LogLevel.INFO, "Started");
        log.log(LogLevel.DEBUG, "Step:", 1);
        log.log(LogLevel.DEBUG, "Step:", 1, 2);
        log.log(LogLevel.DEBUG, "Alpha:", 0.5);
        log.log(LogLevel.INFO, "Ignored", new IllegalStateException());

        assertTrue(sink.messages.isEmpty());
    }

    /**
     * The sink can be replaced.
     */
    @Test
    void testSetSink()
    {
        final RecordingSink first = new RecordingSink();
        final RecordingSink second = new RecordingSink();
        final Log log = new Log(first);

        log.setSink(second);
        log.log(LogLevel.ERROR, "Replaced");

        assertSame(second, log.getSink());
        assertTrue(first.messages.isEmpty());
        assertEquals(List.of("Replaced"), second.messages);
    }

    /**
     * {@code null} sinks and levels are rejected.
     */
    @Test
    void testNullArguments()
    {
        final Log log = new Log(new RecordingSink());

        assertThrows(IllegalArgumentException.class, () -> new Log(null));
        assertThrows(IllegalArgumentException.class, () -> log.setSink(null));
        assertThrows(IllegalArgumentException.class, () -> log.setLevel(null));
        assertThrows(IllegalArgumentException.class, () -> log.isEnabled(null));
        assertThrows(IllegalArgumentException.class, () -> log.log(null, "Message"));
    }

    /**
     * A disabled level allocates nothing, whatever the arguments (REQ-CORE-006, measured with the per-thread
     * allocation counter of the JVM).
     */
    @Test
    void testDisabledLevelAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final Log log = new Log(new RecordingSink());
        final IllegalStateException error = new IllegalStateException();
        logDisabled(log, error);

        final long before = bean.getCurrentThreadAllocatedBytes();
        logDisabled(log, error);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
    }

    /**
     * Logs many debug messages, disabled by default.
     *
     * @param log The log.
     * @param error The error to pass.
     */
    private static void logDisabled(Log log, Throwable error)
    {
        for (int i = 0; i < ITERATIONS; i++)
        {
            log.log(LogLevel.DEBUG, "Message");
            log.log(LogLevel.DEBUG, "Index:", i);
            log.log(LogLevel.DEBUG, "Pair:", i, i);
            log.log(LogLevel.DEBUG, "Ratio:", i * 0.5);
            log.log(LogLevel.DEBUG, "Failure", error);
        }
    }
}
