/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.lionengine.android.graphic;

import android.view.MotionEvent;
import android.view.SurfaceView;

import com.b3dgs.lionengine.android.ActivityGame;
import com.b3dgs.lionengine.android.Gamepad;

/**
 * Surface view implementation.
 */
final class ViewAndroid extends SurfaceView
{
    /** Mouse. */
    private final MouseAndroid mouse = new MouseAndroid();
    /** Gamepad. */
    private final Gamepad gamepad;

    /**
     * Internal constructor.
     * 
     * @param activity The activity reference.
     */
    ViewAndroid(ActivityGame activity)
    {
        super(activity);

        gamepad = activity.getGamepad();
    }

    /**
     * Set the mouse reference.
     * 
     * @return The mouse reference.
     */
    MouseAndroid getMouse()
    {
        return mouse;
    }

    /**
     * Get gamepad.
     * @return The gamepad.
     */
    Gamepad getGamepad()
    {
        return gamepad;
    }

    /*
     * SurfaceView
     */

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mouse != null)
        {
            mouse.updateEvent(event);
        }
        return true;
    }
}
