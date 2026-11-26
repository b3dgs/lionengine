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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.b3dgs.lionengine.LoopControl;
import com.sun.management.ThreadMXBean;

/**
 * Test {@link SceneFlow} and {@link SceneFlowBuilder} with {@code SWITCH} and {@code EXIT}: start scene, new instance
 * per entry, context, resolution order, conditions, any-scene fallback, one pending transition, application at the
 * frame boundary, exit, listener, validation errors and allocation (REQ-SCENE-004 to REQ-SCENE-008, ADR-0036).
 */
final class SceneFlowTest
{
    /** Step duration, in seconds. */
    private static final double STEP = 0.01;
    /** Presentation factor. */
    private static final double ALPHA = 0.5;
    /** Id of the introduction. */
    private static final String INTRO = "game:intro";
    /** Id of the menu. */
    private static final String MENU = "game:menu";
    /** Id of the level. */
    private static final String LEVEL = "game:level";
    /** Id of the credits. */
    private static final String CREDITS = "game:credits";
    /** Trigger of the end of a scene. */
    private static final String FINISHED = "finished";
    /** Trigger of the start of the level. */
    private static final String START = "start";
    /** Trigger of the end of the game. */
    private static final String QUIT = "quit";
    /** Frames of the allocation measurement. */
    private static final int FRAMES = 1_000;
    /** Bytes tolerated for the measurement itself: far below one allocation per frame. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /**
     * Game context of the tests.
     */
    private static final class Game
    {
        /** {@code true} once a save slot is selected. */
        private boolean slot;
        /** Number of scenes created. */
        private int created;

        /**
         * Creates the context.
         */
        Game()
        {
            super();
        }
    }

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
    /** The game context. */
    private final Game game = new Game();
    /** The loop. */
    private final Loop loop = new Loop();

    /**
     * Returns a factory of recording scenes named after their id, counting its scenes in the context.
     *
     * @param id The scene id.
     * @return The factory.
     */
    private SceneFactory<Game> factory(String id)
    {
        return context ->
        {
            context.created++;
            final RecordingScene scene = new RecordingScene(id, calls);
            created.add(scene);
            return scene;
        };
    }

    /**
     * Returns a builder of the four scenes of the example of ADR-0036, started at the introduction.
     *
     * @return The builder.
     */
    private SceneFlowBuilder<Game> builder()
    {
        return new SceneFlowBuilder<Game>().scene(INTRO, factory(INTRO))
                                           .scene(MENU, factory(MENU))
                                           .scene(LEVEL, factory(LEVEL))
                                           .scene(CREDITS, factory(CREDITS))
                                           .start(INTRO);
    }

    /**
     * Runs a frame: updates then a render.
     *
     * @param flow The flow.
     * @param updates The number of updates.
     */
    private static void frame(SceneFlow<?> flow, int updates)
    {
        for (int i = 0; i < updates; i++)
        {
            flow.update(STEP);
        }
        flow.render(ALPHA, STEP);
    }

    /**
     * The start scene is created with the context and entered at the first frame boundary; the flow refuses triggers
     * and calls before it is started, and a second start.
     */
    @Test
    void testStart()
    {
        final SceneFlow<Game> flow = builder().build();

        assertThrows(IllegalStateException.class, () -> flow.trigger(FINISHED));
        assertThrows(IllegalStateException.class, () -> flow.update(STEP));
        assertThrows(IllegalArgumentException.class, () -> flow.start(game, null));
        flow.start(game, loop);
        assertThrows(IllegalStateException.class, () -> flow.start(game, loop));
        assertTrue(flow.isTransitionPending());
        assertFalse(flow.trigger(FINISHED));
        assertTrue(flow.getSceneIds().isEmpty());

        frame(flow, 1);

        assertEquals(List.of(INTRO + ' ' + ENTER, INTRO + ' ' + UPDATE, INTRO + ' ' + RENDER), calls);
        assertEquals(List.of(INTRO), flow.getSceneIds());
        assertEquals(1, game.created);
        assertFalse(flow.isTransitionPending());
    }

