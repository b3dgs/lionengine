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
package com.b3dgs.lionengine.editor.dialog.sheets.palette;

import com.b3dgs.lionengine.game.map.TileRef;

/**
 * Contains the sheets palette data.
 */
public class SheetsPaletteModel
{
    /** Palette model. */
    public static final SheetsPaletteModel INSTANCE = new SheetsPaletteModel();

    /** Selected palette tile. */
    private TileRef tile = new TileRef(Integer.valueOf(0), 0);
    /** Sheet palette type. */
    private SheetPaletteType type = SheetPaletteType.SELECTION;

    /**
     * Create the model.
     */
    protected SheetsPaletteModel()
    {
        // Nothing to do
    }

    /**
     * Set the selected tile.
     * 
     * @param tile The selected tile, <code>null</code> if none.
     */
    public void setSelectedTile(TileRef tile)
    {
        this.tile = tile;
    }

    /**
     * Set the sheet palette type.
     * 
     * @param type The sheet palette type.
     */
    public void setSheetPaletteType(SheetPaletteType type)
    {
        this.type = type;
    }

    /**
     * Get the selected tile.
     * 
     * @return The selected tile, <code>null</code> if none.
     */
    public TileRef getSelectedTile()
    {
        return tile;
    }

    /**
     * Get the sheet palette type.
     * 
     * @return the type The sheet palette type.
     */
    public SheetPaletteType getSheetPaletteType()
    {
        return type;
    }
}
