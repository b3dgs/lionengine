package com.b3dgs.lionengine;

import javax.swing.JApplet;

/**
 * Describe the engine screen configuration. It allows to define different parameters:
 * <ul>
 * <li>internal : It is corresponding to the native screen display for what the program was designed for</li>
 * <li>external : It is corresponding to the desired screen display output. If internal & external are not equal, the
 * screen will be stretched</li>
 * <li>windowed : Allows to set the screen output mode (<code>true</code> for windowed, <code>false</code> for
 * fullscreen)</li>
 * <li>filter : Used only in case of screen stretching, depending of the external display</li>
 * <li>applet : Can be used to set the applet reference in case of applet mode</li>
 * </ul>
 */
public final class Config
{
    /** Error message internal. */
    private static final String MESSAGE_ERROR_INTERNAL = "The internal display must not be null !";
    /** Error message external. */
    private static final String MESSAGE_ERROR_EXTERNAL = "The external display must not be null !";
    /** Error message filter. */
    private static final String MESSAGE_ERROR_FILTER = "The filter must not be null !";
    /** Internal display reference. */
    public final Display internal;
    /** External display reference. */
    public final Display external;
    /** Filter reference. */
    public final Filter filter;
    /** Windowed mode. */
    private final boolean windowed;
    /** Applet reference. */
    private JApplet applet;

    /**
     * Create a config.
     * 
     * @param internal The internal display (must not be null).
     * @param external The external display (must not be null).
     * @param windowed The windowed mode: <code>true</code> for windowed, <code>false</code> for fullscreen.
     */
    public Config(Display internal, Display external, boolean windowed)
    {
        this(internal, external, windowed, Filter.NONE);
    }

    /**
     * Create a config.
     * 
     * @param internal The internal display (must not be null).
     * @param external The external display (must not be null).
     * @param windowed The windowed mode: <code>true</code> for windowed, <code>false</code> for fullscreen.
     * @param filter The filter mode (must not be null).
     */
    public Config(Display internal, Display external, boolean windowed, Filter filter)
    {
        Check.notNull(internal, Config.MESSAGE_ERROR_INTERNAL);
        Check.notNull(external, Config.MESSAGE_ERROR_EXTERNAL);
        Check.notNull(filter, Config.MESSAGE_ERROR_FILTER);

        this.internal = internal;
        this.filter = filter;
        this.windowed = windowed;

        final int width = internal.getWidth();
        final int height = internal.getHeight();
        final int depth = internal.getDepth();
        final int rate = internal.getRate();

        switch (filter)
        {
            case HQ2X:
                this.external = new Display(width * 2, height * 2, depth, rate);
                break;
            case HQ3X:
                this.external = new Display(width * 3, height * 3, depth, rate);
                break;
            default:
                this.external = external;
                break;
        }
    }

    /**
     * Get the windowed mode.
     * 
     * @return <code>true</code> if is windowed, <code>false</code> else.
     */
    public boolean isWindowed()
    {
        return windowed;
    }

    /**
     * Set applet reference, and enable applet mode.
     * 
     * @param applet The applet reference.
     */
    public void setApplet(JApplet applet)
    {
        this.applet = applet;
    }

    /**
     * Get applet reference.
     * 
     * @return The applet reference.
     */
    public JApplet getApplet()
    {
        return applet;
    }
}
