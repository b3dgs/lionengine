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
package com.b3dgs.lionengine.editor.dialog.sheets.imports;

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
    /** Dialog title. */
    public static String Title;
    /** Title header. */
    public static String HeaderTitle;
    /** Description header. */
    public static String HeaderDesc;
    /** Rips list. */
    public static String RipsList;
    /** Add remove rip. */
    public static String AddRemoveRip;
    /** Add level rip. */
    public static String AddLevelRip;
    /** Level rip file filter. */
    public static String LevelRipFileFilter;
    /** Remove level rip. */
    public static String RemoveLevelRip;
    /** No level rip defined. */
    public static String NoLevelRipDefined;
    /** Tile width. */
    public static String TileWidth;
    /** Tile height. */
    public static String TileHeight;
    /** Horizontal tiles. */
    public static String HorizontalTiles;
    /** Vertical tiles. */
    public static String VerticalTiles;
    /** Extract to. */
    public static String ExtractTo;
    /** Generate sheets config. */
    public static String GenerateSheetsConfig;
    /** Import sheets finished. */
    public static String Finished;
    /** Import sheets progress. */
    public static String Progress;

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