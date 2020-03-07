/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio;

import java.util.Collection;
import java.util.Collections;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Void audio format.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class AudioVoidFormat implements AudioFormat
{
    /** Formats list as read only. */
    private final Collection<String> formats;

    /**
     * Create void audio format.
     * 
     * @param formats The associated files type (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public AudioVoidFormat(Collection<String> formats)
    {
        super();

        Check.notNull(formats);

        this.formats = Collections.unmodifiableCollection(formats);
    }

    /*
     * AudioFormat
     */

    @Override
    public AudioVoid loadAudio(Media media)
    {
        return AudioVoid.INSTANCE;
    }

    @Override
    public Collection<String> getFormats()
    {
        return formats;
    }

    @Override
    public void close()
    {
        // Nothing to do
    }
}
