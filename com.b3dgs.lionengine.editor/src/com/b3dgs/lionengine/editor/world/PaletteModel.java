/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.world;

/**
 * Represents the palette model.
 */
public class PaletteModel
{
    /** Palette prefix for items ID. */
    public static final String ID_PREFIX = "palette.";

    /** Selected palette. */
    private Enum<?> palette;

    /**
     * Create the model.
     */
    public PaletteModel()
    {
        palette = PaletteType.POINTER_OBJECT;
    }

    /**
     * Set the selected palette.
     * 
     * @param palette The selected palette.
     */
    public void setSelectedPalette(Enum<?> palette)
    {
        this.palette = palette;
    }

    /**
     * Check if current palette is the specified type.
     * 
     * @param type The expected type.
     * @return <code>true</code> if right type, <code>false</code> else.
     */
    public boolean isPalette(Enum<?> type)
    {
        return palette == type;
    }
}
