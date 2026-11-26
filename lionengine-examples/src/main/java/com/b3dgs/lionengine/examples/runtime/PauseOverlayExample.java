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
import com.b3dgs.lionengine.LoopControl;
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
        final Level level = new Level(scenes, loop);

        scenes.switchTo(level);
        while (loop.getState() != LoopState.STOPPED)
        {
            loop.iterate();
            now[0] += FRAME_NANOS;
        }
        scenes.clear();
        return new Result(level.journal.toString(), level.updates, level.pauseUpdates, level.rendersUnderPause);
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

    /**
     * The level: plays, pauses once, and stops the game when its play time is over.
     */
    private static final class Level implements Scene
    {
        /** Scene manager, to push the pause menu. */
        private final SceneManager scenes;
        /** Loop control, to stop the game. */
        private final LoopControl loop;
        /** Lifecycle calls of the level and the menu. */
        private final StringBuilder journal = new StringBuilder();
        /** Play time of the level, in seconds. */
        private double playTime;
        /** {@code true} once the player has paused. */
        private boolean paused;
        /** Steps of the level. */
        private long updates;
        /** Steps of the pause menu. */
        private long pauseUpdates;
        /** Renders of the level while covered. */
        private long rendersUnderPause;

        /**
         * Creates the level.
         *
         * @param scenes The scene manager.
         * @param loop The loop control.
         */
        Level(SceneManager scenes, LoopControl loop)
        {
            super();

            this.scenes = scenes;
            this.loop = loop;
        }

        /**
         * Writes a lifecycle call in the journal.
         *
         * @param call The scene and the lifecycle method.
         */
        void note(String call)
        {
            if (journal.length() > 0)
            {
                journal.append(", ");
            }
            journal.append(call);
        }

        @Override
        public void enter()
        {
            note("level enter");
        }

        @Override
        public void update(double stepSeconds)
        {
            updates++;
            playTime += stepSeconds;
            if (!paused && playTime >= PLAY_BEFORE_PAUSE)
            {
                paused = scenes.push(new Pause(this));
            }
            if (playTime >= PLAY_SECONDS)
            {
                loop.stop();
            }
        }

        @Override
        public void render(double alpha, double unscaledDeltaSeconds)
        {
            if (scenes.getActive() != this)
            {
                rendersUnderPause++;
            }
        }

        @Override
        public void exit()
        {
            note("level exit");
        }

        @Override
        public void dispose()
        {
            note("level dispose");
        }
    }

    /**
     * The pause menu: closes itself after {@link #PAUSE_SECONDS}.
     */
    private static final class Pause implements Scene
    {
        /** The level below, for its journal and its manager. */
        private final Level level;
        /** Time the menu has been open, in seconds. */
        private double openTime;

        /**
         * Creates the menu.
         *
         * @param level The level below.
         */
        Pause(Level level)
        {
            super();

            this.level = level;
        }

        @Override
        public void enter()
        {
            level.note("pause enter");
        }

        @Override
        public void update(double stepSeconds)
        {
            level.pauseUpdates++;
            openTime += stepSeconds;
            if (openTime >= PAUSE_SECONDS)
            {
                level.scenes.pop();
            }
        }

        @Override
        public void render(double alpha, double unscaledDeltaSeconds)
        {
            // A real menu draws here, above the level: the example has no display.
        }

        @Override
        public void exit()
        {
            level.note("pause exit");
        }

        @Override
        public void dispose()
        {
            level.note("pause dispose");
        }
    }
}
