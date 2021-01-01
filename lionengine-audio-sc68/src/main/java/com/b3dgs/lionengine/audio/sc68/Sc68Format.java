/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio.sc68;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.b3dgs.lionengine.Generated;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.AudioFormat;
import com.b3dgs.lionengine.audio.AudioVoidFormat;
import com.sun.jna.Native;

/**
 * Handle audio sc68.
 */
public final class Sc68Format implements AudioFormat
{
    /** Load library error. */
    public static final String ERROR_LOAD_LIBRARY = "Error on loading SC68 Library: ";
    /** Sc68 format. */
    private static final String SC68 = "sc68";
    /** Standard library name. */
    private static final String LIBRARY_NAME = SC68;
    /** Audio extensions as read only. */
    private static final Collection<String> FORMATS = Collections.unmodifiableCollection(Arrays.asList(SC68));

    /**
     * Get the library, or void format if not found.
     * 
     * @return The audio format.
     */
    @Generated
    public static AudioFormat getFailsafe()
    {
        try
        {
            return new Sc68Format();
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
    @Generated
    private static Sc68Binding loadLibrary()
    {
        try
        {
            return getLibrary();
        }
        catch (final LinkageError exception)
        {
            throw new LionEngineException(exception, ERROR_LOAD_LIBRARY + LIBRARY_NAME);
        }
    }

    /**
     * Load the library.
     * 
     * @return The library binding.
     */
    private static Sc68Binding getLibrary()
    {
        Verbose.info("Load library: ", LIBRARY_NAME);
        final Sc68Binding binding = Native.load(LIBRARY_NAME, Sc68Binding.class);
        Verbose.info("Library ", LIBRARY_NAME, " loaded");
        return binding;
    }

    /** Sc68 binding. */
    private final Sc68Binding bind;

    /**
     * Create format.
     * 
     * @throws LionEngineException If unable to load library.
     */
    public Sc68Format()
    {
        super();

        bind = loadLibrary();
    }

    /*
     * AudioFormat
     */

    @Override
    public Sc68 loadAudio(Media media)
    {
        return new Sc68Player(media, bind);
    }

    @Override
    public Collection<String> getFormats()
    {
        return FORMATS;
    }

    @Override
    public void close()
    {
        bind.sc68Stop();
    }
}
