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
package com.b3dgs.lionengine.game.feature;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the handler class.
 */
public class HandlerTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Add a featurable and get it.
     */
    @Test
    public void testAddGetObject()
    {
        final Handler handler = new Handler(new Services());
        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new IdentifiableModel());
        handler.add(featurable);
        try
        {
            Assert.assertNotNull(handler.get(featurable.getFeature(Identifiable.class).getId()));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
        handler.update(1.0);
        Assert.assertEquals(1, handler.size());
        Assert.assertNotNull(handler.get(featurable.getFeature(Identifiable.class).getId()));
        Assert.assertTrue(handler.get(Featurable.class).iterator().hasNext());
        Assert.assertTrue(handler.values().iterator().hasNext());

        handler.removeAll();
        handler.update(1.0);
    }

    /**
     * Get not found featurable from id.
     */
    @Test(expected = LionEngineException.class)
    public void testObjectIdNotFound()
    {
        final Handler handler = new Handler(new Services());
        Assert.assertNull(handler.get(Integer.valueOf(0)));
    }

    /**
     * Get not found featurable from type.
     */
    @Test
    public void testObjectTypeNotFound()
    {
        final Handler handler = new Handler(new Services());
        Assert.assertFalse(handler.get(Featurable.class).iterator().hasNext());
    }

    /**
     * Add a featurable and get it.
     */
    @Test
    public void testRemoveObject()
    {
        final Handler handler = new Handler(new Services());
        Assert.assertEquals(0, handler.size());

        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new IdentifiableModel());
        handler.add(featurable);
        Assert.assertEquals(0, handler.size());

        handler.update(1.0);
        Assert.assertEquals(1, handler.size());

        handler.remove(featurable);
        Assert.assertEquals(1, handler.size());

        handler.update(1.0);
        Assert.assertEquals(0, handler.size());

        handler.add(featurable);
        Assert.assertEquals(0, handler.size());
        handler.update(1.0);
        Assert.assertEquals(1, handler.size());

        handler.removeAll();

        Assert.assertEquals(1, handler.size());
        handler.update(1.0);
        Assert.assertEquals(0, handler.size());
    }

    /**
     * Add a featurable and destroy it.
     */
    @Test
    public void testDestroyObject()
    {
        final Handler handler = new Handler(new Services());
        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new IdentifiableModel());
        handler.add(featurable);

        Assert.assertEquals(0, handler.size());
        handler.update(1.0);
        Assert.assertEquals(1, handler.size());

        featurable.getFeature(Identifiable.class).destroy();

        Assert.assertEquals(1, handler.size());
        handler.update(1.0);
        Assert.assertEquals(0, handler.size());
    }

    /**
     * Add and remove handler listener for featurable added and removed.
     */
    @Test
    public void testAddRemoveListener()
    {
        final Handler handler = new Handler(new Services());
        final AtomicReference<Featurable> added = new AtomicReference<Featurable>();
        final AtomicReference<Featurable> removed = new AtomicReference<Featurable>();
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

        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new IdentifiableModel());

        Assert.assertNull(added.get());
        handler.add(featurable);
        Assert.assertNull(added.get());

        handler.update(1.0);
        Assert.assertEquals(featurable, added.get());

        Assert.assertNull(removed.get());
        handler.remove(featurable);
        Assert.assertNull(removed.get());

        handler.update(1.0);
        Assert.assertEquals(featurable, removed.get());

        added.set(null);
        removed.set(null);
        handler.removeListener(listener);

        handler.add(featurable);
        handler.update(1.0);
        handler.remove(featurable);
        handler.update(1.0);

        Assert.assertNull(added.get());
        Assert.assertNull(removed.get());

        handler.removeAll();
        handler.update(1.0);
        Assert.assertEquals(0, handler.size());
    }

    /**
     * Add updatable component.
     */
    @Test
    public void testUpdatable()
    {
        final Handler handler = new Handler(new Services());
        final AtomicReference<Double> extrapolation = new AtomicReference<Double>();
        final AtomicReference<Featurable> updated = new AtomicReference<Featurable>();
        handler.addComponent(new ComponentUpdater()
        {
            @Override
            public void update(double extrp, Handlables featurables)
            {
                extrapolation.set(Double.valueOf(extrp));
                for (final Featurable featurable : featurables.values())
                {
                    updated.set(featurable);
                }
            }
        });

        Assert.assertNull(extrapolation.get());
        Assert.assertNull(updated.get());

        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new IdentifiableModel());
        handler.add(featurable);
        handler.update(1.0);

        Assert.assertEquals(1.0, extrapolation.get().doubleValue(), UtilTests.PRECISION);
        Assert.assertEquals(featurable, updated.get());

        handler.removeAll();
        handler.update(1.0);
        Assert.assertEquals(0, handler.size());
    }

    /**
     * Add renderable component.
     */
    @Test
    public void testRenderable()
    {
        final Handler handler = new Handler(new Services());
        final AtomicReference<Featurable> rendered = new AtomicReference<Featurable>();
        handler.addComponent(new ComponentRenderer()
        {
            @Override
            public void render(Graphic g, Handlables featurables)
            {
                rendered.set(featurables.values().iterator().next());
            }
        });

        Assert.assertNull(rendered.get());

        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new IdentifiableModel());
        handler.add(featurable);
        handler.update(1.0);
        handler.render(Graphics.createGraphic());

        Assert.assertEquals(featurable, rendered.get());

        handler.removeAll();
        handler.update(1.0);
        Assert.assertEquals(0, handler.size());
    }

    /**
     * Test with features.
     */
    @Test
    public void testFeatures()
    {
        final Services services = new Services();
        services.add(new LayerableListener()
        {
            @Override
            public void notifyLayerChanged(FeatureProvider provider,
                                           Integer layerRefreshOld,
                                           Integer layerRefreshNew,
                                           Integer layerDisplayOld,
                                           Integer layerDisplayNew)
            {
                // Mock
            }
        });
        final Handler handler = new Handler(services);
        final Featurable featurable = new ObjectFeatures();
        featurable.addFeature(new IdentifiableModel());
        featurable.prepareFeatures(services);

        handler.add(featurable);
        handler.update(1.0);
        handler.remove(featurable);
        handler.remove(featurable);
        handler.update(1.0);

        handler.removeAll();
        handler.update(1.0);

        Assert.assertEquals(0, handler.size());
    }

    /**
     * Test add component with listener.
     */
    @Test
    public void testAddComponentWithListener()
    {
        final AtomicBoolean add = new AtomicBoolean();
        final AtomicBoolean remove = new AtomicBoolean();
        final Listener listener = new Listener(add, remove);
        final Handler handler = new Handler(new Services());
        handler.addComponent(listener);

        final Featurable featurable = new FeaturableModel();
        handler.add(featurable);

        Assert.assertFalse(add.get());
        Assert.assertFalse(remove.get());

        handler.update(1.0);

        Assert.assertTrue(add.get());
        Assert.assertFalse(remove.get());

        add.set(false);
        handler.remove(featurable);

        Assert.assertFalse(add.get());
        Assert.assertFalse(remove.get());

        handler.update(1.0);

        Assert.assertFalse(add.get());
        Assert.assertTrue(remove.get());

        Assert.assertEquals(0, handler.size());
    }

    /**
     * Listener mock.
     */
    private static class Listener extends ComponentUpdatable implements HandlerListener
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
