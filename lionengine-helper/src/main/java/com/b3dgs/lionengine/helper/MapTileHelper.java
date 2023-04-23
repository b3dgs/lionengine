/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.helper;

import java.io.IOException;
import java.util.function.Consumer;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.HandlerListener;
import com.b3dgs.lionengine.game.feature.HandlerPersister;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileAppenderModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroupConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePathModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableListener;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableListenerVoid;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindingConfig;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterListener;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.game.feature.tile.map.raster.MapTileRastered;
import com.b3dgs.lionengine.game.feature.tile.map.raster.MapTileRasteredModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransitionModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.TransitionsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.CircuitsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.MapTileCircuit;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.MapTileCircuitModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.FogOfWar;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.Fovable;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Map tile helper implementation.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling|FanOutComplexity
public class MapTileHelper extends MapTileGame
{
    /**
     * Import and save the level.
     * 
     * @param levelrip The level rip.
     * @param out The output media.
     */
    public static void importAndSave(Media levelrip, Media out)
    {
        importAndSave(levelrip, out, new MapTilePersisterModel());
    }

    /**
     * Import and save the level.
     * 
     * @param levelrip The level rip.
     * @param out The output media.
     * @param mapPersister The persister reference.
     */
    public static void importAndSave(Media levelrip, Media out, MapTilePersister mapPersister)
    {
        importAndSave(levelrip, Medias.create(levelrip.getParentPath(), TileSheetsConfig.FILENAME), out, mapPersister);
    }

