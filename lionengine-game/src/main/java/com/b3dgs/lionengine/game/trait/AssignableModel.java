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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.InputDevicePointer;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.configurer.ConfigAction;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.object.Services;

/**
 * Assignable implementation.
 *
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class AssignableModel
        implements Assignable
{
    /** Cursor reference. */
    private final Cursor cursor;
    /** Mouse click number to assign action. */
    private int clickAssign;

    /**
     * Create an assignable model.
     * <p>
     * The {@link Configurer} must provide a valid configuration compatible with {@link ConfigAction}.
     * </p>
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Cursor}</li>
     * </ul>
     *
     * @param configurer The configurer reference.
     * @param services The services reference.
     * @throws LionEngineException If wrong configurer or missing {@link Services}.
     */
    public AssignableModel(Configurer configurer, Services services) throws LionEngineException
    {
        cursor = services.get(Cursor.class);
    }

    /**
     * Set the mouse click assign value to {@link #assign()} the action.
     *
     * @param click The click number.
     * @see InputDevicePointer
     */
    public void setClickAction(int click)
    {
        clickAssign = click;
    }

    /*
     * Assignable
     */

    @Override
    public void update(double extrp)
    {
        if (cursor.hasClickedOnce(clickAssign))
        {
            assign();
        }
    }
}
