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
 * Runs {@link SceneExample} (REQ-DOC-001, REQ-SCENE-001, REQ-SCENE-002).
 */
final class SceneExampleTest
{
    /**
     * Each scene is exited and disposed before the next is entered; 1 + 0.5 + 2 seconds at 60 Hz are 210 updates;
     * clearing the manager after the stop exits and disposes the level.
     */
    @Test
    void testRun()
    {
        final String journal = "intro enter, intro exit, intro dispose, "
                               + "menu enter, menu exit, menu dispose, "
                               + "level enter, level exit, level dispose";

        assertEquals(new SceneExample.Result(journal, 210L, LoopState.STOPPED), SceneExample.run(new Log()));
    }

    /**
     * The example runs.
     */
    @Test
    void testMain()
    {
        assertDoesNotThrow(() -> SceneExample.main(new String[0]));
    }
}
