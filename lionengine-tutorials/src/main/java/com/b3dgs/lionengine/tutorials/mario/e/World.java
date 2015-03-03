/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.mario.e;

import java.io.IOException;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.game.map.MapTileCollisionModel;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.object.ComponentCollision;
import com.b3dgs.lionengine.game.object.ComponentRenderer;
import com.b3dgs.lionengine.game.object.ComponentUpdater;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;

/**
 * World implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class World
        extends WorldGame
{
    /** Background color. */
    private static final ColorRgba BACKGROUND_COLOR = new ColorRgba(107, 136, 255);

    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Camera reference. */
    private final Camera camera;
    /** Map reference. */
    private final MapTile map;
    /** Map collision. */
    private final MapTileCollision mapCollision;
    /** Factory reference. */
    private final Factory factory;
    /** Handler reference. */
    private final Handler handler;
    /** Mario reference. */
    private Mario mario;

    /**
     * Constructor.
     * 
     * @param config The config reference.
     * @param keyboard The keyboard reference.
     */
    public World(Config config, Keyboard keyboard)
    {
        super(config);

        this.keyboard = keyboard;
        camera = new Camera();
        map = new MapTileGame(camera, 16, 16);
        mapCollision = new MapTileCollisionModel(map, camera);
        map.addFeature(mapCollision);
        factory = new Factory();
        handler = new Handler();
        handler.addUpdatable(new ComponentUpdater());
        handler.addUpdatable(new ComponentCollision());
        handler.addRenderable(new ComponentRenderer());
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        handler.update(extrp);
        camera.follow(mario.getLocalizable());
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(BACKGROUND_COLOR);
        g.drawRect(0, 0, width, height, true);
        map.render(g);
        mapCollision.render(g);
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
        mapCollision.loadCollisions(Core.MEDIA.create("map", "formulas.xml"),
                Core.MEDIA.create("map", "collisions.xml"));
        mapCollision.createCollisionDraw();
        camera.setIntervals(16, 0);
        camera.setView(0, 0, width, height);
        camera.setLimits(map);

        final Services services = new Services();
        services.add(Integer.valueOf(source.getRate()));
        services.add(camera);
        services.add(map);
        services.add(keyboard);
        factory.setServices(services);

        mario = factory.create(Mario.CONFIG);
        mario.respawn(160);
        camera.resetInterval(mario.getLocalizable());
        handler.add(mario);
        for (int i = 0; i < 20; i++)
        {
            final Goomba goomba = factory.create(Goomba.CONFIG);
            goomba.respawn(500 + i * 50);
            handler.add(goomba);
        }
    }
}
