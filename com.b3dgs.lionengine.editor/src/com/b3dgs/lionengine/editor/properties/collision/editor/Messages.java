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
package com.b3dgs.lionengine.editor.properties.collision.editor;

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
    private static final String BUNDLE_NAME = Activator.PLUGIN_ID + ".properties.collision.editor.messages"; //$NON-NLS-1$

    /** Entity collision properties offset X. */
    public static String EntityCollisionProperties_OffsetX;
    /** Entity collision properties offset Y. */
    public static String EntityCollisionProperties_OffsetY;
    /** Entity collision properties width. */
    public static String EntityCollisionProperties_Width;
    /** Entity collision properties height. */
    public static String EntityCollisionProperties_Height;
    /** Entity collision properties mirror. */
    public static String EntityCollisionProperties_Mirror;

    /** Tile collision composite formula. */
    public static String TileCollisionComposite_Formula;
    /** Tile collision composite axis. */
    public static String TileCollisionComposite_Axis;
    /** Tile collision composite range. */
    public static String TileCollisionComposite_Range;
    /** Tile collision composite min. */
    public static String TileCollisionComposite_Min;
    /** Tile collision composite max. */
    public static String TileCollisionComposite_Max;
    /** Tile collision composite apply. */
    public static String TileCollisionComposite_Apply;
    /** Tile collision composite delete. */
    public static String TileCollisionComposite_Delete;

    /** Tile collision formula name title. */
    public static String TileCollision_AddFunction_Title;
    /** Tile collision formula name text. */
    public static String TileCollision_AddFunction_Text;
    /** Tile collision add formula. */
    public static String TileCollision_AddFormula;
    /** Tile collision save. */
    public static String TileCollision_Save;

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
