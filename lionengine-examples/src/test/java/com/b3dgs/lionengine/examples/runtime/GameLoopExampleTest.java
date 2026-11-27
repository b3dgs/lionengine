/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.examples.runtime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.runtime.LoopState;

/**
 * Runs {@link GameLoopExample} (REQ-DOC-001, REQ-LOOP-007, REQ-LOOP-010).
 */
final class GameLoopExampleTest
{
    /**
     * Three seconds at 60 Hz are 180 updates; the start and stop iterations render too.
     */
    @Test
    void testRun()
    {
        assertEquals(new GameLoopExample.Result(180L, 182, LoopState.STOPPED), GameLoopExample.run(new Log()));
    }

    /**
     * The example runs.
     */
    @Test
    void testMain()
    {
        assertDoesNotThrow(() -> GameLoopExample.main(new String[0]));
    }
}
