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
package com.b3dgs.lionengine.game.object.trait.producible;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.ObjectGameTest;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.trait.transformable.TransformableModel;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the producer model trait.
 */
public class ProducerModelTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Create listener.
     * 
     * @param start The start.
     * @param current The current.
     * @param done The done.
     * @param cant The cannot.
     * @return The listener.
     */
    private static ProducerListener createProducerListener(final AtomicReference<Producible> start,
                                                           final AtomicReference<Producible> current,
                                                           final AtomicReference<Producible> done,
                                                           final AtomicReference<Producible> cant)
    {
        return new ProducerListener()
        {
            @Override
            public void notifyStartProduction(Producible producible, ObjectGame object)
            {
                start.set(producible);
            }

            @Override
            public void notifyProducing(Producible producible, ObjectGame object)
            {
                current.set(producible);
            }

            @Override
            public void notifyProduced(Producible producible, ObjectGame object)
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
    private static ProducibleListener createProducibleListener(final AtomicBoolean start,
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
    private static Producible createProducible(Services services)
    {
        final Media media = ProducibleModelTest.createProducibleMedia();
        final ObjectGame object = new ObjectGame(new Setup(media), services);
        object.addType(new TransformableModel());
        final Producible producible = new ProducibleModel();
        producible.prepare(object, services);
        return producible;
    }

    /**
     * Test the production.
     */
    @Test
    public void testProduction()
    {
        final Services services = new Services();
        services.add(new Handler());
        services.add(Integer.valueOf(50));
        final Media media = ObjectGameTest.createMedia(ProducerObject.class);
        final ProducerObject object = new ProducerObject(new Setup(media), services);
        final ProducerModel producer = new ProducerModel();
        producer.prepare(object, services);
        producer.setStepsPerSecond(25.0);

        final AtomicReference<Producible> start = new AtomicReference<Producible>();
        final AtomicReference<Producible> current = new AtomicReference<Producible>();
        final AtomicReference<Producible> done = new AtomicReference<Producible>();
        final AtomicReference<Producible> cant = new AtomicReference<Producible>();
        producer.addListener(createProducerListener(start, current, done, cant));

        producer.update(1.0);

        Assert.assertNull(producer.getProducingElement());
        Assert.assertEquals(-1.0, producer.getProgress(), UtilTests.PRECISION);
        Assert.assertEquals(-1, producer.getProgressPercent());
        Assert.assertEquals(0, producer.getQueueLength());
        Assert.assertFalse(producer.isProducing());

        final Producible producible = createProducible(services);
        producer.addToProductionQueue(producible);

        Assert.assertEquals(0, producer.getQueueLength());
        Assert.assertNull(start.get());
        Assert.assertFalse(producer.isProducing());

        producer.update(1.0);

        Assert.assertEquals(0.0, producer.getProgress(), UtilTests.PRECISION);
        Assert.assertEquals(0, producer.getProgressPercent());
        Assert.assertEquals(0, producer.getQueueLength());
        Assert.assertEquals(producible, start.get());
        Assert.assertNull(current.get());
        Assert.assertTrue(producer.isProducing());
        Assert.assertEquals(producible.getOwner().getMedia(), producer.getProducingElement());

        producer.update(1.0);

        Assert.assertEquals(0.5, producer.getProgress(), UtilTests.PRECISION);
        Assert.assertEquals(50, producer.getProgressPercent());
        Assert.assertEquals(producible, current.get());
        Assert.assertNull(done.get());
        Assert.assertTrue(producer.isProducing());

        producer.update(1.0);

        Assert.assertEquals(1.0, producer.getProgress(), UtilTests.PRECISION);
        Assert.assertEquals(100, producer.getProgressPercent());
        Assert.assertEquals(producible, current.get());
        Assert.assertNull(done.get());
        Assert.assertFalse(producer.isProducing());

        producer.update(1.0);

        Assert.assertEquals(producible, done.get());
        Assert.assertNull(cant.get());
        Assert.assertFalse(producer.isProducing());

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the production with self listener.
     */
    @Test
    public void testProductionListenerSelf()
    {
        final Services services = new Services();
        services.add(new Handler());
        services.add(Integer.valueOf(50));
        final Media media = ObjectGameTest.createMedia(ProducerObject.class);
        final ProducerObjectSelf object = new ProducerObjectSelf(new Setup(media), services);
        final ProducerModel producer = new ProducerModel();
        producer.prepare(object, services);
        producer.setStepsPerSecond(50.0);
        producer.addListener(object);

        final Producible producible = createProducible(services);
        producer.addToProductionQueue(producible);

        Assert.assertEquals(0, object.flag.get());

        producer.update(1.0);

        Assert.assertEquals(1, object.flag.get());

        producer.update(1.0);

        Assert.assertEquals(2, object.flag.get());

        producer.update(1.0);

        Assert.assertEquals(3, object.flag.get());

        producer.update(1.0);

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the production pending.
     */
    @Test
    public void testPending()
    {
        final Services services = new Services();
        services.add(new Handler());
        services.add(Integer.valueOf(50));
        final Media media = ObjectGameTest.createMedia(ProducerObject.class);
        final ProducerObject object = new ProducerObject(new Setup(media), services);
        final ProducerModel producer = new ProducerModel();
        producer.prepare(object, services);
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Producible> start = new AtomicReference<Producible>();
        final AtomicReference<Producible> skip = new AtomicReference<Producible>();
        producer.addListener(createProducerListener(start, skip, skip, skip));

        final Producible producible = createProducible(services);
        producer.addToProductionQueue(producible);
        producer.addToProductionQueue(producible);

        Assert.assertEquals(1, producer.getQueueLength());
        Assert.assertNull(start.get());

        producer.update(1.0);

        Assert.assertTrue(producer.iterator().hasNext());
        Assert.assertEquals(1, producer.getQueueLength());
        Assert.assertNotNull(start.get());

        start.set(null);
        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);

        Assert.assertEquals(0, producer.getQueueLength());
        Assert.assertNotNull(start.get());
        Assert.assertFalse(producer.iterator().hasNext());

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the production pending and cannot.
     */
    @Test
    public void testPendingCannot()
    {
        final Services services = new Services();
        services.add(new Handler());
        services.add(Integer.valueOf(50));
        final Media media = ObjectGameTest.createMedia(ProducerObject.class);
        final ProducerObject object = new ProducerObject(new Setup(media), services);
        final ProducerModel producer = new ProducerModel();
        producer.prepare(object, services);
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Producible> start = new AtomicReference<Producible>();
        final AtomicReference<Producible> skip = new AtomicReference<Producible>();
        producer.addListener(createProducerListener(start, skip, skip, skip));

        final Producible producible = createProducible(services);
        producer.addToProductionQueue(producible);
        producer.addToProductionQueue(producible);

        producer.update(1.0);
        producer.update(1.0);
        object.check.set(false);
        producer.update(1.0);
        producer.update(1.0);

        Assert.assertEquals(1, producer.getQueueLength());
        Assert.assertNotNull(start.get());

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the skip production.
     */
    @Test
    public void testSkip()
    {
        final Services services = new Services();
        services.add(new Handler());
        services.add(Integer.valueOf(50));
        final Media media = ObjectGameTest.createMedia(ProducerObject.class);
        final ProducerObject object = new ProducerObject(new Setup(media), services);
        final ProducerModel producer = new ProducerModel();
        producer.prepare(object, services);
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Producible> done = new AtomicReference<Producible>();
        final AtomicReference<Producible> skip = new AtomicReference<Producible>();
        producer.addListener(createProducerListener(skip, skip, done, skip));

        producer.skipProduction();

        final Producible producible = createProducible(services);
        producer.addToProductionQueue(producible);
        producer.addToProductionQueue(producible);

        producer.update(1.0);
        producer.skipProduction();
        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);

        Assert.assertNull(done.get());

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the stop production.
     */
    @Test
    public void testStop()
    {
        final Services services = new Services();
        services.add(new Handler());
        services.add(Integer.valueOf(50));
        final Media media = ObjectGameTest.createMedia(ProducerObject.class);
        final ProducerObject object = new ProducerObject(new Setup(media), services);
        final ProducerModel producer = new ProducerModel();
        producer.prepare(object, services);
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Producible> done = new AtomicReference<Producible>();
        final AtomicReference<Producible> skip = new AtomicReference<Producible>();
        producer.addListener(createProducerListener(skip, skip, done, skip));

        final Producible producible = createProducible(services);
        producer.addToProductionQueue(producible);
        producer.addToProductionQueue(producible);

        producer.update(1.0);

        Assert.assertTrue(producer.isProducing());

        producer.stopProduction();

        Assert.assertFalse(producer.iterator().hasNext());
        Assert.assertFalse(producer.isProducing());

        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);

        Assert.assertNull(done.get());

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the cannot produce.
     */
    @Test
    public void testCannot()
    {
        final Services services = new Services();
        services.add(new Handler());
        services.add(Integer.valueOf(50));
        final Media media = ObjectGameTest.createMedia(ProducerObject.class);
        final ProducerObject object = new ProducerObject(new Setup(media), services);
        object.check.set(false);
        final ProducerModel producer = new ProducerModel();
        producer.prepare(object, services);
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Producible> skip = new AtomicReference<Producible>();
        final AtomicReference<Producible> cant = new AtomicReference<Producible>();
        producer.addListener(createProducerListener(skip, skip, skip, cant));

        final Producible producible = createProducible(services);
        producer.addToProductionQueue(producible);
        producer.update(1.0);

        Assert.assertFalse(producer.isProducing());
        Assert.assertEquals(producible, cant.get());

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the production listener.
     */
    @Test
    public void testProducibleListener()
    {
        final Services services = new Services();
        services.add(new Handler());
        services.add(Integer.valueOf(50));
        final Media media = ObjectGameTest.createMedia(ProducerObject.class);
        final ProducerObject object = new ProducerObject(new Setup(media), services);
        final ProducerModel producer = new ProducerModel();
        producer.prepare(object, services);
        producer.setStepsPerSecond(50.0);

        final Producible producible = createProducible(services);
        final AtomicBoolean start = new AtomicBoolean();
        final AtomicBoolean progress = new AtomicBoolean();
        final AtomicBoolean end = new AtomicBoolean();
        producible.addListener(createProducibleListener(start, progress, end));

        producer.addToProductionQueue(producible);

        Assert.assertFalse(start.get());
        Assert.assertFalse(progress.get());
        Assert.assertFalse(end.get());

        producer.update(1.0);

        Assert.assertTrue(start.get());
        Assert.assertFalse(progress.get());
        Assert.assertFalse(end.get());

        producer.update(1.0);

        Assert.assertTrue(progress.get());
        Assert.assertFalse(end.get());

        producer.update(1.0);

        Assert.assertTrue(end.get());

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Object producer test.
     */
    private static class ProducerObject extends ObjectGame implements ProducerChecker
    {
        /** Checker. */
        private final AtomicBoolean check = new AtomicBoolean(true);

        /**
         * Constructor.
         * 
         * @param setup The setup.
         * @param services The services.
         */
        public ProducerObject(Setup setup, Services services)
        {
            super(setup, services);
        }

        @Override
        public boolean checkProduction(Producible producible)
        {
            return check.get();
        }
    }

    /**
     * Object producer self listener test.
     */
    private static class ProducerObjectSelf extends ObjectGame implements ProducerChecker, ProducerListener
    {
        /** Checker. */
        private final AtomicBoolean check = new AtomicBoolean(true);
        /** Flag. */
        private final AtomicInteger flag = new AtomicInteger();

        /**
         * Constructor.
         * 
         * @param setup The setup.
         * @param services The services.
         */
        public ProducerObjectSelf(Setup setup, Services services)
        {
            super(setup, services);
        }

        @Override
        public boolean checkProduction(Producible producible)
        {
            return check.get();
        }

        @Override
        public void notifyStartProduction(Producible producible, ObjectGame object)
        {
            flag.set(1);
        }

        @Override
        public void notifyProducing(Producible producible, ObjectGame object)
        {
            flag.set(2);
        }

        @Override
        public void notifyProduced(Producible producible, ObjectGame object)
        {
            flag.set(3);
        }

        @Override
        public void notifyCanNotProduce(Producible producible)
        {
            flag.set(4);
        }
    }
}
