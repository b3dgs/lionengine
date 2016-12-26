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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.IdentifiableModel;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;

/**
 * Utilities dedicated to extractable test.
 */
public class UtilExtractable
{
    /**
     * Create extractable.
     * 
     * @return The extractable.
     */
    public static Extractable createExtractable()
    {
        final Services services = new Services();
        services.add(new MapTileGame());

        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new IdentifiableModel());
        featurable.addFeature(new TransformableModel());

        final Extractable extractable = new ExtractableModel();
        extractable.setResourcesQuantity(10);
        extractable.setResourcesType(ResourceType.WOOD);
        extractable.prepare(featurable, services);

        featurable.prepareFeatures(services);

        return extractable;
    }

    /**
     * Create listener.
     * 
     * @param goTo The go to.
     * @param startExtract The start extract.
     * @param extracted The extracted.
     * @param carry The carry.
     * @param startDrop The start drop.
     * @param endDrop The dropped.
     * @return The created listener.
     */
    public static ExtractorListener createListener(final AtomicReference<Enum<?>> goTo,
                                                   final AtomicReference<Enum<?>> startExtract,
                                                   final AtomicReference<Enum<?>> extracted,
                                                   final AtomicReference<Enum<?>> carry,
                                                   final AtomicReference<Enum<?>> startDrop,
                                                   final AtomicReference<Enum<?>> endDrop)
    {
        return new ExtractorListener()
        {
            @Override
            public void notifyStartGoToRessources(Enum<?> type, Tiled resourceLocation)
            {
                goTo.set(type);
            }

            @Override
            public void notifyStartExtraction(Enum<?> type, Tiled resourceLocation)
            {
                startExtract.set(type);
            }

            @Override
            public void notifyExtracted(Enum<?> type, int currentQuantity)
            {
                extracted.set(type);
            }

            @Override
            public void notifyStartCarry(Enum<?> type, int totalQuantity)
            {
                carry.set(type);
            }

            @Override
            public void notifyStartDropOff(Enum<?> type, int totalQuantity)
            {
                startDrop.set(type);
            }

            @Override
            public void notifyDroppedOff(Enum<?> type, int droppedQuantity)
            {
                endDrop.set(type);
            }
        };
    }
}
