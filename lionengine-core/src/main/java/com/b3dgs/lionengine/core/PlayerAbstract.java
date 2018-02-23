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
package com.b3dgs.lionengine.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.util.UtilStream;

/**
 * Abstract player base implementation.
 */
public abstract class PlayerAbstract implements Audio
{
    /** Minimum volume value. */
    public static final int VOLUME_MIN = 0;
    /** Maximum volume value. */
    public static final int VOLUME_MAX = 100;
    /** Info playing. */
    private static final String INFO_PLAYING = "Playing track: ";

    /**
     * Extract music from jar to temp file.
     * 
     * @param media The music media.
     * @return The path of temp file.
     */
    private static String extractFromJar(Media media)
    {
        try (InputStream input = media.getInputStream())
        {
            final File file = UtilStream.getCopy(media.getFile().getName(), input);
            return file.getAbsolutePath();
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /** Media reference. */
    private final Media media;
    /** Music cache. */
    private String cache;

    /**
     * Internal constructor.
     * 
     * @param media The media to play.
     * @throws LionEngineException If arguments are <code>null</code>
     */
    protected PlayerAbstract(Media media)
    {
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
        Verbose.info(INFO_PLAYING, name);
        play(track);
    }

    /**
     * Play the track.
     * 
     * @param track The track path.
     */
    protected abstract void play(String track);

    /*
     * Audio
     */

    @Override
    public void play()
    {
        final String name = media.getPath();
        if (Medias.getResourcesLoader() != null)
        {
            if (cache == null)
            {
                cache = extractFromJar(media);
            }
            play(cache, name);
        }
        else
        {
            play(media.getFile().getAbsolutePath(), name);
        }
    }
}
