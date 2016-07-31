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

import com.b3dgs.lionengine.Architecture;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.OperatingSystem;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.util.UtilStream;
import com.sun.jna.Native;

/**
 * Handle audio AdPlug.
 */
public final class AudioAdPlug
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

    /**
     * Create a AdPlug player.
     * 
     * @return The AdPlug instance.
     * @throws LionEngineException If unable to load library.
     */
    public static AdPlug createAdPlugPlayer()
    {
        final AudioAdPlug adplug = new AudioAdPlug();
        return new AdPlugPlayer(adplug.getBinding());
    }

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
        final InputStream input = AudioAdPlug.class.getResourceAsStream(library);
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
            throw new LionEngineException(exception, AudioAdPlug.ERROR_LOAD_LIBRARY, library);
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
            return AudioAdPlug.SYSTEM_WINDOW;
        }
        return AudioAdPlug.SYSTEM_LINUX;
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
            return AudioAdPlug.EXTENSION_DLL;
        }
        return AudioAdPlug.EXTENSION_SO;
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
            return AudioAdPlug.ARCHITECTURE_X64;
        }
        return AudioAdPlug.ARCHITECTURE_X86;
    }

    /** AdPlug binding. */
    private final AdPlugBinding bind;

    /**
     * Private constructor.
     * 
     * @throws LionEngineException If unable to load library.
     */
    private AudioAdPlug()
    {
        final String ext = AudioAdPlug.getLibraryExtension();
        final String sys = AudioAdPlug.getLibrarySystem();
        final String arch = AudioAdPlug.getLibraryArchitecture();

        final String name = AudioAdPlug.LIBRARY_NAME + ext;
        final String library = sys + arch + '/' + name;
        Verbose.info("Load library: ", library);
        bind = AudioAdPlug.loadLibrary(name, library);
    }

    /**
     * Get the binding reference.
     * 
     * @return The binding reference.
     */
    private AdPlugBinding getBinding()
    {
        return bind;
    }
}
