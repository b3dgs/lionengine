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
package com.b3dgs.lionengine.example.game.pathfinding;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.MapTilePath;
import com.b3dgs.lionengine.game.map.MapTilePathModel;
import com.b3dgs.lionengine.game.map.Tile;
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

    /** Text reference. */
    private final TextGame text = new TextGame(Text.SANS_SERIF, 10, TextStyle.NORMAL);
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
    /** Keyboard reference. */
    private final Keyboard keyboard = getInputDevice(Keyboard.class);
    /** Mouse reference. */
    private final Mouse mouse = getInputDevice(Mouse.class);
    /** Peon reference. */
    private Peon peon;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, NATIVE);
        setSystemCursorVisible(false);
        keyboard.addActionPressed(Keyboard.ESCAPE, () -> end());
    }

    @Override
    protected void load()
    {
        map.create(Medias.create("level.png"));
        mapPath.loadPathfinding(Medias.create("pathfinding.xml"));

        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.load();
        cursor.setArea(0, 0, getWidth(), getHeight());
        cursor.setGrid(map.getTileWidth(), map.getTileHeight());
        cursor.setInputDevice(mouse);
        cursor.setViewer(camera);

        camera.setView(0, 0, getWidth(), getHeight());
        camera.setLimits(map);
        camera.setLocation(320, 208);

        peon = factory.create(Peon.MEDIA);
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);
        cursor.update(extrp);
        peon.update(extrp);
        text.update(camera);
    }

    @Override
    public void render(Graphic g)
    {
        map.render(g);
        peon.render(g);
        cursor.render(g);

        final Tile tile = map.getTile(cursor.getInTileX(), cursor.getInTileY());
        if (tile != null)
        {
            text.drawRect(g, ColorRgba.GREEN, tile.getX(), tile.getY(), map.getTileWidth(), map.getTileHeight());
            text.setColor(ColorRgba.YELLOW);
        }
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
