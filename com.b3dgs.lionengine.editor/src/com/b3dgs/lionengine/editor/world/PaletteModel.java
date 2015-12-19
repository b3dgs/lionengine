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
package com.b3dgs.lionengine.editor.world;

/**
 * Represents the palette model.
 */
public class PaletteModel
{
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
    public boolean isPalette(PaletteType type)
    {
        return palette.equals(type);
    }
}
