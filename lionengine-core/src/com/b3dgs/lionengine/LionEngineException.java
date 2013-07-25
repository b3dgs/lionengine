package com.b3dgs.lionengine;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Special engine exception implementation which limit the trace to the user side.
 */
public final class LionEngineException
        extends RuntimeException
{
    /**
     * Constants list.
     */
    private static class Constants
    {
        /** The main ignored package. */
        static final String IGNORE = "com.b3dgs.lionengine.";
        /** The number of ignored characters. */
        static final int IGNORE_SIZE = Constants.IGNORE.length();
        /** The list of ignored sub packages and main class. */
        static final String[] IGNORED =
        {
                "Engine", "Drawable", "File", "Audio", "Input", "Network", "game", "audio", "drawable", "engine",
                "file", "geometry", "input", "network", "utility", "window"
        };
    }

    /** Uid. */
    private static final long serialVersionUID = 5387489108947599463L;
    /** The message. */
    private final String message;
    /** The reason. */
    private final Throwable reason;
    /** Stack trace. */
    private final StackTraceElement[] stack;

    /**
     * Constructor.
     * 
     * @param messages The exception message(s).
     */
    public LionEngineException(String... messages)
    {
        this(null, null, messages);
    }

    /**
     * Constructor.
     * 
     * @param messages The exception message(s).
     * @param media The media error source.
     */
    public LionEngineException(Media media, String... messages)
    {
        this(null, media, messages);
    }

    /**
     * Constructor.
     * 
     * @param throwable The throwable reference.
     * @param messages The exception message(s).
     */
    public LionEngineException(Throwable throwable, String... messages)
    {
        this(throwable, null, messages);
    }

    /**
     * Constructor.
     * 
     * @param throwable The throwable reference.
     * @param media The media error source.
     * @param messages The exception message(s).
     */
    public LionEngineException(Throwable throwable, Media media, String... messages)
    {
        super();
        final StringBuilder buffer = new StringBuilder(16);
        if (media != null && media.getPath() != null)
        {
            buffer.append("[").append(media.getPath()).append("] ");
        }
        for (final String m : messages)
        {
            buffer.append(m);
        }
        message = buffer.toString();
        reason = throwable;
        stack = throwable != null ? throwable.getStackTrace() : new StackTraceElement[] {};
    }

    @Override
    public void printStackTrace(PrintStream stream)
    {
        synchronized (stream)
        {
            stream.print(this.getClass().getSimpleName());
            final StackTraceElement[] trace = getStackTrace();
            final int size = trace.length + stack.length;

            final StackTraceElement[] allTrace = new StackTraceElement[size];

            // Get all traces
            int j = 0;
            for (final StackTraceElement element : stack)
            {
                allTrace[j] = element;
                j++;
            }
            for (final StackTraceElement element : trace)
            {
                allTrace[j] = element;
                j++;
            }

            // Filter traces
            final List<StackTraceElement> neededTrace = new ArrayList<>(4);
            for (final StackTraceElement element : allTrace)
            {
                final String className = element.getClassName();

                // Ignored package
                boolean add = true;
                if (className.startsWith(Constants.IGNORE))
                {
                    final String pack = className.substring(Constants.IGNORE_SIZE);
                    for (final String ignore : Constants.IGNORED)
                    {
                        // Ignored sub package
                        if (pack.startsWith(ignore))
                        {
                            add = false;
                            break;
                        }
                    }
                }
                if (add)
                {
                    neededTrace.add(element);
                }
            }

            // Display traces
            boolean first = true;
            for (final StackTraceElement element : neededTrace)
            {
                final boolean display = true;
                if (display)
                {
                    if (first)
                    {
                        stream.println(": " + message + "\n\tReason: " + reason + "\n\tat " + element);
                    }
                    else
                    {
                        stream.println("\tat " + element);
                    }
                }
                first = false;
            }
        }
    }

    @Override
    public String getMessage()
    {
        return message;
    }
}
