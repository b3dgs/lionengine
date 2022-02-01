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
package com.b3dgs.lionengine.helper;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.DeviceControllerVoid;

/**
 * Entity input control implementation.
 */
@FeatureInterface
public final class EntityInputController extends FeatureModel implements Routine
{
    private final DeviceController controller;

    @FeatureGet private EntityModelHelper model;

    /**
     * Create controller.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @throws LionEngineException If invalid class control.
     */
    public EntityInputController(Services services, Setup setup)
    {
        super(services, setup);

        controller = services.getOptional(DeviceController.class).orElse(DeviceControllerVoid.getInstance());
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        model.setInput(controller);
    }

    @Override
    public void update(double extrp)
    {
        controller.update(extrp);
    }
}
