package com.b3dgs.lionengine;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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
 *     Verbose.exception(MyClass.class, &quot;function&quot;, exception);
 * }
 * </pre>
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
    /** Verbose flag. */
    private static Verbose level;

    /**
     * Display an informative verbose message to standard output.
     * 
     * @param message The list of messages.
     */
    public static void info(String... message)
    {
        if (Verbose.NONE != Verbose.level)
        {
            Verbose.verbose(Verbose.INFORMATION, null, null, null, message);
        }
    }

    /**
     * Display a check verbose message to error output.
     * 
     * @param clazz The current class name.
     * @param function The current function name.
     * @param message The list of messages.
     */
    public static void warning(Class<?> clazz, String function, String... message)
    {
        if (Verbose.WARNING == Verbose.level || Verbose.CRITICAL == Verbose.level)
        {
            Verbose.verbose(Verbose.WARNING, clazz, function, null, message);
        }
    }

    /**
     * Display a critical verbose message to error output.
     * 
     * @param clazz The current class name.
     * @param function The current function name.
     * @param message The list of messages.
     */
    public static void critical(Class<?> clazz, String function, String... message)
    {
        if (Verbose.CRITICAL == Verbose.level)
        {
            Verbose.verbose(Verbose.CRITICAL, clazz, function, null, message);
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
    public static void exception(Class<?> clazz, String function, Throwable thrown, String... message)
    {
        if (Verbose.CRITICAL == Verbose.level)
        {
            Verbose.verbose(Verbose.CRITICAL, clazz, function, thrown, message);
        }
    }

    /**
     * Set the verbosity level.
     * 
     * @param verbose Verbosity level.
     */
    static void set(Verbose verbose)
    {
        Verbose.level = verbose;
    }

    /**
     * Prepare the logger handler.
     */
    static void prepareLogger()
    {
        try
        {
            final Handler[] handlers = Logger.getLogger("").getHandlers();
            for (final Handler handler : handlers)
            {
                handler.setFormatter(new Formatter()
                {
                    @Override
                    public String format(LogRecord event)
                    {
                        final String clazz = event.getSourceClassName();
                        final String function = event.getSourceMethodName();
                        final StringBuilder message = new StringBuilder(event.getLevel().getName()).append(": ");

                        if (clazz != null)
                        {
                            message.append("in ").append(clazz);
                        }
                        if (function != null)
                        {
                            message.append(" at ").append(function).append(": ");
                        }

                        return message.append(event.getMessage()).append("\n").toString();
                    }
                });
            }
        }
        catch (final Exception exception)
        {
            Verbose.critical(Engine.class, "start", exception.getMessage());
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
        String verbose = null;
        if (Verbose.NONE != level)
        {
            final StringBuilder builder = new StringBuilder("");
            for (final String element : message)
            {
                builder.append(element);
            }
            verbose = builder.toString();
        }
        switch (level)
        {
            case INFORMATION:
                Verbose.LOGGER.setLevel(Level.INFO);
                Verbose.LOGGER.logp(Level.INFO, null, null, verbose, thrown);
                break;
            case WARNING:
                Verbose.LOGGER.setLevel(Level.WARNING);
                Verbose.LOGGER.logp(Level.WARNING, clazz.getSimpleName(), function, verbose, thrown);
                break;
            case CRITICAL:
                Verbose.LOGGER.setLevel(Level.SEVERE);
                Verbose.LOGGER.logp(Level.SEVERE, clazz.getSimpleName(), function, verbose, thrown);
                break;
            default:
                throw new LionEngineException("Unknown level: " + level);
        }
    }
}
