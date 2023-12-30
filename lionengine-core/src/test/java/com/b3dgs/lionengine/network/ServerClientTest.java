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
package com.b3dgs.lionengine.network;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTimeout;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.network.client.ClientUdp;
import com.b3dgs.lionengine.network.server.ServerListener;
import com.b3dgs.lionengine.network.server.ServerUdp;

/**
 * Test {@link ServerUdp} and {@link ClientUdp}.
 */
final class ServerClientTest
{
    /**
     * Test client connect to server.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testConnectDisconnect() throws IOException
    {
        final ChannelBuffer channel = new ChannelBuffer();
        final ServerUdp server = new ServerUdp(channel);

        final AtomicReference<String> startedIp = new AtomicReference<>();
        final AtomicInteger startedPort = new AtomicInteger();

        final AtomicReference<String> clientIp = new AtomicReference<>();
        final AtomicReference<String> clientName = new AtomicReference<>();

        server.addListener(new ServerListener()
        {
            @Override
            public void notifyServerStarted(String ip, int port)
            {
                startedIp.set(ip);
                startedPort.set(port);
            }

            @Override
            public void notifyClientConnected(String ip, int port, Integer id)
            {
                clientIp.set(ip);
            }

            @Override
            public void notifyClientDisconnected(String ip, int port, Integer id)
            {
                clientIp.set(null);
            }

            @Override
            public void notifyClientNamed(Integer id, String name)
            {
                clientName.set(name);
            }

            @Override
            public void notifyServerStopped()
            {
                startedIp.set(null);
                startedPort.set(0);
            }
        });
        server.start("127.0.0.1", 1000);

        assertEquals("127.0.0.1", startedIp.get());
        assertEquals(1000, startedPort.get());

        final ClientUdp client = new ClientUdp(channel);
        client.connect("127.0.0.1", 1000);
        client.setName("name");

        assertEquals("/127.0.0.1", clientIp.get());
        assertTimeout(1000L, () ->
        {
            while (!"name".equals(clientName.get()))
            {
                UtilTests.pause(100L);
            }
        });

        assertEquals(1, server.getClients());

        client.disconnect();

        assertTimeout(1000L, () ->
        {
            while (clientIp.get() != null)
            {
                UtilTests.pause(100L);
            }
        });

        assertEquals(0, server.getClients());

        server.stop();

        assertTimeout(1000L, () ->
        {
            while (startedIp.get() != null)
            {
                UtilTests.pause(100L);
            }
        });
        assertTimeout(1000L, () ->
        {
            while (startedPort.get() != 0)
            {
                UtilTests.pause(100L);
            }
        });
    }
}
