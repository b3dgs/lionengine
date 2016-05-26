/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.assignable;

/**
 * Represents something that can be assigned by an {@link Assignable} from an
 * {@link com.b3dgs.lionengine.game.feature.actionable.Action}. Usually a consequence of a
 * click over a button, ready to assign on map (in other this is the second part of the action).
 * 
 * @see Assignable
 * @see com.b3dgs.lionengine.game.feature.actionable.Actionable
 * @see com.b3dgs.lionengine.game.feature.actionable.Action
 */
public interface Assign
{
    /**
     * Assign the action somewhere.
     */
    void assign();
}
