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
package com.b3dgs.lionengine.game.object;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.layer.Layerable;
import com.b3dgs.lionengine.game.layer.LayerableListener;
import com.b3dgs.lionengine.game.layer.LayerableModel;
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
        final Handler handler = new Handler();
        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)), new Services());
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
        final Handler handler = new Handler();
        Assert.assertNull(handler.get(Integer.valueOf(0)));
    }

    /**
     * Get not found object from type.
     */
    @Test
    public void testObjectTypeNotFound()
    {
        final Handler handler = new Handler();
        Assert.assertFalse(handler.get(ObjectGame.class).iterator().hasNext());
    }

    /**
     * Add an object and get it.
     */
    @Test
    public void testRemoveObject()
    {
        final Handler handler = new Handler();
        Assert.assertEquals(0, handler.size());

        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)), new Services());
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

        handler.add(object);
        handler.update(1.0);
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
        final Handler handler = new Handler();
        final AtomicReference<ObjectGame> added = new AtomicReference<ObjectGame>();
        final AtomicReference<ObjectGame> removed = new AtomicReference<ObjectGame>();
        final HandlerListener listener = new HandlerListener()
        {
            @Override
            public void notifyObjectAdded(ObjectGame object)
            {
                added.set(object);
            }

            @Override
            public void notifyObjectRemoved(ObjectGame object)
            {
                removed.set(object);
            }
        };
        handler.addListener(listener);

        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)), new Services());
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
        final Handler handler = new Handler();
        final AtomicReference<Double> extrapolation = new AtomicReference<Double>();
        final AtomicReference<ObjectGame> updated = new AtomicReference<ObjectGame>();
        handler.addUpdatable(new ComponentUpdatable()
        {
            @Override
            public void update(double extrp, HandledObjects objects)
            {
                extrapolation.set(Double.valueOf(extrp));
                for (final ObjectGame object : objects.values())
                {
                    updated.set(object);
                }
            }
        });

        Assert.assertNull(extrapolation.get());
        Assert.assertNull(updated.get());

        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)), new Services());
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
        final Handler handler = new Handler();
        final AtomicReference<ObjectGame> rendered = new AtomicReference<ObjectGame>();
        handler.addRenderable(new ComponentRenderable()
        {
            @Override
            public void render(Graphic g, HandledObjects objects)
            {
                rendered.set(objects.values().iterator().next());
            }
        });

        Assert.assertNull(rendered.get());

        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)), new Services());
        handler.add(object);
        handler.update(1.0);
        handler.render(Graphics.createGraphic());

        Assert.assertEquals(object, rendered.get());

        handler.removeAll();
        handler.update(1.0);
        Assert.assertEquals(0, handler.size());
    }

    /**
     * Test with traits.
     */
    @Test
    public void testTrait()
    {
        final Handler handler = new Handler();
        final Services services = new Services();
        services.add(new LayerableListener()
        {
            @Override
            public void notifyLayerChanged(ObjectGame object, Integer oldLayer, Integer newLayer)
            {
                // Mock
            }
        });
        final ObjectGame object = new ObjectTrait(new Setup(Medias.create(OBJECT_XML)), services);
        object.prepareTraits(services);
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
     * Object with trait.
     */
    private class ObjectTrait extends ObjectGame implements Localizable, Layerable, LayerableListener
    {
        /**
         * Create object.
         * 
         * @param setup The setup reference.
         * @param services The services.
         */
        public ObjectTrait(Setup setup, Services services)
        {
            super(setup, services);
            addTrait(new LayerableModel());
        }

        /*
         * Localizable
         */

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
        public void prepare(ObjectGame owner, Services services)
        {
            // Mock
        }

        @Override
        public <O extends ObjectGame> O getOwner()
        {
            return null;
        }

        @Override
        public void addListener(LayerableListener listener)
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
        public void notifyLayerChanged(ObjectGame object, Integer oldLayer, Integer newLayer)
        {
            // Mock
        }
    }
}
