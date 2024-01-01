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
package com.b3dgs.lionengine.example.game.network;

import java.io.IOException;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.awt.Mouse;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.networkable.ComponentNetwork;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.helper.WorldHelper;
import com.b3dgs.lionengine.network.Channel;
import com.b3dgs.lionengine.network.ChannelBuffer;
import com.b3dgs.lionengine.network.client.Client;
import com.b3dgs.lionengine.network.client.ClientUdp;
import com.b3dgs.lionengine.network.server.Server;
import com.b3dgs.lionengine.network.server.ServerUdp;

/**
 * World representation.
 */
public final class World extends WorldHelper
{
    private final Mouse mouse = services.add(getInputDevice(Mouse.class));

    /**
     * Create world.
     * 
     * @param services The services reference.
     */
    public World(Services services)
    {
        super(services);

        camera.setView(source, 0, 0, Origin.TOP_LEFT);
    }

    private void createPlayer()
    {
        final Featurable f = factory.create(Medias.create("Player.xml"));
        handler.add(f);
    }

    /**
     * Prepare network.
     * 
     * @param host The host flag (1 = server, 2 = client, other = local).
     * @throws IOException If network error.
     */
    public void prepareNetwork(int host) throws IOException
    {
        if (host == 1)
        {
            final Channel channel = services.create(ChannelBuffer.class);
            final Server server = services.add(new ServerUdp(channel));
            server.addListener((ip, port) -> createPlayer());
            server.start("127.0.0.1", 1000);
            handler.addComponent(new ComponentNetwork(services));
        }
        else if (host == 2)
        {
            final Channel channel = services.create(ChannelBuffer.class);
            final Client client = services.add(new ClientUdp(channel));
            client.addListener((ip, port, id) -> createPlayer());
            client.connect("127.0.0.1", 1000);
            handler.addComponent(new ComponentNetwork(services));
        }
        else
        {
            createPlayer();
        }
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);

        super.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, source.getWidth(), source.getHeight());

        super.render(g);
    }
}
