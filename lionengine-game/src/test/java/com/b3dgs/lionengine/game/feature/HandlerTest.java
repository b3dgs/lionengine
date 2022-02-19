/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link Handler}.
 */
final class HandlerTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig(HandlerTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Graphics.setFactoryGraphic(null);
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);
    private final Handler handler = new Handler(services);

    /**
     * Add a featurable and get it.
     */
    @Test
    void testAddGetObject()
    {
        final Featurable featurable = new FeaturableModel(services, setup);
        handler.add(featurable);

        final Integer id = featurable.getFeature(Identifiable.class).getId();
        assertNotNull(handler.get(id));

        handler.update(1.0);
        assertEquals(1, handler.size());
        assertNotNull(handler.get(featurable.getFeature(Identifiable.class).getId()));
        assertTrue(handler.get(Featurable.class).iterator().hasNext());
        assertTrue(handler.values().iterator().hasNext());

        handler.removeAll();
        handler.update(1.0);
    }

    /**
     * Add a featurable and remove it without update.
     */
    @Test
    void testAddRemoveWithoutUpdate()
    {
        final Featurable featurable = new FeaturableModel(services, setup);
        handler.add(featurable);
        handler.remove(featurable);
        handler.update(1.0);
    }

    /**
     * Get not found featurable from id.
     */
    @Test
    void testObjectIdNotFound()
    {
        assertNull(handler.get(Integer.valueOf(0)));
    }

    /**
     * Get not found featurable from type.
     */
    @Test
    void testObjectTypeNotFound()
    {
        assertFalse(handler.get(Featurable.class).iterator().hasNext());
    }

    /**
     * Add a featurable and get it.
     */
    @Test
    void testRemoveObject()
    {
        assertEquals(0, handler.size());

        final Featurable featurable = new FeaturableModel(services, setup);
        handler.add(featurable);
        assertEquals(0, handler.size());

        handler.update(1.0);
        assertEquals(1, handler.size());

        handler.remove(featurable);
        assertEquals(1, handler.size());

        handler.update(1.0);
        assertEquals(0, handler.size());

        handler.add(featurable);
        assertEquals(0, handler.size());
        handler.update(1.0);
        assertEquals(1, handler.size());

        handler.removeAll();

        assertEquals(1, handler.size());
        handler.update(1.0);
        assertEquals(0, handler.size());
    }

    /**
     * Add a featurable and destroy it.
     */
    @Test
    void testDestroyObject()
    {
        final Featurable featurable = new FeaturableModel(services, setup);
        handler.add(featurable);

        assertEquals(0, handler.size());
        handler.update(1.0);
        assertEquals(1, handler.size());

        featurable.getFeature(Identifiable.class).destroy();

        assertEquals(1, handler.size());
        handler.update(1.0);
        assertEquals(0, handler.size());
    }

    /**
     * Add and remove handler listener for featurable added and removed.
     */
    @Test
    void testAddRemoveListener()
    {
        final AtomicReference<Featurable> added = new AtomicReference<>();
        final AtomicReference<Featurable> removed = new AtomicReference<>();
        final HandlerListener listener = new HandlerListener()
        {
            @Override
            public void notifyHandlableAdded(Featurable featurable)
            {
                added.set(featurable);
            }

            @Override
            public void notifyHandlableRemoved(Featurable featurable)
            {
                removed.set(featurable);
            }
        };
        handler.addListener(listener);

        final Featurable featurable = new FeaturableModel(services, setup);

        assertNull(added.get());
        handler.add(featurable);
        assertNull(added.get());

        handler.update(1.0);
        assertEquals(featurable, added.get());

        assertNull(removed.get());
        handler.remove(featurable);
        assertNull(removed.get());

        handler.update(1.0);
        assertEquals(featurable, removed.get());

        added.set(null);
        removed.set(null);
        handler.removeListener(listener);

        handler.add(featurable);
        handler.update(1.0);
        handler.remove(featurable);
        handler.update(1.0);

        assertNull(added.get());
        assertNull(removed.get());

        handler.removeAll();
        handler.update(1.0);
        assertEquals(0, handler.size());
    }

    /**
     * Add updatable component.
     */
    @Test
    void testUpdatable()
    {
        final AtomicReference<Double> extrapolation = new AtomicReference<>();
        final AtomicReference<Featurable> updated = new AtomicReference<>();
        handler.addComponent((ComponentUpdater) (extrp, featurables) ->
        {
            extrapolation.set(Double.valueOf(extrp));
            for (final Featurable featurable : featurables.values())
            {
                updated.set(featurable);
            }
        });

        assertNull(extrapolation.get());
        assertNull(updated.get());

        final Featurable featurable = new FeaturableModel(services, setup);
        handler.add(featurable);
        handler.update(1.0);

        assertEquals(1.0, extrapolation.get().doubleValue());
        assertEquals(featurable, updated.get());

        handler.removeAll();
        handler.update(1.0);
        assertEquals(0, handler.size());
    }

    /**
     * Add renderable component.
     */
    @Test
    void testRenderable()
    {
        final AtomicReference<Featurable> rendered = new AtomicReference<>();
        handler.addComponent((ComponentRenderer) (g,
                                                  featurables) -> rendered.set(featurables.values().iterator().next()));

        assertNull(rendered.get());

        final Featurable featurable = new FeaturableModel(services, setup);
        handler.add(featurable);
        handler.update(1.0);
        handler.render(Graphics.createGraphic());

        assertEquals(featurable, rendered.get());

        handler.removeAll();
        handler.update(1.0);
        assertEquals(0, handler.size());
    }

    /**
     * Test with features.
     */
    @Test
    void testFeatures()
    {
        services.add((LayerableListener) (provider,
                                          layerRefreshOld,
                                          layerRefreshNew,
                                          layerDisplayOld,
                                          layerDisplayNew) ->
        {
            // Mock
        });
        final Featurable featurable = new ObjectFeatures(services, setup);

        handler.add(featurable);
        handler.update(1.0);
        handler.remove(featurable);
        handler.remove(featurable);
        handler.update(1.0);

        handler.removeAll();
        handler.update(1.0);

        assertEquals(0, handler.size());
    }

    /**
     * Test add component with listener.
     */
    @Test
    void testAddComponentWithListener()
    {
        final AtomicBoolean add = new AtomicBoolean();
        final AtomicBoolean remove = new AtomicBoolean();
        final Listener listener = new Listener(add, remove);
        handler.addComponent(listener);

        final Featurable featurable = new FeaturableModel(services, setup);
        handler.add(featurable);

        assertFalse(add.get());
        assertFalse(remove.get());

        handler.update(1.0);

        assertTrue(add.get());
        assertFalse(remove.get());

        add.set(false);
        handler.remove(featurable);

        assertFalse(add.get());
        assertFalse(remove.get());

        handler.update(1.0);

        assertFalse(add.get());
        assertTrue(remove.get());

        assertEquals(0, handler.size());
    }

    /**
     * Listener mock.
     */
    private static final class Listener extends ComponentUpdatable implements HandlerListener
    {
        private final AtomicBoolean add;
        private final AtomicBoolean remove;

        /**
         * Create listener.
         * 
         * @param add Add boolean.
         * @param remove Remove boolean.
         */
        public Listener(AtomicBoolean add, AtomicBoolean remove)
        {
            this.add = add;
            this.remove = remove;
        }

        @Override
        public void notifyHandlableAdded(Featurable featurable)
        {
            add.set(true);
        }

        @Override
        public void notifyHandlableRemoved(Featurable featurable)
        {
            remove.set(true);
        }
    }
}
