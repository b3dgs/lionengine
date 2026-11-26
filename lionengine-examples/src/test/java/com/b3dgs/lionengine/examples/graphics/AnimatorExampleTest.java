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
package com.b3dgs.lionengine.examples.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphics.AnimatorState;

/**
 * Runs {@link AnimatorExample} (REQ-DOC-001, REQ-ANIM-002, REQ-ANIM-003).
 */
final class AnimatorExampleTest
{
    /**
     * Two steps per walk frame, nothing moves during the pause, the walk continues where it stopped, and the attack
     * passes its wind-up in its first step, then shows its strike until it finishes, four steps in all.
     */
    @Test
    void testFrames()
    {
        final AnimatorExample.Result result = AnimatorExample.run();

        assertEquals(List.of(Integer.valueOf(0),
                             Integer.valueOf(1),
                             Integer.valueOf(1),
                             Integer.valueOf(2),
                             Integer.valueOf(2),
                             Integer.valueOf(2),
                             Integer.valueOf(2),
                             Integer.valueOf(2),
                             Integer.valueOf(3),
                             Integer.valueOf(3),
                             Integer.valueOf(5),
                             Integer.valueOf(5),
                             Integer.valueOf(5),
                             Integer.valueOf(5)),
                     result.cells());
        assertEquals(4, result.attackSteps());
        assertEquals(AnimatorState.FINISHED, result.end());
    }
}
