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
package com.b3dgs.lionengine.example.game.raster;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.EventAction;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.MapTileRastered;
import com.b3dgs.lionengine.game.map.MapTileRasteredModel;
import com.b3dgs.lionengine.game.object.Services;

/**
 * Game loop designed to handle our world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Timing value. */
    private final Timing timing = new Timing();
    /** Services reference. */
    private final Services services = new Services();
    /** Camera reference. */
    private final Camera camera = services.create(Camera.class);
    /** Map reference. */
    private final MapTile map = services.create(MapTileGame.class);
    /** Map raster reference. */
    private final MapTileRastered raster = services.create(MapTileRasteredModel.class);
    /** Keyboard reference. */
    private final Keyboard keyboard = getInputDevice(Keyboard.class);
    /** Renderable selection (false = default, true = raster). */
    private boolean useRaster;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, NATIVE);
        keyboard.addActionPressed(Keyboard.ESCAPE, new EventAction()
        {
            @Override
            public void action()
            {
                end();
            }
        });
    }

    @Override
    protected void load()
    {
        map.create(Medias.create("level.png"), Medias.create("sheets.xml"), Medias.create("groups.xml"));
        raster.loadSheets(Medias.create("sheets.xml"), Medias.create("raster.xml"), false);

        camera.setView(0, 0, getWidth(), getHeight());
        camera.setLimits(map);

        timing.start();
    }

    @Override
    public void update(double extrp)
    {
        if (timing.isStarted() && timing.elapsed(1000))
        {
            useRaster = !useRaster;
            map.setTileRenderer(useRaster ? raster : map);
            timing.restart();
        }
    }

    @Override
    public void render(Graphic g)
    {
        map.render(g);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
