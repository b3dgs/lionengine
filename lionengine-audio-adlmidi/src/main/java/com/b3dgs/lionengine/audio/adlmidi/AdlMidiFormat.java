/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio.adlmidi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.AudioFormat;
import com.b3dgs.lionengine.audio.AudioVoidFormat;
import com.sun.jna.Native;

/**
 * Handle AdlMidi audio.
 */
public final class AdlMidiFormat implements AudioFormat
{
    /** Load library error. */
    public static final String ERROR_LOAD_LIBRARY = "Error on loading Library: ";
    /** Standard library name. */
    private static final String LIBRARY_NAME;
    /** Audio extensions as read only. */
    private static final Collection<String> FORMATS = Collections.unmodifiableCollection(Arrays.asList("xmi",
                                                                                                       "midi",
                                                                                                       "mdi",
                                                                                                       "rmi",
                                                                                                       "imf",
                                                                                                       "mus"));

    /**
     * Specific case to not inline for test purpose.
     */
    static
    {
        LIBRARY_NAME = "adlmidi";
    }

    /**
     * Get the library, or void format if not found.
     * 
     * @return The audio format.
     */
    public static AudioFormat getFailsafe()
    {
        try
        {
            return new AdlMidiFormat();
        }
        catch (final LionEngineException exception)
        {
            Verbose.exception(exception, ERROR_LOAD_LIBRARY);
            return new AudioVoidFormat(FORMATS);
        }
    }

    /**
     * Load the library.
     * 
     * @return The library binding.
     * @throws LionEngineException If error on loading.
     */
    private static AdlMidiBinding loadLibrary()
    {
        try
        {
            Verbose.info("Load library: ", LIBRARY_NAME);
            final AdlMidiBinding binding = Native.loadLibrary(LIBRARY_NAME, AdlMidiBinding.class);
            Verbose.info("Library ", LIBRARY_NAME, " loaded");
            return binding;
        }
        catch (final LinkageError exception)
        {
            throw new LionEngineException(exception, ERROR_LOAD_LIBRARY + LIBRARY_NAME);
        }
    }

    /** Midi binding. */
    private final AdlMidiBinding bind;

    /**
     * Create format.
     * 
     * @throws LionEngineException If unable to load library.
     */
    public AdlMidiFormat()
    {
        super();

        bind = loadLibrary();
    }

    /*
     * AudioFormat
     */

    @Override
    public AdlMidi loadAudio(Media media)
    {
        return new AdlMidiPlayer(media, bind);
    }

    @Override
    public Collection<String> getFormats()
    {
        return FORMATS;
    }

    @Override
    public void close()
    {
        bind.adlStop();
    }
}
