/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilFile;

/**
 * Allows to create audio player depending on format.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class AudioFactory
{
    /** Unknown audio format. */
    public static final String ERROR_FORMAT = "Unsupported audio format: ";
    /** Format already exists. */
    static final String ERROR_EXISTS = "Format already exists: ";
    /** Factories by audio format. */
    private static final Map<String, AudioFormat> FACTORIES = new HashMap<>();
    /** General volume. */
    private static int volume = Constant.HUNDRED;

    /**
     * Load an audio file and prepare it to be played.
     * 
     * @param media The audio media (must not be <code>null</code>).
     * @return The loaded audio.
     * @throws LionEngineException If invalid audio.
     */
    public static synchronized Audio loadAudio(Media media)
    {
        Check.notNull(media);

        final String extension = UtilFile.getExtension(media.getPath());
        return Optional.ofNullable(FACTORIES.get(extension))
                       .orElseThrow(() -> new LionEngineException(media, ERROR_FORMAT))
                       .loadAudio(media);
    }

    /**
     * Load an audio file and prepare it to be played.
     * 
     * @param <A> The audio type.
     * @param media The audio media (must not be <code>null</code>).
     * @param type The expected audio type (must not be <code>null</code>).
     * @return The loaded audio.
     * @throws LionEngineException If invalid arguments or invalid audio.
     */
    public static synchronized <A extends Audio> A loadAudio(Media media, Class<A> type)
    {
        Check.notNull(media);
        Check.notNull(type);

        final String extension = UtilFile.getExtension(media.getPath());
        try
        {
            return type.cast(Optional.ofNullable(FACTORIES.get(extension))
                                     .orElseThrow(() -> new LionEngineException(media, ERROR_FORMAT))
                                     .loadAudio(media));
        }
        catch (final ClassCastException exception)
        {
            throw new LionEngineException(exception, media, ERROR_FORMAT);
        }
    }

    /**
     * Add a supported audio format.
     * 
     * @param format The supported format (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument or format already exists.
     */
    public static synchronized void addFormat(AudioFormat format)
    {
        Check.notNull(format);

        for (final String current : format.getFormats())
        {
            if (FACTORIES.put(current, format) != null)
            {
                throw new LionEngineException(ERROR_EXISTS + current);
            }
        }
    }

    /**
     * Clear all supported audio formats.
     */
    public static synchronized void clearFormats()
    {
        FACTORIES.forEach((extension, format) -> format.close());
        FACTORIES.clear();
    }

    /**
     * Set general volume.
     * 
     * @param volume The general volume [0-100].
     */
    public static synchronized void setVolume(int volume)
    {
        Check.superiorOrEqual(volume, 0);
        Check.inferiorOrEqual(volume, Constant.HUNDRED);

        AudioFactory.volume = volume;
    }

    /**
     * Get general volume.
     * 
     * @return The general volume.
     */
    public static synchronized int getVolume()
    {
        return volume;
    }

    /**
     * Private constructor.
     */
    private AudioFactory()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
