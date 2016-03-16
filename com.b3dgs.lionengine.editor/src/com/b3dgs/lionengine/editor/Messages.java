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
package com.b3dgs.lionengine.editor;

import java.util.Locale;

import org.eclipse.osgi.util.NLS;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Messages internationalization.
 */
public final class Messages extends NLS
{
    /** Object properties. */
    public static String ObjectProperties_Properties;
    /** Object properties confirm. */
    public static String ObjectProperties_Confirm;
    /** Object properties reset. */
    public static String ObjectProperties_Reset;
    /** Object list. */
    public static String ObjectList_List;
    /** Object list add title. */
    public static String ObjectList_AddObject_Title;
    /** Object list add text. */
    public static String ObjectList_AddObject_Text;

    /** Input validator name error text. */
    public static String InputValidator_Error_Name;

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
