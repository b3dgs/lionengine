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
package com.b3dgs.lionengine.example.game.selector;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.Selector;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.MapTilePath;
import com.b3dgs.lionengine.game.map.MapTilePathModel;
import com.b3dgs.lionengine.game.map.Minimap;
import com.b3dgs.lionengine.game.object.Factory;
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
    /** Map path. */
    private final MapTilePath mapPath = map.createFeature(MapTilePathModel.class);
    /** Minimap reference. */
    private final Minimap minimap = new Minimap(map);
    /** Keyboard reference. */
    private final Keyboard keyboard = getInputDevice(Keyboard.class);
    /** Mouse reference. */
    private final Mouse mouse = getInputDevice(Mouse.class);
    /** Selector reference. */
    private final Selector selector = new Selector(camera, cursor);
    /** HUD image. */
    private final Image hud;
    /** Peon reference. */
    private Peon peon;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        hud = Drawable.loadImage(Medias.create("hud.png"));
        setSystemCursorVisible(false);
    }

    @Override
    protected void load()
    {
        map.create(Medias.create("map", "level.png"), Medias.create("map", "sheets.xml"),
                Medias.create("map", "groups.xml"));
        mapPath.loadPathfinding(Medias.create("map", "pathfinding.xml"));
        minimap.loadPixelConfig(Medias.create("map", "minimap.xml"));
        minimap.load(false);
        minimap.setLocation(3, 6);

        hud.load(false);
        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.load(false);
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

        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
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
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
