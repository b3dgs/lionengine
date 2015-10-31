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
package com.b3dgs.lionengine.audio.sc68;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilFile;

/**
 * SC68 player implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Sc68Player implements Sc68
{
    /** Max volume. */
    private static final int MAX_VOLUME = 100;

    /** Binding reference. */
    private final Sc68Binding binding;

    /**
     * Internal constructor.
     * 
     * @param binding The binding reference.
     * @throws LionEngineException If binding is <code>null</code>
     */
    Sc68Player(Sc68Binding binding)
    {
        Check.notNull(binding);

        this.binding = binding;
    }

    /*
     * Sc68
     */

    @Override
    public void play(Media media)
    {
        Check.notNull(media);

        final InputStream input = media.getInputStream();
        try
        {
            final File music = UtilFile.getCopy(media.getFile().getName(), input);
            binding.Sc68Play(music.getCanonicalPath());
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media);
        }
        finally
        {
            UtilFile.safeClose(input);
        }
    }

    @Override
    public void setVolume(int volume)
    {
        Check.superiorOrEqual(volume, 0);
        Check.inferiorOrEqual(volume, MAX_VOLUME);

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
    public int seek()
    {
        return binding.Sc68Seek();
    }
}
