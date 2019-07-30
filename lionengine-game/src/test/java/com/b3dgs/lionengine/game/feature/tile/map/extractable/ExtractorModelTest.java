/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilEnum;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Test {@link ExtractorModel}.
 */
public final class ExtractorModelTest
{
    /** Hack enum. */
    private static final UtilEnum<ExtractorState> HACK = new UtilEnum<>(ExtractorState.class, ExtractorModel.class);

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        HACK.addByValue(HACK.make("FAIL"));
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        HACK.restore();
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        services.add(new SourceResolutionProvider()
        {
            @Override
            public int getWidth()
            {
                return 0;
            }

            @Override
            public int getHeight()
            {
                return 0;
            }

            @Override
            public int getRate()
            {
                return 50;
            }
        });
        services.add(new MapTileGame());
    }

    /**
     * Test constructor with null services.
     */
    @Test
    public void testConstructorNullServices()
    {
        assertThrows(() -> new ExtractorModel(null, null), "Unexpected null argument !");
        assertThrows(() -> new ExtractorModel(new Services(), null), "Unexpected null argument !");
    }

    /**
     * Test the extractor config.
     */
    @Test
    public void testConfig()
    {
        final ObjectExtractor object = new ObjectExtractor(true, true);
        object.addFeature(new TransformableModel());

        final Media media = UtilSetup.createConfig();
        final Xml xml = new Xml(media);
        xml.add(ExtractorConfig.exports(new ExtractorConfig(1.0, 2.0, 5)));
        xml.save(media);

        final ExtractorModel extractor = new ExtractorModel(services, new Setup(media));
        extractor.recycle();

        assertEquals(5, extractor.getExtractionCapacity());
        assertEquals(1.0, extractor.getExtractionSpeed());
        assertEquals(2.0, extractor.getDropOffSpeed());

        extractor.prepare(object);

        assertEquals(5, extractor.getExtractionCapacity());
        assertEquals(1.0, extractor.getExtractionSpeed());
        assertEquals(2.0, extractor.getDropOffSpeed());

        object.getFeature(Identifiable.class).notifyDestroyed();

        assertTrue(media.getFile().delete());
    }

    /**
     * Test the extractor.
     */
    @Test
    public void testExtractor()
    {
        final ObjectExtractor object = new ObjectExtractor(true, true);
        object.addFeature(new TransformableModel());

        final ExtractorModel extractor = new ExtractorModel(services, new Setup(UtilSetup.createConfig()));
        extractor.recycle();
        extractor.setCapacity(6);
        extractor.setExtractionSpeed(50.0);
        extractor.setChecker(object);
        extractor.setDropOffSpeed(100.0);
        extractor.prepare(object);

        final AtomicReference<String> goTo = new AtomicReference<>();
        final AtomicReference<String> startExtract = new AtomicReference<>();
        final AtomicReference<String> extracted = new AtomicReference<>();
        final AtomicReference<String> carry = new AtomicReference<>();
        final AtomicReference<String> startDrop = new AtomicReference<>();
        final AtomicReference<String> endDrop = new AtomicReference<>();
        extractor.addListener(UtilExtractable.createListener(goTo, startExtract, extracted, carry, startDrop, endDrop));

        assertNull(extractor.getResourceLocation());
        assertNull(extractor.getResourceType());

        extractor.setResource("wood", 1, 2, 1, 1);

        final Tiled location = extractor.getResourceLocation();
        assertEquals(1.0, location.getInTileX());
        assertEquals(2.0, location.getInTileY());
        assertEquals(1.0, location.getInTileWidth());
        assertEquals(1.0, location.getInTileHeight());
        assertEquals("wood", extractor.getResourceType());
        assertFalse(extractor.isExtracting());

        extractor.startExtraction();

        assertFalse(extractor.isExtracting());
        assertEquals("wood", goTo.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals("wood", startExtract.get());
        assertNotEquals("wood", extracted.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals("wood", extracted.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals("wood", carry.get());

        extractor.update(1.0);

        assertFalse(extractor.isExtracting());
        assertEquals("wood", startDrop.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals("wood", endDrop.get());

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

        final Extractor extractor = new ExtractorModel(services, new Setup(UtilSetup.createConfig()));
        extractor.setCapacity(1);
        extractor.setExtractionSpeed(50.0);
        extractor.setDropOffSpeed(50.0);
        extractor.prepare(object);

        final AtomicReference<String> goTo = new AtomicReference<>();
        final AtomicReference<String> skip = new AtomicReference<>();
        extractor.addListener(UtilExtractable.createListener(goTo, skip, skip, skip, skip, skip));
        extractor.setResource("wood", 1, 2, 1, 1);
        extractor.startExtraction();
        extractor.update(1.0);

        assertFalse(extractor.isExtracting());
        assertNotNull(goTo.get());

        extractor.update(1.0);

        assertFalse(extractor.isExtracting());
        assertNotNull(goTo.get());

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

        final Extractor extractor = new ExtractorModel(services, new Setup(UtilSetup.createConfig()));
        extractor.setCapacity(1);
        extractor.setExtractionSpeed(50.0);
        extractor.setDropOffSpeed(50.0);
        extractor.prepare(object);

        final AtomicReference<String> drop = new AtomicReference<>();
        final AtomicReference<String> skip = new AtomicReference<>();
        extractor.addListener(UtilExtractable.createListener(skip, skip, skip, skip, drop, skip));
        extractor.setResource("wood", 1, 2, 1, 1);
        extractor.startExtraction();
        extractor.update(1.0);

        assertTrue(extractor.isExtracting());

        extractor.update(1.0);
        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertNull(drop.get());

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

        final Extractor extractor = new ExtractorModel(services, new Setup(UtilSetup.createConfig()));
        extractor.setCapacity(2);
        extractor.setExtractionSpeed(25.0);
        extractor.setDropOffSpeed(100.0);
        extractor.prepare(object);
        extractor.addListener(object);

        assertNull(extractor.getResourceLocation());
        assertNull(extractor.getResourceType());

        final Extractable extractable = UtilExtractable.createExtractable();
        extractor.setResource(extractable);

        assertFalse(extractor.isExtracting());

        extractor.startExtraction();

        assertFalse(extractor.isExtracting());
        assertEquals(1, object.flag.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(2, object.flag.get());

        extractor.update(1.0);
        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(5, object.flag.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        assertFalse(extractor.isExtracting());
        assertEquals(6, object.flag.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(2, object.flag.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(2, object.flag.get());

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

        final Extractor extractor = new ExtractorModel(services, new Setup(UtilSetup.createConfig()));
        extractor.setCapacity(6);
        extractor.setExtractionSpeed(50.0);
        extractor.setDropOffSpeed(100.0);
        extractor.prepare(object);
        extractor.addListener(object);

        assertNull(extractor.getResourceLocation());
        assertNull(extractor.getResourceType());

        final Extractable extractable = UtilExtractable.createExtractable();
        extractable.setResourcesQuantity(0);
        extractor.setResource(extractable);

        assertFalse(extractor.isExtracting());

        extractor.startExtraction();

        assertFalse(extractor.isExtracting());
        assertEquals(1, object.flag.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(2, object.flag.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(2, object.flag.get());

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

        final Extractor extractor = new ExtractorModel(services, new Setup(UtilSetup.createConfig()));
        extractor.prepare(object);
        extractor.setCapacity(6);
        extractor.setExtractionSpeed(50.0);
        extractor.setDropOffSpeed(100.0);

        final AtomicReference<String> goTo = new AtomicReference<>();
        final AtomicReference<String> startExtract = new AtomicReference<>();
        final AtomicReference<String> empty = new AtomicReference<>();
        final AtomicReference<String> extracted = new AtomicReference<>();
        extractor.addListener(UtilExtractable.createListener(goTo, startExtract, extracted, empty, empty, empty));

        assertNull(extractor.getResourceLocation());
        assertNull(extractor.getResourceType());

        extractor.setResource("wood", 1, 2, 1, 1);
        extractor.startExtraction();

        assertFalse(extractor.isExtracting());
        assertEquals("wood", goTo.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals("wood", startExtract.get());
        assertNotEquals("wood", extracted.get());

        extractor.stopExtraction();
        extractor.update(1.0);

        assertFalse(extractor.isExtracting());
        assertNotEquals("wood", extracted.get());

        assertNull(goTo.get());
        assertNull(startExtract.get());
        assertNull(extracted.get());
        assertNull(empty.get());

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

        final Extractor extractor = new ExtractorModel(services, new Setup(UtilSetup.createConfig()));
        extractor.prepare(object);
        extractor.checkListener(object);
        extractor.setResource("wood", 1, 2, 1, 1);
        extractor.startExtraction();
        extractor.update(1.0);

        assertEquals(2, object.flag.get());

        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the check listener.
     */
    @Test
    public void testCheckListener()
    {
        final ObjectExtractorSelf object = new ObjectExtractorSelf();
        object.addFeature(new TransformableModel());

        final AtomicBoolean add = new AtomicBoolean();
        final Extractor extractor = new ExtractorModel(services, new Setup(UtilSetup.createConfig()))
        {
            @Override
            public void addListener(ExtractorListener listener)
            {
                add.set(true);
            }
        };
        extractor.prepare(new FeaturableModel());

        assertFalse(add.get());

        extractor.checkListener(object);

        assertTrue(add.get());
    }

    /**
     * Test the remove listener.
     */
    @Test
    public void testRemoveListener()
    {
        final AtomicBoolean check = new AtomicBoolean();
        final Extractor extractor = new ExtractorModel(services, new Setup(UtilSetup.createConfig()));
        final ExtractorListener listener = new ExtractorListenerVoid()
        {
            @Override
            public void notifyStartGoToRessources(String type, Tiled resourceLocation)
            {
                check.set(true);
            }
        };
        extractor.addListener(listener);
        extractor.prepare(new FeaturableModel());
        extractor.startExtraction();

        assertTrue(check.get());

        extractor.removeListener(listener);
        check.set(false);
        extractor.startExtraction();

        assertFalse(check.get());
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
        final ExtractorModel extractor = new ExtractorModel(services, new Setup(UtilSetup.createConfig()));
        final Field field = extractor.getClass().getDeclaredField("state");
        UtilReflection.setAccessible(field, true);
        field.set(extractor, ExtractorState.values()[5]);

        assertThrows(() -> extractor.update(1.0), "Unknown enum: FAIL");
    }
}
