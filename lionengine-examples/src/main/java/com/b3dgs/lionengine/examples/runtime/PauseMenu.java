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
 * The pause menu of {@link PauseOverlayExample}: pushed above the level, it closes itself with a pop after
 * {@link PauseOverlayExample#PAUSE_SECONDS} (REQ-SCENE-003).
 */
final class PauseMenu implements Scene
{
    /** The level below, for its journal and its manager. */
    private final PausableLevel level;
    /** Time the menu has been open, in seconds. */
    private double openTime;

    /**
     * Creates the menu.
     *
     * @param level The level below.
     */
    PauseMenu(PausableLevel level)
    {
        super();

        this.level = level;
    }

    @Override
    public void enter()
    {
        level.note("pause enter");
    }

    @Override
    public void update(double stepSeconds)
    {
        level.countPauseUpdate();
        openTime += stepSeconds;
        if (openTime >= PauseOverlayExample.PAUSE_SECONDS)
        {
            level.getScenes().pop();
        }
    }

    @Override
    public void render(double alpha, double unscaledDeltaSeconds)
    {
        // A real menu draws here, above the level: the example has no display.
    }

    @Override
    public void exit()
    {
        level.note("pause exit");
    }

    @Override
    public void dispose()
    {
        level.note("pause dispose");
    }
}
