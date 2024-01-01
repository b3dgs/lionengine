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
package com.b3dgs.lionengine.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.b3dgs.lionengine.Engine;

/**
 * Activity base implementation for game.
 */
public abstract class ActivityGame extends Activity
{
    private final Gamepad gamepad = new Gamepad();

    /**
     * Constructor.
     */
    public ActivityGame()
    {
        super();
    }

    /**
     * Get gamepad.
     * @return The gamepad.
     */
    public Gamepad getGamepad()
    {
        return gamepad;
    }

    /**
     * Start the activity.
     * 
     * @param bundle The bundle reference.
     */
    protected abstract void start(Bundle bundle);

    /*
     * Activity
     */

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        start(bundle);
    }

    @Override
    public void finish()
    {
        Engine.terminate();
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        gamepad.onKeyDown(keyCode, event);
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        gamepad.onKeyUp(keyCode, event);
        return true;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        gamepad.checkDirectionPressed(event);
        return true;
    }
}
