/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.UtilSetup;

/**
 * Test {@link ProducibleModel}.
 */
final class ProducibleModelTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();

    /**
     * Test the producible with no node.
     */
    @Test
    void testNoNode()
    {
        final Media media = UtilSetup.createMedia(Featurable.class);
        final Setup setup = new Setup(media);
        final Producible producible = new ProducibleModel(services, setup);

        assertNull(producible.getMedia());
        assertEquals(0, producible.getSteps());
        assertEquals(0, producible.getWidth());
        assertEquals(0, producible.getHeight());
    }

    /**
     * Test the producible.
     */
    @Test
    void testProducible()
    {
        final Media media = UtilProducible.createProducibleMedia();
        final Setup setup = new Setup(media);
        final Featurable featurable = new FeaturableModel(services, setup);
        final ProducibleModel producible = new ProducibleModel(services, setup);
        producible.recycle();
        final ProducibleListener listener = new ProducibleListenerVoid();
        producible.setLocation(1.0, 2.0);
        producible.prepare(featurable);
        producible.addListener(listener);

        assertEquals(media, producible.getMedia());
        assertEquals(1, producible.getSteps());
        assertEquals(2, producible.getWidth());
        assertEquals(3, producible.getHeight());
        assertEquals(1.0, producible.getX());
        assertEquals(2.0, producible.getY());
        assertTrue(producible.getListeners().contains(listener));

        producible.getFeature(Identifiable.class).notifyDestroyed();
        assertTrue(media.getFile().delete());
    }

    /**
     * Test the producible self listener.
     */
    @Test
    void testProducibleSelf()
    {
        final Media media = UtilProducible.createProducibleMedia();
        final Setup setup = new Setup(media);
        final ProducibleListenerSelf object = new ProducibleListenerSelf(services, setup);
        final Producible producible = new ProducibleModel(services, setup);
        producible.prepare(object);

        assertTrue(producible.getListeners().contains(object));

        producible.getFeature(Identifiable.class).notifyDestroyed();

        assertTrue(media.getFile().delete());
    }

    /**
     * Test the producible listener auto add.
     */
    @Test
    void testListenerAutoAdd()
    {
        final Media media = UtilProducible.createProducibleMedia();
        final Setup setup = new Setup(media);
        final ProducibleListenerSelf object = new ProducibleListenerSelf(services, setup);
        final Object object2 = new Object();
        final Producible producible = new ProducibleModel(services, setup);
        producible.checkListener(object);
        producible.checkListener(object2);

        assertTrue(producible.getListeners().contains(object));
        assertFalse(producible.getListeners().contains(object2));

        producible.removeListener(object);

        assertFalse(producible.getListeners().contains(object));

        object.getFeature(Identifiable.class).notifyDestroyed();

        assertTrue(media.getFile().delete());
    }
}
