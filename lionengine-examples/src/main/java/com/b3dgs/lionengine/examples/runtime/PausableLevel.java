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

import com.b3dgs.lionengine.LoopControl;
import com.b3dgs.lionengine.runtime.Scene;
import com.b3dgs.lionengine.runtime.SceneManager;

/**
 * The level of {@link PauseOverlayExample}: plays, pauses once by pushing a {@link PauseMenu}, and stops the game when
 * its play time is over (REQ-SCENE-003).
 */
final class PausableLevel implements Scene
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
    PausableLevel(SceneManager scenes, LoopControl loop)
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
        if (!journal.isEmpty())
        {
            journal.append(", ");
        }
        journal.append(call);
    }

    /**
     * Counts a step of the pause menu.
     */
    void countPauseUpdate()
    {
        pauseUpdates++;
    }

    /**
     * Returns the scene manager.
     *
     * @return The scene manager, for the menu to close itself.
     */
    SceneManager getScenes()
    {
        return scenes;
    }

    /**
     * Returns the journal.
     *
     * @return The lifecycle calls of the level and the menu, in order.
     */
    String getJournal()
    {
        return journal.toString();
    }

    /**
     * Returns the steps of the level.
     *
     * @return The number of steps.
     */
    long getUpdates()
    {
        return updates;
    }

    /**
     * Returns the steps of the pause menu.
     *
     * @return The number of steps.
     */
    long getPauseUpdates()
    {
        return pauseUpdates;
    }

    /**
     * Returns the renders of the level while covered.
     *
     * @return The number of renders.
     */
    long getRendersUnderPause()
    {
        return rendersUnderPause;
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
        if (!paused && playTime >= PauseOverlayExample.PLAY_BEFORE_PAUSE)
        {
            paused = scenes.push(new PauseMenu(this));
        }
        if (playTime >= PauseOverlayExample.PLAY_SECONDS)
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
