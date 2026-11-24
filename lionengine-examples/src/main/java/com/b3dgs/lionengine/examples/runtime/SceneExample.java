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
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.LoopControl;
import com.b3dgs.lionengine.runtime.GameLoop;
import com.b3dgs.lionengine.runtime.LoopConfig;
import com.b3dgs.lionengine.runtime.LoopState;
import com.b3dgs.lionengine.runtime.Scene;
import com.b3dgs.lionengine.runtime.SceneManager;

/**
 * Splits a game into scenes switched by a scene manager: intro, then menu, then level, then stop (REQ-SCENE-001,
 * REQ-SCENE-002).
 *
 * <p>
 * The game creates each scene instance and requests the switch with {@link SceneManager#switchTo(Scene)} from the
 * running scene; the switch applies at the next frame boundary. The manager is given to the loop as both callbacks.
 * Once the loop has stopped, {@link SceneManager#clear()} exits and disposes the last scene. As in
 * {@link GameLoopExample}, the example drives the loop itself with a simulated clock, one 60 Hz display frame per
 * iteration, so it runs instantly and always gives the same result.
 * </p>
 *
 * @since 11.0.0
 */
public final class SceneExample
{
    /** Duration of the intro, in seconds. */
    public static final double INTRO_SECONDS = 1.0;
    /** Time the player spends in the menu before choosing to play, in seconds. */
    public static final double MENU_SECONDS = 0.5;
    /** Duration of the level, in seconds. */
    public static final double LEVEL_SECONDS = 2.0;

    /** Duration of one display frame at 60 Hz, in nanoseconds. */
    private static final long FRAME_NANOS = 16_666_667L;

    /**
     * Private constructor.
     */
    private SceneExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs the game from the intro until the level stops the loop, then clears the scene manager.
     *
     * @param log The log given to the loop for its inbox reports.
     * @return The lifecycle calls of the scenes, the number of updates and the final state of the loop.
     */
    public static Result run(Log log)
    {
        final long[] now = new long[1];
        final SceneManager scenes = new SceneManager();
        final GameLoop loop = new GameLoop(LoopConfig.DEFAULT, () -> now[0], log, scenes, scenes);
        final Game game = new Game(scenes, loop);

        scenes.switchTo(new Intro(game));
        while (loop.getState() != LoopState.STOPPED)
        {
            loop.iterate();
            now[0] += FRAME_NANOS;
        }
        scenes.clear();

        return new Result(game.journal.toString(), game.updates, loop.getState());
    }

    /**
     * Runs the example and logs the result.
     *
     * @param args Unused.
     */
    // CHECKSTYLE IGNORE LINE: UncommentedMain
    public static void main(String[] args)
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);

        final Result result = run(log);
        log.log(LogLevel.INFO, "Updates of the three scenes:", result.updates());
    }

    /**
     * Result of the example.
     *
     * @param journal The lifecycle calls of the scenes, in order, such as {@code "intro enter, intro exit, ..."}.
     * @param updates The number of fixed steps run by the scenes.
     * @param state The final state of the loop.
     * @since 11.0.0
     */
    public record Result(String journal, long updates, LoopState state)
    {
        // Value type of the example.
    }

    /**
     * What the scenes share: the scene manager, the loop control and the records of the example.
     */
    private static final class Game
    {
        /** Scene manager, to request switches. */
        private final SceneManager scenes;
        /** Loop control, to stop the game. */
        private final LoopControl loop;
        /** Lifecycle calls of the scenes. */
        private final StringBuilder journal = new StringBuilder();
        /** Number of fixed steps run by the scenes. */
        private long updates;

        /**
         * Creates the shared state.
         *
         * @param scenes The scene manager.
         * @param loop The loop control.
         */
        Game(SceneManager scenes, LoopControl loop)
        {
            this.scenes = scenes;
            this.loop = loop;
        }

        /**
         * Records a lifecycle call.
         *
         * @param scene The scene name.
         * @param call The lifecycle method.
         */
        void record(String scene, String call)
        {
            if (journal.length() > 0)
            {
                journal.append(", ");
            }
            journal.append(scene).append(' ').append(call);
        }
    }

    /**
     * Scene that lasts a given time, then calls {@link #finish()} once.
     */
    private abstract static class TimedScene implements Scene
    {
        /** Shared state. */
        protected final Game game;
        /** Name in the records. */
        private final String name;
        /** Duration, in seconds. */
        private final double seconds;
        /** Number of steps run. */
        private long steps;

        /**
         * Creates the scene.
         *
         * @param game The shared state.
         * @param name The name in the records.
         * @param seconds The duration, in seconds.
         */
        TimedScene(Game game, String name, double seconds)
        {
            this.game = game;
            this.name = name;
            this.seconds = seconds;
        }

        /**
         * Called once, at the step where the duration is reached.
         */
        protected abstract void finish();

        @Override
        public void enter()
        {
            game.record(name, "enter");
        }

        @Override
        public void update(double stepSeconds)
        {
            steps++;
            game.updates++;
            if (steps == Math.round(seconds / stepSeconds))
            {
                finish();
            }
        }

        @Override
        public void render(double alpha, double unscaledDeltaSeconds)
        {
            // A real scene draws here: the example has no display.
        }

        @Override
        public void exit()
        {
            game.record(name, "exit");
        }

        @Override
        public void dispose()
        {
            game.record(name, "dispose");
        }
    }

    /**
     * Intro: lasts one second, then shows the menu.
     */
    private static final class Intro extends TimedScene
    {
        /**
         * Creates the scene.
         *
         * @param game The shared state.
         */
        Intro(Game game)
        {
            super(game, "intro", INTRO_SECONDS);
        }

        @Override
        protected void finish()
        {
            game.scenes.switchTo(new Menu(game));
        }
    }

    /**
     * Menu: the player chooses to play after half a second, which starts the level.
     */
    private static final class Menu extends TimedScene
    {
        /**
         * Creates the scene.
         *
         * @param game The shared state.
         */
        Menu(Game game)
        {
            super(game, "menu", MENU_SECONDS);
        }

        @Override
        protected void finish()
        {
            game.scenes.switchTo(new Level(game));
        }
    }

    /**
     * Level: lasts two seconds, then stops the game.
     */
    private static final class Level extends TimedScene
    {
        /**
         * Creates the scene.
         *
         * @param game The shared state.
         */
        Level(Game game)
        {
            super(game, "level", LEVEL_SECONDS);
        }

        @Override
        protected void finish()
        {
            game.loop.stop();
        }
    }
}
