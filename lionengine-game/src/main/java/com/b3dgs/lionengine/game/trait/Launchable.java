/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.game.Force;

/**
 * Represents something which can be launched with a specified vector.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Launchable
        extends Trait, Updatable
{
    /**
     * Initiate launch by using the defined vector with {@link #setVector(Force)} starting at this location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    void launch(double x, double y);

    /**
     * Set the vector to use when launched.
     * 
     * @param force The force to use.
     */
    void setVector(Force force);

    /**
     * Set the delay time before being effectively launched after a call to {@link #launch(double, double)}.
     * 
     * @param time The delay time in millisecond.
     */
    void setDelay(long time);
}
