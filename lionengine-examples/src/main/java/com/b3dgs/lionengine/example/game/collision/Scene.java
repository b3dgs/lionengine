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
package com.b3dgs.lionengine.example.game.collision;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.sequence.Sequence;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.ComponentDisplayable;
import com.b3dgs.lionengine.game.feature.ComponentRefreshable;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileRendererModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRenderer;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRendererModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.io.awt.Keyboard;

/**
 * Game loop designed to handle our little world.
 * 
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene extends Sequence
{
    /** Native resolution. */
    public static final Resolution NATIVE = new Resolution(320, 240, 60);

    private final Services services = new Services();
    private final Handler handler = services.create(Handler.class);

    /**
     * Create the scene.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());

        services.add(getInputDevice(Keyboard.class)).addActionPressed(Keyboard.ESCAPE, () -> end());
        services.add(context);
    }

    @Override
    public void load()
    {
        final MapTile map = services.create(MapTileGame.class);
        map.create(Medias.create("level.png"));

        final Camera camera = services.create(Camera.class);
        camera.setIntervals(16, 0);
        camera.setView(0, 0, getWidth(), getHeight(), getHeight());
        camera.setLimits(map);
        handler.add(camera);

        final CameraTracker tracker = new CameraTracker(services);
        handler.add(tracker);

        map.addFeatureAndGet(new MapTileGroupModel()).loadGroups(Medias.create("groups.xml"));
        map.addFeatureAndGet(new MapTileCollisionModel(services)).loadCollisions(Medias.create("formulas.xml"),
                                                                                 Medias.create("collisions.xml"));
        final MapTileCollisionRenderer mapCollisionRenderer;
        mapCollisionRenderer = map.addFeatureAndGet(new MapTileCollisionRendererModel(services));
        mapCollisionRenderer.createCollisionDraw();

        final MapTileViewer mapViewer = map.addFeatureAndGet(new MapTileViewerModel(services));
        mapViewer.addRenderer(new MapTileRendererModel());
        mapViewer.addRenderer(mapCollisionRenderer);
        handler.add(map);

        final Factory factory = services.create(Factory.class);
        final Player player = factory.create(Player.MEDIA);
        handler.add(player);
        tracker.track(player);
    }

    @Override
    public void update(double extrp)
    {
        handler.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        handler.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