    /**
     * A switch selected during an update applies at the next frame boundary: the other steps of the frame still update
     * the source; then the source is left, a new target is entered, and the listener is called before its update.
     */
    @Test
    void testSwitchAtFrameBoundary()
    {
        final List<String> transitions = new ArrayList<>();
        final SceneFlow<Game> flow = builder().transition(INTRO, FINISHED, TransitionKind.SWITCH, MENU)
                                              .listener((from, trigger, to) -> transitions.add(from
                                                                                               + ' '
                                                                                               + trigger
                                                                                               + ' '
                                                                                               + to
                                                                                               + ' '
                                                                                               + calls.size()))
                                              .build();
        flow.start(game, loop);
        frame(flow, 1);
        calls.clear();
        created.get(0).setAction(UPDATE, () -> flow.trigger(FINISHED));

        frame(flow, 2);
        frame(flow, 1);

        assertEquals(List.of(INTRO + ' ' + UPDATE,
                             INTRO + ' ' + UPDATE,
                             INTRO + ' ' + RENDER,
                             INTRO + ' ' + EXIT,
                             INTRO + ' ' + DISPOSE,
                             MENU + ' ' + ENTER,
                             MENU + ' ' + UPDATE,
                             MENU + ' ' + RENDER),
                     calls);
        assertEquals(List.of(INTRO + ' ' + FINISHED + ' ' + MENU + " 6"), transitions);
        assertEquals(List.of(MENU), flow.getSceneIds());
    }

    /**
     * Every entry creates a new instance from the factory, with the context.
     */
    @Test
    void testNewInstancePerEntry()
    {
        final SceneFlow<Game> flow = builder().transition(INTRO, FINISHED, TransitionKind.SWITCH, MENU)
                                              .transition(MENU, START, TransitionKind.SWITCH, LEVEL)
                                              .transition(LEVEL, FINISHED, TransitionKind.SWITCH, MENU)
                                              .build();
        flow.start(game, loop);
        frame(flow, 1);

        for (final String trigger : List.of(FINISHED, START, FINISHED))
        {
            assertTrue(flow.trigger(trigger));
            frame(flow, 1);
        }

        assertEquals(4, game.created);
        assertNotSame(created.get(1), created.get(3));
        assertEquals(List.of(MENU), flow.getSceneIds());
    }

    /**
     * Resolution tries the transitions of the top scene in declaration order, then those of any scene; a false
     * condition is skipped; no match returns {@code false} and changes nothing.
     */
    @Test
    void testResolutionOrder()
    {
        final SceneFlow<Game> flow = builder().transition(SceneFlowBuilder.ANY_SCENE,
                                                          START,
                                                          TransitionKind.SWITCH,
                                                          CREDITS)
                                              .transition(INTRO, FINISHED, TransitionKind.SWITCH, MENU)
                                              .transition(MENU, START, g -> g.slot, TransitionKind.SWITCH, LEVEL)
                                              .transition(MENU, START, TransitionKind.SWITCH, INTRO)
                                              .transition(SceneFlowBuilder.ANY_SCENE,
                                                          FINISHED,
                                                          TransitionKind.EXIT,
                                                          null)
                                              .build();
        flow.start(game, loop);
        frame(flow, 1);

        assertFalse(flow.trigger("unknown"));
        assertFalse(flow.isTransitionPending());
        assertTrue(flow.trigger(FINISHED));
        frame(flow, 1);
        assertEquals(List.of(MENU), flow.getSceneIds());

        assertTrue(flow.trigger(START));
        frame(flow, 1);
        assertEquals(List.of(INTRO), flow.getSceneIds());

        flow.trigger(FINISHED);
        frame(flow, 1);
        game.slot = true;
        assertTrue(flow.trigger(START));
        frame(flow, 1);
        assertEquals(List.of(LEVEL), flow.getSceneIds());

        assertTrue(flow.trigger(START));
        frame(flow, 1);
        assertEquals(List.of(CREDITS), flow.getSceneIds());
        assertEquals(0, loop.stops);
    }

    /**
     * One transition is pending at a time: later triggers of the frame return {@code false}, whatever they match.
     */
    @Test
    void testOnePending()
    {
        final SceneFlow<Game> flow = builder().transition(INTRO, FINISHED, TransitionKind.SWITCH, MENU)
                                              .transition(INTRO, START, TransitionKind.SWITCH, LEVEL)
                                              .build();
        flow.start(game, loop);
        frame(flow, 1);

        assertTrue(flow.trigger(FINISHED));
        assertFalse(flow.trigger(START));
        assertFalse(flow.trigger(FINISHED));
        assertTrue(flow.isTransitionPending());
        frame(flow, 3);

        assertEquals(List.of(MENU), flow.getSceneIds());
        assertEquals(2, game.created);
    }

