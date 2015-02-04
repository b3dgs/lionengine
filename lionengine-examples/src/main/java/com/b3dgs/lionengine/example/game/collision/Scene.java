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
package com.b3dgs.lionengine.example.game.collision;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Services;

/**
 * Game loop designed to handle our little world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);
    /** Background color. */
    private static final ColorRgba BACKGROUND_COLOR = new ColorRgba(107, 136, 255);

    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Camera reference. */
    private final Camera camera;
    /** Map reference. */
    private final Map map;
    /** Mario reference. */
    private Mario hero;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        keyboard = getInputDevice(Keyboard.class);
        camera = new Camera();
        map = new Map();
    }

    /*
     * Sequence
     */

    @Override
    public void load()
    {
        map.load(Core.MEDIA.create("level.png"), Core.MEDIA.create("tile"));
        map.createCollisionDraw();

        final Services services = new Services();
        services.add(map);
        services.add(Integer.valueOf(getConfig().getSource().getRate()));
        services.add(keyboard);
        services.add(camera);
        hero = new Mario(services);

        camera.setLimits(map);
        camera.setView(0, 0, getWidth(), getHeight());
        camera.setIntervals(16, 0);
    }

    @Override
    public void update(double extrp)
    {
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }
        hero.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(Scene.BACKGROUND_COLOR);
        g.drawRect(0, 0, getWidth(), getHeight(), true);

        map.render(g, camera);
        hero.render(g);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
