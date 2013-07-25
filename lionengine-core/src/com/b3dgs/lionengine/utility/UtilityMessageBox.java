package com.b3dgs.lionengine.utility;

import javax.swing.JOptionPane;

/**
 * Static functions displaying message on screen.
 */
public final class UtilityMessageBox
{
    /**
     * Private constructor.
     */
    private UtilityMessageBox()
    {
        throw new RuntimeException();
    }

    /**
     * Displays an information message.
     * 
     * @param title The information title.
     * @param message The information message.
     */
    public static void information(String title, String message)
    {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays a warning message.
     * 
     * @param title The warning title.
     * @param message The warning message.
     */
    public static void warning(String title, String message)
    {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Displays an error message.
     * 
     * @param title The error title.
     * @param message The error message.
     */
    public static void error(String title, String message)
    {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
