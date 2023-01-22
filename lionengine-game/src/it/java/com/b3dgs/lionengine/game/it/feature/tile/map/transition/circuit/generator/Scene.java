/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.it.feature.tile.map.transition.circuit.generator;

import java.util.Arrays;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileAppender;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileAppenderModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransitionModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.TransitionsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.MapTileCircuitModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator.GeneratorParameter;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator.MapGenerator;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator.MapGeneratorImpl;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator.PrefMapFill;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator.PrefMapRegion;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator.PrefMapSize;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator.TileArea;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Sequence;

/**
 * Scene to show map random generation.
 */
public final class Scene extends Sequence
{
    private final Services services = new Services();
    private final Camera camera = services.create(Camera.class);
    private final MapTileGame map = services.create(MapTileGame.class);
    private final MapTileViewer mapViewer = map.addFeatureAndGet(new MapTileViewerModel(services));
    private final MapTileAppender append = map.addFeatureAndGet(new MapTileAppenderModel());
    private final GeneratorParameter parameters = new GeneratorParameter();
    private final MapGenerator generator = new MapGeneratorImpl();
    private final Tick tickGen = new Tick();
    private final Tick tick = new Tick();

    /**
     * Create the scene.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, new Resolution(1024, 768, 60));
    }

    @Override
    public void load()
    {
        map.create(Medias.create("forest.png"));
        mapViewer.prepare(map);

        final Media media = Medias.create("transitions.xml");
        TransitionsConfig.exports(media,
                                  Arrays.asList(Medias.create("forest.png")),
                                  Medias.create("sheets.xml"),
                                  Medias.create("groups.xml"));

        map.addFeatureAndGet(new MapTileGroupModel()).loadGroups(Medias.create("groups.xml"));
        map.addFeatureAndGet(new MapTileTransitionModel()).loadTransitions(Medias.create("transitions.xml"));
        map.addFeatureAndGet(new MapTileCircuitModel()).loadCircuits(Medias.create("circuits.xml"));

        UtilFile.deleteFile(media.getFile());

        camera.setView(0, 0, 1024, 768, getHeight());
        camera.setLimits(map);

        parameters.add(new PrefMapSize(16, 16, 64, 48))
                  .add(new PrefMapFill(0))
                  .add(new PrefMapRegion(12, new TileArea(0, 0, 8, 48), 4, 60))
                  .add(new PrefMapRegion(12, new TileArea(56, 0, 64, 48), 4, 60))
                  .add(new PrefMapRegion(12, new TileArea(0, 0, 64, 8), 4, 60))
                  .add(new PrefMapRegion(12, new TileArea(0, 40, 64, 48), 4, 60))
                  .add(new PrefMapRegion(29, new TileArea(12, 12, 56, 42), 2, 250))
                  .add(new PrefMapRegion(12, new TileArea(24, 24, 40, 40), 2, 80))
                  .add(new PrefMapRegion(0, new TileArea(4, 4, 60, 40), 1, 100));

        tickGen.start();
        tick.start();
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        tickGen.update(extrp);
        if (tickGen.elapsed(10L))
        {
            final MapTile generated = generator.generateMap(parameters,
                                                            Arrays.asList(Medias.create("forest.png")),
                                                            Medias.create("sheets.xml"),
                                                            Medias.create("groups.xml"));
            append.append(generated, 0, 0);
            tickGen.restart();
        }
        if (tick.elapsed(40L))
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        mapViewer.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
