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
package com.b3dgs.lionengine.core.android;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.audio.android.Wav;

/**
 * Wav audio implementation.
 */
final class WavImpl implements Wav
{
    /** Sound pool. */
    private final SoundPool pool = new SoundPool(1, AudioManager.USE_DEFAULT_STREAM_TYPE, 100);
    /** Sound ID. */
    private final int id;
    /** Current volume. */
    private int volume = 100;

    /**
     * Internal constructor.
     * 
     * @param media The audio sound media.
     * @throws LionEngineException If media is <code>null</code>
     */
    WavImpl(Media media)
    {
        Check.notNull(media);

        final AssetFileDescriptor d = ((MediaAndroid) media).getDescriptor();
        id = pool.load(d.getFileDescriptor(), d.getStartOffset(), d.getLength(), 0);
    }

    /**
     * Play sound.
     * 
     * @param alignment The sound alignment.
     * @param volume The volume in percent.
     */
    private void playSound(Align alignment, int volume)
    {
        final float left;
        final float right;
        switch (alignment)
        {
            case LEFT:
                left = volume / 100.0F;
                right = 0.0F;
                break;
            case CENTER:
                left = volume / 100.0F;
                right = volume / 100.0F;
                break;
            case RIGHT:
                left = 0.0F;
                right = volume / 100.0F;
                break;
            default:
                throw new LionEngineException(alignment);
        }
        pool.play(id, left, right, 0, 0, 1);
    }

    /*
     * Wav
     */

    @Override
    public void play()
    {
        play(Align.CENTER);
    }

    @Override
    public void play(Align alignment)
    {
        playSound(alignment, volume);
    }

    @Override
    public void stop()
    {
        pool.stop(id);
        pool.release();
    }

    @Override
    public void setVolume(int volume)
    {
        Check.superiorOrEqual(volume, 0);
        Check.inferiorOrEqual(volume, 100);

        this.volume = volume;
    }
}
