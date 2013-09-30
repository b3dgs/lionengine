/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Media;

/**
 * Ogg player implementation.
 */
class OggPlayer
        implements Ogg
{
    /** Music media. */
    private final Media media;
    /** Thread. */
    private OggRoutine routine;
    /** Volume value. */
    private int volume;

    /**
     * Constructor.
     * 
     * @param media music media.
     */
    OggPlayer(Media media)
    {
        Check.notNull(media);
        this.media = media;
        routine = null;
    }

    /*
     * Ogg
     */

    @Override
    public void setVolume(int vol)
    {
        Check.argument(vol >= Ogg.VOLUME_MIN && vol <= Ogg.VOLUME_MAX, "Wrong volume value: ", String.valueOf(vol),
                " [" + Ogg.VOLUME_MIN + "-" + Ogg.VOLUME_MAX + "]");
        volume = vol;
    }

    @Override
    public void play(boolean repeat)
    {
        if (routine == null)
        {
            routine = new OggRoutine(media, repeat);
            routine.setVolume(volume);
            routine.start();
        }
    }

    @Override
    public void stop()
    {
        if (routine != null)
        {
            routine.terminate();
            routine = null;
        }
    }
}
