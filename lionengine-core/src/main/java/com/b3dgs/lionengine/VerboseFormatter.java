/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Verbose formatter.
 */
final class VerboseFormatter extends Formatter
{
    private static final int DATE_LENGTH = 23;
    private static final int LOG_LEVEL_LENGTH = 7;
    private static final DateTimeFormatter DATE_TIME_FORMAT;
    private static final String IN = "in ";
    private static final String AT = " at ";

    static
    {
        DATE_TIME_FORMAT = new DateTimeFormatterBuilder().parseCaseInsensitive()
                                                         .append(DateTimeFormatter.ISO_LOCAL_DATE)
                                                         .appendLiteral(Constant.SPACE)
                                                         .append(DateTimeFormatter.ISO_LOCAL_TIME)
                                                         .toFormatter(Locale.ENGLISH);
    }

    /**
     * Append date.
     * 
     * @param message The message builder.
     */
    private static void appendDate(StringBuilder message)
    {
        final String date = LocalDateTime.now().format(DATE_TIME_FORMAT);
        message.append(date);
        for (int i = date.length(); i < DATE_LENGTH; i++)
        {
            message.append(Constant.SPACE);
        }
        message.append(Constant.SPACE);
    }

    /**
     * Append log level.
     * 
     * @param message The message builder.
     * @param event The log record.
     */
    private static void appendLevel(StringBuilder message, LogRecord event)
    {
        final String logLevel = event.getLevel().getName();
        for (int i = logLevel.length(); i < LOG_LEVEL_LENGTH; i++)
        {
            message.append(Constant.SPACE);
        }
        message.append(logLevel).append(Constant.DOUBLE_DOT);
    }

    /**
     * Append function location.
     * 
     * @param message The message builder.
     * @param event The log record.
     */
    private static void appendFunction(StringBuilder message, LogRecord event)
    {
        final String clazz = event.getSourceClassName();
        if (clazz != null)
        {
            message.append(IN).append(clazz);
        }

        final String function = event.getSourceMethodName();
        if (function != null)
        {
            message.append(AT).append(function).append(Constant.DOUBLE_DOT);
        }
    }

    /**
     * Append thrown exception trace.
     * 
     * @param message The message builder.
     * @param event The log record.
     */
    private static void appendThrown(StringBuilder message, LogRecord event)
    {
        final Throwable thrown = event.getThrown();
        if (thrown != null)
        {
            final StringWriter sw = new StringWriter();
            thrown.printStackTrace(new PrintWriter(sw));
            message.append(sw);
        }
    }

    /**
     * Internal constructor.
     */
    VerboseFormatter()
    {
        super();
    }

    /*
     * Formatter
     */

    @Override
    public String format(LogRecord event)
    {
        final StringBuilder message = new StringBuilder(Constant.HUNDRED);
        appendDate(message);
        appendLevel(message, event);
        appendFunction(message, event);
        message.append(event.getMessage()).append(Constant.NEW_LINE);
        appendThrown(message, event);

        return message.toString();
    }
}
