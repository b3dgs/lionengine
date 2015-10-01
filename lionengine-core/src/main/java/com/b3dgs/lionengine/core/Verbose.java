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
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Level of verbosity.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * Verbose.set(Verbose.INFORMATION, Verbose.WARNING, Verbose.CRITICAL);
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
 * 
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum Verbose
{
    /** Only information messages. */
    INFORMATION,
    /** Information and warning messages. */
    WARNING,
    /** Information, warning and critical messages. */
    CRITICAL;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(Constant.EMPTY_STRING);
    /** Log size. */
    private static final int LOG_SIZE = Constant.MEGA_BYTE * 4;
    /** Log count. */
    private static final int LOG_COUNT = 6;
    /** Log file. */
    private static final String LOG_FILE = "%t/lionengine-%g.log";
    /** Separator date. */
    private static final String SEPARATOR_DATE = " - ";
    /** In. */
    private static final String IN = "in ";
    /** At. */
    private static final String AT = " at ";
    /** Error formatter. */
    private static final String ERROR_FORMATTER = "Unable to set logger formatter due to security exception !";
    /** Error level. */
    private static final String ERROR_LEVEL = "Unknown level: ";
    /** Verbose flag. */
    private static final Collection<Verbose> LEVELS = new HashSet<Verbose>();

    /**
     * Set default verbose values.
     */
    static
    {
        for (final Verbose verbose : Verbose.values())
        {
            LEVELS.add(verbose);
        }
    }

    /**
     * Display an informative verbose message to standard output.
     * 
     * @param message The list of messages.
     * @see Verbose#INFORMATION
     */
    public static synchronized void info(String... message)
    {
        verbose(INFORMATION, null, null, null, message);
    }

    /**
     * Display a check verbose message to error output.
     * 
     * @param clazz The current class name.
     * @param function The current function name.
     * @param message The list of messages.
     * @see Verbose#WARNING
     */
    public static synchronized void warning(Class<?> clazz, String function, String... message)
    {
        verbose(WARNING, clazz, function, null, message);
    }

    /**
     * Display a critical verbose message to error output.
     * 
     * @param clazz The current class name.
     * @param function The current function name.
     * @param message The list of messages.
     * @see Verbose#CRITICAL
     */
    public static synchronized void critical(Class<?> clazz, String function, String... message)
    {
        verbose(CRITICAL, clazz, function, null, message);
    }

    /**
     * Display a critical verbose message to error output.
     * 
     * @param clazz The current class name.
     * @param function The current function name.
     * @param thrown The thrown exception.
     * @param message The list of messages.
     * @see Verbose#CRITICAL
     */
    public static synchronized void exception(Class<?> clazz, String function, Throwable thrown, String... message)
    {
        verbose(CRITICAL, clazz, function, thrown, message);
    }

    /**
     * Set the verbosity level.
     * 
     * @param verboses Verbosity levels.
     */
    public static synchronized void set(Verbose... verboses)
    {
        LEVELS.clear();
        for (final Verbose verbose : verboses)
        {
            LEVELS.add(verbose);
        }
    }

    /**
     * Prepare the logger handler.
     */
    static synchronized void prepareLogger()
    {
        try
        {
            final VerboseFormatter formatter = new VerboseFormatter();
            final FileHandler fileHandler = new FileHandler(LOG_FILE, LOG_SIZE, LOG_COUNT, true);
            LOGGER.addHandler(fileHandler);

            final Handler[] handlers = LOGGER.getHandlers();
            for (final Handler handler : handlers)
            {
                handler.setFormatter(formatter);
            }
        }
        catch (final Exception exception)
        {
            exception(Verbose.class, "prepareLogger", exception, ERROR_FORMATTER);
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
        if (LEVELS.contains(level))
        {
            final StringBuilder builder = new StringBuilder();
            for (final String element : message)
            {
                builder.append(element);
            }
            final String verbose = builder.toString();
            switch (level)
            {
                case INFORMATION:
                    LOGGER.logp(Level.INFO, null, null, verbose, thrown);
                    break;
                case WARNING:
                    LOGGER.logp(Level.WARNING, clazz.getSimpleName(), function, verbose, thrown);
                    break;
                case CRITICAL:
                    LOGGER.logp(Level.SEVERE, clazz.getSimpleName(), function, verbose, thrown);
                    break;
                default:
                    throw new LionEngineException(ERROR_LEVEL + level);
            }
        }
    }

    /**
     * Verbose formatter.
     * 
     * @author Pierre-Alexandre (contact@b3dgs.com)
     */
    private static final class VerboseFormatter extends Formatter
    {
        /** Date formatter. */
        private final DateFormat format = DateFormat.getInstance();

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
            final StringBuilder message = new StringBuilder(event.getLevel().getName()).append(Constant.DOUBLE_DOT);

            message.append(format.format(Calendar.getInstance().getTime()));
            message.append(SEPARATOR_DATE);
            if (clazz != null)
            {
                message.append(IN).append(clazz);
            }
            if (function != null)
            {
                message.append(AT).append(function).append(Constant.DOUBLE_DOT);
            }
            message.append(event.getMessage()).append(Constant.NEW_LINE);
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
