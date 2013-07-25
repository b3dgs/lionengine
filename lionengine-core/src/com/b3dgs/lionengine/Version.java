package com.b3dgs.lionengine;

/**
 * Represents a program version.
 */
public final class Version
{
    /** Major version. */
    public final int major;
    /** Minor version. */
    public final int minor;
    /** Micro version. */
    public final int micro;

    /**
     * Create a new version descriptor.
     * 
     * @param major The major version.
     * @param minor The minor version.
     * @param micro The micro version.
     * @return The version descriptor.
     */
    public static Version create(int major, int minor, int micro)
    {
        return new Version(major, minor, micro);
    }

    /**
     * Create a new version descriptor.
     * 
     * @param major The major version.
     * @param minor The minor version.
     * @param micro The micro version.
     */
    private Version(int major, int minor, int micro)
    {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(String.valueOf(major)).append(".").append(minor).append(".").append(micro).toString();
    }
}
