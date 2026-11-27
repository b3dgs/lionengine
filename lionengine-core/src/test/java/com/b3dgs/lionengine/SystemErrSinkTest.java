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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test {@link SystemErrSink} (REQ-CORE-006).
 */
final class SystemErrSinkTest
{
    /** Captured standard error. */
    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();
    /** Original standard error. */
    private PrintStream original;

    /**
     * Redirects standard error.
     */
    @BeforeEach
    void redirect()
    {
        original = System.err;
        System.setErr(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    /**
     * Restores standard error.
     */
    @AfterEach
    void restore()
    {
        System.setErr(original);
    }

    /**
     * A message is written on one line with its level.
     */
    @Test
    void testMessage()
    {
        new SystemErrSink().write(LogLevel.WARNING, new StringBuilder("Steps dropped: 3"), null);

        assertEquals("WARNING: Steps dropped: 3" + System.lineSeparator(), captured.toString(StandardCharsets.UTF_8));
    }

    /**
     * The stack trace of the error follows the message.
     */
    @Test
    void testError()
    {
        new SystemErrSink().write(LogLevel.ERROR, "Loop stopped", new IllegalStateException("cause"));

        final String text = captured.toString(StandardCharsets.UTF_8);
        assertTrue(text.startsWith("ERROR: Loop stopped" + System.lineSeparator()), text);
        assertTrue(text.contains("java.lang.IllegalStateException: cause"), text);
    }

    /**
     * The default log writes warnings to standard error and drops debug messages.
     */
    @Test
    void testDefaultLog()
    {
        final Log log = new Log();

        log.log(LogLevel.DEBUG, "Hidden");
        log.log(LogLevel.WARNING, "Shown");

        assertEquals("WARNING: Shown" + System.lineSeparator(), captured.toString(StandardCharsets.UTF_8));
    }
}
