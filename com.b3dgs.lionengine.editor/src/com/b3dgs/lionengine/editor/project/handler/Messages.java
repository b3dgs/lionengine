/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Locale;

import org.eclipse.osgi.util.NLS;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Messages internationalization.
 */
public final class Messages extends NLS
{
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

    /** Remove object title. */
    public static String RemoveObject_Title;
    /** Remove object text. */
    public static String RemoveObject_Text;
    /** Remove object error title. */
    public static String RemoveObject_Error_Title;
    /** Remove object error text. */
    public static String RemoveObject_Error_Text;

    /** Add minimap title. */
    public static String AddMinimap_Title;
    /** Add minimap title. */
    public static String AddMinimap_Text;
    /** Add minimap error title. */
    public static String AddMinimap_Error_Title;
    /** Add minimap error text. */
    public static String AddMinimap_Error_Text;

    /**
     * Initialize.
     */
    static
    {
        NLS.initializeMessages(Messages.class.getName().toLowerCase(Locale.ENGLISH), Messages.class);
    }

    /**
     * Private constructor.
     */
    private Messages()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
