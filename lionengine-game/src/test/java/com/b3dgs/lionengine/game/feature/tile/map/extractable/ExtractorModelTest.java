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

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.util.UtilEnum;
import com.b3dgs.lionengine.util.UtilReflection;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the extractor.
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
        HACK.addByValue(HACK.make("FAIL"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        HACK.restore();
    }

    private final Services services = new Services();

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        services.add(Integer.valueOf(50));
        services.add(new MapTileGame());
    }

    /**
     * Test the extractor config.
     */
    @Test
    public void testConfig()
    {
        final ObjectExtractor object = new ObjectExtractor(true, true);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.setCapacity(5);
        extractor.setExtractionPerSecond(1.0);
        extractor.setDropOffPerSecond(2.0);
        extractor.prepare(object);

        Assert.assertEquals(5, extractor.getExtractionCapacity());
        Assert.assertEquals(1.0, extractor.getExtractionPerSecond(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, extractor.getDropOffPerSecond(), UtilTests.PRECISION);

        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the extractor.
     */
    @Test
    public void testExtractor()
    {
        final ObjectExtractor object = new ObjectExtractor(true, true);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.setCapacity(6);
        extractor.setExtractionPerSecond(50.0);
        extractor.setChecker(object);
        extractor.setDropOffPerSecond(100.0);
        extractor.prepare(object);

        final AtomicReference<Enum<?>> goTo = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> startExtract = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> extracted = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> carry = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> startDrop = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> endDrop = new AtomicReference<Enum<?>>();
        extractor.addListener(UtilExtractable.createListener(goTo, startExtract, extracted, carry, startDrop, endDrop));

        Assert.assertNull(extractor.getResourceLocation());
        Assert.assertNull(extractor.getResourceType());

        extractor.setResource(ResourceType.WOOD, 1, 2, 1, 1);

        final Tiled location = extractor.getResourceLocation();
        Assert.assertEquals(1.0, location.getInTileX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, location.getInTileY(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, location.getInTileWidth(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, location.getInTileHeight(), UtilTests.PRECISION);
        Assert.assertEquals(ResourceType.WOOD, extractor.getResourceType());
        Assert.assertFalse(extractor.isExtracting());

        extractor.startExtraction();

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertEquals(ResourceType.WOOD, goTo.get());

        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(ResourceType.WOOD, startExtract.get());
        Assert.assertNotEquals(ResourceType.WOOD, extracted.get());

        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(ResourceType.WOOD, extracted.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(ResourceType.WOOD, carry.get());
        Assert.assertNotEquals(ResourceType.WOOD, startDrop.get());

        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(ResourceType.WOOD, startDrop.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertEquals(ResourceType.WOOD, endDrop.get());

        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the extractor cannot extract.
     */
    @Test
    public void testCannotExtract()
    {
        final ObjectExtractor object = new ObjectExtractor(false, true);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.setCapacity(1);
        extractor.setExtractionPerSecond(50.0);
        extractor.setDropOffPerSecond(50.0);
        extractor.prepare(object);

        final AtomicReference<Enum<?>> goTo = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> skip = new AtomicReference<Enum<?>>();
        extractor.addListener(UtilExtractable.createListener(goTo, skip, skip, skip, skip, skip));
        extractor.setResource(ResourceType.WOOD, 1, 2, 1, 1);
        extractor.startExtraction();
        extractor.update(1.0);

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertNotNull(goTo.get());

        extractor.update(1.0);

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertNotNull(goTo.get());

        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the extractor cannot carry.
     */
    @Test
    public void testCannotCarry()
    {
        final ObjectExtractor object = new ObjectExtractor(true, false);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.setCapacity(1);
        extractor.setExtractionPerSecond(50.0);
        extractor.setDropOffPerSecond(50.0);
        extractor.prepare(object);

        final AtomicReference<Enum<?>> drop = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> skip = new AtomicReference<Enum<?>>();
        extractor.addListener(UtilExtractable.createListener(skip, skip, skip, skip, drop, skip));
        extractor.setResource(ResourceType.WOOD, 1, 2, 1, 1);
        extractor.startExtraction();
        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());

        extractor.update(1.0);
        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertNull(drop.get());

        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the extractor with extractable.
     */
    @Test
    public void testExtractorExtractable()
    {
        final ObjectExtractorSelf object = new ObjectExtractorSelf();
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.setCapacity(2);
        extractor.setExtractionPerSecond(25.0);
        extractor.setDropOffPerSecond(100.0);
        extractor.prepare(object);
        extractor.addListener(object);

        Assert.assertNull(extractor.getResourceLocation());
        Assert.assertNull(extractor.getResourceType());

        final Extractable extractable = UtilExtractable.createExtractable();
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

        object.getFeature(Identifiable.class).notifyDestroyed();
        extractable.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the extractor with extractable without resource.
     */
    @Test
    public void testExtractorExtractableNoResource()
    {
        final ObjectExtractorSelf object = new ObjectExtractorSelf();
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.setCapacity(6);
        extractor.setExtractionPerSecond(50.0);
        extractor.setDropOffPerSecond(100.0);
        extractor.prepare(object);
        extractor.addListener(object);

        Assert.assertNull(extractor.getResourceLocation());
        Assert.assertNull(extractor.getResourceType());

        final Extractable extractable = UtilExtractable.createExtractable();
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

        object.getFeature(Identifiable.class).notifyDestroyed();
        extractable.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the stop extraction.
     */
    @Test
    public void testStopExtraction()
    {
        final ObjectExtractor object = new ObjectExtractor(true, true);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.prepare(object);
        extractor.setCapacity(6);
        extractor.setExtractionPerSecond(50.0);
        extractor.setDropOffPerSecond(100.0);

        final AtomicReference<Enum<?>> goTo = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> startExtract = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> empty = new AtomicReference<Enum<?>>();
        final AtomicReference<Enum<?>> extracted = new AtomicReference<Enum<?>>();
        extractor.addListener(UtilExtractable.createListener(goTo, startExtract, extracted, empty, empty, empty));

        Assert.assertNull(extractor.getResourceLocation());
        Assert.assertNull(extractor.getResourceType());

        extractor.setResource(ResourceType.WOOD, 1, 2, 1, 1);
        extractor.startExtraction();

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertEquals(ResourceType.WOOD, goTo.get());

        extractor.update(1.0);

        Assert.assertTrue(extractor.isExtracting());
        Assert.assertEquals(ResourceType.WOOD, startExtract.get());
        Assert.assertNotEquals(ResourceType.WOOD, extracted.get());

        extractor.stopExtraction();
        extractor.update(1.0);

        Assert.assertFalse(extractor.isExtracting());
        Assert.assertNotEquals(ResourceType.WOOD, extracted.get());

        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the auto add listener.
     */
    @Test
    public void testListenerAutoAdd()
    {
        final ObjectExtractorSelf object = new ObjectExtractorSelf();
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.prepare(object);
        extractor.checkListener(object);
        extractor.setResource(ResourceType.WOOD, 1, 2, 1, 1);
        extractor.startExtraction();
        extractor.update(1.0);

        Assert.assertEquals(2, object.flag.get());

        object.getFeature(Identifiable.class).notifyDestroyed();
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
        final ExtractorModel extractor = new ExtractorModel(services);
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
}
