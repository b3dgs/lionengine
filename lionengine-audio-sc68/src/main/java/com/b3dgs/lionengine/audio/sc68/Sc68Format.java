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
package com.b3dgs.lionengine.audio.sc68;

import java.util.Arrays;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.AudioFormat;
import com.b3dgs.lionengine.audio.AudioVoidFormat;
import com.sun.jna.Native;

/**
 * Handle audio sc68.
 */
public final class Sc68Format implements AudioFormat<Sc68>
{
    /** Load library error. */
    public static final String ERROR_LOAD_LIBRARY = "Error on loading SC68 Library: ";
    /** Standard library name. */
    private static final String LIBRARY_NAME;
    /** Audio extensions. */
    private static final String[] FORMATS =
    {
        "sc68"
    };

    /**
     * Specific case to not inline for test purpose.
     */
    static
    {
        LIBRARY_NAME = "sc68";
    }

    /**
     * Get the AdPlug library, or disabled format if not found.
     * 
     * @return The AdPlug audio format.
     */
    public static AudioFormat<?> getFailsafe()
    {
        try
        {
            return new Sc68Format();
        }
        catch (final LionEngineException exception)
        {
            Verbose.exception(exception, "Unable to load music library !");
            return new AudioVoidFormat(FORMATS);
        }
    }

    /**
     * Load the library.
     * 
     * @return The library binding.
     * @throws LionEngineException If error on loading.
     */
    private static Sc68Binding loadLibrary()
    {
        try
        {
            Verbose.info("Load library: ", LIBRARY_NAME);
            final Sc68Binding binding = Native.loadLibrary(LIBRARY_NAME, Sc68Binding.class);
            Verbose.info("Library ", LIBRARY_NAME, " loaded");
            return binding;
        }
        catch (final LinkageError exception)
        {
            throw new LionEngineException(exception, ERROR_LOAD_LIBRARY, LIBRARY_NAME);
        }
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
        return Arrays.asList(FORMATS);
    }

    @Override
    public void close()
    {
        bind.sc68Stop();
    }
}
