/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.OperatingSystem;
import com.sun.jna.Native;

/**
 * Handle audio sc68.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class AudioSc68
{
    /**
     * Create a sc68 player.
     * 
     * @return The sc68 player instance.
     */
    public static Sc68 createSc68Player()
    {
        final AudioSc68 sc68 = new AudioSc68();
        return new Sc68Player(sc68.getBinding());
    }

    /**
     * Copy stream into a temporary file and return this file (localized on current system).
     * 
     * @param outfile The file which will store stream.
     * @param stream The input stream.
     * @return The file containing a copy of the input stream.
     */
    static File getFile(String outfile, InputStream stream)
    {
        final File file = new File(outfile);
        try (OutputStream out = new FileOutputStream(file);)
        {
            try
            {
                final byte[] bytes = new byte[1024];
                int read;

                while ((read = stream.read(bytes)) != -1)
                {
                    out.write(bytes, 0, read);
                }
                out.flush();
                Verbose.info("File temporary created: ", outfile);
            }
            finally
            {
                stream.close();
            }
        }
        catch (final IOException exception)
        {
            Verbose.exception(Media.class, "getFile", exception, "Temporary file error on: \"", outfile, "\"");
        }
        return file;
    }

    /** Sc68 binding. */
    private final Sc68Binding bind;

    /**
     * Private constructor.
     */
    private AudioSc68()
    {
        final String arch;
        final String ext;
        switch (OperatingSystem.getOperatingSystem())
        {
            case WINDOWS:
                ext = ".dll";
                arch = OperatingSystem.getArchitecture().name();
                break;
            default:
                ext = ".so";
                arch = "x86_64";
                break;
        }

        final String name = "sc68_player_" + arch.toLowerCase(Locale.getDefault()) + ext;
        try
        {
            final Media media = Media.create(Media.getPath(UtilityFile.getTempDir(), name));
            final File lib = AudioSc68.getFile(media.getPath(), getClass().getResourceAsStream(name));
            bind = (Sc68Binding) Native.loadLibrary(lib.getAbsolutePath(), Sc68Binding.class);
            Verbose.info("Library ", name, " loaded");
        }
        catch (final Throwable exception)
        {
            throw new LionEngineException(exception, "Error on loading SC68 Library: ", name);
        }
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
