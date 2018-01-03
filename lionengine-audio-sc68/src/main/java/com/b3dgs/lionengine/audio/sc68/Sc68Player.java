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
package com.b3dgs.lionengine.audio.sc68;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.util.UtilConversion;
import com.b3dgs.lionengine.util.UtilStream;

/**
 * SC68 player implementation.
 */
final class Sc68Player implements Sc68
{
    /** Info playing. */
    private static final String INFO_PLAYING = "Playing SC68 track: ";

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
    /** Binding reference. */
    private final Sc68Binding binding;
    /** Music cache. */
    private String cache;

    /**
     * Internal constructor.
     * 
     * @param media The media to play.
     * @param binding The binding reference.
     * @throws LionEngineException If arguments are <code>null</code>
     */
    Sc68Player(Media media, Sc68Binding binding)
    {
        Check.notNull(media);
        Check.notNull(binding);

        this.media = media;
        this.binding = binding;
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
        binding.Sc68Play(track);
    }

    /*
     * Sc68
     */

    @Override
    public void play()
    {
        play(Align.CENTER, true);
    }

    @Override
    public void play(Align alignment, boolean loop)
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

    @Override
    public void setStart(long tick)
    {
        // Nothing to do
    }

    @Override
    public void setLoop(long first, long last)
    {
        // Nothing to do
    }

    @Override
    public void setVolume(int volume)
    {
        Check.superiorOrEqual(volume, 0);
        Check.inferiorOrEqual(volume, VOLUME_MAX);

        binding.Sc68SetVolume(volume);
    }

    @Override
    public void setConfig(boolean interpolation, boolean joinStereo)
    {
        binding.Sc68Config(UtilConversion.boolToInt(interpolation), UtilConversion.boolToInt(joinStereo));
    }

    @Override
    public void pause()
    {
        binding.Sc68Pause();
    }

    @Override
    public void resume()
    {
        binding.Sc68Resume();
    }

    @Override
    public void stop()
    {
        binding.Sc68Stop();
    }

    @Override
    public long getTicks()
    {
        return binding.Sc68Seek();
    }
}
