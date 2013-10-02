/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Handle java theme selection.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
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
