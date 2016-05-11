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
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.collision.tile.MapTileCollision;
import com.b3dgs.lionengine.game.collision.tile.MapTileCollisionModel;
import com.b3dgs.lionengine.game.collision.tile.TileCollidable;
import com.b3dgs.lionengine.game.collision.tile.TileCollidableListener;
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
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);
    /** Background color. */
    private static final ColorRgba BACKGROUND_COLOR = new ColorRgba(107, 136, 255);

    /** Services reference. */
    private final Services services = new Services();
    /** Game factory. */
    private final Factory factory = services.create(Factory.class);
    /** Camera reference. */
    private final Camera camera = services.create(Camera.class);
    /** Map reference. */
    private final MapTile map = services.create(MapTileGame.class);
    /** Map viewer. */
    private final MapTileViewer mapViewer = map.createFeature(MapTileViewerModel.class);
    /** Map group reference. */
    private final MapTileGroup mapGroup = map.createFeature(MapTileGroupModel.class);
    /** Map collision. */
    private final MapTileCollision mapCollision = map.createFeature(MapTileCollisionModel.class);
    /** Timeout. */
    private final Timing timing = new Timing();
    /** Mario reference. */
    private Mario hero;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);
    }

    /*
     * Sequence
     */

    @Override
    public void load()
    {
        map.create(Medias.create("level.png"));
        mapGroup.loadGroups(Medias.create("groups.xml"));
        mapCollision.loadCollisions(Medias.create("formulas.xml"), Medias.create("collisions.xml"));
        mapCollision.createCollisionDraw();

        camera.setIntervals(16, 0);
        camera.setView(0, 0, getWidth(), getHeight());
        camera.setLimits(map);

        services.add(Integer.valueOf(getConfig().getSource().getRate()));
        hero = factory.create(Mario.MEDIA);

        hero.getFeature(TileCollidable.class).addListener(new TileCollidableListener()
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

        timing.start();
    }

    @Override
    public void update(double extrp)
    {
        hero.update(extrp);
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

        mapViewer.render(g);
        mapCollision.render(g);
        hero.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
