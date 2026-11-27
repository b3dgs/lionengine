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
package com.b3dgs.lionengine.platform.awt;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import com.b3dgs.lionengine.LogSink;

/**
 * Shared fixtures of the AWT platform tests.
 */
final class AwtFixtures
{
    /** Tag of the tests needing a real display (testing.md, platform tests). */
    static final String PLATFORM = "platform";
    /** Sink writing nothing. */
    static final LogSink SINK = (level, message, error) ->
    {
        // Nothing to write.
    };

    /**
     * Runs an action on the AWT event thread and waits for it.
     *
     * @param action The action.
     * @throws IllegalStateException If the action fails or the wait is interrupted.
     */
    static void onEventThread(Runnable action)
    {
        try
        {
            EventQueue.invokeAndWait(action);
        }
        catch (final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(exception);
        }
        catch (final InvocationTargetException exception)
        {
            throw new IllegalStateException(exception.getCause());
        }
    }

    /**
     * Waits until the AWT event thread has processed the events queued before this call.
     */
    static void flush()
    {
        onEventThread(() ->
        {
            // Queued after the pending events: they are processed when it runs.
        });
    }

    /**
     * Private constructor.
     */
    private AwtFixtures()
    {
        throw new AssertionError();
    }
}
