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
package com.b3dgs.lionengine.editor.project.handler;

import org.eclipse.osgi.util.NLS;

import com.b3dgs.lionengine.editor.Activator;

/**
 * Messages internationalization.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Messages
        extends NLS
{
    /** Bundle name. */
    private static final String BUNDLE_NAME = Activator.PLUGIN_ID + ".project.handler.messages"; //$NON-NLS-1$

    /** Add object title. */
    public static String AddObject_Title;
    /** Add object title. */
    public static String AddObject_Text;
    /** Add object error title. */
    public static String AddObject_Error_Title;
    /** Add object error text. */
    public static String AddObject_Error_Text;

    /** Add sheets title. */
    public static String AddSheets_Title;
    /** Add sheets title. */
    public static String AddSheets_Text;
    /** Add sheets error title. */
    public static String AddSheets_Error_Title;
    /** Add sheets error text. */
    public static String AddSheets_Error_Text;

    /** Add groups title. */
    public static String AddGroups_Title;
    /** Add groups title. */
    public static String AddGroups_Text;
    /** Add groups error title. */
    public static String AddGroups_Error_Title;
    /** Add groups error text. */
    public static String AddGroups_Error_Text;

    /** Add formulas title. */
    public static String AddFormulas_Title;
    /** Add formulas title. */
    public static String AddFormulas_Text;
    /** Add formulas error title. */
    public static String AddFormulas_Error_Title;
    /** Add formulas error text. */
    public static String AddFormulas_Error_Text;

    /** Add collisions title. */
    public static String AddCollisions_Title;
    /** Add collisions title. */
    public static String AddCollisions_Text;
    /** Add collisions error title. */
    public static String AddCollisions_Error_Title;
    /** Add collisions error text. */
    public static String AddCollisions_Error_Text;

    /** Remove object title. */
    public static String RemoveObject_Title;
    /** Remove object text. */
    public static String RemoveObject_Text;
    /** Remove object error title. */
    public static String RemoveObject_Error_Title;
    /** Remove object error text. */
    public static String RemoveObject_Error_Text;

    /**
     * Initialize.
     */
    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * Private constructor.
     */
    private Messages()
    {
        throw new RuntimeException();
    }
}
