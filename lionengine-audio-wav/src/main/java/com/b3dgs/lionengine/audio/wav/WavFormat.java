/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.audio.wav;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.Mixer;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.audio.AudioFormat;

/**
 * Wav audio format implementation.
 */
public final class WavFormat implements AudioFormat
{
    /** Channels handler. */
    private static final ExecutorService EXECUTOR;
    /** Audio extensions. */
    private static final Collection<String> FORMATS = Collections.unmodifiableCollection(Arrays.asList("wav", "wave"));

    /** Custom mixer, <code>null</code> for default. */
    static volatile Mixer.Info mixer;

    /**
     * Init.
     */
    static
    {
        EXECUTOR = Executors.newCachedThreadPool(runnable -> new Thread(runnable, WavFormat.class.getSimpleName()));
    }

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
        return FORMATS;
    }

    @Override
    public void close()
    {
        EXECUTOR.shutdownNow();
    }
}
