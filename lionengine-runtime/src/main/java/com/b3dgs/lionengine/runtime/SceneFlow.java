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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.LoopControl;

/**
 * Moves a game from scene to scene by named triggers: a small state machine over scenes, built by a
 * {@link SceneFlowBuilder} and driving its own {@link SceneManager} (ADR-0036, REQ-SCENE-004 to REQ-SCENE-008).
 *
 * <p>
 * The game gives the flow to the {@link GameLoop} as both callbacks, then calls
 * {@link #start(Object, LoopControl)} with its context and the loop: the start scene is created and entered at the
 * first frame boundary. Code running on the runtime thread, usually a scene's update, calls {@link #trigger(String)}:
 * the transitions of the top scene are tried in declaration order, then those of any scene; the first with that trigger
 * and a true condition is selected. Nothing changes before the next frame boundary, never inside a step; until then
 * other triggers return {@code false}, so the result does not depend on how many steps ran.
 * </p>
 *
 * <p>
 * At the boundary, a {@link TransitionKind#SWITCH} creates a new instance of the target with its factory, then the
 * scene manager leaves every scene of the stack from the top and enters it; a {@link TransitionKind#PUSH} creates the
 * target and enters it above the stack, whose covered scenes stop updating and keep rendering; a
 * {@link TransitionKind#POP} exits and disposes the top overlay, and the scene below resumes updating (an
 * {@link IllegalStateException} if there is no overlay); an {@link TransitionKind#EXIT} leaves every scene and requests
 * the stop of the loop. The listener is then called, before the first update or render of the
 * frame. Every entry creates a new instance; scenes share data only through the context, which the flow never reads.
 * An exception of a factory, a condition or a scene propagates, so the loop fails (ADR-0017).
 * </p>
 *
 * <p>
 * A game using a flow changes scenes only through it. At the end of the game, once the loop has stopped or failed,
 * {@link #clear()} leaves the remaining scenes. Thread: runtime thread only; not thread-safe. Triggers, updates and
 * renders allocate nothing; a transition allocates the scene its factory creates.
 * </p>
 *
 * @param <C> The type of the game context.
 * @since 11.0.0
 */
public final class SceneFlow<C> implements UpdateCallback, RenderCallback
{
    /** Initial capacity of the id stack. */
    private static final int INITIAL_DEPTH = 4;

    /** The scene factories by id. */
    private final Map<String, SceneFactory<C>> scenes;
    /** The start id. */
    private final String start;
    /** The transitions, in declaration order. */
    private final List<FlowTransition<C>> transitions;
    /** The listener, {@code null} if none. */
    private final TransitionListener listener;
    /** The scene manager driven by the flow. */
    private final SceneManager manager = new SceneManager();
    /** The game context, given at start. */
    private C context;
    /** The loop control, given at start; {@code null} before. */
    private LoopControl loop;
    /** {@code true} until the start scene is entered. */
    private boolean startPending;
    /** The transition applied at the next frame boundary, {@code null} if none. */
    private FlowTransition<C> pending;
    /** The id of the top scene when the pending transition was selected. */
    private String pendingFrom;
    /** The ids of the scenes of the stack, from the bottom; {@code null} above {@link #idCount}. */
    private String[] ids = new String[INITIAL_DEPTH];
    /** The number of scenes in the stack. */
    private int idCount;
    /** {@code true} between the first call of a frame and its render. */
    private boolean frameStarted;

    /**
     * Creates a flow from checked declarations, for {@link SceneFlowBuilder}.
     *
     * @param scenes The scene factories by id.
     * @param start The start id, declared.
     * @param transitions The transitions, checked, in declaration order.
     * @param listener The listener, {@code null} if none.
     */
    SceneFlow(Map<String, SceneFactory<C>> scenes,
              String start,
              List<FlowTransition<C>> transitions,
              TransitionListener listener)
    {
        super();

        this.scenes = Collections.unmodifiableMap(new LinkedHashMap<>(scenes));
        this.start = start;
        this.transitions = Collections.unmodifiableList(new ArrayList<>(transitions));
        this.listener = listener;
    }

