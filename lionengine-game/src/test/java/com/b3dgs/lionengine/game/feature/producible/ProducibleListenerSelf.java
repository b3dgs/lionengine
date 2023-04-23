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
package com.b3dgs.lionengine.game.feature.producible;

import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Producible self listener test.
 */
final class ProducibleListenerSelf extends FeaturableModel implements ProducibleListener
{
    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public ProducibleListenerSelf(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void notifyProductionStarted(Producer producer)
    {
        // Mock
    }

    @Override
    public void notifyProductionProgress(Producer producer)
    {
        // Mock
    }

    @Override
    public void notifyProductionEnded(Producer producer)
    {
        // Mock
    }
}