    /**
     * Import and save the level.
     * 
     * @param levelrip The level rip.
     * @param sheetsConfig The file that define the sheets configuration.
     * @param out The output media.
     * @param mapPersister The persister reference.
     */
    public static void importAndSave(Media levelrip, Media sheetsConfig, Media out, MapTilePersister mapPersister)
    {
        final Services services = new Services();
        final MapTileGame map = services.create(MapTileGame.class);
        map.create(levelrip, sheetsConfig);
        map.addFeature(mapPersister);

        services.add(new Factory(services));
        services.add(new Handler(services));
        final HandlerPersister persister = new HandlerPersister(services);

        try (FileWriting output = new FileWriting(out))
        {
            mapPersister.save(output);
            persister.save(output);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception, "Error on saving map !");
        }
    }

    /**
     * Load configuration from file.
     * 
     * @param media The media parent.
     * @param loader The loader reference.
     * @param file The file to load.
     */
    private static void load(Media media, Consumer<Media> loader, String file)
    {
        final Media config = Medias.create(media.getParentPath(), file);
        if (config.exists())
        {
            loader.accept(config);
        }
    }

    private final MapTileGroup mapGroup;
    private final MapTileCollision mapCollision;
    private final MapTilePath mapPath;
    private final MapTileTransition mapTransition;
    private final MapTileCircuit mapCircuit;
    private final MapTileRastered mapRaster;
    private final MapTileViewer mapViewer;
    private final FogOfWar fogOfWar;
    // TODO Notify tiled moved
    private final PathfindableListener listener = new PathfindableListenerVoid()
    {
        @Override
        public void notifyMoving(Pathfindable pathfindable)
        {
            fogOfWar.update(pathfindable.getFeature(Fovable.class));
        }
    };

    /**
     * Create helper.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * </ul>
     * 
     * @param services The services reference.
     */
    public MapTileHelper(Services services)
    {
        super();

        mapGroup = addFeatureAndGet(new MapTileGroupModel());
        mapCollision = addFeatureAndGet(new MapTileCollisionModel());
        mapPath = addFeatureAndGet(new MapTilePathModel());
        mapTransition = addFeatureAndGet(new MapTileTransitionModel());
        mapCircuit = addFeatureAndGet(new MapTileCircuitModel());
        mapRaster = addFeatureAndGet(new MapTileRasteredModel());
        mapViewer = addFeatureAndGet(new MapTileViewerModel(services));
        fogOfWar = services.add(addFeatureAndGet(new FogOfWar()));
        addFeature(new MapTileAppenderModel());
        addFeatureAndGet(new MapTilePersisterModel()).addListener(new MapTilePersisterListener()
        {
            @Override
            public void notifyMapLoadStart()
            {
                loadBefore(getMedia());
            }

            @Override
            public void notifyMapLoaded()
            {
                loadAfter(getMedia());
                services.get(Camera.class).setLimits(mapSurface);
            }
        });

        final Handler handler = services.get(Handler.class);
        handler.addListener(new HandlerListener()
        {
            @Override
            public void notifyHandlableAdded(Featurable featurable)
            {
                handleAdded(featurable);
            }

            @Override
            public void notifyHandlableRemoved(Featurable featurable)
            {
                handleRemoved(featurable);
            }
        });
    }

    /**
     * Load features.
     * 
     * @param media The parent media.
     */
    public void loadBefore(Media media)
    {
        if (media != null)
        {
            load(media, mapGroup::loadGroups, TileGroupsConfig.FILENAME);
        }
    }

    /**
     * Load features.
     * 
     * @param media The parent media.
     */
    public void loadAfter(Media media)
    {
        if (media != null)
        {
            final String parent = media.getParentPath();

            final Media configFormulas = Medias.create(parent, CollisionFormulaConfig.FILENAME);
            if (configFormulas.exists())
            {
                mapCollision.loadCollisions(configFormulas, Medias.create(parent, CollisionGroupConfig.FILENAME));
            }
            load(media, mapPath::loadPathfinding, PathfindingConfig.FILENAME);
            load(media, mapTransition::loadTransitions, TransitionsConfig.FILENAME);
            load(media, mapCircuit::loadCircuits, CircuitsConfig.FILENAME);

            if (mapRaster.loadSheets())
            {
                mapViewer.clear();
                mapViewer.addRenderer(mapRaster);
            }

            final Media configFog = Medias.create(parent, "fog.xml");
            if (configFog.exists())
            {
                fogOfWar.create(configFog);
                final int tw = getTileWidth();
                final int th = getTileHeight();
                final SpriteTiled hide = Drawable.loadSpriteTiled(Medias.create(parent, "hide.png"), tw, th);
                final SpriteTiled fog = Drawable.loadSpriteTiled(Medias.create(parent, "fog.png"), tw, th);
                hide.load();
                hide.prepare();
                fog.load();
                fog.prepare();
                fogOfWar.setTilesheet(hide, fog);
                fogOfWar.setEnabled(true, true);
                mapViewer.addRenderer(fogOfWar);
            }
        }
    }

    /**
     * Handle added featurable.
     * 
     * @param featurable The added featurable.
     */
    private void handleAdded(Featurable featurable)
    {
        if (fogOfWar.hasFogOfWar() && featurable.hasFeature(Fovable.class) && featurable.hasFeature(Pathfindable.class))
        {
            featurable.getFeature(Pathfindable.class).addListener(listener);
            fogOfWar.update(featurable.getFeature(Fovable.class));
        }
    }

    /**
     * Handle removed featurable.
     * 
     * @param featurable The removed featurable.
     */
    private void handleRemoved(Featurable featurable)
    {
        if (fogOfWar.hasFogOfWar() && featurable.hasFeature(Fovable.class) && featurable.hasFeature(Pathfindable.class))
        {
            featurable.getFeature(Pathfindable.class).removeListener(listener);
            fogOfWar.update(featurable.getFeature(Fovable.class));
        }
    }

    /*
     * MapTileGame
     */

    @Override
    public void create(int tileWidth, int tileHeight, int widthInTile, int heightInTile)
    {
        super.create(tileWidth, tileHeight, widthInTile, heightInTile);

        loadBefore(getMedia());
        loadAfter(getMedia());
    }

    @Override
    public void create(Media levelrip, int tileWidth, int tileHeight, int horizontalTiles)
    {
        super.create(levelrip, tileWidth, tileHeight, horizontalTiles);

        loadBefore(getMedia());
        loadAfter(levelrip);
    }

    @Override
    public void create(Media levelrip)
    {
        super.create(levelrip);

        loadBefore(getMedia());
        loadAfter(getMedia());
    }
}
