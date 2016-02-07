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
package com.b3dgs.lionengine.game.state;

import com.b3dgs.lionengine.core.InputDevice;

/**
 * Represents the {@link InputDevice} updater for the state.
 * <p>
 * {@link StateTransition} <b>must not implements this interface</b> to check input, but must instead use specific
 * interfaces, such as:
 * </p>
 * <ul>
 * <li>{@link StateTransitionInputDirectionalChecker}</li>
 * <li>{@link StateTransitionInputPointerChecker}</li>
 * </ul>
 * 
 * @param <I> The input device type.
 */
public interface StateTransitionInputChecker<I extends InputDevice>
{
    /**
     * Check if transition is effective.
     * 
     * @param input The input device reference.
     * @return <code>true</code> if transition can be made, <code>false</code> else.
     */
    boolean check(I input);
}
