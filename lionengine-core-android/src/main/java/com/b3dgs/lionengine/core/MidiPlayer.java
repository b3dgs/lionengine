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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.audio.Midi;

/**
 * Default midi implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class MidiPlayer
        implements Midi
{
    /** Total ticks. */
    private final long ticks;
    /** Paused flag. */
    private boolean paused;

    /**
     * Constructor.
     * 
     * @param media The media midi to play.
     */
    MidiPlayer(Media media)
    {
        ticks = 0;
        paused = false;
        throw new LionEngineException("Not supported !");
    }

    /*
     * Midi
     */

    @Override
    public void play(boolean loop)
    {
        throw new LionEngineException("Not supported !");
    }

    @Override
    public void setStart(long tick)
    {
        Check.argument(tick >= 0 && tick <= ticks, "Wrong tick value: ", String.valueOf(tick), " (total = )",
                String.valueOf(ticks));

        throw new LionEngineException("Not supported !");
    }

    @Override
    public void setLoop(long first, long last)
    {
        Check.argument(first >= 0 && first <= last, "Wrong first value: ", String.valueOf(first), " (total = )",
                String.valueOf(ticks));
        Check.argument(last <= ticks, "Wrong last value: ", String.valueOf(last), " (total = )", String.valueOf(ticks));

        throw new LionEngineException("Not supported !");
    }

    @Override
    public void setVolume(final int volume)
    {
        Check.argument(volume >= Midi.VOLUME_MIN && volume <= Midi.VOLUME_MAX, "Wrong volume value: ",
                String.valueOf(volume), " [" + Midi.VOLUME_MIN + "-" + Midi.VOLUME_MAX + "]");

        throw new LionEngineException("Not supported !");
    }

    @Override
    public long getTicks()
    {
        throw new LionEngineException("Not supported !");
    }

    @Override
    public void stop()
    {
        throw new LionEngineException("Not supported !");
    }

    @Override
    public void pause()
    {
        if (!paused)
        {
            paused = true;
        }
    }

    @Override
    public void resume()
    {
        if (paused)
        {
            paused = false;
        }
    }
}
