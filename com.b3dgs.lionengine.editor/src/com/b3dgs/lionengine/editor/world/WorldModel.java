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
package com.b3dgs.lionengine.editor.world;

import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.collision.tile.MapTileCollisionModel;
import com.b3dgs.lionengine.game.handler.ComponentRenderable;
import com.b3dgs.lionengine.game.handler.ComponentUpdatable;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.Minimap;
import com.b3dgs.lionengine.game.map.feature.circuit.MapTileCircuitModel;
import com.b3dgs.lionengine.game.map.feature.group.MapTileGroupModel;
import com.b3dgs.lionengine.game.map.feature.transition.MapTileTransitionModel;
import com.b3dgs.lionengine.game.map.feature.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.pathfinding.MapTilePathModel;

/**
 * Contains the objects of the world.
 */
public class WorldModel
{
    /** World model. */
    public static final WorldModel INSTANCE = new WorldModel();

    /** Services reference. */
    private final Services services = new Services();
    /** Factory reference. */
    private final Factory factory = services.create(Factory.class);
    /** Camera reference. */
    private final Camera camera = services.create(Camera.class);
    /** Map reference. */
    private final MapTile map = services.create(MapTileGame.class);
    /** Minimap reference. */
    private final Minimap minimap = new Minimap(map);

    /**
     * Constructor.
     */
    protected WorldModel()
    {
        final Handler handler = new Handler(services);
        handler.addComponent(new ComponentUpdatable());
        handler.addComponent(new ComponentRenderable());
        services.add(handler);
        services.add(new PaletteModel());

        final Selection selection = new Selection();
        services.add(selection);

        map.addFeature(new MapTileViewerModel(services));
        map.addFeature(new MapTileGroupModel());
        map.addFeature(new MapTileTransitionModel(services));
        map.addFeature(new MapTileCircuitModel(services));
        map.addFeature(new MapTilePathModel(services));
        map.addFeature(new MapTileCollisionModel(services));
    }

    /**
     * Get the services reference.
     * 
     * @return The services reference.
     */
    public Services getServices()
    {
        return services;
    }

    /**
     * Get the factory reference.
     * 
     * @return The factory reference.
     */
    public Factory getFactory()
    {
        return factory;
    }

    /**
     * Get the camera reference.
     * 
     * @return The camera reference.
     */
    public Camera getCamera()
    {
        return camera;
    }

    /**
     * Get the map reference.
     * 
     * @return The map reference.
     */
    public MapTile getMap()
    {
        return map;
    }

    /**
     * Get the minimap reference.
     * 
     * @return The minimap reference.
     */
    public Minimap getMinimap()
    {
        return minimap;
    }
}
