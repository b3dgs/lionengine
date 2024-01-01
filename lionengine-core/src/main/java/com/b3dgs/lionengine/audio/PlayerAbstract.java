/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilStream;

/**
 * Abstract player base implementation.
 */
public abstract class PlayerAbstract implements Audio
{
    /** Minimum volume value. */
    public static final int VOLUME_MIN = 0;
    /** Maximum volume value. */
    public static final int VOLUME_MAX = 100;
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerAbstract.class);

    /**
     * Extract music from jar to temp file.
     * 
     * @param media The music media.
     * @return The path of temp file.
     */
    private static String extractFromJar(Media media)
    {
        if (media.isJar())
        {
            final File file = UtilStream.getCopy(media);
            return file.getAbsolutePath();
        }
        return media.getFile().getAbsolutePath();
    }

    /** Media reference. */
    private final Media media;
    /** Music cache. */
    private String cache;

    /**
     * Internal constructor.
     * 
     * @param media The media to play (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    protected PlayerAbstract(Media media)
    {
        super();

        Check.notNull(media);

        this.media = media;
    }

    /**
     * Play the track.
     * 
     * @param track The track path.
     * @param name The track name.
     */
    private void play(String track, String name)
    {
        LOGGER.info("Playing track: {}", name);
        play(track);
    }

    /**
     * Play the track.
     * 
     * @param track The track path.
     */
    protected abstract void play(String track);

    @Override
    public void play()
    {
        final String name = media.getPath();
        if (cache == null)
        {
            cache = extractFromJar(media);
        }
        play(cache, name);
    }
}
