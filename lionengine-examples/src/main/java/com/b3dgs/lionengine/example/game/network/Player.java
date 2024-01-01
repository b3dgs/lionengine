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

import java.nio.ByteBuffer;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.awt.Mouse;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.networkable.NetworkableModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.network.Packet;

/**
 * Player network representation.
 */
@FeatureInterface
public final class Player extends NetworkableModel implements Routine
{
    private final Text text = Graphics.createText(12);
    private final Timing timing = new Timing();

    private final Mouse mouse = services.get(Mouse.class);
    private final Factory factory = services.get(Factory.class);
    private final Handler handler = services.get(Handler.class);

    /**
     * Create player.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     */
    public Player(Services services, Setup setup)
    {
        super(services, setup);

        timing.start();
        text.setText(Constant.EMPTY_STRING);
    }

    private ByteBuffer getMouse()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putInt((int) mouse.getX());
        buffer.putInt((int) mouse.getY());
        return buffer;
    }

    @Override
    public void update(double extrp)
    {
        if (timing.elapsed(100))
        {
            timing.restart();

            if (isOwner())
            {
                final Featurable effect = factory.create(Effect.EXPLODE);
                effect.getFeature(Effect.class).start(mouse);
                handler.add(effect);
                text.setLocation(mouse.getX(), mouse.getY() - 20);
                if (getClientId() != null)
                {
                    text.setText(String.valueOf(getClientId()));
                }
                send(this::getMouse);
            }
        }
    }

    @Override
    public void render(Graphic g)
    {
        text.render(g);
    }

    @Override
    public void onReceived(Packet packet)
    {
        final Featurable effect = factory.create(Effect.EXPLODE);
        final int x = packet.readInt();
        final int y = packet.readInt();
        text.setLocation(x, y - 20);
        text.setText(String.valueOf(packet.getClientSourceId()));
        effect.getFeature(Effect.class).start(x, y);
        handler.add(effect);
    }
}
