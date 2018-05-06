/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import java.util.concurrent.atomic.AtomicInteger;

import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.IdentifiableModel;

/**
 * Extractor test with self listener.
 */
final class ObjectExtractorSelf extends FeaturableModel implements ExtractorChecker, ExtractorListener
{
    /** Flag. */
    final AtomicInteger flag = new AtomicInteger();

    /**
     * Constructor.
     */
    public ObjectExtractorSelf()
    {
        super();

        addFeature(new IdentifiableModel());
    }

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
    public void notifyStartGoToRessources(Enum<?> type, Tiled resourceLocation)
    {
        flag.compareAndSet(0, 1);
    }

    @Override
    public void notifyStartExtraction(Enum<?> type, Tiled resourceLocation)
    {
        flag.set(2);
    }

    @Override
    public void notifyExtracted(Enum<?> type, int currentQuantity)
    {
        flag.set(3);
    }

    @Override
    public void notifyStartCarry(Enum<?> type, int totalQuantity)
    {
        flag.set(4);
    }

    @Override
    public void notifyStartDropOff(Enum<?> type, int totalQuantity)
    {
        flag.set(5);
    }

    @Override
    public void notifyDroppedOff(Enum<?> type, int droppedQuantity)
    {
        flag.set(6);
    }
}
