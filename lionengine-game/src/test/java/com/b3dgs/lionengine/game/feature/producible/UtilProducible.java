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
package com.b3dgs.lionengine.game.feature.producible;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;

/**
 * Utilities dedicated to producible test.
 */
final class UtilProducible
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
    public static ProducerListener createProducerListener(final AtomicReference<Featurable> start,
                                                          final AtomicReference<Featurable> current,
                                                          final AtomicReference<Featurable> done,
                                                          final AtomicReference<Featurable> cant)
    {
        return new ProducerListener()
        {
            @Override
            public void notifyStartProduction(Featurable featurable)
            {
                start.set(featurable);
            }

            @Override
            public void notifyProducing(Featurable featurable)
            {
                current.set(featurable);
            }

            @Override
            public void notifyProduced(Featurable featurable)
            {
                done.set(featurable);
            }

            @Override
            public void notifyCanNotProduce(Featurable featurable)
            {
                cant.set(featurable);
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
            public void notifyProductionStarted(Producer producer)
            {
                start.set(true);
            }

            @Override
            public void notifyProductionProgress(Producer producer)
            {
                progress.set(true);
            }

            @Override
            public void notifyProductionEnded(Producer producer)
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
    public static Featurable createProducible(Services services)
    {
        final Media media = createProducibleMedia();
        final Setup setup = new Setup(media);
        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new TransformableModel());
        featurable.addFeature(new ProducibleModel(setup));

        return featurable;
    }

    /**
     * Create media.
     * 
     * @return The media.
     */
    public static Media createProducibleMedia()
    {
        final ProducibleConfig producibleConfig = new ProducibleConfig(1, 2, 3);

        final Media media = UtilSetup.createMedia(Featurable.class);
        final Xml root = new Xml("test");
        root.add(SizeConfig.exports(new SizeConfig(producibleConfig.getWidth(), producibleConfig.getHeight())));
        root.add(ProducibleConfig.exports(producibleConfig));
        root.save(media);

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
        };
    }
}
