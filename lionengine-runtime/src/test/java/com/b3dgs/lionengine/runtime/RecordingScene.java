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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scene that records its lifecycle calls, for the tests of scene managers and scene flows (ADR-0045, REQ-SCENE-001).
 *
 * <p>
 * Each call adds {@code "<name> <method>"} to a list shared by the scenes of a test, for example {@code "menu enter"},
 * so that the order of calls across scenes can be checked. An action can run after a method is recorded (to request a
 * switch from inside a scene), and one method can be made to fail. Published in the {@code runtime} test JAR.
 * </p>
 */
public final class RecordingScene implements Scene
{
    /** Recorded name of {@link #enter()}. */
    public static final String ENTER = "enter";
    /** Recorded name of {@link #update(double)}. */
    public static final String UPDATE = "update";
    /** Recorded name of {@link #render(double, double)}. */
    public static final String RENDER = "render";
    /** Recorded name of {@link #exit()}. */
    public static final String EXIT = "exit";
    /** Recorded name of {@link #dispose()}. */
    public static final String DISPOSE = "dispose";

    /** Name of the scene in the records. */
    private final String name;
    /** Records shared by the scenes of a test. */
    private final List<String> calls;
    /** Actions run after a method is recorded, by recorded method name. */
    private final Map<String, Runnable> actions = new HashMap<>();
    /** Method that throws, {@code null} if none. */
    private String failure;
    /** Number of updates. */
    private int updates;
    /** Number of renders. */
    private int renders;

    /**
     * Creates a scene.
     *
     * @param name The name of the scene in the records.
     * @param calls The records shared by the scenes of a test.
     */
    public RecordingScene(String name, List<String> calls)
    {
        this.name = name;
        this.calls = calls;
    }

    /**
     * Sets the action run each time a method is called, after its call is recorded.
     *
     * @param method The recorded name of the method ({@link #ENTER}, {@link #UPDATE}...).
     * @param action The action, {@code null} for none.
     */
    public void setAction(String method, Runnable action)
    {
        actions.put(method, action);
    }

    /**
     * Makes a method throw an {@link IllegalStateException} after recording its call.
     *
     * @param method The recorded name of the method ({@link #ENTER}, {@link #UPDATE}...), {@code null} for none.
     */
    public void failOn(String method)
    {
        failure = method;
    }

    /**
     * Returns the number of updates.
     *
     * @return The number of updates.
     */
    public int getUpdates()
    {
        return updates;
    }

    /**
     * Returns the number of renders.
     *
     * @return The number of renders.
     */
    public int getRenders()
    {
        return renders;
    }

    /**
     * Returns the record of a call of this scene.
     *
     * @param method The recorded name of the method.
     * @return The record, {@code "<name> <method>"}.
     */
    public String record(String method)
    {
        return name + ' ' + method;
    }

    /**
     * Records a call, throws if the method is the failing one, then runs its action.
     *
     * @param method The recorded name of the method.
     * @throws IllegalStateException If the method is the failing one.
     */
    private void call(String method)
    {
        calls.add(record(method));
        if (method.equals(failure))
        {
            throw new IllegalStateException(record(method));
        }
        final Runnable action = actions.get(method);
        if (action != null)
        {
            action.run();
        }
    }

    @Override
    public void enter()
    {
        call(ENTER);
    }

    @Override
    public void update(double stepSeconds)
    {
        updates++;
        call(UPDATE);
    }

    @Override
    public void render(double alpha, double unscaledDeltaSeconds)
    {
        renders++;
        call(RENDER);
    }

    @Override
    public void exit()
    {
        call(EXIT);
    }

    @Override
    public void dispose()
    {
        call(DISPOSE);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
