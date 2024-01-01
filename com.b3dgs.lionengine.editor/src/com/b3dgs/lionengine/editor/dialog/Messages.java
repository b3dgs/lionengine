/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.editor.dialog;

import java.util.Locale;

import org.eclipse.osgi.util.NLS;

/**
 * Messages internationalization.
 */
public final class Messages extends NLS
{
    /** Cancel button. */
    public static String Cancel;
    /** Next button. */
    public static String Next;
    /** Finish button. */
    public static String Finish;
    /** Exit button. */
    public static String Exit;

    /** Resource dialog title. */
    public static String ResourceDialog_Title;
    /** Resource dialog header title. */
    public static String ResourceDialog_HeaderTitle;
    /** Resource dialog header description. */
    public static String ResourceDialog_HeaderDesc;
    /** Resource dialog header file name. */
    public static String ResourceDialog_Filename;

    /**
     * Initialize.
     */
    static
    {
        NLS.initializeMessages(Messages.class.getName().toLowerCase(Locale.ENGLISH), Messages.class);
    }
}
