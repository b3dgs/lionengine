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
package com.b3dgs.lionengine.game.map.generator.it;

import java.util.Arrays;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.feature.circuit.MapTileCircuit;
import com.b3dgs.lionengine.game.map.feature.circuit.MapTileCircuitModel;
import com.b3dgs.lionengine.game.map.feature.group.MapTileGroup;
import com.b3dgs.lionengine.game.map.feature.group.MapTileGroupModel;
import com.b3dgs.lionengine.game.map.feature.transition.MapTileTransition;
import com.b3dgs.lionengine.game.map.feature.transition.MapTileTransitionModel;
import com.b3dgs.lionengine.game.map.feature.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.map.feature.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.game.map.generator.GeneratorParameter;
import com.b3dgs.lionengine.game.map.generator.MapGenerator;
import com.b3dgs.lionengine.game.map.generator.MapGeneratorImpl;
import com.b3dgs.lionengine.game.map.generator.PrefMapFill;
import com.b3dgs.lionengine.game.map.generator.PrefMapRegion;
import com.b3dgs.lionengine.game.map.generator.PrefMapSize;
import com.b3dgs.lionengine.game.map.generator.TileArea;
import com.b3dgs.lionengine.game.tile.TileRef;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Scene to show map random generation.
 */
class Scene extends Sequence
{
    private final Services services = new Services();
    private final Camera camera = services.create(Camera.class);
    private final MapTile map = services.create(MapTileGame.class);
    private final MapTileViewer mapViewer = map.createFeature(MapTileViewerModel.class);
    private final GeneratorParameter parameters = new GeneratorParameter();
    private final MapGenerator generator = new MapGeneratorImpl();
    private final Timing timing = new Timing();

    private int count;

    /**
     * Create the scene.
     * 
     * @param context The context reference.
     */
    Scene(Context context)
    {
        super(context, new Resolution(1024, 768, 60));
    }

    @Override
    public void load()
    {
        map.create(Medias.create("forest.png"));

        final MapTileGroup mapGroup = map.createFeature(MapTileGroupModel.class);
        mapGroup.loadGroups(Medias.create("groups.xml"));

        final MapTileTransition mapTransition = map.createFeature(MapTileTransitionModel.class);
        mapTransition.loadTransitions(Medias.create("transitions.xml"));

        final MapTileCircuit mapCircuit = map.createFeature(MapTileCircuitModel.class);
        mapCircuit.loadCircuits(Medias.create("circuits.xml"));

        camera.setView(0, 0, 1024, 768);
        camera.setLimits(map);

        parameters.add(new PrefMapSize(16, 16, 64, 48))
                  .add(new PrefMapFill(new TileRef(0, 0)))
                  .add(new PrefMapRegion(new TileRef(0, 12), new TileArea(0, 0, 8, 48), 4, 60))
                  .add(new PrefMapRegion(new TileRef(0, 12), new TileArea(56, 0, 64, 48), 4, 60))
                  .add(new PrefMapRegion(new TileRef(0, 12), new TileArea(0, 0, 64, 8), 4, 60))
                  .add(new PrefMapRegion(new TileRef(0, 12), new TileArea(0, 40, 64, 48), 4, 60))
                  .add(new PrefMapRegion(new TileRef(0, 29), new TileArea(12, 12, 56, 42), 2, 250))
                  .add(new PrefMapRegion(new TileRef(0, 12), new TileArea(24, 24, 40, 40), 2, 80))
                  .add(new PrefMapRegion(new TileRef(0, 0), new TileArea(4, 4, 60, 40), 1, 100));

        timing.start();
        timing.set(250L);
    }

    @Override
    public void update(double extrp)
    {
        if (timing.elapsed(250L))
        {
            final MapTile generated = generator.generateMap(parameters,
                                                            Arrays.asList(Medias.create("forest.png")),
                                                            Medias.create("sheets.xml"),
                                                            Medias.create("groups.xml"));
            map.append(generated, 0, 0);
            count++;
            timing.restart();
        }
        if (count > 5)
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        mapViewer.render(g);
    }
}
