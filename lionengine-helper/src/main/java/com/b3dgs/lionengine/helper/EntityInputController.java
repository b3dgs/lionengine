/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Map.Entry;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.io.InputDeviceControl;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * Entity input control implementation.
 */
@FeatureInterface
public final class EntityInputController extends FeatureModel
{
    private final InputDeviceControl controller;

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

        final InputDeviceDirectional device = services.get(InputDeviceDirectional.class);
        try
        {
            final InputControllerConfig config = InputControllerConfig.imports(services, setup);
            controller = UtilReflection.createReduce(config.getControl(), device);

            for (final Entry<Integer, Integer> entry : config.getCodes().entrySet())
            {
                controller.setFireButton(entry.getKey(), entry.getValue());
            }
        }
        catch (final ReflectiveOperationException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Set the fire button code.
     * 
     * @param index The button index (must not be <code>null</code>, must be positive).
     * @param code The fire button code (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public void setFireButton(Integer index, Integer code)
    {
        controller.setFireButton(index, code);
    }

    /**
     * Set the horizontal positive control code.
     * 
     * @param code The horizontal positive control code (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public void setHorizontalControlPositive(Integer code)
    {
        controller.setHorizontalControlPositive(code);
    }

    /**
     * Set the horizontal negative control code.
     * 
     * @param code The horizontal negative control code (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public void setHorizontalControlNegative(Integer code)
    {
        controller.setHorizontalControlNegative(code);
    }

    /**
     * Set the vertical positive control code.
     * 
     * @param code The vertical positive control code (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public void setVerticalControlPositive(Integer code)
    {
        controller.setVerticalControlPositive(code);
    }

    /**
     * Set the vertical negative control code.
     * 
     * @param code The vertical negative control code (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public void setVerticalControlNegative(Integer code)
    {
        controller.setVerticalControlNegative(code);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        model.setInput(controller);
    }
}
