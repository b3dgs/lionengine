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
package com.b3dgs.lionengine.example.game.assign;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Medias;
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
import com.b3dgs.lionengine.game.map.MapTilePath;
import com.b3dgs.lionengine.game.map.MapTilePathModel;
import com.b3dgs.lionengine.game.object.ComponentRenderer;
import com.b3dgs.lionengine.game.object.ComponentUpdater;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;

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

    /** Action factory. */
    private final Factory factory = new Factory();
    /** Camera reference. */
    private final Camera camera = new Camera();
    /** Actions handler. */
    private final Handler handler = new Handler();
    /** Text reference. */
    private final Text text = Graphics.createText(Text.SANS_SERIF, 9, TextStyle.NORMAL);
    /** Map reference. */
    private final MapTile map = new MapTileGame(16, 16, camera);
    /** Map path. */
    private final MapTilePath mapPath = new MapTilePathModel(map);
    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Mouse reference. */
    private final Mouse mouse;
    /** Cursor reference. */
    private final Cursor cursor;
    /** HUD image. */
    private final Image hud;

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
        cursor = new Cursor(mouse, Medias.create("cursor.png"), Medias.create("cursor_order.png"));
        hud = Drawable.loadImage(Medias.create("hud.png"));
        setSystemCursorVisible(false);
    }

    @Override
    protected void load()
    {
        map.addFeature(mapPath);
        map.create(Medias.create("map", "level.png"), Medias.create("map", "sheets.xml"),
                Medias.create("map", "groups.xml"));
        mapPath.loadPathfinding(Medias.create("map", "pathfinding.xml"));

        hud.load(false);
        text.setLocation(74, 192);
        cursor.load(false);
        cursor.setArea(0, 0, getWidth(), getHeight());
        cursor.setGrid(map.getTileWidth(), map.getTileHeight());
        cursor.setViewer(camera);

        camera.setView(72, 12, 240, 176);
        camera.setLimits(map);
        camera.setLocation(320, 208);

        factory.addService(cursor);
        factory.addService(text);
        factory.addService(map);
        factory.addService(camera);
        factory.addService(handler);

        handler.addUpdatable(new ComponentUpdater());
        handler.addRenderable(new ComponentRenderer());

        final ObjectGame move = factory.create(Move.MEDIA);
        final ObjectGame peon = factory.create(Peon.MEDIA);
        handler.add(move);
        handler.add(peon);
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
