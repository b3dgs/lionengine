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
package com.b3dgs.lionengine.game.collision.tile.it;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.camera.CameraTracker;
import com.b3dgs.lionengine.game.collision.tile.MapTileCollision;
import com.b3dgs.lionengine.game.collision.tile.MapTileCollisionModel;
import com.b3dgs.lionengine.game.collision.tile.MapTileCollisionRenderer;
import com.b3dgs.lionengine.game.collision.tile.MapTileCollisionRendererModel;
import com.b3dgs.lionengine.game.collision.tile.TileCollidable;
import com.b3dgs.lionengine.game.collision.tile.TileCollidableListener;
import com.b3dgs.lionengine.game.handler.ComponentRefreshable;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.layer.ComponentDisplayerLayer;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.feature.group.MapTileGroup;
import com.b3dgs.lionengine.game.map.feature.group.MapTileGroupModel;
import com.b3dgs.lionengine.game.map.feature.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.map.feature.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Game loop designed to handle our little world.
 */
class Scene extends Sequence
{
    private static final Resolution NATIVE = new Resolution(320, 240, 60);
    private static final ColorRgba BACKGROUND_COLOR = new ColorRgba(107, 136, 255);

    private final Services services = new Services();
    private final Handler handler = services.create(Handler.class);
    private final Timing timing = new Timing();

    /**
     * Create the scene.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);

        services.add(getInputDevice(Keyboard.class));
        services.add(Integer.valueOf(getConfig().getSource().getRate()));

        handler.addComponent(new ComponentDisplayerLayer());
        handler.addComponent(new ComponentRefreshable());
    }

    @Override
    public void load()
    {
        final MapTile map = services.create(MapTileGame.class);
        map.create(Medias.create("level.png"));
        handler.add(map);

        final Camera camera = services.create(Camera.class);
        camera.setIntervals(16, 0);
        camera.setView(0, 0, getWidth(), getHeight());
        camera.setLimits(map);
        handler.add(camera);

        final MapTileGroup mapGroup = map.createFeature(MapTileGroupModel.class);
        mapGroup.loadGroups(Medias.create("groups.xml"));

        final MapTileCollision mapCollision = map.createFeature(MapTileCollisionModel.class);
        mapCollision.loadCollisions(Medias.create("formulas.xml"), Medias.create("collisions.xml"));

        final MapTileCollisionRenderer mapCollisionRenderer = new MapTileCollisionRendererModel(services);
        mapCollisionRenderer.createCollisionDraw();

        final MapTileViewer mapViewer = map.createFeature(MapTileViewerModel.class);
        mapViewer.addRenderer(mapCollisionRenderer);

        final Factory factory = services.create(Factory.class);
        final Mario mario = factory.create(Mario.MEDIA);
        mario.getFeature(TileCollidable.class).addListener(new TileCollidableListener()
        {
            @Override
            public void notifyTileCollided(Tile tile, Axis axis)
            {
                if (Axis.X == axis)
                {
                    end();
                }
            }
        });
        handler.add(mario);

        final CameraTracker tracker = new CameraTracker();
        camera.addFeature(tracker);
        tracker.track(mario);

        timing.start();
    }

    @Override
    public void update(double extrp)
    {
        handler.update(extrp);

        if (timing.elapsed(5000L))
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
