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
package com.b3dgs.lionengine.example.game.collision;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.game.map.MapTileCollisionModel;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Services;

/**
 * Game loop designed to handle our little world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);
    /** Background color. */
    private static final ColorRgba BACKGROUND_COLOR = new ColorRgba(107, 136, 255);

    /** Services reference. */
    private final Services services = new Services();
    /** Game factory. */
    private final Factory factory = services.create(Factory.class);
    /** Camera reference. */
    private final Camera camera = services.create(Camera.class);
    /** Map reference. */
    private final MapTile map = services.create(MapTileGame.class);
    /** Map collision. */
    private final MapTileCollision mapCollision = map.createFeature(MapTileCollisionModel.class);
    /** Keyboard reference. */
    private final Keyboard keyboard = services.add(getInputDevice(Keyboard.class));
    /** Mario reference. */
    private Mario hero;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, NATIVE);
        keyboard.addActionPressed(Keyboard.ESCAPE, () -> end());
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        map.create(Medias.create("level.png"), Medias.create("sheets.xml"), Medias.create("groups.xml"));
        mapCollision.loadCollisions(Medias.create("formulas.xml"), Medias.create("collisions.xml"));
        mapCollision.createCollisionDraw();

        camera.setIntervals(16, 0);
        camera.setView(0, 0, getWidth(), getHeight());
        camera.setLimits(map);

        services.add(Integer.valueOf(getConfig().getSource().getRate()));
        hero = factory.create(Mario.MEDIA);
    }

    @Override
    public void update(double extrp)
    {
        hero.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(Scene.BACKGROUND_COLOR);
        g.drawRect(0, 0, getWidth(), getHeight(), true);

        map.render(g);
        mapCollision.render(g);
        hero.render(g);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
