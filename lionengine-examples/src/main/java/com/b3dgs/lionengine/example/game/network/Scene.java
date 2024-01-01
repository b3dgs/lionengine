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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.KeyboardAwt;
import com.b3dgs.lionengine.game.feature.SequenceGame;
import com.b3dgs.lionengine.network.client.Client;
import com.b3dgs.lionengine.network.server.Server;

/**
 * Scene implementation.
 */
public final class Scene extends SequenceGame<World>
{
    /** Native resolution. */
    static final Resolution NATIVE = new Resolution(320, 240, 60);
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Scene.class);

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param host <code>true</code> if host, <code>false</code> else.
     */
    public Scene(Context context, Integer host)
    {
        super(context, NATIVE, World::new);

        getInputDevice(Keyboard.class).addActionPressed(KeyboardAwt.ESCAPE, this::end);
        setSystemCursorVisible(true);

        try
        {
            world.prepareNetwork(host.intValue());
        }
        catch (final IOException exception)
        {
            LOGGER.error("Constructor error", exception);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        services.getOptional(Server.class).ifPresent(Server::stop);
        services.getOptional(Client.class).ifPresent(Client::disconnect);

        Engine.terminate();
    }
}
