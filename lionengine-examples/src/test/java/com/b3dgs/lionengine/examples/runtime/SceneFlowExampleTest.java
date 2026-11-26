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

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.runtime.LoopState;

/**
 * Runs {@link SceneFlowExample} (REQ-DOC-001, REQ-SCENE-004 to REQ-SCENE-008).
 */
final class SceneFlowExampleTest
{
    /**
     * The game goes from the introduction to the menu, to the level once a save slot is selected, to the credits,
     * then exits from any scene: each scene is left before the next is entered, and the loop stops.
     */
    @Test
    void testFlow()
    {
        final SceneFlowExample.Result result = SceneFlowExample.run(new Log());

        assertEquals("intro enter, intro exit, menu enter, game:intro --finished--> game:menu, menu exit, level enter, "
                     + "game:menu --start--> game:level, level exit, credits enter, "
                     + "game:level --finished--> game:credits, credits exit, game:credits --quit--> null",
                     result.journal());
        assertEquals(4, result.created());
        assertEquals(LoopState.STOPPED, result.state());
    }
}
