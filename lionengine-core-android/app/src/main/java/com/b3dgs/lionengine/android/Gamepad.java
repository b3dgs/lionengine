/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.InputDeviceListener;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.io.DevicePointer;
import com.b3dgs.lionengine.io.DevicePush;

import java.util.HashSet;
import java.util.Set;

/**
 * Gamepad implementation.
 */
public class Gamepad implements DevicePush, DevicePointer
{
    /** Push listener. */
    private final ListenableModel<InputDeviceListener> listeners = new ListenableModel<>();
    private final Set<Integer> keys = new HashSet<>();
    private final Set<Integer> pressed = new HashSet<>();
    private final Integer index = Integer.valueOf(0);

    private double mx;
    private double my;
    private double x;
    private double y;
    private Integer last;
    private Runnable used;

    /**
     * Create pad.
     */
    public Gamepad()
    {
        super();
    }

    /**
     * Set on used callback.
     * @param used The reference.
     */
    public void setOnUsed(Runnable used)
    {
        this.used = used;
    }

    /**
     * Called on key down.
     *
     * @param keyCode The key code.
     * @param event   The key event.
     */
    public void onKeyDown(int keyCode, KeyEvent event)
    {
        if (isDpadDevice(event))
        {
            last = keyCode;
            keys.add(last);

            final int n = listeners.size();
            for (int i = 0; i < n; i++)
            {
                listeners.get(i).onDeviceChanged(index, keyCode, (char) keyCode, true);
            }

            if (used != null)
            {
                used.run();
            }
        }
    }

    /**
     * Called on key up.
     *
     * @param keyCode The key code.
     * @param event   The key event.
     */
    public void onKeyUp(int keyCode, KeyEvent event)
    {
        if (isDpadDevice(event))
        {
            final int n = listeners.size();
            for (int i = 0; i < n; i++)
            {
                listeners.get(i).onDeviceChanged(index, keyCode, (char) keyCode, false);
            }

            keys.remove(keyCode);
            pressed.remove(keyCode);
            last = null;

            if (used != null)
            {
                used.run();
            }
        }
    }

    /**
     * Called on direction pressed.
     *
     * @param event The key event.
     */
    public void checkDirectionPressed(InputEvent event)
    {
        if (isDpadDevice(event) && event instanceof MotionEvent)
        {
            final MotionEvent motionEvent = (MotionEvent) event;
            mx = motionEvent.getAxisValue(MotionEvent.AXIS_X) + motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X);
            my = motionEvent.getAxisValue(MotionEvent.AXIS_Y) + motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y);

            if (Math.abs(mx) < 0.25)
            {
                mx = 0.0;
            }
            if (Math.abs(my) < 0.25)
            {
                my = 0.0;
            }

            x += mx;
            y += my;

            if (used != null)
            {
                used.run();
            }
        }
    }

    private static boolean isDpadDevice(InputEvent event)
    {
        return true;
    }

    @Override
    public void addListener(InputDeviceListener listener)
    {
        listeners.addListener(listener);
    }

    @Override
    public void removeListener(InputDeviceListener listener)
    {
        listeners.removeListener(listener);
    }

    @Override
    public InputDevice getCurrent(int id)
    {
        return this;
    }

    @Override
    public void setVisible(boolean visible)
    {
        // Void
    }

    @Override
    public void lock(int x, int y)
    {
        this.x = x;
        this.y = y;
        mx = 0.0;
        my = 0.0;
    }

    @Override
    public void unlock()
    {
        // Nothing to do
    }

    @Override
    public void update(double extrp)
    {
        // Void
    }

    @Override
    public boolean isPushed()
    {
        return !keys.isEmpty();
    }

    @Override
    public boolean isPushed(Integer index)
    {
        return keys.contains(index);
    }

    @Override
    public boolean isPushedOnce(Integer index)
    {
        if (keys.contains(index) && !pressed.contains(index))
        {
            pressed.add(index);
            return true;
        }
        return false;
    }

    @Override
    public Integer getPushed()
    {
        return last;
    }

    @Override
    public double getMoveX()
    {
        return mx;
    }

    @Override
    public double getMoveY()
    {
        return my;
    }

    @Override
    public double getX()
    {
        return x;
    }

    @Override
    public double getY()
    {
        return y;
    }

    @Override
    public String getName()
    {
        return Gamepad.class.getSimpleName();
    }
}