    /**
     * Starts the flow: the start scene is created and entered at the next frame boundary. Called once, before the
     * loop runs.
     *
     * @param gameContext The game context, given to the factories and the conditions; may be {@code null} for a game
     *            without shared data.
     * @param loopControl The loop, stopped by an {@link TransitionKind#EXIT} (never {@code null}).
     * @throws IllegalArgumentException If the loop is {@code null}.
     * @throws IllegalStateException If the flow is already started.
     */
    public void start(C gameContext, LoopControl loopControl)
    {
        SceneFlowBuilder.checkNotNull(loopControl, "Loop");
        if (loop != null)
        {
            throw new IllegalStateException("Scene flow already started");
        }
        context = gameContext;
        loop = loopControl;
        startPending = true;
    }

    /**
     * Fires a trigger: selects the first transition of the top scene, then of any scene, with this trigger and a true
     * condition, applied at the next frame boundary.
     *
     * @param trigger The trigger name, {@code [a-z][a-z0-9_-]*}.
     * @return {@code true} if a transition is selected; {@code false} if none matches, if a transition or the start
     *         is already pending, or once the flow has exited.
     * @throws IllegalArgumentException If the trigger name is not valid.
     * @throws IllegalStateException If the flow is not started.
     */
    public boolean trigger(String trigger)
    {
        if (!FlowNames.isValidTrigger(trigger))
        {
            throw new IllegalArgumentException("Invalid trigger name, expected [a-z][a-z0-9_-]*: " + trigger);
        }
        checkStarted();
        boolean selected = false;
        final String topId = getTopId();
        if (!startPending && pending == null && topId != null)
        {
            FlowTransition<C> found = find(topId, trigger);
            if (found == null)
            {
                found = find(SceneFlowBuilder.ANY_SCENE, trigger);
            }
            if (found != null)
            {
                pending = found;
                pendingFrom = topId;
                selected = true;
            }
        }
        return selected;
    }

    /**
     * Checks if a transition, or the start, waits for the next frame boundary.
     *
     * @return {@code true} if a transition or the start is pending.
     */
    public boolean isTransitionPending()
    {
        return startPending || pending != null;
    }

