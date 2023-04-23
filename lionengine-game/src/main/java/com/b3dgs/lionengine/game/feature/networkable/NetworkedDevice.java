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
package com.b3dgs.lionengine.game.feature.networkable;

import java.nio.ByteBuffer;

import com.b3dgs.lionengine.InputDeviceListener;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.DeviceControllerListener;
import com.b3dgs.lionengine.io.DevicePush;
import com.b3dgs.lionengine.io.DevicePushVirtual;
import com.b3dgs.lionengine.network.Packet;

/**
 * Networked device implementation.
 */
@FeatureInterface
public class NetworkedDevice extends FeatureModel implements Syncable, DevicePush
{
    private final DevicePushVirtual virtual = new DevicePushVirtual();

    @FeatureGet private Networkable networkable;

    private final DeviceControllerListener listener = (n, e, c, s) ->
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2 + 1);
        buffer.putInt(getSyncId());
        buffer.putInt(e.intValue());
        buffer.put(s ? UtilConversion.fromUnsignedByte(1) : UtilConversion.fromUnsignedByte(0));
        networkable.send(buffer);
    };

    /**
     * Create device.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     */
    public NetworkedDevice(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Get virtual device.
     * 
     * @return The virtual device.
     */
    public DevicePushVirtual getVirtual()
    {
        return virtual;
    }

    /**
     * Set device controller.
     * 
     * @param device The device reference.
     */
    public void set(DeviceController device)
    {
        device.addListener(listener);
    }

    /**
     * Remove device controller.
     * 
     * @param device The device reference.
     */
    public void remove(DeviceController device)
    {
        device.removeListener(listener);
    }

    @Override
    public void onReceived(Packet packet)
    {
        final Integer key = Integer.valueOf(packet.readInt());
        if (packet.readBool())
        {
            virtual.onPressed(key);
        }
        else
        {
            virtual.onReleased(key);
        }
    }

    @Override
    public String getName()
    {
        return NetworkedDevice.class.getSimpleName();
    }

    @Override
    public void addListener(InputDeviceListener listener)
    {
        virtual.addListener(listener);
    }

    @Override
    public void removeListener(InputDeviceListener listener)
    {
        virtual.removeListener(listener);
    }

    @Override
    public boolean isPushed()
    {
        return virtual.isPushed();
    }

    @Override
    public Integer getPushed()
    {
        return virtual.getPushed();
    }

    @Override
    public boolean isPushed(Integer index)
    {
        return virtual.isPushed(index);
    }

    @Override
    public boolean isPushedOnce(Integer index)
    {
        return virtual.isPushedOnce(index);
    }
}
