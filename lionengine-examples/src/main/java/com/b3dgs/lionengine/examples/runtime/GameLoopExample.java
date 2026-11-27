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
import com.b3dgs.lionengine.runtime.Clock;
import com.b3dgs.lionengine.runtime.GameLoop;
import com.b3dgs.lionengine.runtime.LoopConfig;
import com.b3dgs.lionengine.runtime.LoopState;
import com.b3dgs.lionengine.runtime.RenderCallback;
import com.b3dgs.lionengine.runtime.UpdateCallback;

/**
 * Runs the game loop headless: start, update 3 seconds of simulated time, stop (REQ-LOOP-007, REQ-LOOP-010).
 *
 * <p>
 * A game gives the loop its update and render callbacks, then hands it to the platform, which calls
 * {@link GameLoop#iterate()} on the runtime thread with the system clock. Without a platform, this example drives the
 * loop itself with a simulated clock that advances one 60 Hz display frame per iteration, so it runs instantly and
 * always gives the same result.
 * </p>
 *
 * @since 11.0.0
 */
public final class GameLoopExample
{
    /** Simulated time run by the example, in seconds. */
    public static final double SIMULATED_SECONDS = 3.0;

    /** Duration of one display frame at 60 Hz, in nanoseconds. */
    private static final long FRAME_NANOS = 16_666_667L;

    /**
     * Private constructor.
     */
    private GameLoopExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs the loop until 3 seconds of simulated time have passed, then stops it.
     *
     * @param log The log given to the loop for its inbox reports.
     * @return The number of updates and frames, and the final state of the loop.
     */
    public static Result run(Log log)
    {
        final SimulatedClock clock = new SimulatedClock();
        final Game game = new Game();
        final GameLoop loop = new GameLoop(LoopConfig.DEFAULT, clock, log, game, game);
        final long updates = Math.round(SIMULATED_SECONDS * LoopConfig.DEFAULT.updateRate());

        loop.iterate();
        while (game.updates < updates)
        {
            clock.nanos += FRAME_NANOS;
            loop.iterate();
        }
        loop.stop();
        loop.iterate();

        return new Result(game.updates, game.frames, loop.getState());
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
        log.log(LogLevel.INFO, "Updates and frames:", result.updates(), result.frames());
    }

    /**
     * Result of the example.
     *
     * @param updates The number of fixed steps run.
     * @param frames The number of frames rendered.
     * @param state The final state of the loop.
     * @since 11.0.0
     */
    public record Result(long updates, int frames, LoopState state)
    {
        // Value type of the example.
    }

    /**
     * Clock moved by the example, in nanoseconds.
     */
    private static final class SimulatedClock implements Clock
    {
        /** Current time, in nanoseconds. */
        private long nanos;

        /**
         * Creates a clock at time {@code 0}.
         */
        SimulatedClock()
        {
            super();
        }

        @Override
        public long nanoTime()
        {
            return nanos;
        }
    }

    /**
     * The game: a simulation that counts its steps and a presentation that counts its frames.
     */
    private static final class Game implements UpdateCallback, RenderCallback
    {
        /** Number of fixed steps. */
        private long updates;
        /** Number of frames. */
        private int frames;

        /**
         * Creates the game.
         */
        Game()
        {
            super();
        }

        @Override
        public void update(double stepSeconds)
        {
            updates++;
        }

        @Override
        public void render(double alpha, double unscaledDeltaSeconds)
        {
            frames++;
        }
    }
}
