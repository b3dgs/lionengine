/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.HandlerPersister;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionengine.io.InputDeviceControlVoid;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * World helper implementation.
 */
public class WorldHelper extends WorldGame
{
    /** Void device error. */
    private static final String ERROR_INPUT_DEVICE = "Void input device used !";

    /** Map reference. */
    protected final MapTileHelper map = services.create(MapTileHelper.class);
    /** Camera tracker. */
    protected final CameraTracker tracker = services.create(CameraTracker.class);
    /** Handler persister. */
    protected final HandlerPersister persister = services.create(HandlerPersister.class);
    /** Source provider. */
    protected final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);

    /**
     * Create world.
     * 
     * @param services The services reference.
     */
    public WorldHelper(Services services)
    {
        super(services);

        addInputDevice();

        handler.addComponent(new ComponentCollision());
        handler.add(map);
        handler.add(tracker);
    }

    /**
     * Add input device or void if none.
     */
    private void addInputDevice()
    {
        try
        {
            services.add(getInputDevice(InputDeviceDirectional.class));
        }
        catch (final LionEngineException exception)
        {
            Verbose.exception(exception, ERROR_INPUT_DEVICE);
            services.add(InputDeviceControlVoid.getInstance());
        }
    }

    /*
     * WorldGame
     */

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
