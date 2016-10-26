/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.b3dgs.lionengine.core.Engine;

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
 */
public enum Verbose
{
    /** Only information messages. */
    INFORMATION,
    /** Only warning messages. */
    WARNING,
    /** Only critical and exceptions messages. */
    CRITICAL;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(Constant.EMPTY_STRING);
    /** Log size. */
    private static final int LOG_SIZE = Constant.MEGA_BYTE * 4;
    /** Log count. */
    private static final int LOG_COUNT = 6;
    /** Temp directory. */
    private static final String REG_TEMP = "%t/";
    /** Log number. */
    private static final String LOG_NUM = "%g";
    /** Log file. */
    private static final String LOG_FILE = REG_TEMP + Engine.NAME.toLowerCase(Locale.ENGLISH) + "-" + LOG_NUM + ".log";
    /** Separator date. */
    private static final String SEPARATOR_DATE = " - ";
    /** In. */
    private static final String IN = "in ";
    /** At. */
    private static final String AT = " at ";
    /** Error formatter log file. */
    private static final String ERROR_LOG_FILE = "Unable to set log file !";
    /** Error formatter. */
    private static final String ERROR_FORMATTER = "Unable to set logger formatter due to security exception !";
    /** Verbose flag. */
    private static final Collection<Verbose> LEVELS = EnumSet.copyOf(Arrays.asList(Verbose.values()));

    /**
     * Configure verbose.
     */
    static
    {
        addFileHandler(LOGGER);
        setFormatter(LOGGER, new VerboseFormatter());
    }

    /**
     * Display an informative verbose message to standard output.
     * 
     * @param message The list of messages.
     * @see Verbose#INFORMATION
     */
    public static synchronized void info(String... message)
    {
        if (LEVELS.contains(INFORMATION))
        {
            LOGGER.logp(Level.INFO, null, null, getMessage(message));
        }
    }

    /**
     * Display a check verbose message to error output.
     * 
     * @param message The list of messages.
     * @see Verbose#WARNING
     */
    public static synchronized void warning(String... message)
    {
        if (LEVELS.contains(WARNING))
        {
            LOGGER.logp(Level.WARNING, null, null, getMessage(message));
        }
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
        if (LEVELS.contains(WARNING))
        {
            LOGGER.logp(Level.WARNING, clazz.getName(), function, getMessage(message));
        }
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
        if (LEVELS.contains(CRITICAL))
        {
            LOGGER.logp(Level.SEVERE, clazz.getName(), function, getMessage(message));
        }
    }

    /**
     * Display a critical verbose message to error output.
     * 
     * @param exception The thrown exception.
     * @param message The list of messages.
     * @see Verbose#CRITICAL
     */
    public static synchronized void exception(Throwable exception, String... message)
    {
        if (LEVELS.contains(CRITICAL))
        {
            LOGGER.logp(Level.SEVERE, null, null, getMessage(message), exception);
        }
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
     * Add file handler to logger.
     * 
     * @param logger The logger reference.
     */
    private static void addFileHandler(Logger logger)
    {
        try
        {
            final FileHandler fileHandler = new FileHandler(LOG_FILE, LOG_SIZE, LOG_COUNT, true);
            logger.addHandler(fileHandler);
        }
        catch (final IOException exception)
        {
            exception(exception, ERROR_LOG_FILE);
        }
    }

    /**
     * Set the logger formatter.
     * 
     * @param logger The logger reference.
     * @param formatter The formatter instance to use.
     */
    private static void setFormatter(Logger logger, Formatter formatter)
    {
        try
        {
            for (final Handler handler : logger.getHandlers())
            {
                handler.setFormatter(formatter);
            }
        }
        catch (final SecurityException exception)
        {
            exception(exception, ERROR_FORMATTER);
        }
    }

    /**
     * Get the verbose message.
     * 
     * @param message The list of messages.
     * @return The concatenated message.
     */
    private static String getMessage(String... message)
    {
        final StringBuilder builder = new StringBuilder();
        for (final String element : message)
        {
            builder.append(element);
        }
        return builder.toString();
    }

    /**
     * Verbose formatter.
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

            message.append(format.format(Calendar.getInstance().getTime())).append(SEPARATOR_DATE);
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
                message.append(sw);
            }

            return message.toString();
        }
    }
}
