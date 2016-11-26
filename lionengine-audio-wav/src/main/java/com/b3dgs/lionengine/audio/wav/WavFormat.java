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

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.sound.sampled.Mixer;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.AudioFormat;

/**
 * Wav audio format implementation.
 */
public final class WavFormat implements AudioFormat<Wav>
{
    /** Channels handler. */
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactory()
    {
        @Override
        public Thread newThread(Runnable runnable)
        {
            return new Thread(runnable, WavFormat.class.getSimpleName());
        }
    });

    /** Audio extensions. */
    private static final String[] FORMATS =
    {
        "wav", "wave"
    };

    /** Custom mixer, <code>null</code> for default. */
    static volatile Mixer.Info mixer;

    /**
     * Set the mixer to use.
     * 
     * @param mixer The mixer to use.
     */
    public static void setMixer(Mixer.Info mixer)
    {
        WavFormat.mixer = mixer;
    }

    /**
     * Create a wav format.
     */
    public WavFormat()
    {
        super();
    }

    /*
     * AudioFormat
     */

    @Override
    public Wav loadAudio(Media media)
    {
        return new WavImpl(EXECUTOR, media);
    }

    @Override
    public Collection<String> getFormats()
    {
        return Arrays.asList(FORMATS);
    }

    @Override
    public void close()
    {
        EXECUTOR.shutdownNow();
    }
}
