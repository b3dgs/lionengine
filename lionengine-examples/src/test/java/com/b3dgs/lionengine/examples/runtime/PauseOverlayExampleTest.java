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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.runtime.LoopConfig;

/**
 * Runs {@link PauseOverlayExample} (REQ-DOC-001, REQ-SCENE-003).
 */
final class PauseOverlayExampleTest
{
    /**
     * The menu is pushed and popped once, the level is never exited during the pause, its play time counts only its
     * own steps, and it is rendered under the menu during the pause.
     */
    @Test
    void testPause()
    {
        final PauseOverlayExample.Result result = PauseOverlayExample.run(new Log());

        assertEquals("level enter, pause enter, pause exit, pause dispose, level exit, level dispose", result.journal());
        assertEquals(Math.round(PauseOverlayExample.PLAY_SECONDS * LoopConfig.DEFAULT.updateRate()),
                     result.levelUpdates());
        assertEquals(Math.round(PauseOverlayExample.PAUSE_SECONDS * LoopConfig.DEFAULT.updateRate()),
                     result.pauseUpdates());
        assertTrue(result.levelRendersUnderPause() > 0, result.levelRendersUnderPause() + " renders");
    }
}
