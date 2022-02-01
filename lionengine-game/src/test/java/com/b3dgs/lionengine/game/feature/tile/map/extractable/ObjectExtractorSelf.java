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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import java.util.concurrent.atomic.AtomicInteger;

import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Extractor test with self listener.
 */
final class ObjectExtractorSelf extends FeaturableModel implements ExtractorChecker, ExtractorListener
{
    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public ObjectExtractorSelf(Services services, Setup setup)
    {
        super(services, setup);
    }

    /** Flag. */
    final AtomicInteger flag = new AtomicInteger();

    @Override
    public boolean canExtract()
    {
        return true;
    }

    @Override
    public boolean canCarry()
    {
        return true;
    }

    @Override
    public void notifyStartGoToRessources(String type, Tiled resourceLocation)
    {
        flag.compareAndSet(0, 1);
    }

    @Override
    public void notifyStartExtraction(String type, Tiled resourceLocation)
    {
        flag.set(2);
    }

    @Override
    public void notifyExtracted(String type, int currentQuantity)
    {
        flag.set(3);
    }

    @Override
    public void notifyStartCarry(String type, int totalQuantity)
    {
        flag.set(4);
    }

    @Override
    public void notifyStartDropOff(String type, int totalQuantity)
    {
        flag.set(5);
    }

    @Override
    public void notifyDroppedOff(String type, int droppedQuantity)
    {
        flag.set(6);
    }

    @Override
    public void notifyStopped()
    {
        flag.set(7);
    }
}
