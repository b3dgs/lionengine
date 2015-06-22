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
package com.b3dgs.lionengine.editor.properties.tile;

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
    private static final String BUNDLE_NAME = Activator.PLUGIN_ID + ".properties.tile.messages"; //$NON-NLS-1$

    /** Tile group property. */
    public static String Properties_TileGroup;
    /** Tile sheet property. */
    public static String Properties_TileSheet;
    /** Tile number property. */
    public static String Properties_TileNumber;
    /** Tile size property. */
    public static String Properties_TileSize;
    /** Tile features property. */
    public static String Properties_TileFeatures;
    /** Tile feature property. */
    public static String Properties_TileFeature;

    /** Group chooser title. */
    public static String GroupChooser_Title;
    /** Group chooser header title. */
    public static String GroupChooser_HeaderTitle;
    /** Group chooser header description. */
    public static String GroupChooser_HeaderDesc;
    /** Group choice. */
    public static String GroupChooser_Choice;
    /** Group add. */
    public static String GroupChooser_Add;

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
