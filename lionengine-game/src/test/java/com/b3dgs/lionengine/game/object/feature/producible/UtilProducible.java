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
package com.b3dgs.lionengine.game.object.feature.producible;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.SizeConfig;
import com.b3dgs.lionengine.game.object.UtilSetup;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Utilities dedicated to producible test.
 */
public class UtilProducible
{
    /**
     * Create listener.
     * 
     * @param start The start.
     * @param current The current.
     * @param done The done.
     * @param cant The cannot.
     * @return The listener.
     */
    public static ProducerListener createProducerListener(final AtomicReference<Producible> start,
                                                          final AtomicReference<Producible> current,
                                                          final AtomicReference<Producible> done,
                                                          final AtomicReference<Producible> cant)
    {
        return new ProducerListener()
        {
            @Override
            public void notifyStartProduction(Producible producible, Handlable handlable)
            {
                start.set(producible);
            }

            @Override
            public void notifyProducing(Producible producible, Handlable handlable)
            {
                current.set(producible);
            }

            @Override
            public void notifyProduced(Producible producible, Handlable handlable)
            {
                done.set(producible);
            }

            @Override
            public void notifyCanNotProduce(Producible producible)
            {
                cant.set(producible);
            }
        };
    }

    /**
     * Create listener.
     * 
     * @param start The start.
     * @param progress The progress.
     * @param end The end.
     * 
     * @return The listener.
     */
    public static ProducibleListener createProducibleListener(final AtomicBoolean start,
                                                              final AtomicBoolean progress,
                                                              final AtomicBoolean end)
    {
        return new ProducibleListener()
        {
            @Override
            public void notifyProductionStarted()
            {
                start.set(true);
            }

            @Override
            public void notifyProductionProgress()
            {
                progress.set(true);
            }

            @Override
            public void notifyProductionEnded()
            {
                end.set(true);
            }
        };
    }

    /**
     * Create producible.
     * 
     * @param services The services.
     * @return The producible.
     */
    public static Producible createProducible(Services services)
    {
        final Media media = createProducibleMedia();
        final Setup setup = new Setup(media);
        final ObjectGame object = new ObjectGame(setup);
        object.addFeature(new TransformableModel());

        final Producible producible = new ProducibleModel(setup);
        producible.prepare(object, services);

        return producible;
    }

    /**
     * Create media.
     * 
     * @return The media.
     */
    public static Media createProducibleMedia()
    {
        final ProducibleConfig producibleConfig = new ProducibleConfig(1, 2, 3);

        final Media media = UtilSetup.createMedia(ObjectGame.class);
        final XmlNode root = Xml.create("test");
        root.add(SizeConfig.exports(new SizeConfig(producibleConfig.getWidth(), producibleConfig.getHeight())));
        root.add(ProducibleConfig.exports(producibleConfig));
        Xml.save(root, media);

        return media;
    }

    /**
     * Create listener.
     * 
     * @return The listener.
     */
    public static ProducibleListener createListener()
    {
        return new ProducibleListener()
        {
            @Override
            public void notifyProductionStarted()
            {
                // Mock
            }

            @Override
            public void notifyProductionProgress()
            {
                // Mock
            }

            @Override
            public void notifyProductionEnded()
            {
                // Mock
            }
        };
    }
}
