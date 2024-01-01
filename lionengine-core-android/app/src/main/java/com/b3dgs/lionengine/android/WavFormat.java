/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.lionengine.android;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.audio.AudioFormat;

import java.util.Arrays;
import java.util.Collection;

/**
 * Wav audio format implementation.
 */
public final class WavFormat implements AudioFormat
{
    /** Audio extensions. */
    private static final String[] FORMATS =
    {
        "wav", "wave"
    };

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
        return new WavImpl(media);
    }

    @Override
    public Collection<String> getFormats()
    {
        return Arrays.asList(FORMATS);
    }

    @Override
    public void close()
    {
        // Nothing to do
    }
}
