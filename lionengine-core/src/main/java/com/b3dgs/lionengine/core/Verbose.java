/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Level of verbosity.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * Verbose.info(&quot;Code reached&quot;);
 * try
 * {
 *     Thread.sleep(1000);
 *     Verbose.warning(MyClass.class, &quot;function&quot;, &quot;Warning level here&quot;);
 *     Verbose.critical(MyClass.class, &quot;function&quot;, &quot;Critical level here&quot;);
 * }
 * catch (final InterruptedException exception)
 * {
 *     Thread.currentThread().interrupt();
 *     Verbose.exception(MyClass.class, &quot;function&quot;, exception);
 * }
 * </pre>
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum Verbose
{
    /** Verbose disabled. */
    NONE,
    /** Only information messages. */
    INFORMATION,
    /** Information and warning messages. */
    WARNING,
    /** Information, warning and critical messages. */
    CRITICAL;

    /** Logger. */
    private static final Logger LOGGER = Logger.getAnonymousLogger();
    /** Error formatter. */
    private static final String ERROR_FORMATTER = "Unable to set logger formatter due to security exception !";
    /** Verbose flag. */
    private static volatile Verbose level = CRITICAL;

    /**
     * Display an informative verbose message to standard output.
     * 
     * @param message The list of messages.
     */
    public static synchronized void info(String... message)
    {
        if (NONE != level)
        {
            verbose(INFORMATION, null, null, null, message);
        }
    }

    /**
     * Display a check verbose message to error output.
     * 
     * @param clazz The current class name.
     * @param function The current function name.
     * @param message The list of messages.
     */
    public static synchronized void warning(Class<?> clazz, String function, String... message)
    {
        if (WARNING == level || CRITICAL == level)
        {
            verbose(WARNING, clazz, function, null, message);
        }
    }

    /**
     * Display a critical verbose message to error output.
     * 
     * @param clazz The current class name.
     * @param function The current function name.
     * @param message The list of messages.
     */
    public static synchronized void critical(Class<?> clazz, String function, String... message)
    {
        if (CRITICAL == level)
        {
            verbose(CRITICAL, clazz, function, null, message);
        }
    }

    /**
     * Display a critical verbose message to error output.
     * 
     * @param clazz The current class name.
     * @param function The current function name.
     * @param thrown The thrown exception.
     * @param message The list of messages.
     */
    public static synchronized void exception(Class<?> clazz, String function, Throwable thrown, String... message)
    {
        if (CRITICAL == level)
        {
            verbose(CRITICAL, clazz, function, thrown, message);
        }
    }

    /**
     * Set the verbosity level.
     * 
     * @param verbose Verbosity level.
     */
    public static synchronized void set(Verbose verbose)
    {
        level = verbose;
    }

    /**
     * Prepare the logger handler.
     */
    static synchronized void prepareLogger()
    {
        try
        {
            final Handler[] handlers = Logger.getLogger("").getHandlers();
            for (final Handler handler : handlers)
            {
                handler.setFormatter(new VerboseFormatter());
            }
        }
        catch (final SecurityException exception)
        {
            critical(Verbose.class, "start", ERROR_FORMATTER);
        }
    }

    /**
     * Display a verbose message.
     * 
     * @param level The verbose level.
     * @param clazz The current class name.
     * @param function The current function name.
     * @param thrown The thrown object.
     * @param message The list of messages.
     */
    private static void verbose(Verbose level, Class<?> clazz, String function, Throwable thrown, String... message)
    {
        final StringBuilder builder = new StringBuilder("");
        for (final String element : message)
        {
            builder.append(element);
        }
        final String verbose = builder.toString();
        switch (level)
        {
            case INFORMATION:
                LOGGER.setLevel(Level.INFO);
                LOGGER.logp(Level.INFO, null, null, verbose, thrown);
                break;
            case WARNING:
                LOGGER.setLevel(Level.WARNING);
                LOGGER.logp(Level.WARNING, clazz.getSimpleName(), function, verbose, thrown);
                break;
            case CRITICAL:
                LOGGER.setLevel(Level.SEVERE);
                LOGGER.logp(Level.SEVERE, clazz.getSimpleName(), function, verbose, thrown);
                break;
            default:
                throw new LionEngineException("Unknown level: " + level);
        }
    }

    /**
     * Verbose formatter.
     * 
     * @author Pierre-Alexandre (contact@b3dgs.com)
     */
    private static class VerboseFormatter
            extends Formatter
    {
        /**
         * Internal constructor.
         */
        VerboseFormatter()
        {
            super();
        }

        @Override
        public String format(LogRecord event)
        {
            final String clazz = event.getSourceClassName();
            final String function = event.getSourceMethodName();
            final Throwable thrown = event.getThrown();
            final StringBuilder message = new StringBuilder(event.getLevel().getName()).append(": ");

            if (clazz != null)
            {
                message.append("in ").append(clazz);
            }
            if (function != null)
            {
                message.append(" at ").append(function).append(": ");
            }
            message.append(event.getMessage()).append("\n");
            if (thrown != null)
            {
                final StringWriter sw = new StringWriter();
                thrown.printStackTrace(new PrintWriter(sw));
                message.append(sw.toString());
            }

            return message.toString();
        }
    }
}
