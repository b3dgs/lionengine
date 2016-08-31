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
package com.b3dgs.lionengine.audio.adplug;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import com.b3dgs.lionengine.Architecture;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.OperatingSystem;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.AudioFormat;
import com.b3dgs.lionengine.util.UtilStream;
import com.sun.jna.Native;

/**
 * Handle audio AdPlug.
 */
public final class AdPlugFormat implements AudioFormat<AdPlug>
{
    /** Load library error. */
    public static final String ERROR_LOAD_LIBRARY = "Error on loading AdPlug Library: ";
    /** Standard library name. */
    private static final String LIBRARY_NAME = "adplugplayer";
    /** DLL extension. */
    private static final String EXTENSION_DLL = ".dll";
    /** SO extension. */
    private static final String EXTENSION_SO = ".so";
    /** Windows system. */
    private static final String SYSTEM_WINDOW = "win32-";
    /** Linux system. */
    private static final String SYSTEM_LINUX = "linux-";
    /** 64bits architecture. */
    private static final String ARCHITECTURE_X64 = "x86-64";
    /** 32bits architecture. */
    private static final String ARCHITECTURE_X86 = "x86";
    /** Audio extensions. */
    private static final String[] FORMATS =
    {
        "lds"
    };

    /**
     * Load the library.
     * 
     * @param name The library name.
     * @param library The library path.
     * @return The library binding.
     * @throws LionEngineException If error on loading.
     */
    private static AdPlugBinding loadLibrary(String name, String library)
    {
        final InputStream input = AdPlugFormat.class.getResourceAsStream(library);
        if (input == null)
        {
            throw new LionEngineException(ERROR_LOAD_LIBRARY, library, " not found !");
        }
        try
        {
            final File tempLib = UtilStream.getCopy(name, input);
            Verbose.info("Temporary copy: ", tempLib.getPath());
            final AdPlugBinding binding = (AdPlugBinding) Native.loadLibrary(tempLib.getPath(), AdPlugBinding.class);
            Verbose.info("Library ", library, " loaded");
            return binding;
        }
        catch (final LinkageError exception)
        {
            throw new LionEngineException(exception, ERROR_LOAD_LIBRARY, library);
        }
        finally
        {
            UtilStream.safeClose(input);
        }
    }

    /**
     * Get the library system.
     * 
     * @return The library system.
     */
    private static String getLibrarySystem()
    {
        final OperatingSystem system = OperatingSystem.getOperatingSystem();
        if (OperatingSystem.WINDOWS == system)
        {
            return SYSTEM_WINDOW;
        }
        return SYSTEM_LINUX;
    }

    /**
     * Get the library extension.
     * 
     * @return The library extension.
     */
    private static String getLibraryExtension()
    {
        final OperatingSystem system = OperatingSystem.getOperatingSystem();
        if (OperatingSystem.WINDOWS == system)
        {
            return EXTENSION_DLL;
        }
        return EXTENSION_SO;
    }

    /**
     * Get the library architecture.
     * 
     * @return The library architecture.
     */
    private static String getLibraryArchitecture()
    {
        final Architecture architecture = Architecture.getArchitecture();
        if (Architecture.X64 == architecture)
        {
            return ARCHITECTURE_X64;
        }
        return ARCHITECTURE_X86;
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
        final String ext = getLibraryExtension();
        final String sys = getLibrarySystem();
        final String arch = getLibraryArchitecture();

        final String name = LIBRARY_NAME + ext;
        final String library = sys + arch + '/' + name;
        Verbose.info("Load library: ", library);
        bind = loadLibrary(name, library);
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
        return Arrays.asList(FORMATS);
    }

    @Override
    public void close()
    {
        // Nothing to do
    }
}
