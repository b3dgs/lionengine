/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.selector;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.Selector;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.Minimap;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Services;

/**
 * Game loop designed to handle our little world.
 * 
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 200, 60);

    /** Services reference. */
    private final Services services = new Services();
    /** Game factory. */
    private final Factory factory = services.create(Factory.class);
    /** Camera reference. */
    private final Camera camera = services.create(Camera.class);
    /** Cursor reference. */
    private final Cursor cursor = services.create(Cursor.class);
    /** Map reference. */
    private final MapTile map = services.create(MapTileGame.class);
    /** Minimap reference. */
    private final Minimap minimap = new Minimap(map);
    /** Selector reference. */
    private final Selector selector = new Selector(camera, cursor);
    /** Keyboard reference. */
    private final Keyboard keyboard = getInputDevice(Keyboard.class);
    /** Mouse reference. */
    private final Mouse mouse = getInputDevice(Mouse.class);
    /** HUD image. */
    private final Image hud;
    /** Peon reference. */
    private Peon peon;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);
        hud = Drawable.loadImage(Medias.create("hud.png"));
        setSystemCursorVisible(false);
        keyboard.addActionPressed(Keyboard.ESCAPE, () -> end());
    }

    @Override
    public void load()
    {
        map.create(Medias.create("level.png"), 16, 16, 16);
        minimap.load();
        minimap.automaticColor(null);
        minimap.prepare();
        minimap.setLocation(3, 6);

        hud.load();
        hud.prepare();
        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.load();
        cursor.setArea(0, 0, getWidth(), getHeight());
        cursor.setGrid(map.getTileWidth(), map.getTileHeight());
        cursor.setInputDevice(mouse);
        cursor.setViewer(camera);

        camera.setView(72, 12, 240, 176);
        camera.setLimits(map);
        camera.setLocation(320, 208);

        peon = factory.create(Peon.MEDIA);

        selector.setClickableArea(camera);
        selector.setSelectionColor(ColorRgba.GREEN);
        selector.setClickSelection(Mouse.LEFT);
        selector.addListener(peon);
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);
        cursor.update(extrp);
        peon.update(extrp);
        selector.update(extrp);

        if (keyboard.isPressed(Keyboard.UP))
        {
            camera.moveLocation(extrp, 0, 16);
        }
        if (keyboard.isPressed(Keyboard.DOWN))
        {
            camera.moveLocation(extrp, 0, -16);
        }
        if (keyboard.isPressed(Keyboard.LEFT))
        {
            camera.moveLocation(extrp, -16, 0);
        }
        if (keyboard.isPressed(Keyboard.RIGHT))
        {
            camera.moveLocation(extrp, 16, 0);
        }
    }

    @Override
    public void render(Graphic g)
    {
        map.render(g);
        peon.render(g);
        hud.render(g);
        selector.render(g);
        minimap.render(g);
        cursor.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
