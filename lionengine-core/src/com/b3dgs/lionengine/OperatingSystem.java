package com.b3dgs.lionengine;

import java.util.Locale;

/**
 * List of available operating systems.
 */
public enum OperatingSystem
{
    /** Window family. */
    WINDOWS,
    /** Unix family. */
    UNIX,
    /** Mac OperatingSystem. */
    MAC,
    /** Sun solaris. */
    SOLARIS,
    /** Unknown system. */
    UNKNOWN;

    /** Operating system name. */
    private static final String SYSTEM_OS;
    /** System architecture. */
    private static final String SYSTEM_ARCHI;
    /** The OS enum. */
    private static final OperatingSystem OS;
    /** The architecture used. */
    private static final Architecture ARCHI;

    // Initialize values
    static
    {
        String system;
        try
        {
            system = System.getProperty("os.name").toLowerCase(Locale.getDefault());
        }
        catch (final SecurityException exception)
        {
            system = "";
        }
        SYSTEM_OS = system;

        if (OperatingSystem.SYSTEM_OS.indexOf("win") >= 0)
        {
            OS = OperatingSystem.WINDOWS;
        }
        else if (OperatingSystem.SYSTEM_OS.indexOf("mac") >= 0)
        {
            OS = OperatingSystem.MAC;
        }
        else if (OperatingSystem.SYSTEM_OS.indexOf("nix") >= 0 || OperatingSystem.SYSTEM_OS.indexOf("nux") >= 0
                || OperatingSystem.SYSTEM_OS.indexOf("bsd") >= 0)
        {
            OS = OperatingSystem.UNIX;
        }
        else if (OperatingSystem.SYSTEM_OS.indexOf("sunos") >= 0)
        {
            OS = OperatingSystem.SOLARIS;
        }
        else
        {
            OS = OperatingSystem.UNKNOWN;
        }

        String archi;
        try
        {
            archi = System.getProperty("sun.arch.data.model");
        }
        catch (final SecurityException exception)
        {
            archi = "";
        }
        SYSTEM_ARCHI = archi;

        if (OperatingSystem.SYSTEM_ARCHI.contains("64"))
        {
            ARCHI = Architecture.X64;
        }
        else if (OperatingSystem.SYSTEM_ARCHI.contains("32"))
        {
            ARCHI = Architecture.X86;
        }
        else
        {
            ARCHI = Architecture.UNKNOWN;
        }
    }

    /**
     * Get operating system name.
     * 
     * @return The operating system name.
     */
    public static OperatingSystem getOperatingSystem()
    {
        return OperatingSystem.OS;
    }

    /**
     * Get java current running architecture.
     * 
     * @return The jvm architecture.
     */
    public static Architecture getArchitecture()
    {
        return OperatingSystem.ARCHI;
    }
}
