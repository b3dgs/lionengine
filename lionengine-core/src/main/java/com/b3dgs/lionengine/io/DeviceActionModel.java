/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents device action.
 */
public class DeviceActionModel implements DeviceAction
{
    /** Axis action. */
    private final DeviceAxis axis;
    /** Device push reference. */
    private final DevicePush push;

    /**
     * Create model.
     * 
     * @param axis The axis action.
     * @param push The device reference.
     * @throws LionEngineException If invalid arguments.
     */
    public DeviceActionModel(DeviceAxis axis, DevicePush push)
    {
        super();

        Check.notNull(axis);
        Check.notNull(push);

        this.axis = axis;
        this.push = push;
    }

    /**
     * Create model.
     * 
     * @param value The value action.
     * @param push The device reference.
     * @throws LionEngineException If invalid arguments.
     */
    public DeviceActionModel(Integer value, DevicePush push)
    {
        super();

        Check.notNull(value);
        Check.notNull(push);

        this.push = push;
        axis = new DeviceAxis(value, value);
    }

    @Override
    public double getAction()
    {
        final double side;
        if (push.isPushed(axis.getPositive()))
        {
            side = 1.0;
        }
        else if (push.isPushed(axis.getNegative()))
        {
            side = -1.0;
        }
        else
        {
            side = 0.0;
        }
        return side;
    }
}
