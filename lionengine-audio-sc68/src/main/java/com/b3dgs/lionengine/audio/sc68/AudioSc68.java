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
package com.b3dgs.lionengine.audio.sc68;

import java.io.File;
import java.io.InputStream;

import com.b3dgs.lionengine.Architecture;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.OperatingSystem;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.util.UtilStream;
import com.sun.jna.Native;

/**
 * Handle audio sc68.
 */
public final class AudioSc68
{
    /** Load library error. */
    public static final String ERROR_LOAD_LIBRARY = "Error on loading SC68 Library: ";
    /** Standard library name. */
    private static final String LIBRARY_NAME = "sc68player";
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
     * Create a sc68 player.
     * 
     * @return The sc68 player instance.
     * @throws LionEngineException If unable to load library.
     */
    public static Sc68 createSc68Player()
    {
        final AudioSc68 sc68 = new AudioSc68();
        return new Sc68Player(sc68.getBinding());
    }

    /**
     * Load the library.
     * 
     * @param name The library name.
     * @param library The library path.
     * @return The library binding.
     * @throws LionEngineException If error on loading.
     */
    private static Sc68Binding loadLibrary(String name, String library)
    {
        final InputStream input = AudioSc68.class.getResourceAsStream(library);
        try
        {
            final File tempLib = UtilStream.getCopy(name, input);
            Verbose.info("Temporary copy: ", tempLib.getPath());
            final Sc68Binding binding = (Sc68Binding) Native.loadLibrary(tempLib.getPath(), Sc68Binding.class);
            Verbose.info("Library ", library, " loaded");
            return binding;
        }
        catch (final LinkageError exception)
        {
            throw new LionEngineException(exception, AudioSc68.ERROR_LOAD_LIBRARY, library);
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
            return AudioSc68.SYSTEM_WINDOW;
        }
        return AudioSc68.SYSTEM_LINUX;
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
            return AudioSc68.EXTENSION_DLL;
        }
        return AudioSc68.EXTENSION_SO;
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
            return AudioSc68.ARCHITECTURE_X64;
        }
        return AudioSc68.ARCHITECTURE_X86;
    }

    /** Sc68 binding. */
    private final Sc68Binding bind;

    /**
     * Private constructor.
     * 
     * @throws LionEngineException If unable to load library.
     */
    private AudioSc68()
    {
        final String ext = AudioSc68.getLibraryExtension();
        final String sys = AudioSc68.getLibrarySystem();
        final String arch = AudioSc68.getLibraryArchitecture();

        final String name = AudioSc68.LIBRARY_NAME + ext;
        final String library = sys + arch + '/' + name;
        Verbose.info("Load library: ", library);
        bind = AudioSc68.loadLibrary(name, library);
    }

    /**
     * Get the binding reference.
     * 
     * @return The binding reference.
     */
    private Sc68Binding getBinding()
    {
        return bind;
    }
}
