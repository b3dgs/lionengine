/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Level of verbosity.
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
    private static final String LOG_FILE = REG_TEMP
                                           + Constant.ENGINE_NAME.toLowerCase(Locale.ENGLISH)
                                           + "-"
                                           + LOG_NUM
                                           + ".log";
    /** Error formatter log file. */
    private static final String ERROR_LOG_FILE = "Unable to set log file !";
    /** Error formatter. */
    private static final String ERROR_FORMATTER = "Unable to set logger formatter due to security exception !";
    /** Verbose flag. */
    private static final Collection<Verbose> LEVELS = EnumSet.allOf(Verbose.class);

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
     * @param message The list of messages (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     * @see Verbose#INFORMATION
     */
    public static synchronized void info(String... message)
    {
        Check.notNull(message);

        if (LEVELS.contains(INFORMATION))
        {
            LOGGER.logp(Level.INFO, null, null, getMessage(message));
        }
    }

    /**
     * Display a check verbose message to error output.
     * 
     * @param message The list of messages (must not be <code>null</code>).
     * @see Verbose#WARNING
     */
    public static synchronized void warning(String... message)
    {
        Check.notNull(message);

        if (LEVELS.contains(WARNING))
        {
            LOGGER.logp(Level.WARNING, null, null, getMessage(message));
        }
    }

    /**
     * Display a check verbose message to error output.
     * 
     * @param clazz The current class name (must not be <code>null</code>).
     * @param function The current function name (must not be <code>null</code>).
     * @param message The list of messages (must not be <code>null</code>).
     * @see Verbose#WARNING
     */
    public static synchronized void warning(Class<?> clazz, String function, String... message)
    {
        Check.notNull(clazz);
        Check.notNull(function);
        Check.notNull(message);

        if (LEVELS.contains(WARNING))
        {
            LOGGER.logp(Level.WARNING, clazz.getName(), function, getMessage(message));
        }
    }

    /**
     * Display a critical verbose message to error output.
     * 
     * @param clazz The current class name (must not be <code>null</code>).
     * @param function The current function name (must not be <code>null</code>).
     * @param message The list of messages (must not be <code>null</code>).
     * @see Verbose#CRITICAL
     */
    public static synchronized void critical(Class<?> clazz, String function, String... message)
    {
        Check.notNull(clazz);
        Check.notNull(function);
        Check.notNull(message);

        if (LEVELS.contains(CRITICAL))
        {
            LOGGER.logp(Level.SEVERE, clazz.getName(), function, getMessage(message));
        }
    }

    /**
     * Display a critical verbose message to error output.
     * 
     * @param exception The thrown exception (must not be <code>null</code>).
     * @param message The list of messages (must not be <code>null</code>).
     * @see Verbose#CRITICAL
     */
    public static synchronized void exception(Throwable exception, String... message)
    {
        Check.notNull(exception);
        Check.notNull(message);

        if (LEVELS.contains(CRITICAL))
        {
            LOGGER.logp(Level.SEVERE, null, null, getMessage(message), exception);
        }
    }

    /**
     * Set the verbosity level.
     * 
     * @param verboses Verbosity levels (must not be <code>null</code>).
     */
    public static synchronized void set(Verbose... verboses)
    {
        Check.notNull(verboses);

        LEVELS.clear();
        Collections.addAll(LEVELS, verboses);
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
}
