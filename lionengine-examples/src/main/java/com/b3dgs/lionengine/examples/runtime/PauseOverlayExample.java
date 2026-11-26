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

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.runtime.GameLoop;
import com.b3dgs.lionengine.runtime.LoopConfig;
import com.b3dgs.lionengine.runtime.LoopState;
import com.b3dgs.lionengine.runtime.Scene;
import com.b3dgs.lionengine.runtime.SceneManager;

/**
 * A level paused by a pause menu pushed above it, then resumed when the menu is popped (REQ-SCENE-003, ADR-0059).
 *
 * <p>
 * The level calls {@link SceneManager#push(Scene)} when the player pauses; at the next frame boundary the pause menu
 * is entered on top of the stack. From then on only the menu is updated, so the level's time stands still, but both
 * are rendered, the level first: a game draws the frozen level under the menu. The menu calls
 * {@link SceneManager#pop()} to resume: it is exited and disposed, and the level continues where it was, without
 * being entered again. Without a platform, the example drives the loop with a simulated clock, as
 * {@code SceneExample} does.
 * </p>
 *
 * @since 11.0.0
 */
public final class PauseOverlayExample
{
    /** Play time of the level before the player pauses, in seconds. */
    public static final double PLAY_BEFORE_PAUSE = 0.5;
    /** Time the pause menu stays open, in seconds. */
    public static final double PAUSE_SECONDS = 0.25;
    /** Total play time of the level, in seconds; the game stops then. */
    public static final double PLAY_SECONDS = 1.0;

    /** Duration of one display frame at 60 Hz, in nanoseconds. */
    private static final long FRAME_NANOS = 16_666_667L;

    /**
     * Private constructor.
     */
    private PauseOverlayExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Plays the level, pauses it, resumes it, and stops once its play time is over.
     *
     * @param log The log given to the loop.
     * @return The journal of the lifecycle calls and the counts of updates and renders.
     */
    public static Result run(Log log)
    {
        final long[] now = new long[1];
        final SceneManager scenes = new SceneManager();
        final GameLoop loop = new GameLoop(LoopConfig.DEFAULT, () -> now[0], log, scenes, scenes);
        final PausableLevel level = new PausableLevel(scenes, loop);

        scenes.switchTo(level);
        while (loop.getState() != LoopState.STOPPED)
        {
            loop.iterate();
            now[0] += FRAME_NANOS;
        }
        scenes.clear();
        return new Result(level.getJournal(),
                          level.getUpdates(),
                          level.getPauseUpdates(),
                          level.getRendersUnderPause());
    }

    /**
     * Result of the example.
     *
     * @param journal The lifecycle calls of the level and the pause menu, in order.
     * @param levelUpdates The number of steps of the level: its play time only.
     * @param pauseUpdates The number of steps of the pause menu.
     * @param levelRendersUnderPause The number of renders of the level while the menu covered it.
     * @since 11.0.0
     */
    public record Result(String journal, long levelUpdates, long pauseUpdates, long levelRendersUnderPause)
    {
        // Value type of the example.
    }
}
