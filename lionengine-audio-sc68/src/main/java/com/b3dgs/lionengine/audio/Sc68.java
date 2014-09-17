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
package com.b3dgs.lionengine.audio;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;

/**
 * Allows to play SonicArranger musics (original Amiga player).
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Sc68 sc68 = AudioSc68.createSc68Player();
 * sc68.setVolume(25);
 * sc68.play(Media.get(&quot;music.sc68&quot;));
 * 
 * Thread.sleep(1000);
 * sc68.pause();
 * Thread.sleep(500);
 * sc68.setVolume(75);
 * sc68.resume();
 * Thread.sleep(1000);
 * Assert.assertTrue(sc68.seek() &gt;= 0);
 * 
 * sc68.stop();
 * sc68.free();
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Sc68
{
    /**
     * Play a music from its id, previously loaded.
     * 
     * @param media The music media.
     * @throws LionEngineException If media is <code>null</code>
     */
    void play(Media media) throws LionEngineException;

    /**
     * Set player volume (between 0 and 100, as a percent).
     * 
     * @param volume The music volume [0-100].
     * @throws LionEngineException If argument is invalid.
     */
    void setVolume(int volume) throws LionEngineException;

    /**
     * Pause a playing music.
     */
    void pause();

    /**
     * Continue to play music after being paused.
     */
    void resume();

    /**
     * Stop a music.
     */
    void stop();

    /**
     * Get music progress counter.
     * 
     * @return The music progress counter.
     */
    int seek();
}
