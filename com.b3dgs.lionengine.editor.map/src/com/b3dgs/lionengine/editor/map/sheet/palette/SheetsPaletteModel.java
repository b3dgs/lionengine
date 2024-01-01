/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.editor.map.sheet.palette;

/**
 * Contains the sheets palette data.
 */
public class SheetsPaletteModel
{
    /** Palette model. */
    public static final SheetsPaletteModel INSTANCE = new SheetsPaletteModel();

    /** Selected palette tile. */
    private int tile;
    /** Sheet palette type. */
    private SheetPaletteType type = SheetPaletteType.SELECTION;

    /**
     * Create the model.
     */
    protected SheetsPaletteModel()
    {
        super();
    }

    /**
     * Set the selected tile.
     * 
     * @param tile The selected tile, <code>null</code> if none.
     */
    public void setSelectedTile(int tile)
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
    public int getSelectedTile()
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
