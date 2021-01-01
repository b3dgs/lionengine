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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.io.InputDeviceControl;
import com.b3dgs.lionengine.io.InputDeviceControlVoid;

/**
 * Entity model implementation.
 */
@FeatureInterface
public class EntityModelHelper extends FeatureModel
{
    /** Input reference. */
    protected InputDeviceControl input = InputDeviceControlVoid.getInstance();

    /**
     * Create model.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public EntityModelHelper(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Set the input device used.
     * 
     * @param input The input reference.
     */
    public void setInput(InputDeviceControl input)
    {
        this.input = input;
    }

    /**
     * Get current input device control.
     * 
     * @return The input reference.
     */
    public InputDeviceControl getInput()
    {
        return input;
    }
}
