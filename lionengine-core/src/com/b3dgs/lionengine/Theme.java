package com.b3dgs.lionengine;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Handle java theme selection.
 */
public enum Theme
{
    /** GTK theme. */
    GTK,
    /** Metal theme. */
    METAL,
    /** Motif theme. */
    MOTIF,
    /** Current system theme. */
    SYSTEM;

    /** Error message theme. */
    private static final String MESSAGE_ERROR_THEME = "Theme must not be null !";
    /** Error message theme set. */
    private static final String MESSAGE_ERROR_SET = "Error on setting theme !";

    /**
     * Set the java frame theme.
     * 
     * @param theme The theme.
     */
    public static void set(Theme theme)
    {
        Check.notNull(theme, Theme.MESSAGE_ERROR_THEME);
        final String lookAndFeel;
        switch (theme)
        {
            case METAL:
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
                break;
            case SYSTEM:
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
                break;
            case MOTIF:
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
                break;
            case GTK:
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
                break;
            default:
                Verbose.warning(Theme.class, "set", "Unknown theme: " + theme);
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
                break;
        }
        try
        {
            UIManager.setLookAndFeel(lookAndFeel);
        }
        catch (ClassNotFoundException
               | InstantiationException
               | IllegalAccessException
               | UnsupportedLookAndFeelException exception)
        {
            throw new LionEngineException(exception, Theme.MESSAGE_ERROR_SET);
        }
    }
}
