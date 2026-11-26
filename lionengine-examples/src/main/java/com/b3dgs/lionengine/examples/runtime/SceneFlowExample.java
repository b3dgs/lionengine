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
import com.b3dgs.lionengine.runtime.SceneFlow;
import com.b3dgs.lionengine.runtime.SceneFlowBuilder;
import com.b3dgs.lionengine.runtime.TransitionKind;

/**
 * A game declared as a scene flow: introduction, menu, level and credits, linked by named triggers, with a condition
 * and an exit from any scene (REQ-SCENE-004 to REQ-SCENE-008, ADR-0036).
 *
 * <p>
 * The flow is declared in one place with a {@link SceneFlowBuilder}: each scene id {@code namespace:name} with the
 * factory creating a new instance at each entry, the start scene, and the transitions:
 * </p>
 *
 * <pre>
 * start game:intro
 * game:intro   --finished--&gt; SWITCH game:menu
 * game:menu    --start-----&gt; SWITCH game:level    (condition: a save slot is selected)
 * game:level   --finished--&gt; SWITCH game:credits
 * any scene    --quit------&gt; EXIT
 * </pre>
 *
 * <p>
 * A scene fires a trigger by name with {@link SceneFlow#trigger(String)}: here each scene fires its trigger once its
 * time is over, and the credits fire {@code quit}, which exits from any scene. The flow applies the transition at the
 * next frame boundary; {@code EXIT} leaves every scene and stops the loop. The scenes share data only through the game
 * context, here the selected save slot. The flow is given to the loop as both callbacks. Without a platform, the
 * example drives the loop with a simulated clock, as {@code SceneExample} does.
 * </p>
 *
 * @since 11.0.0
 */
public final class SceneFlowExample
{
    /** Id of the introduction. */
    public static final String INTRO = "game:intro";
    /** Id of the menu. */
    public static final String MENU = "game:menu";
    /** Id of the level. */
    public static final String LEVEL = "game:level";
    /** Id of the credits. */
    public static final String CREDITS = "game:credits";
    /** Trigger of the end of a scene. */
    public static final String FINISHED = "finished";
    /** Trigger of the start of the level. */
    public static final String START = "start";
    /** Trigger of the end of the game. */
    public static final String QUIT = "quit";

    /** Duration of one display frame at 60 Hz, in nanoseconds. */
    private static final long FRAME_NANOS = 16_666_667L;
    /** Duration of each scene, in seconds. */
    private static final double SCENE_SECONDS = 0.5;

    /**
     * Private constructor.
     */
    private SceneFlowExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Declares the flow, runs the game until it exits, and returns its journal.
     *
     * @param log The log given to the loop.
     * @return The journal of the scenes and transitions, the number of scenes created and the final loop state.
     */
    public static Result run(Log log)
    {
        final FlowStory story = new FlowStory();
        final SceneFlow<FlowStory> flow = new SceneFlowBuilder<FlowStory>()
            .scene(INTRO, s -> new TimedScene(s, "intro", SCENE_SECONDS, FINISHED))
            .scene(MENU, s -> new TimedScene(s, "menu", SCENE_SECONDS, START))
            .scene(LEVEL, s -> new TimedScene(s, "level", SCENE_SECONDS, FINISHED))
            .scene(CREDITS, s -> new TimedScene(s, "credits", SCENE_SECONDS, QUIT))
            .start(INTRO)
            .transition(INTRO, FINISHED, TransitionKind.SWITCH, MENU)
            .transition(MENU, START, FlowStory::isSlotSelected, TransitionKind.SWITCH, LEVEL)
            .transition(LEVEL, FINISHED, TransitionKind.SWITCH, CREDITS)
            .transition(SceneFlowBuilder.ANY_SCENE, QUIT, TransitionKind.EXIT, null)
            .listener((from, trigger, to) -> story.note(from + " --" + trigger + "--> " + to))
            .build();
        story.setFlow(flow);
        final long[] now = new long[1];
        final GameLoop loop = new GameLoop(LoopConfig.DEFAULT, () -> now[0], log, flow, flow);

        flow.start(story, loop);
        while (loop.getState() != LoopState.STOPPED)
        {
            loop.iterate();
            now[0] += FRAME_NANOS;
        }
        flow.clear();
        return new Result(story.getJournal(), story.getCreated(), loop.getState());
    }

    /**
     * Result of the example.
     *
     * @param journal The lifecycle calls of the scenes and the applied transitions, in order.
     * @param created The number of scenes created by the factories.
     * @param state The final state of the loop.
     * @since 11.0.0
     */
    public record Result(String journal, int created, LoopState state)
    {
        // Value type of the example.
    }
}
