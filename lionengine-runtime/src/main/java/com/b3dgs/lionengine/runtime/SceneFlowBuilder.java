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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Declares a {@link SceneFlow}: its scenes by id, its start scene, its transitions and its listener, checked by
 * {@link #build()} (ADR-0036, REQ-SCENE-004, REQ-SCENE-005, REQ-SCENE-007).
 *
 * <p>
 * Scene ids have the registry form {@code namespace:name}, trigger names the form {@code [a-z][a-z0-9_-]*}
 * (ADR-0032). A transition goes from a scene id, or from {@link #ANY_SCENE}, on a trigger, under an optional
 * condition; its kind says what it does: {@link TransitionKind#SWITCH} to a target id, or {@link TransitionKind#EXIT}
 * without target. Transitions are tried in the order they are declared. A {@code null} argument is refused at once;
 * every other mistake is reported by {@link #build()}, naming the first problem found.
 * </p>
 *
 * <p>
 * A builder can build several flows, each with its own state. Not thread-safe; usually used once at startup.
 * </p>
 *
 * @param <C> The type of the game context, shared by the factories and the conditions.
 * @since 11.0.0
 */
public final class SceneFlowBuilder<C>
{
    /** Source of a transition tried from any scene, after the transitions of the top scene. */
    public static final String ANY_SCENE = "*";

    /** Name of the trigger argument, for the messages. */
    private static final String NAME_TRIGGER = "Trigger";
    /** Name of the kind argument, for the messages. */
    private static final String NAME_KIND = "Kind";
    /** Name of the source argument, for the messages. */
    private static final String NAME_FROM = "From";
    /** Separator of the messages. */
    private static final String IN = " in ";

    /**
     * Checks that an argument is not {@code null}.
     *
     * @param value The argument.
     * @param name The name of the argument, for the message.
     * @throws IllegalArgumentException If the argument is {@code null}.
     */
    static void checkNotNull(Object value, String name)
    {
        if (value == null)
        {
            throw new IllegalArgumentException(name + " must not be null");
        }
    }

    /** The scene factories by id, in declaration order. */
    private final Map<String, SceneFactory<C>> scenes = new LinkedHashMap<>();
    /** The transitions, in declaration order. */
    private final List<FlowTransition<C>> transitions = new ArrayList<>();
    /** The first problem found while declaring, {@code null} if none. */
    private String problem;
    /** The start id, {@code null} until given. */
    private String start;
    /** The listener, {@code null} if none. */
    private TransitionListener listener;

    /**
     * Creates an empty builder.
     */
    public SceneFlowBuilder()
    {
        super();
    }

    /**
     * Declares a scene.
     *
     * @param id The scene id, {@code namespace:name}, unique in the flow.
     * @param factory The factory creating a new instance each time the scene is entered (never {@code null}).
     * @return This builder.
     * @throws IllegalArgumentException If an argument is {@code null}.
     */
    public SceneFlowBuilder<C> scene(String id, SceneFactory<C> factory)
    {
        checkNotNull(id, "Id");
        checkNotNull(factory, "Factory");
        if (!FlowNames.isValidId(id))
        {
            report("Invalid scene id, expected namespace:name: " + id);
        }
        else if (scenes.containsKey(id))
        {
            report("Scene id declared twice: " + id);
        }
        else
        {
            scenes.put(id, factory);
        }
        return this;
    }

    /**
     * Sets the start scene, entered at the first frame boundary after {@link SceneFlow#start(Object,
     * com.b3dgs.lionengine.LoopControl)}.
     *
     * @param id The id of a declared scene.
     * @return This builder.
     * @throws IllegalArgumentException If the id is {@code null}.
     */
    public SceneFlowBuilder<C> start(String id)
    {
        checkNotNull(id, "Start");
        if (start != null)
        {
            report("Start scene given twice: " + start + ", " + id);
        }
        start = id;
        return this;
    }

    /**
     * Declares a transition without condition.
     *
     * @param from The id of the source scene, or {@link #ANY_SCENE}.
     * @param trigger The trigger name, {@code [a-z][a-z0-9_-]*}.
     * @param kind The kind (never {@code null}).
     * @param to The id of the target scene for {@link TransitionKind#SWITCH}, {@code null} for
     *            {@link TransitionKind#EXIT}.
     * @return This builder.
     * @throws IllegalArgumentException If {@code from}, {@code trigger} or {@code kind} is {@code null}.
     */
    public SceneFlowBuilder<C> transition(String from, String trigger, TransitionKind kind, String to)
    {
        return transition(from, trigger, context -> true, kind, to);
    }

    /**
     * Declares a transition with a condition, tested when the trigger is fired.
     *
     * @param from The id of the source scene, or {@link #ANY_SCENE}.
     * @param trigger The trigger name, {@code [a-z][a-z0-9_-]*}.
     * @param condition The condition on the game context, without side effect (never {@code null}).
     * @param kind The kind (never {@code null}).
     * @param to The id of the target scene for {@link TransitionKind#SWITCH}, {@code null} for
     *            {@link TransitionKind#EXIT}.
     * @return This builder.
     * @throws IllegalArgumentException If an argument but {@code to} is {@code null}.
     */
    public SceneFlowBuilder<C> transition(String from,
                                          String trigger,
                                          TransitionCondition<C> condition,
                                          TransitionKind kind,
                                          String to)
    {
        checkNotNull(from, NAME_FROM);
        checkNotNull(trigger, NAME_TRIGGER);
        checkNotNull(condition, "Condition");
        checkNotNull(kind, NAME_KIND);
        transitions.add(new FlowTransition<>(from, trigger, condition, kind, to));
        return this;
    }

    /**
     * Sets the listener called when a transition is applied.
     *
     * @param transitionListener The listener, {@code null} for none.
     * @return This builder.
     */
    public SceneFlowBuilder<C> listener(TransitionListener transitionListener)
    {
        listener = transitionListener;
        return this;
    }

    /**
     * Checks the declarations and builds a flow, not started.
     *
     * @return The flow, immutable but for its run state.
     * @throws IllegalStateException Naming the first problem: an invalid or duplicated scene id, a start scene missing,
     *             given twice or not declared, a source or target id not declared, an invalid trigger name, or a
     *             transition of the wrong shape for its kind (a target on {@link TransitionKind#EXIT}, none on
     *             {@link TransitionKind#SWITCH}).
     */
    public SceneFlow<C> build()
    {
        if (problem != null)
        {
            throw new IllegalStateException(problem);
        }
        if (start == null)
        {
            throw new IllegalStateException("No start scene");
        }
        if (!scenes.containsKey(start))
        {
            throw new IllegalStateException("Start scene not declared: " + start);
        }
        final int count = transitions.size();
        for (int i = 0; i < count; i++)
        {
            checkTransition(transitions.get(i));
        }
        return new SceneFlow<>(scenes, start, transitions, listener);
    }

    /**
     * Keeps the first problem found while declaring.
     *
     * @param message The problem.
     */
    private void report(String message)
    {
        if (problem == null)
        {
            problem = message;
        }
    }

    /**
     * Checks a transition.
     *
     * @param transition The transition.
     * @throws IllegalStateException If the transition is not valid.
     */
    private void checkTransition(FlowTransition<C> transition)
    {
        final String from = transition.from();
        final String where = IN + from + " --" + transition.trigger() + "-->";
        if (!ANY_SCENE.equals(from) && !scenes.containsKey(from))
        {
            throw new IllegalStateException("Source scene not declared" + where);
        }
        if (!FlowNames.isValidTrigger(transition.trigger()))
        {
            throw new IllegalStateException("Invalid trigger name, expected [a-z][a-z0-9_-]*" + where);
        }
        final String to = transition.to();
        if (transition.kind() == TransitionKind.EXIT)
        {
            if (to != null)
            {
                throw new IllegalStateException("Target given to an EXIT" + where + ' ' + to);
            }
        }
        else if (to == null)
        {
            throw new IllegalStateException("No target for a " + transition.kind() + where);
        }
        else if (!scenes.containsKey(to))
        {
            throw new IllegalStateException("Target scene not declared" + where + ' ' + to);
        }
    }
}
