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
package com.b3dgs.lionengine.editor.project;

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
    private static final String BUNDLE_NAME = "com.b3dgs.lionengine.project.messages"; //$NON-NLS-1$

    /** Add entity title. */
    public static String AddEntity_Title;
    /** Add entity title. */
    public static String AddEntity_Text;
    /** Add entity error title. */
    public static String AddEntity_Error_Title;
    /** Add entity error text. */
    public static String AddEntity_Error_Text;
    /** Edit entities folder type name title. */
    public static String EditEntitiesFolderType_Name_Title;
    /** Edit entities folder type name text. */
    public static String EditEntitiesFolderType_Name_Text;
    /** Remove entity title. */
    public static String RemoveEntity_Title;
    /** Remove entity text. */
    public static String RemoveEntity_Text;
    /** Remove entity error title. */
    public static String RemoveEntity_Error_Title;
    /** Remove entity error text. */
    public static String RemoveEntity_Error_Text;

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
