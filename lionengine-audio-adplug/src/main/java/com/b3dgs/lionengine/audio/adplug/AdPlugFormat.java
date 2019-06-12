/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio.adplug;

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
 * Handle audio AdPlug.
 */
public final class AdPlugFormat implements AudioFormat
{
    /** Load library error. */
    public static final String ERROR_LOAD_LIBRARY = "Error on loading AdPlug Library: ";
    /** Standard library name. */
    private static final String LIBRARY_NAME;
    /** Audio extensions as read only. */
    private static final Collection<String> FORMATS = Collections.unmodifiableCollection(Arrays.asList("lds"));

    /**
     * Specific case to not inline for test purpose.
     */
    static
    {
        LIBRARY_NAME = "adplug";
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
            return new AdPlugFormat();
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
    private static AdPlugBinding loadLibrary()
    {
        Verbose.info("Load library: ", LIBRARY_NAME);
        try
        {
            final AdPlugBinding binding = Native.loadLibrary(LIBRARY_NAME, AdPlugBinding.class);
            Verbose.info("Library ", LIBRARY_NAME, " loaded");
            return binding;
        }
        catch (final LinkageError exception)
        {
            throw new LionEngineException(exception, ERROR_LOAD_LIBRARY + LIBRARY_NAME);
        }
    }

    /** AdPlug binding. */
    private final AdPlugBinding bind;

    /**
     * Create format.
     * 
     * @throws LionEngineException If unable to load library.
     */
    public AdPlugFormat()
    {
        super();

        bind = loadLibrary();
    }

    /*
     * AudioFormat
     */

    @Override
    public AdPlug loadAudio(Media media)
    {
        return new AdPlugPlayer(media, bind);
    }

    @Override
    public Collection<String> getFormats()
    {
        return FORMATS;
    }

    @Override
    public void close()
    {
        bind.adplugStop();
    }
}
