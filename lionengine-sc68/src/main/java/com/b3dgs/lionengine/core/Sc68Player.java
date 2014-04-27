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
package com.b3dgs.lionengine.core;

import java.io.File;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.UtilityFile;

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
     * Constructor.
     * 
     * @param binding The binding reference.
     */
    Sc68Player(Sc68Binding binding)
    {
        Check.notNull(binding, "SC68 binding must not be null !");
        this.binding = binding;
    }

    /*
     * Sc68
     */

    @Override
    public void play(Media media)
    {
        Check.notNull(media);
        final String str = media.getPath().replace(Core.MEDIA.getSeparator().charAt(0), ';');
        final String[] slp = str.split(";");
        final String n = slp[slp.length - 1];
        final String file = UtilityFile.getPath(UtilityFile.getTempDir(), n);
        final File music;
        if (!UtilityFile.exists(file))
        {
            music = AudioSc68.getFile(file, media.getInputStream());
        }
        else
        {
            music = new File(file);
        }
        binding.Sc68Play(music.getPath());
    }

    @Override
    public void setVolume(int volume)
    {
        Check.argument(volume >= 0 && volume <= 100, "Wrong volume value !");
        binding.Sc68SetVolume(volume);
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
