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
    public static ProducerListener createProducerListener(AtomicReference<Featurable> start,
                                                          AtomicReference<Featurable> current,
                                                          AtomicReference<Featurable> done,
                                                          AtomicReference<Featurable> cant)
    {
        return new ProducerListenerVoid()
        {
            @Override
            public void notifyStartProduction(Featurable featurable)
            {
                super.notifyStartProduction(featurable);
                start.set(featurable);
            }

            @Override
            public void notifyProducing(Featurable featurable)
            {
                super.notifyProducing(featurable);
                current.set(featurable);
            }

            @Override
            public void notifyProduced(Featurable featurable)
            {
                super.notifyProduced(featurable);
                done.set(featurable);
            }

            @Override
            public void notifyCanNotProduce(Featurable featurable)
            {
                super.notifyCanNotProduce(featurable);
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
    public static ProducibleListener createProducibleListener(AtomicBoolean start,
                                                              AtomicBoolean progress,
                                                              AtomicBoolean end)
    {
        return new ProducibleListenerVoid()
        {
            @Override
            public void notifyProductionStarted(Producer producer)
            {
                super.notifyProductionStarted(producer);
                start.set(true);
            }

            @Override
            public void notifyProductionProgress(Producer producer)
            {
                super.notifyProductionProgress(producer);
                progress.set(true);
            }

            @Override
            public void notifyProductionEnded(Producer producer)
            {
                super.notifyProductionEnded(producer);
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
        final Featurable featurable = new FeaturableModel(services, setup);
        featurable.addFeature(TransformableModel.class, services, setup);
        featurable.addFeature(ProducibleModel.class, services, setup);

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
}
