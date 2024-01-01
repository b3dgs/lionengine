/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.world;

import com.b3dgs.lionengine.editor.ObjectRepresentation;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.ComponentDisplayable;
import com.b3dgs.lionengine.game.feature.ComponentRefreshable;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.HandlerPersister;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.Minimap;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRendererModel;
import com.b3dgs.lionengine.helper.MapTileHelper;

/**
 * Contains the objects of the world.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling
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
    /** Handler reference. */
    private final Handler handler = services.create(Handler.class);
    /** Handler persister reference. */
    private final HandlerPersister handlerPersister = services.create(HandlerPersisterEditor.class);
    /** Map reference. */
    private final MapTileHelper map = services.create(MapTileHelper.class);
    /** Minimap reference. */
    private final Minimap minimap = new Minimap(map);

    /**
     * Constructor.
     */
    protected WorldModel()
    {
        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
        services.add(new PaletteModel());

        map.addFeature(new MapTileCollisionRendererModel());

        final Selection selection = new Selection();
        services.add(selection);

        services.add((Spawner) (media, x, y) ->
        {
            final Featurable featurable = factory.create(media, ObjectRepresentation.class);
            featurable.getFeature(Transformable.class).teleport(x, y);
            handler.add(featurable);
            return featurable;
        });
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
    public MapTileHelper getMap()
    {
        return map;
    }

    /**
     * Get the handler reference.
     * 
     * @return The handler reference.
     */
    public Handler getHandler()
    {
        return handler;
    }

    /**
     * Get the handler persister reference.
     * 
     * @return The handler persister reference.
     */
    public HandlerPersister getHandlerPersister()
    {
        return handlerPersister;
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
