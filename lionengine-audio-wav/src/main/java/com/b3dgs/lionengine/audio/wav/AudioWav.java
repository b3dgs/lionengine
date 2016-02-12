/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio.wav;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Wav factory. Allows to create wav player.
 */
public final class AudioWav
{
    /** Channels handler. */
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactory()
    {
        @Override
        public Thread newThread(Runnable runnable)
        {
            return new Thread(runnable, AudioWav.class.getSimpleName());
        }
    });

    /**
     * Load a sound file <code>(.wav)</code>.
     * Don't forget to call {@link #terminate()} once program is finished.
     * 
     * @param media The audio sound media.
     * @return The loaded sound.
     * @throws LionEngineException If media is <code>null</code>
     */
    public static Wav loadWav(Media media)
    {
        return new WavImpl(EXECUTOR, media);
    }

    /**
     * Terminate all audio task definitely. Must be called when program ends.
     */
    public static void terminate()
    {
        EXECUTOR.shutdownNow();
    }

    /**
     * Private constructor.
     */
    private AudioWav()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
