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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;

/**
 * Utilities dedicated to extractable test.
 */
final class UtilExtractable
{
    /**
     * Create media.
     * 
     * @return The media.
     */
    public static Media createMedia()
    {
        final ExtractableConfig config = new ExtractableConfig("gold", 10);

        final Media media = UtilSetup.createMedia(Featurable.class);
        final Xml root = new Xml("test");
        root.add(ExtractableConfig.exports(config));
        root.save(media);

        return media;
    }

    /**
     * Create extractable.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @return The extractable.
     */
    public static Extractable createExtractable(Services services, Setup setup)
    {
        services.add(new MapTileGame());

        final Featurable featurable = new FeaturableModel(services, setup);
        final Transformable transformable = featurable.addFeature(TransformableModel.class, services, setup);

        final Extractable extractable = new ExtractableModel(services, setup, transformable);
        extractable.setResourcesQuantity(10);
        extractable.setResourcesType("wood");
        extractable.prepare(featurable);

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
    public static ExtractorListener createListener(AtomicReference<String> goTo,
                                                   AtomicReference<String> startExtract,
                                                   AtomicReference<String> extracted,
                                                   AtomicReference<String> carry,
                                                   AtomicReference<String> startDrop,
                                                   AtomicReference<String> endDrop)
    {
        return new ExtractorListener()
        {
            @Override
            public void notifyStartGoToRessources(String type, Tiled resourceLocation)
            {
                goTo.set(type);
            }

            @Override
            public void notifyStartExtraction(String type, Tiled resourceLocation)
            {
                startExtract.set(type);
            }

            @Override
            public void notifyExtracted(String type, int currentQuantity)
            {
                extracted.set(type);
            }

            @Override
            public void notifyStartCarry(String type, int totalQuantity)
            {
                carry.set(type);
            }

            @Override
            public void notifyStartDropOff(String type, int totalQuantity)
            {
                startDrop.set(type);
            }

            @Override
            public void notifyDroppedOff(String type, int droppedQuantity)
            {
                endDrop.set(type);
            }

            @Override
            public void notifyStopped()
            {
                goTo.set(null);
                startExtract.set(null);
                extracted.set(null);
                startDrop.set(null);
                endDrop.set(null);
            }
        };
    }
}
