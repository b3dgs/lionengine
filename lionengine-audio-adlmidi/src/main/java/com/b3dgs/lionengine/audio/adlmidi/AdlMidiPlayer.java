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
package com.b3dgs.lionengine.audio.adlmidi;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.AbstractPlayer;

/**
 * AdlMidi player implementation.
 */
final class AdlMidiPlayer extends AbstractPlayer implements AdlMidi
{
    /** Binding reference. */
    private final AdlMidiBinding binding;

    /**
     * Internal constructor.
     * 
     * @param media The media to play.
     * @param binding The binding reference.
     * @throws LionEngineException If arguments are <code>null</code>
     */
    AdlMidiPlayer(Media media, AdlMidiBinding binding)
    {
        super(media);

        Check.notNull(binding);

        this.binding = binding;
    }

    /*
     * AdlMidi
     */

    @Override
    protected void play(String track)
    {
        binding.adlPlay(track);
    }

    @Override
    public void setVolume(int volume)
    {
        Check.superiorOrEqual(volume, 0);
        Check.inferiorOrEqual(volume, VOLUME_MAX);

        binding.adlSetVolume(volume);
    }

    @Override
    public void setBank(int bank)
    {
        binding.adlSetBank(bank);
    }

    @Override
    public void pause()
    {
        binding.adlPause();
    }

    @Override
    public void resume()
    {
        binding.adlResume();
    }

    @Override
    public void stop()
    {
        binding.adlStop();
    }

    @Override
    public long getTicks()
    {
        return binding.adlSeek();
    }
}
