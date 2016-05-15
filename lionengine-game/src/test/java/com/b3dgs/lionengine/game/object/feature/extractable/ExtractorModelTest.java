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
package com.b3dgs.lionengine.game.object.feature.extractable;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.ObjectGameTest;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.tile.Tiled;
import com.b3dgs.lionengine.test.UtilEnum;
import com.b3dgs.lionengine.test.UtilTests;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Test the extractor trait.
 */
public class ExtractorModelTest
{
    /** Hack enum. */
    private static final UtilEnum<ExtractorState> HACK = new UtilEnum<ExtractorState>(ExtractorState.class,
                                                                                      ExtractorModel.class);

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        HACK.addByValue(HACK.make("FAIL"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
        HACK.restore();
    }

    /**
     * Add listener.
     * 
     * @param extractor The extractor.
     * @param goTo The go to.
     * @param startExtract The start extract.
     * @param extracted The extracted.
     * @param carry The carry.
     * @param startDrop The start drop.
     * @param endDrop The dropped.
     */
    private static void addListener(Extractor extractor,
                                    final AtomicReference<Enum<?>> goTo,
                                    final AtomicReference<Enum<?>> startExtract,
                                    final AtomicReference<Enum<?>> extracted,
                                    final AtomicReference<Enum<?>> carry,
                                    final AtomicReference<Enum<?>> startDrop,
                                    final AtomicReference<Enum<?>> endDrop)
    {
        extractor.addListener(new ExtractorListener()
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
        });
    }

