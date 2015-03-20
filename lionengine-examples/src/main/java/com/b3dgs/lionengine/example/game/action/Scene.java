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
package com.b3dgs.lionengine.example.game.action;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.object.ComponentRenderer;
import com.b3dgs.lionengine.game.object.ComponentUpdater;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;

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
    private static final Resolution NATIVE = new Resolution(320, 200, 60);

    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Mouse reference. */
    private final Mouse mouse;
    /** Camera reference. */
    private final Camera camera;
    /** Map reference. */
    private final MapTile map;
    /** Cursor reference. */
    private final Cursor cursor;
    /** HUD image. */
    private final Image hud;
    /** Text reference. */
    private final Text text;
    /** Action factory. */
    private final Factory factory;
    /** Actions handler. */
    private final Handler handler;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        keyboard = getInputDevice(Keyboard.class);
        mouse = getInputDevice(Mouse.class);
        camera = new Camera();
        map = new MapTileGame(camera, 16, 16);
        cursor = new Cursor(mouse, Core.MEDIA.create("cursor.png"));
        factory = new Factory();
        handler = new Handler();
        hud = Drawable.loadImage(Core.MEDIA.create("hud.png"));
        text = Core.GRAPHIC.createText(Text.SANS_SERIF, 9, TextStyle.NORMAL);
        mouse.setConfig(getConfig());
        setSystemCursorVisible(false);
    }

    @Override
    protected void load()
    {
        map.create(Core.MEDIA.create("level.png"), Core.MEDIA.create("sheets.xml"), Core.MEDIA.create("groups.xml"));

        hud.load(false);
        text.setLocation(74, 192);
        cursor.load(false);
        cursor.setArea(0, 0, getWidth(), getHeight());
        cursor.setGrid(map.getTileWidth(), map.getTileHeight());
        cursor.setViewer(camera);

        camera.setView(72, 12, 240, 176);
        camera.setLimits(map);
        camera.setLocation(320, 208);

        final Services services = new Services();
        services.add(cursor);
        services.add(text);
        services.add(factory);
        services.add(handler);
        factory.setServices(services);

        final ObjectGame buildings = factory.create(Button.BUILDINGS);
        handler.addUpdatable(new ComponentUpdater());
        handler.addRenderable(new ComponentRenderer());
        handler.add(buildings);
    }

    @Override
    public void update(double extrp)
    {
        text.setText("");
        mouse.update(extrp);
        cursor.update(extrp);
        handler.update(extrp);
        if (keyboard.isPressedOnce(Keyboard.ESCAPE))
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        map.render(g);
        hud.render(g);
        handler.render(g);
        text.render(g);
        cursor.render(g);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
