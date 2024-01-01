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
package com.b3dgs.lionengine.editor.world;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

import com.b3dgs.lionengine.editor.utility.UtilIcon;

/**
 * Represents the different standard palette types.
 */
public enum PaletteType
{
    /** Object pointer. Allows to interact with object. */
    POINTER_OBJECT(SWT.CURSOR_ARROW),
    /** Tile pointer. Allows to interact with map. */
    POINTER_TILE(SWT.CURSOR_ARROW),
    /** Collision pointer. Allows to place collisions. */
    POINTER_COLLISION(SWT.CURSOR_ARROW),
    /** Hand. Allows to navigate on map with mouse. */
    HAND(SWT.CURSOR_HAND),
    /** Selection. Allows to select many objects. */
    SELECTION(SWT.CURSOR_CROSS),
    /** Pipet. Allows to pick an element (duplicate). */
    PIPET(new Cursor(Display.getDefault(), UtilIcon.get("toolbar", "pipet.png").getImageData(), 0, 15));

    /** The associated cursor. */
    private final Cursor cursor;

    /**
     * Private constructor.
     * 
     * @param cursor The cursor reference.
     */
    PaletteType(int cursor)
    {
        this.cursor = Display.getDefault().getSystemCursor(cursor);
    }

    /**
     * Private constructor.
     * 
     * @param cursor The cursor reference.
     */
    PaletteType(Cursor cursor)
    {
        this.cursor = cursor;
    }

    /**
     * Get the palette cursor.
     * 
     * @return The palette cursor.
     */
    public Cursor getCursor()
    {
        return cursor;
    }
}
