/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.io;

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.InputDeviceListener;
import com.b3dgs.lionengine.ListenableModel;

/**
 * Virtual device implementation.
 */
public class DevicePushVirtual implements DevicePush
{
    /** Push listener. */
    private final ListenableModel<InputDeviceListener> listeners = new ListenableModel<>();
    /** List of keys. */
    private final Collection<Integer> keys = new HashSet<>();
    /** Pressed states. */
    private final Collection<Integer> pressed = new HashSet<>();
    /** Index value. */
    private final Integer index = Integer.valueOf(0);
    /** Last key code. */
    private Integer lastKey;

    /**
     * Create.
     */
    public DevicePushVirtual()
    {
        super();
    }

    /**
     * Update pressed key.
     * 
     * @param key The key index.
     */
    public void onPressed(Integer key)
    {
        keys.add(key);

        if (!key.equals(lastKey))
        {
            final int n = listeners.size();
            for (int i = 0; i < n; i++)
            {
                listeners.get(i).onDeviceChanged(index, key, ' ', true);
            }
            lastKey = key;
        }
        lastKey = key;
    }

    /**
     * Update released key.
     * 
     * @param key The key index.
     */
    public void onReleased(Integer key)
    {
        keys.remove(key);

        if (lastKey != null)
        {
            final int n = listeners.size();
            for (int i = 0; i < n; i++)
            {
                listeners.get(i).onDeviceChanged(index, key, ' ', false);
            }
            lastKey = null;
        }
        lastKey = null;
    }

    @Override
    public String toString()
    {
        return super.hashCode() + keys.toString();
    }

    @Override
    public String getName()
    {
        return DevicePushVirtual.class.getSimpleName();
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
    public Integer getPushed()
    {
        return lastKey;
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
}
