/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.awt;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Verbose;

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

    /** Error message theme set. */
    private static final String MESSAGE_ERROR_SET = "Error on setting theme !";
    /** Motif look. */
    private static final String LOOK_MOTIF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    /** GTK look. */
    private static final String LOOK_GTK = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";

    /**
     * Set the java frame theme.
     * 
     * @param theme The theme.
     * @throws LionEngineException If an error occurred when setting the theme.
     */
    public static void set(Theme theme) throws LionEngineException
    {
        Check.notNull(theme);

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
                lookAndFeel = Theme.LOOK_MOTIF;
                break;
            case GTK:
                lookAndFeel = Theme.LOOK_GTK;
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
        catch (final ReflectiveOperationException
                     | UnsupportedLookAndFeelException exception)
        {
            throw new LionEngineException(exception, Theme.MESSAGE_ERROR_SET);
        }
    }
}
