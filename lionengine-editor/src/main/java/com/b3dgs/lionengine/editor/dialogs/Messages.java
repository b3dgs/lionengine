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
package com.b3dgs.lionengine.editor.dialogs;

import org.eclipse.osgi.util.NLS;

/**
 * Messages internationalization.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Messages
        extends NLS
{
    /** Bundle name. */
    private static final String BUNDLE_NAME = "com.b3dgs.lionengine.editor.dialogs.messages"; //$NON-NLS-1$
    /** Dialog title. */
    public static String NewProjectDialog_0;
    /** Title header. */
    public static String NewProjectDialog_1;
    /** Description header. */
    public static String NewProjectDialog_2;
    /** Project name. */
    public static String NewProjectDialog_3;
    /** Cancel button. */
    public static String NewProjectDialog_4;
    /** Finish button. */
    public static String NewProjectDialog_5;
    /** Folders */
    public static String NewProjectDialog_6;
    /** Sources. */
    public static String NewProjectDialog_7;
    /** Resources */
    public static String NewProjectDialog_8;
    /** Location */
    public static String NewProjectDialog_9;
    /** Browse */
    public static String NewProjectDialog_10;
    /** Generate base code */
    public static String NewProjectDialog_11;
    /** Project package. */
    public static String NewProjectDialog_12;
    /** Invalid package name. */
    public static String NewProjectDialog_13;
    /** About */
    public static String AboutDialog_0;

    /**
     * Initialize.
     */
    static
    {
        NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
    }

    /**
     * Constructor.
     */
    private Messages()
    {
        // Private constructor
    }
}
