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
import java.util.Optional;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.ComponentRenderer;
import com.b3dgs.lionengine.game.feature.ComponentUpdater;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.HandlerPersister;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * World helper implementation.
 */
public class WorldHelper extends WorldGame
{
    /** Map reference. */
    protected final MapTileHelper map = services.create(MapTileHelper.class);
    /** Camera tracker. */
    protected final CameraTracker tracker = services.create(CameraTracker.class);
    /** Handler persister. */
    protected final HandlerPersister persister = services.create(HandlerPersister.class);
    /** Component collisions. */
    protected final ComponentCollision componentCollision = new ComponentCollision(camera);

    /**
     * Create world.
     * 
     * @param services The services reference.
     */
    public WorldHelper(Services services)
    {
        super(services);

        handler.addComponent((ComponentUpdater) componentCollision);
        handler.addComponent((ComponentRenderer) componentCollision);
        handler.add(map);
        handler.add(tracker);
    }

    @Override
    protected Spawner createSpawner()
    {
        return new Spawner()
        {
            private Optional<Media> raster = Optional.empty();

            @Override
            public void setRaster(Media raster)
            {
                this.raster = Optional.ofNullable(raster);
            }

            @Override
            public Featurable spawn(Media media, double x, double y)
            {
                final Featurable f = factory.create(media);
                f.getFeature(Transformable.class).teleport(x, y);
                f.ifIs(Rasterable.class, r -> raster.ifPresent(m -> r.setRaster(true, m, map.getTileHeight())));
                handler.add(f);
                return f;
            }
        };
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        map.getFeature(MapTilePersister.class).save(file);
        persister.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        map.getFeature(MapTilePersister.class).load(file);
        persister.load(file);
    }
}
