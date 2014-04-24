/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Special engine exception implementation which limit the trace to the user side.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class LionEngineException
        extends RuntimeException
{
    /** Activate the ignore flag. */
    private static final boolean IGNORE_ENGINE_TRACE = true;
    /** The main ignored package. */
    private static final String IGNORE = "com.b3dgs.lionengine.";
    /** The number of ignored characters. */
    private static final int IGNORE_SIZE = LionEngineException.IGNORE.length();
    /** The list of ignored sub packages and main class. */
    private static final String[] IGNORED =
    {
            "anim", "core", "drawable", "file", "game", "utility", "network", "audio", "swing", "Align", "Applet",
            "Architecture", "Check", "Checksum", "ColorRgba", "Coord", "Filter", "GradientColor", "Graphic",
            "ImageInfo", "Keyboard", "Line", "LionEngineException", "Mouse", "OperatingSystem", "Polygon", "Ratio",
            "Rectangle", "Resolution", "Strings", "Text", "TestStyle", "Timing", "Transparency", "UtilityConversion",
            "UtilityFile", "UtilityProjectsStats", "UtilityRandom"
    };
    /** Uid. */
    private static final long serialVersionUID = 5387489108947599463L;

    /**
     * Get the filtered stack traces.
     * 
     * @param allTrace All traces.
     * @return Filtered traces.
     */
    private static StackTraceElement[] getFilteredTraces(StackTraceElement[] allTrace)
    {
        final List<StackTraceElement> neededTrace = new ArrayList<>(4);
        for (final StackTraceElement element : allTrace)
        {
            final String className = element.getClassName();

            // Ignored package
            boolean add = true;
            if (LionEngineException.IGNORE_ENGINE_TRACE)
            {
                if (className.startsWith("sun.reflect") || className.startsWith("java.lang.reflect"))
                {
                    add = false;
                }
                if (className.startsWith(LionEngineException.IGNORE))
                {
                    final String pack = className.substring(LionEngineException.IGNORE_SIZE);
                    for (final String ignore : LionEngineException.IGNORED)
                    {
                        // Ignored sub package
                        if (pack.startsWith(ignore))
                        {
                            add = false;
                            break;
                        }
                    }
                }
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
     * @param exception The exception reference.
     * @param messages The exception message(s).
     */
    public LionEngineException(Throwable exception, String... messages)
    {
        this(exception, null, messages);
    }

    /**
     * Constructor.
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

        Throwable current = exception;
        final Set<StackTraceElement> traces = new HashSet<>(1);
        for (final StackTraceElement element : LionEngineException.getFilteredTraces(getStackTrace()))
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
            final StackTraceElement[] elements = LionEngineException.getFilteredTraces(current.getStackTrace());
            for (final StackTraceElement element : elements)
            {
                traces.add(element);
            }
            current = current.getCause();
        }
        message = buffer.toString();
        reason = exception;
        stack = new StackTraceElement[traces.size()];
        traces.toArray(stack);
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
                final boolean display = true;
                if (display)
                {
                    if (first)
                    {
                        final String reasonDesc;
                        if (reason != null)
                        {
                            reasonDesc = "\n\tReason: " + reason;
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
