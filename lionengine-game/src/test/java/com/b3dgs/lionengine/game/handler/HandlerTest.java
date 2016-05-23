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
package com.b3dgs.lionengine.game.handler;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the handler class.
 */
public class HandlerTest
{
    /** Object configuration file name. */
    private static final String OBJECT_XML = "object.xml";

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(HandlerTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Add an object and get it.
     */
    @Test
    public void testAddGetObject()
    {
        final Handler handler = new Handler(new Services());
        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)));
        handler.add(object);
        try
        {
            Assert.assertNotNull(handler.get(object.getId()));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        handler.update(1.0);
        Assert.assertEquals(1, handler.size());
        Assert.assertNotNull(handler.get(object.getId()));
        Assert.assertTrue(handler.get(ObjectGame.class).iterator().hasNext());
        Assert.assertTrue(handler.values().iterator().hasNext());

        handler.removeAll();
        handler.update(1.0);
    }

    /**
     * Get not found object from id.
     */
    @Test(expected = LionEngineException.class)
    public void testObjectIdNotFound()
    {
        final Handler handler = new Handler(new Services());
        Assert.assertNull(handler.get(Integer.valueOf(0)));
    }

    /**
     * Get not found object from type.
     */
    @Test
    public void testObjectTypeNotFound()
    {
        final Handler handler = new Handler(new Services());
        Assert.assertFalse(handler.get(ObjectGame.class).iterator().hasNext());
    }

    /**
     * Add an object and get it.
     */
    @Test
    public void testRemoveObject()
    {
        final Handler handler = new Handler(new Services());
        Assert.assertEquals(0, handler.size());

        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)));
        handler.add(object);
        Assert.assertEquals(0, handler.size());

        handler.update(1.0);
        Assert.assertEquals(1, handler.size());

        handler.remove(object);
        Assert.assertEquals(1, handler.size());

        handler.update(1.0);
        Assert.assertEquals(0, handler.size());

        handler.add(object);
        Assert.assertEquals(0, handler.size());
        handler.update(1.0);
        Assert.assertEquals(1, handler.size());

        handler.removeAll();

        Assert.assertEquals(1, handler.size());
        handler.update(1.0);
        Assert.assertEquals(0, handler.size());
    }

    /**
     * Add an object and destroy it.
     */
    @Test
    public void testDestroyObject()
    {
        final Handler handler = new Handler(new Services());
        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)));
        handler.add(object);

        Assert.assertEquals(0, handler.size());
        handler.update(1.0);
        Assert.assertEquals(1, handler.size());

        object.destroy();

        Assert.assertEquals(1, handler.size());
        handler.update(1.0);
        Assert.assertEquals(0, handler.size());
    }

    /**
     * Add and remove handler listener for object added and removed.
     */
    @Test
    public void testAddRemoveListener()
    {
        final Handler handler = new Handler(new Services());
        final AtomicReference<Handlable> added = new AtomicReference<Handlable>();
        final AtomicReference<Handlable> removed = new AtomicReference<Handlable>();
        final HandlerListener listener = new HandlerListener()
        {
            @Override
            public void notifyHandlableAdded(Handlable handlable)
            {
                added.set(handlable);
            }

            @Override
            public void notifyHandlableRemoved(Handlable handlable)
            {
                removed.set(handlable);
            }
        };
        handler.addListener(listener);

        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)));
        Assert.assertNull(added.get());
        handler.add(object);
        Assert.assertNull(added.get());

        handler.update(1.0);
        Assert.assertEquals(object, added.get());

        Assert.assertNull(removed.get());
        handler.remove(object);
        Assert.assertNull(removed.get());

        handler.update(1.0);
        Assert.assertEquals(object, removed.get());

        added.set(null);
        removed.set(null);
        handler.removeListener(listener);

        handler.add(object);
        handler.update(1.0);
        handler.remove(object);
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
        final AtomicReference<Handlable> updated = new AtomicReference<Handlable>();
        handler.addComponent(new ComponentUpdater()
        {
            @Override
            public void update(double extrp, Handlables objects)
            {
                extrapolation.set(Double.valueOf(extrp));
                for (final Handlable handlable : objects.values())
                {
                    updated.set(handlable);
                }
            }
        });

        Assert.assertNull(extrapolation.get());
        Assert.assertNull(updated.get());

        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)));
        handler.add(object);
        handler.update(1.0);

        Assert.assertEquals(1.0, extrapolation.get().doubleValue(), UtilTests.PRECISION);
        Assert.assertEquals(object, updated.get());

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
        final AtomicReference<Handlable> rendered = new AtomicReference<Handlable>();
        handler.addComponent(new ComponentRenderer()
        {
            @Override
            public void render(Graphic g, Handlables objects)
            {
                rendered.set(objects.values().iterator().next());
            }
        });

        Assert.assertNull(rendered.get());

        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)));
        handler.add(object);
        handler.update(1.0);
        handler.render(Graphics.createGraphic());

        Assert.assertEquals(object, rendered.get());

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
            public void notifyLayerChanged(Featurable featurable, Integer layerOld, Integer layerNew)
            {
                // Mock
            }
        });
        final Handler handler = new Handler(services);
        final ObjectGame object = new ObjectFeatures(new Setup(Medias.create(OBJECT_XML)));
        object.prepareFeatures(object, services);

        handler.add(object);
        handler.update(1.0);
        handler.remove(object);
        handler.remove(object);
        handler.update(1.0);

        handler.removeAll();
        handler.update(1.0);

        Assert.assertEquals(0, handler.size());
    }

    /**
     * Object with features.
     */
    private class ObjectFeatures extends ObjectGame implements Localizable, Layerable, LayerableListener
    {
        /**
         * Create object.
         * 
         * @param setup The setup reference.
         */
        public ObjectFeatures(Setup setup)
        {
            super(setup);
            addFeature(new LayerableModel());
        }

        @Override
        public double getX()
        {
            return 0;
        }

        @Override
        public double getY()
        {
            return 0;
        }

        @Override
        public void prepare(Handlable owner, Services services)
        {
            // Mock
        }

        @Override
        public void checkListener(Object listener)
        {
            // Mock
        }

        @Override
        public <O extends Handlable> O getOwner()
        {
            return null;
        }

        @Override
        public void addListener(LayerableListener listener)
        {
            // Mock
        }

        @Override
        public void setLayer(int layer)
        {
            // Mock
        }

        @Override
        public void setLayer(Integer layer)
        {
            // Mock
        }

        @Override
        public Integer getLayer()
        {
            return null;
        }

        @Override
        public void notifyLayerChanged(Featurable featurable, Integer layerOld, Integer layerNew)
        {
            // Mock
        }
    }
}
