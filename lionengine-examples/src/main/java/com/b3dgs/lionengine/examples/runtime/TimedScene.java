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

import com.b3dgs.lionengine.runtime.Scene;

/**
 * A scene of {@link SceneFlowExample} that fires a trigger after a time, as an introduction that finishes or a menu
 * where the player chooses (REQ-SCENE-004).
 */
final class TimedScene implements Scene
{
    /** The game context. */
    private final FlowStory story;
    /** Name of the scene in the journal. */
    private final String name;
    /** Time before the trigger, in seconds. */
    private final double duration;
    /** Trigger fired once the time is over. */
    private final String trigger;
    /** Trigger fired once at half the time, {@code null} if none. */
    private final String halfway;
    /** {@code true} once the halfway trigger is fired. */
    private boolean halfwayFired;
    /** Time spent in the scene, in seconds. */
    private double time;

    /**
     * Creates the scene, counted in the context.
     *
     * @param story The game context.
     * @param name The name of the scene in the journal.
     * @param duration The time before the trigger, in seconds.
     * @param trigger The trigger fired once the time is over.
     */
    TimedScene(FlowStory story, String name, double duration, String trigger)
    {
        this(story, name, duration, trigger, null);
    }

    /**
     * Creates the scene with a trigger fired once at half its time, such as a pause, counted in the context.
     *
     * @param story The game context.
     * @param name The name of the scene in the journal.
     * @param duration The time before the trigger, in seconds; only the steps of the scene count, not a pause.
     * @param trigger The trigger fired once the time is over.
     * @param halfway The trigger fired once at half the time, {@code null} if none.
     */
    TimedScene(FlowStory story, String name, double duration, String trigger, String halfway)
    {
        super();

        this.story = story;
        this.name = name;
        this.duration = duration;
        this.trigger = trigger;
        this.halfway = halfway;
        story.countCreated();
    }

    @Override
    public void enter()
    {
        story.note(name + " enter");
    }

    @Override
    public void update(double stepSeconds)
    {
        time += stepSeconds;
        if (halfway != null && !halfwayFired && time >= duration / 2.0)
        {
            halfwayFired = story.trigger(halfway);
        }
        else if (time >= duration)
        {
            if ("menu".equals(name))
            {
                story.selectSlot();
            }
            story.trigger(trigger);
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
        story.note(name + " exit");
    }

    @Override
    public void dispose()
    {
        // Nothing to release: the example has no resource.
    }
}
