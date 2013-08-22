package com.b3dgs.lionengine.audio;

import java.io.File;
import java.util.Locale;

import com.b3dgs.lionengine.Architecture;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.OperatingSystem;
import com.b3dgs.lionengine.Verbose;
import com.sun.jna.Native;

/**
 * Handle audio sc68.
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
                arch = Architecture.X64.name();
                break;
        }

        final String name = "SC68Player_" + arch.toLowerCase(Locale.getDefault()) + ext;
        try
        {
            final File lib = Media.getFile(Media.getPath(Media.getTempDir(), name), getClass()
                    .getResourceAsStream(name));
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
