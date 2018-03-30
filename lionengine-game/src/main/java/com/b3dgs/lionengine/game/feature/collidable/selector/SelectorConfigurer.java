/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable.selector;

import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.geom.Area;

/**
 * Allows to configure the selector.
 */
public interface SelectorConfigurer
{
    /**
     * Set the mouse click selection value.
     * 
     * @param click The click number.
     * @see com.b3dgs.lionengine.io.InputDevicePointer
     */
    void setClickSelection(int click);

    /**
     * Set clickable area (where selection can be performed on screen).
     * 
     * @param area The representation of the clickable area.
     */
    void setClickableArea(Area area);

    /**
     * Set clickable area (where selection can be performed on screen).
     * 
     * @param viewer The viewer reference.
     */
    void setClickableArea(Viewer viewer);

    /**
     * Set the enabled flag.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    void setEnabled(boolean enabled);
}
