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
package com.b3dgs.lionengine.editor;

import org.eclipse.osgi.util.NLS;

/**
 * Messages internationalization.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Messages
        extends NLS
{
    /** Bundle name. */
    private static final String BUNDLE_NAME = Activator.PLUGIN_ID + ".messages"; //$NON-NLS-1$
    /** Exit handler title. */
    public static String ExitHandler_Title;
    /** Exit handler text. */
    public static String ExitHandler_Text;
    /** Object properties. */
    public static String ObjectProperties_Properties;
    /** Object properties confirm. */
    public static String ObjectProperties_Confirm;
    /** Object properties reset. */
    public static String ObjectProperties_Reset;
    /** Object list. */
    public static String ObjectList_List;
    /** Object list add animation title. */
    public static String ObjectList_AddAnimation_Title;
    /** Object list add animation text. */
    public static String ObjectList_AddAnimation_Text;

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
