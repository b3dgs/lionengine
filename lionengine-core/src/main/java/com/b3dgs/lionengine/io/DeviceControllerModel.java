/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Map.Entry;
import java.util.Set;

import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.InputDeviceListener;
import com.b3dgs.lionengine.ListenableModel;

/**
 * Represents the device used to control.
 */
public class DeviceControllerModel implements DeviceController
{
    private final ListenableModel<DeviceControllerListener> listeners = new ListenableModel<>();
    private final List<InputDevice> devices = new ArrayList<>();
    private final Set<InputDevice> devicesSet = new HashSet<>();
    private final Map<DeviceAction, String> actionToDevice = new HashMap<>();
    private final List<DeviceAction> horizontal = new ArrayList<>();
    private final List<DeviceAction> vertical = new ArrayList<>();
    private final List<Integer> indexes = new ArrayList<>();
    private final Map<Integer, List<DeviceAction>> fire = new HashMap<>();
    private final Map<Integer, Boolean> fired = new HashMap<>();
    private final Map<String, Boolean> disabledHorizontal = new HashMap<>();
    private final Map<String, Boolean> disabledVertical = new HashMap<>();
    private final Map<Integer, List<String>> codeToDevices = new HashMap<>();

    private final InputDeviceListener listener = (index, push, c, flag) ->
    {
        final int n = listeners.size();
        for (int i = 0; i < n; i++)
        {
            final List<String> names = codeToDevices.get(push);
            if (names != null)
            {
                listeners.get(i).onDeviceChanged(names.get(index.intValue()), push, c, flag);
            }
        }
    };
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

    /**
     * Update horizontal axis.
     */
    private void updateHorizontal()
    {
        axisHorizontal = 0.0;
        for (int i = 0; i < horizontalCount; i++)
        {
            final DeviceAction action = horizontal.get(i);
            if (!Boolean.TRUE.equals(disabledHorizontal.get(actionToDevice.get(action))))
            {
                axisHorizontal += action.getAction();
            }
        }
    }

    /**
     * Update vertical axis.
     */
    private void updateVertical()
    {
        axisVertical = 0.0;
        for (int i = 0; i < verticalCount; i++)
        {
            final DeviceAction action = vertical.get(i);
            if (!Boolean.TRUE.equals(disabledVertical.get(actionToDevice.get(action))))
            {
                axisVertical += vertical.get(i).getAction();
            }
        }
    }

    /**
     * Update fired restore flag.
     */
    private void updateFired()
    {
        for (final Entry<Integer, List<DeviceAction>> entry : fire.entrySet())
        {
            if (fired.get(entry.getKey()) == Boolean.TRUE)
            {
                boolean state = false;

                final List<DeviceAction> actions = entry.getValue();
                final int k = actions.size();
                for (int i = 0; i < k; i++)
                {
                    final DeviceAction action = actions.get(i);
                    if (Double.compare(action.getAction(), 0) > 0)
                    {
                        state = true;
                        break;
                    }
                }
                if (!state)
                {
                    fired.put(entry.getKey(), Boolean.FALSE);
                }
            }
        }
    }

    @Override
    public void addListener(DeviceControllerListener listener)
    {
        listeners.addListener(listener);
    }

    @Override
    public void removeListener(DeviceControllerListener listener)
    {
        listeners.removeListener(listener);
    }

    @Override
    public void addHorizontal(InputDevice device, DeviceAction action)
    {
        if (devicesSet.add(device))
        {
            devices.add(device);
            device.addListener(listener);
        }
        actionToDevice.put(action, device.getName());

        horizontal.add(action);
        horizontalCount++;
    }

    @Override
    public void addVertical(InputDevice device, DeviceAction action)
    {
        if (devicesSet.add(device))
        {
            devices.add(device);
            device.addListener(listener);
        }
        actionToDevice.put(action, device.getName());

        vertical.add(action);
        verticalCount++;
    }

    @Override
    public void addFire(String name, InputDevice device, Integer index, Integer code, DeviceAction action)
    {
        if (devicesSet.add(device))
        {
            devices.add(device);
            device.addListener(listener);
        }
        indexes.add(index);
        actionToDevice.put(action, device.getName());

        List<String> list = codeToDevices.get(code);
        if (list == null)
        {
            list = new ArrayList<>();
            codeToDevices.put(code, list);
        }
        list.add(name);

        List<DeviceAction> list2 = fire.get(index);
        if (list2 == null)
        {
            list2 = new ArrayList<>();
            fire.put(index, list2);
        }
        list2.add(action);

        fired.put(index, Boolean.FALSE);
    }

    @Override
    public void setVisible(boolean visible)
    {
        final int n = devices.size();
        for (int i = 0; i < n; i++)
        {
            final InputDevice device = devices.get(i);
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
                if (fired.get(index) == Boolean.FALSE && action.getAction() > 0)
                {
                    fired.put(index, Boolean.TRUE);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void update(double extrp)
    {
        final int n = devices.size();
        for (int i = 0; i < n; i++)
        {
            final InputDevice device = devices.get(i);
            device.update(extrp);
        }

        updateHorizontal();
        updateVertical();
        updateFired();
    }
}
