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
package com.b3dgs.lionengine.editor.world.dialog;

import org.eclipse.osgi.util.NLS;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.Activator;

/**
 * Messages internationalization.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Messages extends NLS
{
    /** Bundle name. */
    private static final String BUNDLE_NAME = Activator.PLUGIN_ID + ".world.dialog.messages"; //$NON-NLS-1$

    /** Import sheets dialog title. */
    public static String SheetsImportDialog_Title;
    /** Import sheets title header. */
    public static String SheetsImportDialog_HeaderTitle;
    /** Import sheets description header. */
    public static String SheetsImportDialog_HeaderDesc;
    /** Rips list. */
    public static String SheetsImportDialog_RipsList;
    /** Add remove rip. */
    public static String SheetsImportDialog_AddRemoveRip;
    /** Add level rip. */
    public static String SheetsImportDialog_AddLevelRip;
    /** Remove level rip. */
    public static String SheetsImportDialog_RemoveLevelRip;
    /** No level rip defined. */
    public static String SheetsImportDialog_NoLevelRipDefined;
    /** Tile width. */
    public static String SheetsImportDialog_TileWidth;
    /** Tile height. */
    public static String SheetsImportDialog_TileHeight;
    /** Horizontal tiles. */
    public static String SheetsImportDialog_HorizontalTiles;
    /** Vertical tiles. */
    public static String SheetsImportDialog_VerticalTiles;
    /** Extract to. */
    public static String SheetsImportDialog_ExtractTo;
    /** Generate sheets config. */
    public static String SheetsImportDialog_GenerateSheetsConfig;
    /** Import sheets finished. */
    public static String SheetsImportDialog_Finished;
    /** Import sheets progress. */
    public static String SheetsImportDialog_Progress;

    /** Import map dialog title. */
    public static String ImportMapDialog_Title;
    /** Import map title header. */
    public static String ImportMapDialog_HeaderTitle;
    /** Import map description header. */
    public static String ImportMapDialog_HeaderDesc;
    /** Level rip location. */
    public static String ImportMapDialog_LevelRipLocation;
    /** Sheets location. */
    public static String ImportMapDialog_SheetsLocation;
    /** Groups location. */
    public static String ImportMapDialog_GroupsLocation;
    /** Level rip file filter. */
    public static String ImportMapDialog_LevelRipFileFilter;
    /** Sheets config file filter. */
    public static String ImportMapDialog_SheetsConfigFileFilter;
    /** Groups config file filter. */
    public static String ImportMapDialog_GroupsConfigFileFilter;
    /** Error level rip. */
    public static String ImportMapDialog_ErrorLevelRip;
    /** Error sheets. */
    public static String ImportMapDialog_ErrorSheets;
    /** Error groups. */
    public static String ImportMapDialog_ErrorGroups;
    /** Import map progress. */
    public static String ImportMapDialog_Progress;

    /** Import map collision dialog title. */
    public static String ImportMapCollisionDialog_Title;
    /** Import map collision title header. */
    public static String ImportMapCollisionDialog_HeaderTitle;
    /** Import map collision description header. */
    public static String ImportMapCollisionDialog_HeaderDesc;
    /** Map formulas location. */
    public static String ImportMapCollisionDialog_FormulasLocation;
    /** Map collisions location. */
    public static String ImportMapCollisionDialog_CollisionsLocation;
    /** Formulas config file filter. */
    public static String ImportMapCollisionDialog_FormulasConfigFileFilter;
    /** Map collisions file filter. */
    public static String ImportMapCollisionDialog_CollisionsFileFilter;
    /** Error formulas file. */
    public static String ImportMapCollisionDialog_ErrorFormulas;
    /** Error collisions file. */
    public static String ImportMapCollisionDialog_ErrorCollisions;

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
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
