/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents something that can control the end of a sequence.
 */
public interface Sequencer
{
    /**
     * Terminate sequence.
     */
    void end();

    /**
     * Terminate sequence, and set the next sequence.
     * 
     * @param nextSequenceClass The next sequence class reference, <code>null</code> for none.
     * @param arguments The sequence arguments list if needed by its constructor.
     * @throws LionEngineException If sequence cannot be created.
     */
    void end(Class<? extends Sequencable> nextSequenceClass, Object... arguments);

    /**
     * Set next sequence and load it now. Call {@link #end()} to trigger transition.
     * 
     * @param nextSequenceClass The next sequence class reference, <code>null</code> for none.
     * @param arguments The sequence arguments list if needed by its constructor.
     * @throws LionEngineException If sequence is <code>null</code> or cannot be created.
     */
    void load(Class<? extends Sequencable> nextSequenceClass, Object... arguments);

    /**
     * Set the system cursor visibility.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    void setSystemCursorVisible(boolean visible);
}
