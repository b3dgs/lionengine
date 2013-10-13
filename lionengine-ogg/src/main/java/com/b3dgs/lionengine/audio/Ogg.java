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

/**
 * Handle music routine. A music is an heavy sound, designed to be played once (loop or not).
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Ogg ogg = AudioOgg.loadOgg(Media.get(&quot;music.ogg&quot;));
 * ogg.setVolume(100);
 * ogg.play(false);
 * 
 * Thread.sleep(2000);
 * ogg.stop();
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Ogg
{
    /** Minimum volume value. */
    int VOLUME_MIN = 0;
    /** Maximum volume value. */
    int VOLUME_MAX = 100;

    /**
     * Play music. The music will be played until the end. In case of a loop, music will be played in loop. Music are
     * played in a separated thread.
     * 
     * @param repeat The loop flag.
     */
    void play(boolean repeat);

    /**
     * Set the sound volume.
     * 
     * @param volume The volume in percent <code>[{@link #VOLUME_MIN} - {@link #VOLUME_MAX}]</code>.
     */
    void setVolume(int volume);

    /**
     * Stop music. The music will be stopped, but not deleted.
     */
    void stop();
}
