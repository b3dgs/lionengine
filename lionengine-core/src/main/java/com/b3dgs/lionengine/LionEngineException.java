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

/**
 * Engine exception implementation.
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
     * @param media The media reference (must not be <code>null</code>).
     * @return The formatted message.
     * @throws LionEngineException If invalid argument.
     */
    private static String getMessage(Media media)
    {
        Check.notNull(media);

        return new StringBuilder(Constant.BYTE_4).append('[').append(media.getPath()).append("] ").toString();
    }

    /**
     * Get formatted message with media.
     * 
     * @param media The media reference (must not be <code>null</code>).
     * @param message The message to concatenate (must not be <code>null</code>).
     * @return The formatted message.
     * @throws LionEngineException If invalid arguments.
     */
    private static String getMessage(Media media, String message)
    {
        Check.notNull(message);

        return new StringBuilder(getMessage(media)).append(message).toString();
    }

    /**
     * Get the enum name.
     * 
     * @param type The enum type (can be <code>null</code>).
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
     * Create an exception with message.
     * 
     * @param message The exception message.
     */
    public LionEngineException(String message)
    {
        super(message);
    }

    /**
     * Create an exception with media.
     * 
     * @param media The media reference (must not be <code>null</code>).
     */
    public LionEngineException(Media media)
    {
        super(getMessage(media));
    }

    /**
     * Create an exception related to a media and messages.
     * 
     * @param media The media error source (must not be <code>null</code>).
     * @param message The exception message (must not be <code>null</code>).
     */
    public LionEngineException(Media media, String message)
    {
        super(getMessage(media, message));
    }

    /**
     * Create an exception related to an unknown enum type.
     * 
     * @param type The unknown enum type (can be <code>null</code>).
     */
    public LionEngineException(Enum<?> type)
    {
        super(ERROR_UNKNOWN_ENUM + getEnumName(type));
    }

    /**
     * Create an exception related to another exception.
     * 
     * @param exception The exception cause (<code>null</code> if none).
     */
    public LionEngineException(Throwable exception)
    {
        super(exception);
    }

    /**
     * Create an exception related to another exception and message.
     * 
     * @param exception The exception reference (<code>null</code> if none).
     * @param message The exception message (can be <code>null</code>).
     */
    public LionEngineException(Throwable exception, String message)
    {
        super(message, exception);
    }

    /**
     * Create an exception related to an existing exception and a media.
     * 
     * @param exception The exception cause (<code>null</code> if none).
     * @param media The media error source (must not be <code>null</code>).
     */
    public LionEngineException(Throwable exception, Media media)
    {
        super(getMessage(media), exception);
    }

    /**
     * Create an exception related to an existing exception and a media, plus additional message.
     * 
     * @param exception The exception cause (<code>null</code> if none).
     * @param media The media error source (must not be <code>null</code>).
     * @param message The exception message (must not be <code>null</code>).
     */
    public LionEngineException(Throwable exception, Media media, String message)
    {
        super(getMessage(media, message), exception);
    }
}
