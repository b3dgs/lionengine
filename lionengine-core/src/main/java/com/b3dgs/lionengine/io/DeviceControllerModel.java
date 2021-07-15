/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b3dgs.lionengine.InputDevice;

/**
 * Represents the device used to control.
 */
public class DeviceControllerModel implements DeviceController
{
    private final Set<InputDevice> devices = new HashSet<>();
    private final Map<DeviceAction, String> actionToDevice = new HashMap<>();
    private final List<DeviceAction> horizontal = new ArrayList<>();
    private final List<DeviceAction> vertical = new ArrayList<>();
    private final List<Integer> indexes = new ArrayList<>();
    private final Map<Integer, List<DeviceAction>> fire = new HashMap<>();
    private final Map<DeviceAction, Boolean> fired = new HashMap<>();
    private final Map<String, Boolean> disabledHorizontal = new HashMap<>();
    private final Map<String, Boolean> disabledVertical = new HashMap<>();

    private int horizontalCount;
    private int verticalCount;
    private double axisHorizontal;
    private double axisVertical;

    /**
     * Create model.
     */
    public DeviceControllerModel()
    {
        super();
    }

    @Override
    public void addHorizontal(InputDevice device, DeviceAction action)
    {
        devices.add(device);
        actionToDevice.put(action, device.getName());

        horizontal.add(action);
        horizontalCount++;
    }

    @Override
    public void addVertical(InputDevice device, DeviceAction action)
    {
        devices.add(device);
        actionToDevice.put(action, device.getName());

        vertical.add(action);
        verticalCount++;
    }

    @Override
    public void addFire(InputDevice device, Integer index, DeviceAction action)
    {
        indexes.add(index);
        devices.add(device);
        actionToDevice.put(action, device.getName());

        fire.computeIfAbsent(index, ArrayList::new).add(action);
        fired.put(action, Boolean.FALSE);
    }

    @Override
    public void setVisible(boolean visible)
    {
        for (final InputDevice device : devices)
        {
            device.setVisible(visible);
        }
    }

    @Override
    public void setDisabled(String device, boolean horizontal, boolean vertical)
    {
        disabledHorizontal.put(device, Boolean.valueOf(horizontal));
        disabledVertical.put(device, Boolean.valueOf(vertical));
    }

    @Override
    public double getHorizontalDirection()
    {
        return axisHorizontal;
    }

    @Override
    public double getVerticalDirection()
    {
        return axisVertical;
    }

    @Override
    public boolean isFired()
    {
        final int n = indexes.size();
        for (int i = 0; i < n; i++)
        {
            if (isFired(indexes.get(i)))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer getFired()
    {
        final int n = indexes.size();
        for (int i = 0; i < n; i++)
        {
            final Integer index = indexes.get(i);
            if (isFired(index))
            {
                return index;
            }
        }
        return null;
    }

    @Override
    public boolean isFired(Integer index)
    {
        final List<DeviceAction> actions = fire.get(index);
        if (actions != null)
        {
            final int n = actions.size();
            for (int i = 0; i < n; i++)
            {
                final DeviceAction action = actions.get(i);
                if (action.getAction() > 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isFiredOnce(Integer index)
    {
        final List<DeviceAction> actions = fire.get(index);
        if (actions != null)
        {
            final int n = actions.size();
            for (int i = 0; i < n; i++)
            {
                final DeviceAction action = actions.get(i);
                if (!fired.get(action).booleanValue() && action.getAction() > 0)
                {
                    fired.put(action, Boolean.TRUE);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void update(double extrp)
    {
        for (final InputDevice device : devices)
        {
            device.update(extrp);
        }

        axisHorizontal = 0.0;
        for (int i = 0; i < horizontalCount; i++)
        {
            final DeviceAction action = horizontal.get(i);
            if (!Boolean.TRUE.equals(disabledHorizontal.get(actionToDevice.get(action))))
            {
                axisHorizontal += action.getAction();
            }
        }

        axisVertical = 0.0;
        for (int i = 0; i < verticalCount; i++)
        {
            final DeviceAction action = vertical.get(i);
            if (!Boolean.TRUE.equals(disabledVertical.get(actionToDevice.get(action))))
            {
                axisVertical += vertical.get(i).getAction();
            }
        }

        for (final List<DeviceAction> actions : fire.values())
        {
            final int n = actions.size();
            for (int i = 0; i < n; i++)
            {
                final DeviceAction action = actions.get(i);
                if (Double.compare(action.getAction(), 0) <= 0)
                {
                    fired.put(action, Boolean.FALSE);
                }
            }
        }
    }
}
