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
package com.b3dgs.lionengine;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import com.b3dgs.lionengine.core.Media;

/**
 * Special engine exception implementation which limit the trace to the user side.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class LionEngineException
        extends RuntimeException
{
    /** Error private constructor. */
    public static final String ERROR_PRIVATE_CONSTRUCTOR = "Private constructor !";
    /** The main ignored package. */
    private static final String IGNORE = "com.b3dgs.lionengine.";
    /** The number of ignored characters. */
    private static final int IGNORE_SIZE = IGNORE.length();
    /** The list of ignored sub packages and main class. */
    private static final String[] IGNORED =
    {
            "audio", "anim", "core", "drawable", "geom", "stream", "game", "trait", "component", "handler", "factory",
            "network", "utility", "editor", "xsd", "Align", "Architecture", "Check", "Checksum", "ColorGradient",
            "ColorRgba", "Config", "Filter", "Hq2x", "Hq3x", "ImageInfo", "LionEngineException", "OperatingSystem",
            "Ratio", "Resolution", "Strings", "TextStyle", "Timing", "Transparency", "UtilConversion", "UtilFile",
            "UtilMath", "UtilProjectStats", "UtilRandom", "Version"
    };
    /** Uid. */
    private static final long serialVersionUID = 5387489108947599464L;
    /** Activate the ignore flag. */
    private static volatile boolean ignoreEngineTrace = true;

    /**
     * Set the engine trace ignore flag.
     * 
     * @param ignore <code>true</code> to ignore in depth engine trace, <code>false</code> to show full trace.
     */
    public static void setIgnoreEngineTrace(boolean ignore)
    {
        ignoreEngineTrace = ignore;
    }

    /**
     * Revert the stack trace array.
     * 
     * @param current The current stack trace.
     * @return The reverted stack.
     */
    private static StackTraceElement[] revertStack(StackTraceElement[] current)
    {
        final StackTraceElement[] reverted = new StackTraceElement[current.length];
        int i = 0;
        for (final StackTraceElement element : current)
        {
            reverted[reverted.length - i - 1] = element;
            i++;
        }
        return reverted;
    }

    /**
     * Check if ignore engine trace depending of the class name source.
     * 
     * @param className The class name.
     * @return <code>true</code> if ignore trace, <code>false</code> else.
     */
    private static boolean checkIgnoreEngineTrace(String className)
    {
        if (className.startsWith("sun.") || className.startsWith("java.lang") || className.startsWith("java.net")
                || className.startsWith("java.security"))
        {
            return false;
        }
        if (className.startsWith(IGNORE))
        {
            final String pack = className.substring(IGNORE_SIZE);
            for (final String ignore : IGNORED)
            {
                // Ignored sub package
                if (pack.startsWith(ignore))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Get the filtered stack traces.
     * 
     * @param allTrace All traces.
     * @return Filtered traces.
     */
    private static StackTraceElement[] getFilteredTraces(StackTraceElement[] allTrace)
    {
        final Collection<StackTraceElement> neededTrace = new ArrayList<>(4);
        for (final StackTraceElement element : allTrace)
        {
            final String className = element.getClassName();

            // Ignored package
            final boolean add;
            if (ignoreEngineTrace)
            {
                add = checkIgnoreEngineTrace(className);
            }
            else
            {
                add = true;
            }
            if (add)
            {
                if (!neededTrace.contains(element))
                {
                    neededTrace.add(element);
                }
            }
        }
        return neededTrace.toArray(new StackTraceElement[neededTrace.size()]);
    }

    /** The message. */
    private final String message;
    /** The reason. */
    private final Throwable reason;
    /** Stack trace. */
    private final StackTraceElement[] stack;

    /**
     * Create an exception with messages if has.
     * 
     * @param messages The exception message(s).
     */
    public LionEngineException(String... messages)
    {
        this(null, null, messages);
    }

    /**
     * Create an exception related to a media and messages if has.
     * 
     * @param messages The exception message(s).
     * @param media The media error source.
     */
    public LionEngineException(Media media, String... messages)
    {
        this(null, media, messages);
    }

    /**
     * Create an exception related to another exception and messages if has.
     * 
     * @param exception The exception reference.
     * @param messages The exception message(s).
     */
    public LionEngineException(Throwable exception, String... messages)
    {
        this(exception, null, messages);
    }

    /**
     * Create an exception related to an existing exception and a media, plus additional messages if has.
     * 
     * @param exception The exception reference.
     * @param media The media error source.
     * @param messages The exception message(s).
     */
    public LionEngineException(Throwable exception, Media media, String... messages)
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
        reason = exception;

        if (reason instanceof LionEngineException)
        {
            stack = getFilteredStackTrace(exception, buffer);
        }
        else
        {
            stack = revertStack(getFilteredStackTrace(exception, buffer));
        }
        setStackTrace(stack);
    }

    /**
     * Get the filtered stack traces.
     * 
     * @param exception The exception reference.
     * @param buffer The buffer builder.
     * @return The filtered stack.
     */
    private StackTraceElement[] getFilteredStackTrace(Throwable exception, StringBuilder buffer)
    {
        Throwable current = exception;
        final LinkedList<StackTraceElement> traces = new LinkedList<>();
        for (final StackTraceElement element : getFilteredTraces(getStackTrace()))
        {
            traces.add(element);
        }
        while (current != null)
        {
            final String message = current.getMessage();
            if (message != null)
            {
                buffer.append("\n\t\t").append(current.getMessage());
            }
            final StackTraceElement[] elements = getFilteredTraces(current.getStackTrace());
            for (final StackTraceElement element : elements)
            {
                traces.addFirst(element);
            }
            current = current.getCause();
        }
        final StackTraceElement[] stacks = new StackTraceElement[traces.size()];
        for (int i = 0; i < stacks.length; i++)
        {
            stacks[i] = traces.get(i);
        }
        return getFilteredTraces(stacks);
    }

    /*
     * RuntimeException
     */

    @Override
    public void printStackTrace(PrintStream stream)
    {
        synchronized (stream)
        {
            stream.print(getClass().getSimpleName());

            // Display traces
            boolean first = true;
            for (final StackTraceElement element : stack)
            {
                if (first)
                {
                    final String reasonDesc;
                    if (reason != null)
                    {
                        Throwable current = reason;
                        Throwable last = current;
                        while (current != null)
                        {
                            last = current;
                            current = current.getCause();
                        }
                        reasonDesc = "\n\tReason: " + last;
                    }
                    else
                    {
                        reasonDesc = "";
                    }
                    stream.println(": " + message + reasonDesc + "\n\tat " + element);
                }
                else
                {
                    stream.println("\tat " + element);
                }
                first = false;
            }
        }
    }

    @Override
    public void printStackTrace(PrintWriter writer)
    {
        synchronized (writer)
        {
            writer.print(getClass().getSimpleName());

            // Display traces
            boolean first = true;
            for (final StackTraceElement element : stack)
            {
                if (first)
                {
                    final String reasonDesc;
                    if (reason != null)
                    {
                        Throwable current = reason;
                        Throwable last = current;
                        while (current != null)
                        {
                            last = current;
                            current = current.getCause();
                        }
                        reasonDesc = "\n\tReason: " + last;
                    }
                    else
                    {
                        reasonDesc = "";
                    }
                    writer.println(": " + message + reasonDesc + "\n\tat " + element);
                }
                else
                {
                    writer.println("\tat " + element);
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
