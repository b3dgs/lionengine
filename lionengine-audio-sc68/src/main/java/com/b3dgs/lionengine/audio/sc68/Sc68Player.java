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
package com.b3dgs.lionengine.audio.sc68;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;

/**
 * SC68 player implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Sc68Player
        implements Sc68
{
    /** Binding reference. */
    private final Sc68Binding binding;

    /**
     * Internal constructor.
     * 
     * @param binding The binding reference.
     * @throws LionEngineException If binding is <code>null</code>
     */
    Sc68Player(Sc68Binding binding) throws LionEngineException
    {
        Check.notNull(binding);

        this.binding = binding;
    }

    /*
     * Sc68
     */

    @Override
    public void play(Media media) throws LionEngineException
    {
        Check.notNull(media);

        try
        {
            final File music = Files.createTempFile(null, null).toFile();
            music.deleteOnExit();
            Files.copy(media.getInputStream(), music.toPath(), StandardCopyOption.REPLACE_EXISTING);
            binding.Sc68Play(music.getPath());
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media);
        }
    }

    @Override
    public void setVolume(int volume) throws LionEngineException
    {
        Check.superiorOrEqual(volume, 0);
        Check.inferiorOrEqual(volume, 100);

        binding.Sc68SetVolume(volume);
    }

    @Override
    public void setConfig(boolean interpolation, boolean joinStereo)
    {
        binding.Sc68Config(interpolation ? 1 : 0, joinStereo ? 1 : 0);
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
