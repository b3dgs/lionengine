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
package com.b3dgs.lionengine.example.game.pathfinding;

import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.handler.ComponentDisplayable;
import com.b3dgs.lionengine.game.handler.ComponentRefreshable;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.feature.group.MapTileGroup;
import com.b3dgs.lionengine.game.map.feature.group.MapTileGroupModel;
import com.b3dgs.lionengine.game.map.feature.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.game.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.pathfinding.MapTilePathModel;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Game loop designed to handle our little world.
 * 
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene extends Sequence
{
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    private final TextGame text = new TextGame(Text.SANS_SERIF, 10, TextStyle.NORMAL);
    private final Services services = new Services();
    private final Handler handler = services.create(Handler.class);
    private final Camera camera = services.create(Camera.class);
    private final Cursor cursor = services.create(Cursor.class);
    private final MapTile map = services.create(MapTileGame.class);
    private final Mouse mouse = getInputDevice(Mouse.class);

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());

        setSystemCursorVisible(false);
        getInputDevice(Keyboard.class).addActionPressed(Keyboard.ESCAPE, () -> end());
    }

    @Override
    public void load()
    {
        map.create(Medias.create("level.png"));

        final MapTileGroup mapGroup = map.createFeature(MapTileGroupModel.class);
        mapGroup.loadGroups(Medias.create("groups.xml"));

        final MapTilePath mapPath = map.createFeature(MapTilePathModel.class);
        mapPath.loadPathfinding(Medias.create("pathfinding.xml"));

        camera.setView(0, 0, getWidth(), getHeight());
        camera.setLimits(map);
        camera.setLocation(320, 208);

        map.addFeature(new MapTileViewerModel(services));
        handler.add(map);

        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.load();
        cursor.setGrid(map.getTileWidth(), map.getTileHeight());
        cursor.setInputDevice(mouse);
        cursor.setViewer(camera);

        final Factory factory = services.create(Factory.class);
        handler.add(factory.create(Peon.MEDIA));
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);
        cursor.update(extrp);
        handler.update(extrp);
        text.update(camera);
    }

    @Override
    public void render(Graphic g)
    {
        handler.render(g);

        final Tile tile = map.getTile(cursor, 0, 0);
        if (tile != null)
        {
            text.drawRect(g,
                          ColorRgba.GREEN,
                          (int) tile.getX(),
                          (int) tile.getY(),
                          map.getTileWidth(),
                          map.getTileHeight());
            text.setColor(ColorRgba.YELLOW);
        }
        cursor.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
