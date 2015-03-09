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
package com.b3dgs.lionengine.game.trait;

import com.b3dgs.lionengine.core.InputDevicePointer;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Represents a clickable action, allows to perform an action on click.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Actionable
        extends Trait, Updatable
{
    /**
     * Set the executable action.
     * 
     * @param action The action to execute.
     */
    void setAction(Action action);

    /**
     * Set the mouse click selection value to {@link Action#execute()} the action.
     * 
     * @param click The click number.
     * @see InputDevicePointer
     */
    void setClickAction(int click);

    /**
     * Get the button surface representation.
     * 
     * @return The button surface representation.
     */
    Rectangle getButton();

    /**
     * Get the action description.
     * 
     * @return The action description.
     */
    String getDescription();

    /**
     * Check if cursor is over the action button.
     * 
     * @return <code>true</code> if cursor is over, <code>false</code> else.
     */
    boolean isOver();
}
