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
package com.b3dgs.lionengine.game.layer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.handler.DisplayableModel;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.UtilSetup;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Test the component renderer layer class.
 */
public class ComponentRendererLayerTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Create a test object.
     * 
     * @param services The services reference.
     * @param handler The handler reference.
     * @param last The last rendered element.
     * @return The created object.
     */
    private static Layerable createObject(Services services, Handler handler, final AtomicInteger last)
    {
        final Setup setup = new Setup(config);
        final ObjectGame object = new ObjectGame(setup);
        final LayerableModel layerable = object.addFeatureAndGet(new LayerableModel());
        layerable.prepare(object, services);

        object.addFeature(new DisplayableModel(new Renderable()
        {
            @Override
            public void render(Graphic g)
            {
                if (object.getId() != null)
                {
                    last.set(object.getId().intValue());
                }
            }
        }));
        handler.add(object);

        return layerable;
    }

    /**
     * Test the component.
     */
    @Test
    public void testComponentLayer()
    {
        final ComponentDisplayerLayer component = new ComponentDisplayerLayer();
        final Services services = new Services();
        services.add(component);

        final Handler handler = new Handler(services);
        handler.addRenderable(component);
        final AtomicInteger last = new AtomicInteger();

        final Layerable object1 = createObject(services, handler, last);
        final Layerable object2 = createObject(services, handler, last);
        final Layerable object3 = createObject(services, handler, last);
        final Layerable object4 = createObject(services, handler, last);

        handler.update(1.0);
        object1.setLayer(Integer.valueOf(4));
        object2.setLayer(Integer.valueOf(6));
        object3.setLayer(Integer.valueOf(5));
        object4.setLayer(Integer.valueOf(4));
        last.set(-1);

        component.render(null, null);

        Assert.assertEquals(4, object1.getLayer().intValue());
        Assert.assertEquals(6, object2.getLayer().intValue());
        Assert.assertEquals(5, object3.getLayer().intValue());
        Assert.assertEquals(4, object4.getLayer().intValue());

        Assert.assertEquals(object2.getOwner().getId().intValue(), last.get());

        handler.remove(object2.getOwner());
        handler.remove(object4.getOwner());
        handler.update(1.0);
        last.set(-1);
        component.render(null, null);

        Assert.assertEquals(object3.getOwner().getId().intValue(), last.get());
    }

    /**
     * Test the component with default value.
     */
    @Test
    public void testComponentLayerDefault()
    {
        final ComponentDisplayerLayer component = new ComponentDisplayerLayer();
        final Services services = new Services();
        services.add(component);

        final Handler handler = new Handler(services);
        handler.addRenderable(component);
        final AtomicBoolean auto = new AtomicBoolean();

        final ObjectGame object = new ObjectGame(new Setup(config));
        object.addFeature(new DisplayableModel(new Renderable()
        {
            @Override
            public void render(Graphic g)
            {
                auto.set(true);
            }
        }));
        handler.add(object);

        handler.update(1.0);
        component.render(null, null);

        Assert.assertTrue(auto.get());
    }
}
