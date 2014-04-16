/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.tutorials.mario.f;

import java.io.IOException;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Keyboard;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;

/**
 * World implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class World
        extends WorldGame
{
    /** Background color. */
    private static final ColorRgba BACKGROUND_COLOR = new ColorRgba(107, 136, 255);

    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Map reference. */
    private final Map map;
    /** Factory reference. */
    private final FactoryEntity factory;
    /** Mario reference. */
    private final Mario mario;
    /** Handler reference. */
    private final HandlerEntity handler;

    /**
     * @see WorldGame#WorldGame(Sequence)
     */
    World(Sequence sequence)
    {
        super(sequence);
        keyboard = sequence.getInputDevice(Keyboard.class);
        camera = new CameraPlatform(width, height);
        map = new Map();
        factory = new FactoryEntity(map, source.getRate());
        mario = factory.create(Mario.class);
        handler = new HandlerEntity(camera, mario);
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        mario.updateControl(keyboard);
        mario.update(extrp);
        handler.update(extrp);
        camera.follow(mario);
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(World.BACKGROUND_COLOR);
        g.drawRect(0, 0, width, height, true);
        // Draw the map
        map.render(g, camera);
        // Draw the mario
        mario.render(g, camera);
        handler.render(g);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        map.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        map.load(file);
        camera.setLimits(map);
        camera.setIntervals(16, 0);
        map.adjustCollisions();
        mario.respawn();

        // Create two goombas
        for (int i = 0; i < 2; i++)
        {
            final Goomba goomba = factory.create(Goomba.class);
            goomba.setLocation(532 + i * 24, 32);
            handler.add(goomba);
        }
    }
}