    /**
     * An exit leaves every scene, requests the stop of the loop and calls the listener with no target; triggers then
     * return {@code false}.
     */
    @Test
    void testExit()
    {
        final List<String> transitions = new ArrayList<>();
        final SceneFlow<Game> flow = builder().transition(SceneFlowBuilder.ANY_SCENE, QUIT, TransitionKind.EXIT, null)
                                              .listener((from, trigger, to) -> transitions.add(from
                                                                                               + ' '
                                                                                               + trigger
                                                                                               + ' '
                                                                                               + to))
                                              .build();
        flow.start(game, loop);
        frame(flow, 1);
        calls.clear();

        assertTrue(flow.trigger(QUIT));
        frame(flow, 1);

        assertEquals(List.of(INTRO + ' ' + EXIT, INTRO + ' ' + DISPOSE), calls);
        assertEquals(1, loop.stops);
        assertEquals(List.of(INTRO + ' ' + QUIT + " null"), transitions);
        assertTrue(flow.getSceneIds().isEmpty());
        assertFalse(flow.trigger(QUIT));
        flow.clear();
    }

    /**
     * Clear leaves the remaining scene and cancels a pending transition.
     */
    @Test
    void testClear()
    {
        final SceneFlow<Game> flow = builder().transition(INTRO, FINISHED, TransitionKind.SWITCH, MENU).build();
        flow.start(game, loop);
        frame(flow, 1);
        flow.trigger(FINISHED);
        calls.clear();

        flow.clear();
        flow.clear();

        assertEquals(List.of(INTRO + ' ' + EXIT, INTRO + ' ' + DISPOSE), calls);
        assertFalse(flow.isTransitionPending());
        assertTrue(flow.getSceneIds().isEmpty());
    }

    /**
     * A factory returning {@code null} or throwing fails the frame boundary; an invalid trigger name is refused.
     */
    @Test
    void testFactoryFailures()
    {
        final SceneFlow<Game> nullFactory = new SceneFlowBuilder<Game>().scene(INTRO, context -> null)
                                                                        .start(INTRO)
                                                                        .build();
        nullFactory.start(game, loop);
        assertThrows(IllegalStateException.class, () -> nullFactory.update(STEP));

        final SceneFlow<Game> failing = new SceneFlowBuilder<Game>().scene(INTRO, context ->
        {
            throw new IllegalArgumentException("factory");
        }).start(INTRO).build();
        failing.start(game, loop);
        assertEquals("factory", assertThrows(IllegalArgumentException.class, () -> failing.render(ALPHA, STEP))
                                                                                                    .getMessage());

        final SceneFlow<Game> flow = builder().build();
        flow.start(game, loop);
        assertThrows(IllegalArgumentException.class, () -> flow.trigger("Finished"));
        assertThrows(IllegalArgumentException.class, () -> flow.trigger(null));
    }

    /**
     * Returns the invalid declarations and the start of the expected message.
     *
     * @return The cases: declarations applied to a builder of the four scenes, start of the message.
     */
    static Stream<Arguments> invalid()
    {
        final List<Arguments> cases = new ArrayList<>();
        cases.add(Arguments.of((Consumer<SceneFlowBuilder<Object>>) b -> b.scene("menu", c -> null),
                               "Invalid scene id"));
        cases.add(Arguments.of((Consumer<SceneFlowBuilder<Object>>) b -> b.scene("game:Menu", c -> null),
                               "Invalid scene id"));
        cases.add(Arguments.of((Consumer<SceneFlowBuilder<Object>>) b -> b.scene(MENU, c -> null),
                               "Scene id declared twice"));
        cases.add(Arguments.of((Consumer<SceneFlowBuilder<Object>>) b -> b.start(MENU), "Start scene given twice"));
        cases.add(Arguments.of((Consumer<SceneFlowBuilder<Object>>) b -> b.transition("game:pause",
                                                                                       FINISHED,
                                                                                       TransitionKind.SWITCH,
                                                                                       MENU),
                               "Source scene not declared"));
        cases.add(Arguments.of((Consumer<SceneFlowBuilder<Object>>) b -> b.transition(INTRO,
                                                                                       "Finished",
                                                                                       TransitionKind.SWITCH,
                                                                                       MENU),
                               "Invalid trigger name"));
        cases.add(Arguments.of((Consumer<SceneFlowBuilder<Object>>) b -> b.transition(INTRO,
                                                                                       FINISHED,
                                                                                       TransitionKind.SWITCH,
                                                                                       null),
                               "No target for a SWITCH"));
        cases.add(Arguments.of((Consumer<SceneFlowBuilder<Object>>) b -> b.transition(INTRO,
                                                                                       FINISHED,
                                                                                       TransitionKind.SWITCH,
                                                                                       "game:pause"),
                               "Target scene not declared"));
        cases.add(Arguments.of((Consumer<SceneFlowBuilder<Object>>) b -> b.transition(INTRO,
                                                                                       QUIT,
                                                                                       TransitionKind.EXIT,
                                                                                       MENU),
                               "Target given to an EXIT"));
        return cases.stream();
    }