    /**
     * Test the extractor config.
     */
    @Test
    public void testConfig()
    {
        final Media media = ObjectGameTest.createMedia(ObjectExtractor.class);
        final Services services = new Services();
        services.add(Integer.valueOf(50));
        services.add(new MapTileGame());
        final ObjectExtractor object = new ObjectExtractor(new Setup(media), true, true);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel();
        extractor.setCapacity(5);
        extractor.setExtractionPerSecond(1.0);
        extractor.setDropOffPerSecond(2.0);
        extractor.prepare(object, services);

        Assert.assertEquals(5, extractor.getExtractionCapacity());
        Assert.assertEquals(1.0, extractor.getExtractionPerSecond(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, extractor.getDropOffPerSecond(), UtilTests.PRECISION);

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the extractor.
     */
    @Test
    public void testExtractor()
    {
        final Media media = ObjectGameTest.createMedia(ObjectExtractor.class);
        final Services services = new Services();
        services.add(Integer.valueOf(50));
        services.add(new MapTileGame());

        final ObjectExtractor object = new ObjectExtractor(new Setup(media), true, true);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel();
        extractor.setCapacity(6);
        extractor.setExtractionPerSecond(50.0);
        extractor.setDropOffPerSecond(100.0);
        extractor.prepare(object, services);

        final AtomicReference<Enum<?>> goTo = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> startExtract = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> extracted = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> carry = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> startDrop = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> endDrop = new AtomicReference<Enum<?>>();
        addListener(extractor, goTo, startExtract, extracted, carry, startDrop, endDrop);

        Assert.assertNull(extractor.getResourceLocation());
        Assert.assertNull(extractor.getResourceType());

        extractor.setResource(ExtractableModelTest.Type.TYPE, 1, 2, 1, 1);

        final Tiled location = extractor.getResourceLocation();
        Assert.assertEquals(1.0, location.getInTileX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, location.getInTileY(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, location.getInTileWidth(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, location.getInTileHeight(), UtilTests.PRECISION);
        Assert.assertEquals(ExtractableModelTest.Type.TYPE, extractor.getResourceType());
        Assert.assertFalse(extractor.isExtracting());

        extractor.startExtraction();

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertEquals(ExtractableModelTest.Type.TYPE, goTo.get());

        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(ExtractableModelTest.Type.TYPE, startExtract.get());
        Assert.assertNotEquals(ExtractableModelTest.Type.TYPE, extracted.get());

        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(ExtractableModelTest.Type.TYPE, extracted.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(ExtractableModelTest.Type.TYPE, carry.get());
        Assert.assertNotEquals(ExtractableModelTest.Type.TYPE, startDrop.get());

        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(ExtractableModelTest.Type.TYPE, startDrop.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertEquals(ExtractableModelTest.Type.TYPE, endDrop.get());

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the extractor cannot extract.
     */
    @Test
    public void testCannotExtract()
    {
        final Media media = ObjectGameTest.createMedia(ObjectExtractor.class);
        final Services services = new Services();
        services.add(Integer.valueOf(50));
        services.add(new MapTileGame());
        final ObjectExtractor object = new ObjectExtractor(new Setup(media), false, true);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel();
        extractor.setCapacity(1);
        extractor.setExtractionPerSecond(50.0);
        extractor.setDropOffPerSecond(50.0);
        extractor.prepare(object, services);

        final AtomicReference<Enum<?>> goTo = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> skip = new AtomicReference<Enum<?>>();
        addListener(extractor, goTo, skip, skip, skip, skip, skip);
        extractor.setResource(ExtractableModelTest.Type.TYPE, 1, 2, 1, 1);
        extractor.startExtraction();
        extractor.update(1.0);

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertNotNull(goTo.get());

        extractor.update(1.0);

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertNotNull(goTo.get());

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the extractor cannot carry.
     */
    @Test
    public void testCannotCarry()
    {
        final Media media = ObjectGameTest.createMedia(ObjectExtractor.class);
        final Services services = new Services();
        services.add(Integer.valueOf(50));
        services.add(new MapTileGame());

        final ObjectExtractor object = new ObjectExtractor(new Setup(media), true, false);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel();
        extractor.setCapacity(1);
        extractor.setExtractionPerSecond(50.0);
        extractor.setDropOffPerSecond(50.0);
        extractor.prepare(object, services);

        final AtomicReference<Enum<?>> drop = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> skip = new AtomicReference<Enum<?>>();
        addListener(extractor, skip, skip, skip, skip, drop, skip);
        extractor.setResource(ExtractableModelTest.Type.TYPE, 1, 2, 1, 1);
        extractor.startExtraction();
        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());

        extractor.update(1.0);
        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertNull(drop.get());

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the extractor with extractable.
     */
    @Test
    public void testExtractorExtractable()
    {
        final Media media = ObjectGameTest.createMedia(ObjectExtractor.class);
        final Services services = new Services();
        services.add(Integer.valueOf(50));
        services.add(new MapTileGame());

        final ObjectExtractorSelf object = new ObjectExtractorSelf(new Setup(media));
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel();
        extractor.setCapacity(2);
        extractor.setExtractionPerSecond(25.0);
        extractor.setDropOffPerSecond(100.0);
        extractor.prepare(object, services);
        extractor.addListener(object);

        Assert.assertNull(extractor.getResourceLocation());
        Assert.assertNull(extractor.getResourceType());

        final Media extractableMedia = ObjectGameTest.createMedia(ObjectGame.class);
        final Extractable extractable = ExtractableModelTest.createExtractable(extractableMedia);
        extractor.setResource(extractable);

        Assert.assertFalse(extractor.isExtracting());

        extractor.startExtraction();

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertEquals(1, object.flag.get());

        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(2, object.flag.get());

        extractor.update(1.0);
        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(3, object.flag.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(4, object.flag.get());

        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(5, object.flag.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertEquals(6, object.flag.get());

        object.notifyDestroyed();
        extractable.getOwner().notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
        Assert.assertTrue(extractableMedia.getFile().delete());
    }

    /**
     * Test the extractor with extractable without resource.
     */
    @Test
    public void testExtractorExtractableNoResource()
    {

        final Media media = ObjectGameTest.createMedia(ObjectExtractor.class);
        final Services services = new Services();
        services.add(Integer.valueOf(50));
        services.add(new MapTileGame());

        final ObjectExtractorSelf object = new ObjectExtractorSelf(new Setup(media));
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel();
        extractor.setCapacity(6);
        extractor.setExtractionPerSecond(50.0);
        extractor.setDropOffPerSecond(100.0);
        extractor.prepare(object, services);
        extractor.addListener(object);

        Assert.assertNull(extractor.getResourceLocation());
        Assert.assertNull(extractor.getResourceType());

        final Media extractableMedia = ObjectGameTest.createMedia(ObjectGame.class);
        final Extractable extractable = ExtractableModelTest.createExtractable(extractableMedia);
        extractable.setResourcesQuantity(0);
        extractor.setResource(extractable);

        Assert.assertFalse(extractor.isExtracting());

        extractor.startExtraction();

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertEquals(1, object.flag.get());

        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(2, object.flag.get());

        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(2, object.flag.get());

        object.notifyDestroyed();
        extractable.getOwner().notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
        Assert.assertTrue(extractableMedia.getFile().delete());
    }

    /**
     * Test the stop extraction.
     */
    @Test
    public void testStopExtraction()
    {
        final Media media = ObjectGameTest.createMedia(ObjectExtractor.class);
        final Services services = new Services();
        services.add(Integer.valueOf(50));
        services.add(new MapTileGame());

        final ObjectExtractor object = new ObjectExtractor(new Setup(media), true, true);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel();
        extractor.prepare(object, services);
        extractor.setCapacity(6);
        extractor.setExtractionPerSecond(50.0);
        extractor.setDropOffPerSecond(100.0);

        final AtomicReference<Enum<?>> goTo = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> startExtract = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> empty = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> extracted = new AtomicReference<Enum<?>>();
        addListener(extractor, goTo, startExtract, extracted, empty, empty, empty);

        Assert.assertNull(extractor.getResourceLocation());
        Assert.assertNull(extractor.getResourceType());

        extractor.setResource(ExtractableModelTest.Type.TYPE, 1, 2, 1, 1);
        extractor.startExtraction();

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertEquals(ExtractableModelTest.Type.TYPE, goTo.get());

        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(ExtractableModelTest.Type.TYPE, startExtract.get());
        Assert.assertNotEquals(ExtractableModelTest.Type.TYPE, extracted.get());

        extractor.stopExtraction();
        extractor.update(1.0);

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertNotEquals(ExtractableModelTest.Type.TYPE, extracted.get());

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test with enum fail.
     * 
     * @throws NoSuchFieldException If error.
     * @throws IllegalArgumentException If error.
     * @throws IllegalAccessException If error.
     */
    @Test
    public void testEnumFail() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        final ExtractorModel extractor = new ExtractorModel();
        final Field field = extractor.getClass().getDeclaredField("state");
        UtilReflection.setAccessible(field, true);
        field.set(extractor, ExtractorState.values()[5]);
        try
        {
            extractor.update(1.0);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals("Unknown enum: FAIL", exception.getMessage());
        }
    }

    /**
     * Extractor test.
     */
    private static class ObjectExtractor extends ObjectGame implements ExtractorChecker
    {
        /** Extract flag. */
        private final boolean extract;
        /** Carry flag. */
        private final boolean carry;

        /**
         * Constructor.
         * 
         * @param setup The setup.
         * @param extract Extract.
         * @param carry Carry.
         */
        public ObjectExtractor(Setup setup, boolean extract, boolean carry)
        {
            super(setup);
            this.extract = extract;
            this.carry = carry;
        }

        @Override
        public boolean canExtract()
        {
            return extract;
        }

        @Override
        public boolean canCarry()
        {
            return carry;
        }
    }

    /**
     * Extractor test with self listener.
     */
    private static class ObjectExtractorSelf extends ObjectGame implements ExtractorChecker, ExtractorListener
    {
        /** Flag. */
        private final AtomicInteger flag = new AtomicInteger();

        /**
         * Constructor.
         * 
         * @param setup The setup.
         */
        public ObjectExtractorSelf(Setup setup)
        {
            super(setup);
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
}
