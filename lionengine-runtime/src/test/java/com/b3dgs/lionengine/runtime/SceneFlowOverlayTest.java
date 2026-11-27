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
package com.b3dgs.lionengine.runtime;

import static com.b3dgs.lionengine.runtime.RecordingScene.DISPOSE;
import static com.b3dgs.lionengine.runtime.RecordingScene.ENTER;
import static com.b3dgs.lionengine.runtime.RecordingScene.EXIT;
import static com.b3dgs.lionengine.runtime.RecordingScene.RENDER;
import static com.b3dgs.lionengine.runtime.RecordingScene.UPDATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.LoopControl;

/**
 * Test the {@code PUSH} and {@code POP} transitions of {@link SceneFlow}: an overlay opened and closed at the frame
 * boundary, resolution against the overlay, a switch or an exit from an overlay, the id stack, the listener, a pop
 * without overlay, and the validation of their shapes (REQ-SCENE-005 to REQ-SCENE-008, ADR-0036, ADR-0059).
 */
final class SceneFlowOverlayTest
{
    /** Step duration, in seconds. */
    private static final double STEP = 0.01;
    /** Presentation factor. */
    private static final double ALPHA = 0.5;
    /** Id of the level. */
    private static final String LEVEL = "game:level";
    /** Id of the pause menu. */
    private static final String PAUSE = "game:pause";
    /** Id of the main menu. */
    private static final String MENU = "game:menu";
    /** Trigger opening the pause menu. */
    private static final String PAUSE_TRIGGER = "pause";
    /** Trigger closing the pause menu. */
    private static final String RESUME = "resume";
    /** Trigger going back to the main menu. */
    private static final String BACK = "back";
    /** Trigger ending the game. */
    private static final String QUIT = "quit";

    /**
     * Loop control recording its stop.
     */
    private static final class Loop implements LoopControl
    {
        /** Number of stop requests. */
        private int stops;

        /**
         * Creates the loop.
         */
        Loop()
        {
            super();
        }

        @Override
        public void pause()
        {
            // Not used by the flow.
        }

        @Override
        public void resume()
        {
            // Not used by the flow.
        }

        @Override
        public void stop()
        {
            stops++;
        }

        @Override
        public void setTimeScale(double scale)
        {
            // Not used by the flow.
        }
    }

    /** Records of every scene of a test. */
    private final List<String> calls = new ArrayList<>();
    /** The scenes created, in order. */
    private final List<RecordingScene> created = new ArrayList<>();
    /** The applied transitions. */
    private final List<String> transitions = new ArrayList<>();
    /** The loop. */
    private final Loop loop = new Loop();

    /**
     * Returns a factory of recording scenes named after their id.
     *
     * @param id The scene id.
     * @return The factory.
     */
    private SceneFactory<Object> factory(String id)
    {
        return context ->
        {
            final RecordingScene scene = new RecordingScene(id, calls);
            created.add(scene);
            return scene;
        };
    }

    /**
     * Returns a builder of a level with a pause menu, a main menu, and a quit from any scene.
     *
     * @return The builder.
     */
    private SceneFlowBuilder<Object> builder()
    {
        return new SceneFlowBuilder<>().scene(LEVEL, factory(LEVEL))
                                       .scene(PAUSE, factory(PAUSE))
                                       .scene(MENU, factory(MENU))
                                       .start(LEVEL)
                                       .transition(LEVEL, PAUSE_TRIGGER, TransitionKind.PUSH, PAUSE)
                                       .transition(PAUSE, RESUME, TransitionKind.POP, null)
                                       .transition(PAUSE, BACK, TransitionKind.SWITCH, MENU)
                                       .transition(SceneFlowBuilder.ANY_SCENE, QUIT, TransitionKind.EXIT, null)
                                       .listener((from, trigger, to) -> transitions.add(from
                                                                                        + ' '
                                                                                        + trigger
                                                                                        + ' '
                                                                                        + to));
    }

    /**
     * Runs a frame: one update then a render.
     *
     * @param flow The flow.
     */
    private static void frame(SceneFlow<?> flow)
    {
        flow.update(STEP);
        flow.render(ALPHA, STEP);
    }

    /**
     * Starts a flow on the level, paused, and clears the records.
     *
     * @return The flow.
     */
    private SceneFlow<Object> startPaused()
    {
        final SceneFlow<Object> flow = builder().build();
        flow.start(null, loop);
        frame(flow);
        assertTrue(flow.trigger(PAUSE_TRIGGER));
        frame(flow);
        calls.clear();
        transitions.clear();
        return flow;
    }

    /**
     * A push opens the overlay at the frame boundary: the level is not exited, only the overlay is updated, both are
     * rendered, and the ids list the overlay first.
     */
    @Test
    void testPush()
    {
        final SceneFlow<Object> flow = builder().build();
        flow.start(null, loop);
        frame(flow);
        calls.clear();

        assertTrue(flow.trigger(PAUSE_TRIGGER));
        frame(flow);

        assertEquals(List.of(PAUSE + ' ' + ENTER,
                             PAUSE + ' ' + UPDATE,
                             LEVEL + ' ' + RENDER,
                             PAUSE + ' ' + RENDER),
                     calls);
        assertEquals(List.of(PAUSE, LEVEL), flow.getSceneIds());
        assertEquals(List.of(LEVEL + ' ' + PAUSE_TRIGGER + ' ' + PAUSE), transitions);
    }

