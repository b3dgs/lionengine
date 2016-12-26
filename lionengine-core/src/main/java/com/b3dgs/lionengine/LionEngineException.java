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

/**
 * Special engine exception implementation which limit the trace to the user side.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class LionEngineException extends RuntimeException
{
    /** Error private constructor. */
    public static final String ERROR_PRIVATE_CONSTRUCTOR = "Private constructor !";
    /** Error unknown enum type. */
    private static final String ERROR_UNKNOWN_ENUM = "Unknown enum: ";
    /** Null enum. */
    private static final String NULL_ENUM = "null";
    /** Uid. */
    private static final long serialVersionUID = 5387489108947599464L;

    /**
     * Get formatted message with media.
     * 
     * @param media The media reference.
     * @param messages The messages to concatenate.
     * @return The formatted message.
     */
    private static String getMessage(Media media, String... messages)
    {
        final StringBuilder builder = new StringBuilder();
        if (media != null)
        {
            builder.append('[').append(media.getPath()).append("] ");
        }
        for (final String message : messages)
        {
            builder.append(message);
        }
        return builder.toString();
    }

    /**
     * Get the enum name.
     * 
     * @param type The enum type.
     * @return The enum name, {@link #NULL_ENUM} if type is <code>null</code>.
     */
    private static String getEnumName(Enum<?> type)
    {
        if (type == null)
        {
            return NULL_ENUM;
        }
        return type.name();
    }

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
     * Create an exception related to an unknown enum type.
     * 
     * @param type The unknown enum type.
     */
    public LionEngineException(Enum<?> type)
    {
        this(ERROR_UNKNOWN_ENUM, getEnumName(type));
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
        super(getMessage(media, messages), exception);
    }
}
