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
package com.b3dgs.lionengine.example.game.assign;

import com.b3dgs.lionengine.Constant;
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
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePathModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Image;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
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
    private final Text text = services.add(Graphics.createText(Text.SANS_SERIF, 9, TextStyle.NORMAL));
    private final Handler handler = services.create(Handler.class);
    private final Cursor cursor = services.create(Cursor.class);
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

        setSystemCursorVisible(false);
        getInputDevice(Keyboard.class).addActionPressed(Keyboard.ESCAPE, () -> end());
    }

    @Override
    public void load()
    {
        final Camera camera = services.create(Camera.class);
        camera.setView(72, 12, 248, 188, getHeight());
        camera.setLocation(320, 208);

        final MapTile map = services.create(MapTileGame.class);
        map.addFeature(new MapTileViewerModel(services));
        map.create(Medias.create("map", "level.png"));
        map.addFeatureAndGet(new MapTileGroupModel()).loadGroups(Medias.create("map", "groups.xml"));
        map.addFeatureAndGet(new MapTilePathModel(services)).loadPathfinding(Medias.create("map", "pathfinding.xml"));
        camera.setLimits(map);
        handler.add(map);

        hud.load();
        hud.prepare();
        text.setLocation(74, 192);

        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.addImage(1, Medias.create("cursor_order.png"));
        cursor.load();
        cursor.setGrid(map.getTileWidth(), map.getTileHeight());
        cursor.setInputDevice(mouse);
        cursor.setViewer(camera);

        final Factory factory = services.create(Factory.class);
        handler.add(factory.create(Move.MEDIA));
        handler.add(factory.create(Peon.MEDIA));
    }

    @Override
    public void update(double extrp)
    {
        text.setText(Constant.EMPTY_STRING);
        mouse.update(extrp);
        cursor.update(extrp);
        handler.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        hud.render(g);
        handler.render(g);
        text.render(g);
        cursor.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