    /**
     * Triggers resolve against the overlay; a pop closes it and the level resumes; the listener gets no target.
     */
    @Test
    void testPop()
    {
        final SceneFlow<Object> flow = startPaused();

        assertTrue(flow.trigger(RESUME));
        frame(flow);

        assertEquals(List.of(PAUSE + ' ' + EXIT, PAUSE + ' ' + DISPOSE, LEVEL + ' ' + UPDATE, LEVEL + ' ' + RENDER),
                     calls);
        assertEquals(List.of(LEVEL), flow.getSceneIds());
        assertEquals(List.of(PAUSE + ' ' + RESUME + " null"), transitions);
        assertTrue(flow.trigger(PAUSE_TRIGGER));
        frame(flow);
        assertNotSame(created.get(1), created.get(2));
    }

    /**
     * A switch from the overlay replaces the whole stack, the overlay left first.
     */
    @Test
    void testSwitchFromOverlay()
    {
        final SceneFlow<Object> flow = startPaused();

        assertTrue(flow.trigger(BACK));
        frame(flow);

        assertEquals(List.of(PAUSE + ' ' + EXIT,
                             PAUSE + ' ' + DISPOSE,
                             LEVEL + ' ' + EXIT,
                             LEVEL + ' ' + DISPOSE,
                             MENU + ' ' + ENTER,
                             MENU + ' ' + UPDATE,
                             MENU + ' ' + RENDER),
                     calls);
        assertEquals(List.of(MENU), flow.getSceneIds());
    }

    /**
     * An exit from the overlay leaves every scene from the top and stops the loop.
     */
    @Test
    void testExitFromOverlay()
    {
        final SceneFlow<Object> flow = startPaused();

        assertTrue(flow.trigger(QUIT));
        frame(flow);

        assertEquals(List.of(PAUSE + ' ' + EXIT, PAUSE + ' ' + DISPOSE, LEVEL + ' ' + EXIT, LEVEL + ' ' + DISPOSE),
                     calls);
        assertTrue(flow.getSceneIds().isEmpty());
        assertEquals(1, loop.stops);
    }

    /**
     * A pop from any scene without an overlay is selected, then refused at application with an
     * {@link IllegalStateException}; the stack is unchanged and the listener is not called.
     */
    @Test
    void testPopWithoutOverlay()
    {
        final SceneFlow<Object> flow = new SceneFlowBuilder<>().scene(LEVEL, factory(LEVEL))
                                                               .start(LEVEL)
                                                               .transition(SceneFlowBuilder.ANY_SCENE,
                                                                           BACK,
                                                                           TransitionKind.POP,
                                                                           null)
                                                               .listener((from, trigger, to) -> transitions.add(to))
                                                               .build();
        flow.start(null, loop);
        frame(flow);

        assertTrue(flow.trigger(BACK));
        assertThrows(IllegalStateException.class, () -> flow.update(STEP));

        assertEquals(List.of(LEVEL), flow.getSceneIds());
        assertTrue(transitions.isEmpty());
    }

    /**
     * A pushed scene whose enter throws is disposed and not added: the ids are unchanged.
     */
    @Test
    void testPushFailure()
    {
        final SceneFactory<Object> failing = context ->
        {
            final RecordingScene pause = new RecordingScene(PAUSE, calls);
            pause.failOn(ENTER);
            return pause;
        };
        final SceneFlow<Object> flow = new SceneFlowBuilder<>().scene(LEVEL, factory(LEVEL))
                                                               .scene(PAUSE, failing)
                                                               .start(LEVEL)
                                                               .transition(LEVEL,
                                                                           PAUSE_TRIGGER,
                                                                           TransitionKind.PUSH,
                                                                           PAUSE)
                                                               .build();
        flow.start(null, loop);
        frame(flow);

        flow.trigger(PAUSE_TRIGGER);
        assertThrows(IllegalStateException.class, () -> flow.update(STEP));

        assertEquals(List.of(LEVEL), flow.getSceneIds());
    }

    /**
     * Builds a flow with one more transition and returns the error of the build.
     *
     * @param from The source of the transition.
     * @param kind The kind of the transition.
     * @param to The target of the transition.
     * @return The message of the error.
     */
    private String buildError(String from, TransitionKind kind, String to)
    {
        final SceneFlowBuilder<Object> builder = builder().transition(from, "close", kind, to);
        return assertThrows(IllegalStateException.class, builder::build).getMessage();
    }

    /**
     * A push needs a declared target; a pop takes no target and must start from a scene some push opens, or from any
     * scene.
     */
    @Test
    void testInvalidShapes()
    {
        assertTrue(buildError(LEVEL, TransitionKind.PUSH, null).startsWith("No target for PUSH"));
        assertTrue(buildError(LEVEL, TransitionKind.PUSH, "game:none").startsWith("Target scene not declared"));
        assertTrue(buildError(PAUSE, TransitionKind.POP, LEVEL).startsWith("Target given to POP"));
        assertTrue(buildError(MENU, TransitionKind.POP, null).startsWith("POP from a scene that no PUSH opens"));
        assertEquals(List.of(), builder().transition(SceneFlowBuilder.ANY_SCENE, "close", TransitionKind.POP, null)
                                         .build()
                                         .getSceneIds());
    }
}
