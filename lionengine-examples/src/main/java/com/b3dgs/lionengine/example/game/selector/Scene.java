/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.core.sequence.Sequence;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.ComponentDisplayable;
import com.b3dgs.lionengine.game.feature.ComponentRefreshable;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.Minimap;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Image;
import com.b3dgs.lionengine.io.awt.Keyboard;
import com.b3dgs.lionengine.io.awt.Mouse;

/**
 * Game loop designed to handle our little world.
 * 
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene extends Sequence
{
    private static final Resolution NATIVE = new Resolution(320, 200, 60);

    private final Services services = new Services();
    private final Factory factory = services.create(Factory.class);
    private final Handler handler = services.create(Handler.class);
    private final Camera camera = services.create(Camera.class);
    private final Cursor cursor = services.create(Cursor.class);
    private final MapTile map = services.create(MapTileGame.class);
    private final Minimap minimap = new Minimap(map);
    private final Keyboard keyboard = getInputDevice(Keyboard.class);
    private final Mouse mouse = getInputDevice(Mouse.class);
    private final Image hud;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);

        hud = Drawable.loadImage(Medias.create("hud.png"));

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());

        keyboard.addActionPressed(Keyboard.ESCAPE, () -> end());

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        map.create(Medias.create("level.png"), 16, 16, 16);

        camera.setView(72, 12, 240, 176, getHeight());
        camera.setLimits(map);
        camera.setLocation(192, 96);

        map.addFeature(new MapTileViewerModel());
        handler.add(map);

        minimap.load();
        minimap.automaticColor();
        minimap.prepare();
        minimap.setLocation(3, 6);

        hud.load();
        hud.prepare();

        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.load();
        cursor.setGrid(map.getTileWidth(), map.getTileHeight());
        cursor.setInputDevice(mouse);
        cursor.setViewer(camera);

        final Peon peon = factory.create(Peon.MEDIA);
        handler.add(peon);

        final Selector selector = new Selector();
        selector.addFeature(new LayerableModel(1));
        selector.setClickableArea(camera);
        selector.setSelectionColor(ColorRgba.GREEN);
        selector.setClickSelection(Mouse.LEFT);
        selector.addListener(peon);
        handler.add(selector);
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);
        cursor.update(extrp);
        handler.update(extrp);

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
        handler.render(g);
        hud.render(g);
        minimap.render(g);
        cursor.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
