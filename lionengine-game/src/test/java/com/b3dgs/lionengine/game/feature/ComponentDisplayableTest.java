/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Test {@link ComponentDisplayable}.
 */
final class ComponentDisplayableTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilTransformable.createMedia(ComponentDisplayableTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);

    /**
     * Test the component.
     */
    @Test
    void testComponentLayer()
    {
        final ComponentDisplayable component = new ComponentDisplayable();
        services.add(component);

        final AtomicInteger last = new AtomicInteger();

        final Layerable object1 = createObject(component, last);
        final Layerable object2 = createObject(component, last);
        final Layerable object3 = createObject(component, last);
        final Layerable object4 = createObject(component, last);

        object1.setLayer(Integer.valueOf(4), Integer.valueOf(4));
        object2.setLayer(Integer.valueOf(6), Integer.valueOf(6));
        object3.setLayer(Integer.valueOf(5), Integer.valueOf(5));
        object4.setLayer(Integer.valueOf(4), Integer.valueOf(4));
        last.set(-1);

        component.render(null, null);

        assertEquals(4, object1.getLayerDisplay().intValue());
        assertEquals(6, object2.getLayerDisplay().intValue());
        assertEquals(5, object3.getLayerDisplay().intValue());
        assertEquals(4, object4.getLayerDisplay().intValue());

        assertEquals(-1, last.get());

        component.render(null, null);

        assertEquals(object2.getFeature(Identifiable.class).getId().intValue(), last.get());

        object2.getFeature(Identifiable.class).notifyDestroyed();
        object4.getFeature(Identifiable.class).notifyDestroyed();

        last.set(-1);
        component.render(null, null);

        assertEquals(object3.getFeature(Identifiable.class).getId().intValue(), last.get());
    }

    /**
     * Test the component with default value.
     */
    @Test
    void testComponentLayerDefault()
    {
        final ComponentDisplayable component = new ComponentDisplayable();
        services.add(component);

        final AtomicBoolean auto = new AtomicBoolean();

        final Featurable featurable = new FeaturableModel(services, setup);
        featurable.addFeature(new DisplayableModel(g -> auto.set(true)));
        component.notifyHandlableAdded(featurable);
        component.render(null, null);

        assertTrue(auto.get());
    }

    /**
     * Test the component notification.
     */
    @Test
    void testNotify()
    {
        final ComponentDisplayable component = new ComponentDisplayable();

        final Featurable featurable = new FeaturableModel(services, setup);
        final Featurable featurable2 = new FeaturableModel(services, setup);
        component.notifyHandlableAdded(featurable);
        component.notifyHandlableAdded(featurable2);
        component.notifyLayerChanged(featurable, null, null, null, null);
        component.notifyLayerChanged(featurable2, null, null, null, null);

        assertTrue(((TreeSet<?>) UtilReflection.getField(component, "indexs")).isEmpty());

        component.notifyHandlableRemoved(featurable);

        featurable.addFeature(new DisplayableModel(g ->
        {
            // Mock
        }));
        featurable2.addFeature(new DisplayableModel(g ->
        {
            // Mock
        }));
        component.notifyHandlableAdded(featurable);
        component.notifyHandlableAdded(featurable2);

        assertFalse(((HashSet<?>) UtilReflection.getMethod(component, "getLayer", Integer.valueOf(0))).isEmpty());

        component.notifyHandlableRemoved(featurable);

        assertFalse(((HashSet<?>) UtilReflection.getMethod(component, "getLayer", Integer.valueOf(0))).isEmpty());

        component.notifyHandlableRemoved(featurable2);

        assertTrue(((HashSet<?>) UtilReflection.getMethod(component, "getLayer", Integer.valueOf(0))).isEmpty());
    }

    /**
     * Test the component with layerable remove.
     */
    @Test
    void testRemoveLayerable()
    {
        final AtomicBoolean called = new AtomicBoolean();
        final ComponentDisplayable component = new ComponentDisplayable()
        {
            @Override
            public void notifyLayerChanged(FeatureProvider provider,
                                           Integer layerRefreshOld,
                                           Integer layerRefreshNew,
                                           Integer layerDisplayOld,
                                           Integer layerDisplayNew)
            {
                super.notifyLayerChanged(provider, layerRefreshOld, layerRefreshNew, layerDisplayOld, layerDisplayNew);
                called.set(true);
            }
        };

        final Featurable featurable = new FeaturableModel(services, setup);
        featurable.addFeature(new RefreshableModel(extrp ->
        {
            // Mock
        }));
        final Layerable layerable = featurable.addFeatureAndGet(new LayerableModel(services, setup));
        component.notifyHandlableAdded(featurable);
        component.notifyHandlableRemoved(featurable);

        layerable.setLayer(Integer.valueOf(1), Integer.valueOf(1));

        assertFalse(called.get());
    }

    /**
     * Test with handler.
     */
    @Test
    void testWithHandler()
    {
        final FeaturableModel object = new FeaturableModel(services, setup);
        final Layerable layerable = new LayerableModel(services, setup);
        object.addFeature(layerable);
        object.addFeature(new DisplayableModel(g ->
        {
            // Mock
        }));
        final Handler handler = new Handler(services);
        handler.addComponent(new ComponentDisplayable());
        handler.add(object);
        handler.update(1.0);

        assertEquals(0, layerable.getLayerDisplay().intValue());
    }

    /**
     * Create a test object.
     * 
     * @param component The component reference.
     * @param last The last rendered element.
     * @return The created object.
     */
    private Layerable createObject(ComponentDisplayable component, AtomicInteger last)
    {
        final FeaturableModel object = new FeaturableModel(services, setup);
        final LayerableModel layerable = object.addFeatureAndGet(new LayerableModel(services, setup));
        layerable.prepare(object);
        component.notifyHandlableAdded(object);

        object.addFeature(new DisplayableModel(g ->
        {
            if (object.getFeature(Identifiable.class).getId() != null)
            {
                last.set(object.getFeature(Identifiable.class).getId().intValue());
            }
        }));

        return layerable;
    }
}
