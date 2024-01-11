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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Updatable;

/**
 * Represents something that can be mirrored on different axis.
 * 
 * @see Mirror
 */
@FeatureInterface
public interface Mirrorable extends RoutineUpdate, Listenable<MirrorableListener>
{
    /**
     * Set the next mirror state and apply it on next {@link Updatable#update(double)} call.
     * 
     * @param state The next mirror state (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void mirror(Mirror state);

    /**
     * Get current mirror state.
     * 
     * @return The current mirror state.
     */
    Mirror getMirror();

    /**
     * Check if is current mirror state.
     * 
     * @param mirror The expected state to be (can be <code>null</code>).
     * @return <code>true</code> if is mirror, <code>false</code> else or <code>null</code>.
     */
    boolean is(Mirror mirror);
}
