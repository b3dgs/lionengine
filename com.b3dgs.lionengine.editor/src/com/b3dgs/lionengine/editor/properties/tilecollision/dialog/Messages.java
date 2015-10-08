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
package com.b3dgs.lionengine.editor.properties.tilecollision.dialog;

import java.util.Locale;

import org.eclipse.osgi.util.NLS;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Messages internationalization.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Messages extends NLS
{
    /** Tile collision dialog title. */
    public static String Dialog_TileCollision_Title;
    /** Tile collision dialog header title. */
    public static String Dialog_TileCollision_HeaderTitle;
    /** Tile collision dialog header description. */
    public static String Dialog_TileCollision_HeaderDesc;

    /** Tile collision name. */
    public static String Dialog_TileCollision_Name;

    /** Tile collision range. */
    public static String Dialog_TileCollision_Range;
    /** Tile collision axis. */
    public static String Dialog_TileCollision_Axis;
    /** Tile collision min x. */
    public static String Dialog_TileCollision_MinX;
    /** Tile collision max x. */
    public static String Dialog_TileCollision_MaxX;
    /** Tile collision min y. */
    public static String Dialog_TileCollision_MinY;
    /** Tile collision max y. */
    public static String Dialog_TileCollision_MaxY;

    /** Tile collision function. */
    public static String Dialog_TileCollision_Function;
    /** Tile collision linear a. */
    public static String Dialog_TileCollision_Linear_A;
    /** Tile collision linear b. */
    public static String Dialog_TileCollision_Linear_B;

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
