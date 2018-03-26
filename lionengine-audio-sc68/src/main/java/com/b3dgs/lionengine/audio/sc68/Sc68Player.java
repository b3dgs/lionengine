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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.PlayerAbstract;
import com.b3dgs.lionengine.util.UtilConversion;

/**
 * Sc68 player implementation.
 */
final class Sc68Player extends PlayerAbstract implements Sc68
{
    /** Binding reference. */
    private final Sc68Binding binding;

    /**
     * Internal constructor.
     * 
     * @param media The media to play.
     * @param binding The binding reference.
     * @throws LionEngineException If arguments are <code>null</code>
     */
    Sc68Player(Media media, Sc68Binding binding)
    {
        super(media);

        Check.notNull(binding);

        this.binding = binding;
    }

    /*
     * Sc68
     */

    @Override
    protected void play(String track)
    {
        binding.sc68Play(track);
    }

    @Override
    public void setStart(long tick)
    {
        binding.sc68SetStart((int) tick);
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

        binding.sc68SetVolume(volume);
    }

    @Override
    public void setConfig(boolean interpolation, boolean joinStereo)
    {
        binding.sc68Config(UtilConversion.boolToInt(interpolation), UtilConversion.boolToInt(joinStereo));
    }

    @Override
    public void pause()
    {
        binding.sc68Pause();
    }

    @Override
    public void resume()
    {
        binding.sc68Resume();
    }

    @Override
    public void stop()
    {
        binding.sc68Stop();
    }

    @Override
    public long getTicks()
    {
        return binding.sc68Seek();
    }
}
