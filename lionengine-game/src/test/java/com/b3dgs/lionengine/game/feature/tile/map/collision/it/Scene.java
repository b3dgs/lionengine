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
package com.b3dgs.lionengine.game.feature.tile.map.collision.it;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
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
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileRendererModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRenderer;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRendererModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.io.awt.Keyboard;

/**
 * Game loop designed to handle our little world.
 */
class Scene extends Sequence
{
    private static final Resolution NATIVE = new Resolution(320, 240, 60);
    private static final ColorRgba BACKGROUND_COLOR = new ColorRgba(107, 136, 255);

    private final Services services = new Services();
    private final Handler handler = services.create(Handler.class);
    private final Timing clear = new Timing();
    private final Timing timing = new Timing();

    private MapTileCollisionRenderer mapCollisionRenderer;

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

        services.add(getInputDevice(Keyboard.class));
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

        final MapTileGroup mapGroup = map.addFeatureAndGet(new MapTileGroupModel());
        final MapTileCollision mapCollision = map.addFeatureAndGet(new MapTileCollisionModel());
        mapCollisionRenderer = map.addFeatureAndGet(new MapTileCollisionRendererModel());

        handler.add(map);

        mapGroup.loadGroups(Medias.create("groups.xml"));
        mapCollision.loadCollisions(Medias.create("formulas.xml"), Medias.create("collisions.xml"));
        mapCollisionRenderer.createCollisionDraw();

        final MapTileViewer mapViewer = map.addFeatureAndGet(new MapTileViewerModel());
        mapViewer.addRenderer(new MapTileRendererModel());
        mapViewer.addRenderer(mapCollisionRenderer);
        mapViewer.prepare(map, services);

        final Factory factory = services.create(Factory.class);
        final Mario mario = factory.create(Mario.MEDIA);
        handler.add(mario);

        final CameraTracker tracker = new CameraTracker();
        tracker.track(mario);
        handler.add(tracker);

        clear.start();
        timing.start();
    }

    @Override
    public void update(double extrp)
    {
        handler.update(extrp);

        if (clear.elapsed(500L))
        {
            mapCollisionRenderer.clearCollisionDraw();
            clear.stop();
        }
        if (timing.elapsed(1000L))
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(Scene.BACKGROUND_COLOR);
        g.drawRect(0, 0, getWidth(), getHeight(), true);

        handler.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