    /**
     * Each invalid declaration makes the build fail with an {@link IllegalStateException} naming the problem.
     *
     * @param declaration The invalid declaration.
     * @param message The start of the expected message.
     */
    @ParameterizedTest
    @MethodSource("invalid")
    void testInvalid(Consumer<SceneFlowBuilder<Object>> declaration, String message)
    {
        final SceneFlowBuilder<Object> builder = new SceneFlowBuilder<>().scene(INTRO, c -> null)
                                                                         .scene(MENU, c -> null)
                                                                         .start(INTRO);
        declaration.accept(builder);

        final IllegalStateException error = assertThrows(IllegalStateException.class, builder::build);

        assertTrue(error.getMessage().startsWith(message), error.getMessage());
    }

    /**
     * A flow without start, or with an undeclared start, cannot be built; {@code null} arguments are refused at once.
     */
    @Test
    void testInvalidStartAndNulls()
    {
        final SceneFlowBuilder<Object> none = new SceneFlowBuilder<>().scene(INTRO, c -> null);
        assertEquals("No start scene", assertThrows(IllegalStateException.class, none::build).getMessage());
        none.start(MENU);
        assertTrue(assertThrows(IllegalStateException.class, none::build).getMessage()
                                                                           .startsWith("Start scene not declared"));
        assertThrows(IllegalArgumentException.class, () -> none.scene(null, c -> null));
        assertThrows(IllegalArgumentException.class, () -> none.scene(MENU, null));
        assertThrows(IllegalArgumentException.class, () -> none.start(null));
        assertThrows(IllegalArgumentException.class, () -> none.transition(null, QUIT, TransitionKind.EXIT, null));
        assertThrows(IllegalArgumentException.class, () -> none.transition(INTRO, null, TransitionKind.EXIT, null));
        assertThrows(IllegalArgumentException.class, () -> none.transition(INTRO, QUIT, null, null));
        assertThrows(IllegalArgumentException.class,
                     () -> none.transition(INTRO, QUIT, null, TransitionKind.EXIT, null));
    }

    /**
     * A builder builds independent flows, each with its own state.
     */
    @Test
    void testSeveralFlows()
    {
        final SceneFlowBuilder<Game> builder = builder().transition(INTRO, FINISHED, TransitionKind.SWITCH, MENU);
        final SceneFlow<Game> first = builder.build();
        final SceneFlow<Game> second = builder.build();
        first.start(game, loop);
        second.start(game, loop);
        frame(first, 1);
        frame(second, 1);

        first.trigger(FINISHED);
        frame(first, 1);

        assertEquals(List.of(MENU), first.getSceneIds());
        assertEquals(List.of(INTRO), second.getSceneIds());
    }

    /**
     * Updates, renders and unmatched triggers allocate nothing in steady state.
     */
    @Test
    void testSteadyStateAllocatesNothing()
    {
        final SceneFlow<Game> flow = new SceneFlowBuilder<Game>().scene(INTRO, context -> new EmptyScene())
                                                                 .start(INTRO)
                                                                 .transition(INTRO,
                                                                             FINISHED,
                                                                             g -> g.slot,
                                                                             TransitionKind.SWITCH,
                                                                             INTRO)
                                                                 .build();
        flow.start(game, loop);
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        frames(flow);

        final long before = bean.getCurrentThreadAllocatedBytes();
        frames(flow);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
    }

    /**
     * Runs frames firing a trigger whose condition is false.
     *
     * @param flow The flow.
     */
    private static void frames(SceneFlow<Game> flow)
    {
        for (int i = 0; i < FRAMES; i++)
        {
            flow.trigger(FINISHED);
            frame(flow, 1);
        }
    }

    /**
     * Scene that does nothing, so that only the flow can allocate.
     */
    private static final class EmptyScene implements Scene
    {
        /**
         * Creates the scene.
         */
        EmptyScene()
        {
            super();
        }

        @Override
        public void enter()
        {
            // Nothing to enter.
        }

        @Override
        public void update(double stepSeconds)
        {
            // Nothing to update.
        }

        @Override
        public void render(double alpha, double unscaledDeltaSeconds)
        {
            // Nothing to render.
        }

        @Override
        public void exit()
        {
            // Nothing to exit.
        }

        @Override
        public void dispose()
        {
            // Nothing to dispose.
        }
    }
}
