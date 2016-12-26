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
package com.b3dgs.lionengine.game.feature;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Test the component refreshable layer class.
 */
public class ComponentRefreshableTest
{
    /**
     * Create a test object.
     * 
     * @param services The services reference.
     * @param last The last rendered element.
     * @return The created object.
     */
    private static Layerable createObject(Services services, final AtomicInteger last)
    {
        final FeaturableModel object = new FeaturableModel();
        object.addFeature(new IdentifiableModel());

        final LayerableModel layerable = object.addFeatureAndGet(new LayerableModel());
        layerable.prepare(object, services);

        object.addFeature(new RefreshableModel(new Updatable()
        {
            @Override
            public void update(double extrp)
            {
                if (object.getFeature(Identifiable.class).getId() != null)
                {
                    last.set(object.getFeature(Identifiable.class).getId().intValue());
                }
            }
        }));

        object.prepareFeatures(services);

        return layerable;
    }

    /**
     * Test the component.
     */
    @Test
    public void testComponentLayer()
    {
        final ComponentRefreshable component = new ComponentRefreshable();
        final Services services = new Services();
        services.add(component);

        final AtomicInteger last = new AtomicInteger();

        final Layerable object1 = createObject(services, last);
        final Layerable object2 = createObject(services, last);
        final Layerable object3 = createObject(services, last);
        final Layerable object4 = createObject(services, last);

        object1.setLayer(4);
        object2.setLayer(6);
        object3.setLayer(5);
        object4.setLayer(4);
        last.set(-1);

        component.update(1.0, null);

        Assert.assertEquals(4, object1.getLayerDisplay().intValue());
        Assert.assertEquals(6, object2.getLayerDisplay().intValue());
        Assert.assertEquals(5, object3.getLayerDisplay().intValue());
        Assert.assertEquals(4, object4.getLayerDisplay().intValue());

        Assert.assertEquals(object2.getFeature(Identifiable.class).getId().intValue(), last.get());

        object2.getFeature(Identifiable.class).notifyDestroyed();
        object4.getFeature(Identifiable.class).notifyDestroyed();

        last.set(-1);
        component.update(1.0, null);

        Assert.assertEquals(object3.getFeature(Identifiable.class).getId().intValue(), last.get());
    }

    /**
     * Test the component with default value.
     */
    @Test
    public void testComponentLayerDefault()
    {
        final ComponentRefreshable component = new ComponentRefreshable();
        final Services services = new Services();
        services.add(component);

        final AtomicBoolean auto = new AtomicBoolean();

        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new RefreshableModel(new Updatable()
        {
            @Override
            public void update(double extrp)
            {
                auto.set(true);
            }
        }));
        component.notifyHandlableAdded(featurable);
        component.update(1.0, null);

        Assert.assertTrue(auto.get());
    }

    /**
     * Test the component notification.
     */
    @Test
    public void testNotify()
    {
        final ComponentRefreshable component = new ComponentRefreshable();

        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new RefreshableModel(new Updatable()
        {
            @Override
            public void update(double extrp)
            {
                // Mock
            }
        }));
        component.notifyHandlableAdded(featurable);

        Assert.assertFalse(((HashSet<?>) UtilReflection.getMethod(component,
                                                                  "getLayer",
                                                                  Integer.valueOf(0))).isEmpty());

        component.notifyHandlableRemoved(featurable);

        Assert.assertTrue(((HashSet<?>) UtilReflection.getMethod(component, "getLayer", Integer.valueOf(0))).isEmpty());
    }
}
