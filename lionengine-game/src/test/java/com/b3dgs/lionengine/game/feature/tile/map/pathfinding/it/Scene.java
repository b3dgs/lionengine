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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding.it;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.sequence.Sequence;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.ComponentDisplayable;
import com.b3dgs.lionengine.game.feature.ComponentRefreshable;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePathModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.io.awt.Mouse;

/**
 * Game loop designed to handle our little world.
 */
class Scene extends Sequence
{
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    private final TextGame text = new TextGame(Text.SANS_SERIF, 10, TextStyle.NORMAL);
    private final Services services = new Services();
    private final Handler handler = services.create(Handler.class);
    private final Camera camera = services.create(Camera.class);
    private final MapTile map = services.create(MapTileGame.class);
    private final Mouse mouse = getInputDevice(Mouse.class);
    private final Timing timing = new Timing();

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
    }

    @Override
    public void load()
    {
        map.create(Medias.create("level.png"));

        camera.setView(0, 0, getWidth(), getHeight(), getHeight());
        camera.setLocation(160, 96);

        map.create(Medias.create("level.png"));
        map.addFeature(new MapTileViewerModel(services));
        map.addFeatureAndGet(new MapTileGroupModel()).loadGroups(Medias.create("groups.xml"));
        map.addFeatureAndGet(new MapTilePathModel(services)).loadPathfinding(Medias.create("pathfinding.xml"));
        camera.setLimits(map);
        handler.add(map);

        final Factory factory = services.create(Factory.class);

        Featurable peon = factory.create(Peon.MEDIA);
        peon.getFeature(Pathfindable.class).setLocation(20, 16);
        peon.getFeature(Pathfindable.class).setDestination(20, 12);
        handler.add(peon);

        peon = factory.create(Peon.MEDIA);
        peon.getFeature(Pathfindable.class).setLocation(22, 14);
        handler.add(peon);

        timing.start();
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);
        handler.update(extrp);
        text.update(camera);
        if (timing.elapsed(1000L))
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        handler.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