    /**
     * Returns the ids of the scenes of the stack, top first.
     *
     * @return The ids, unmodifiable; empty before the start scene is entered and after an exit.
     */
    public List<String> getSceneIds()
    {
        final List<String> list = new ArrayList<>(idCount);
        for (int i = idCount - 1; i >= 0; i--)
        {
            list.add(ids[i]);
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Leaves every remaining scene now and cancels a pending transition. Meant for the end of the game, once the loop
     * has stopped or failed. Idempotent.
     *
     * @throws IllegalStateException If called from a scene method called by the scene manager.
     */
    public void clear()
    {
        try
        {
            manager.clear();
        }
        finally
        {
            startPending = false;
            pending = null;
            syncIds();
        }
    }

    /**
     * Updates the top scene, after applying a pending transition if this call starts a frame.
     *
     * @param stepSeconds The constant step duration in seconds.
     * @throws IllegalStateException If the flow is not started, or if called from a scene method.
     */
    @Override
    public void update(double stepSeconds)
    {
        startFrame();
        manager.update(stepSeconds);
    }

    /**
     * Renders the scenes of the stack and ends the frame, after applying a pending transition if this call starts the
     * frame.
     *
     * @param alpha The presentation factor in {@code [0, 1)}.
     * @param unscaledDeltaSeconds The real time elapsed since the previous render, in seconds.
     * @throws IllegalStateException If the flow is not started, or if called from a scene method.
     */
    @Override
    public void render(double alpha, double unscaledDeltaSeconds)
    {
        startFrame();
        frameStarted = false;
        manager.render(alpha, unscaledDeltaSeconds);
    }

    /**
     * Checks that the flow is started.
     *
     * @throws IllegalStateException If the flow is not started.
     */
    private void checkStarted()
    {
        if (loop == null)
        {
            throw new IllegalStateException("Scene flow not started");
        }
    }

    /**
     * Returns the id of the top scene.
     *
     * @return The id, {@code null} if the stack is empty.
     */
    private String getTopId()
    {
        final String top;
        if (idCount > 0)
        {
            top = ids[idCount - 1];
        }
        else
        {
            top = null;
        }
        return top;
    }

    /**
     * Puts an id on top of the id stack.
     *
     * @param id The id.
     */
    private void pushId(String id)
    {
        if (idCount == ids.length)
        {
            final String[] grown = new String[idCount * 2];
            System.arraycopy(ids, 0, grown, 0, idCount);
            ids = grown;
        }
        ids[idCount] = id;
        idCount++;
    }

    /**
     * Removes the ids of the scenes the scene manager no longer holds, after a change or a failure.
     */
    private void syncIds()
    {
        final int count = manager.getSceneCount();
        while (idCount > count)
        {
            idCount--;
            ids[idCount] = null;
        }
    }

    /**
     * Finds the first transition from a source with a trigger and a true condition.
     *
     * @param from The source id, or {@link SceneFlowBuilder#ANY_SCENE}.
     * @param trigger The trigger name.
     * @return The transition, {@code null} if none.
     */
    private FlowTransition<C> find(String from, String trigger)
    {
        FlowTransition<C> found = null;
        final int count = transitions.size();
        for (int i = 0; found == null && i < count; i++)
        {
            final FlowTransition<C> transition = transitions.get(i);
            if (transition.from().equals(from)
                && transition.trigger().equals(trigger)
                && transition.condition().test(context))
            {
                found = transition;
            }
        }
        return found;
    }

    /**
     * Applies the start or a pending transition at the first call of a frame.
     */
    private void startFrame()
    {
        checkStarted();
        if (!frameStarted)
        {
            frameStarted = true;
            if (startPending)
            {
                startPending = false;
                enter(start);
            }
            else if (pending != null)
            {
                apply();
            }
            else
            {
                manager.beginFrame();
            }
        }
    }

    /**
     * Applies the pending transition and calls the listener.
     */
    private void apply()
    {
        final FlowTransition<C> transition = pending;
        final String from = pendingFrom;
        pending = null;
        pendingFrom = null;
        try
        {
            switch (transition.kind())
            {
                case SWITCH -> enter(transition.to());
                case PUSH -> push(transition.to());
                case POP ->
                {
                    manager.pop();
                    manager.beginFrame();
                }
                default ->
                {
                    manager.clear();
                    loop.stop();
                    manager.beginFrame();
                }
            }
        }
        finally
        {
            syncIds();
        }
        if (listener != null)
        {
            listener.onTransition(from, transition.trigger(), transition.to());
        }
    }

    /**
     * Creates a new scene of an id with its factory.
     *
     * @param id The id of the scene.
     * @return The scene.
     * @throws IllegalStateException If the factory returns {@code null}.
     */
    private Scene create(String id)
    {
        final Scene scene = scenes.get(id).create(context);
        if (scene == null)
        {
            throw new IllegalStateException("Scene factory returned null: " + id);
        }
        return scene;
    }

    /**
     * Creates a scene of an id and makes the scene manager replace its stack by it, at once.
     *
     * @param id The id of the scene.
     * @throws IllegalStateException If the factory returns {@code null}.
     */
    private void enter(String id)
    {
        final Scene scene = create(id);
        manager.switchTo(scene);
        manager.beginFrame();
        while (idCount > 0)
        {
            idCount--;
            ids[idCount] = null;
        }
        pushId(id);
    }

    /**
     * Creates a scene of an id and makes the scene manager enter it above the stack, at once.
     *
     * @param id The id of the scene.
     * @throws IllegalStateException If the factory returns {@code null}.
     */
    private void push(String id)
    {
        final Scene scene = create(id);
        manager.push(scene);
        manager.beginFrame();
        pushId(id);
    }
}
