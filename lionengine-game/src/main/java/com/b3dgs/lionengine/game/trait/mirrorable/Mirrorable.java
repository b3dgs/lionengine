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
package com.b3dgs.lionengine.game.trait.mirrorable;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.game.trait.Trait;

/**
 * Represents something that can be mirrored on different axis.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Mirror
 */
public interface Mirrorable extends Trait, Updatable
{
    /**
     * Set the next mirror state and apply it on next {@link Updatable#update(double)} call.
     * 
     * @param state The next mirror state.
     */
    void mirror(Mirror state);

    /**
     * Get current mirror state.
     * 
     * @return The current mirror state.
     */
    Mirror getMirror();
}
