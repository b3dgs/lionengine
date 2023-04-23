/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;

/**
 * Test {@link HandlablesImpl}.
 */
final class HandlablesImplTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig(HandlablesImplTest.class);
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
    private final HandlablesImpl featurables = new HandlablesImpl();
    private final FeaturableModel object = new FeaturableModel(services, setup);

    /**
     * Clean test.
     */
    @AfterEach
    public void clean()
    {
        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test ID manipulation.
     */
    @Test
    void testId()
    {
        featurables.add(object);

        assertEquals(object, featurables.get(object.getFeature(Identifiable.class).getId()));
        assertEquals(object, featurables.values().iterator().next());
        assertEquals(1, featurables.getIds().size());

        featurables.remove(object, object.getFeature(Identifiable.class).getId());

        assertTrue(featurables.getIds().isEmpty());
        assertFalse(featurables.values().iterator().hasNext());

        final Integer id = object.getFeature(Identifiable.class).getId();
        assertNull(featurables.get(id));
    }

    /**
     * Test feature manipulation.
     */
    @Test
    void testFeature()
    {
        final Mirrorable mirrorable = new MirrorableModel(services, setup);
        object.addFeatureAndGet(mirrorable);

        featurables.remove(object, object.getFeature(Identifiable.class).getId());

        assertFalse(featurables.get(Mirrorable.class).iterator().hasNext());

        featurables.add(object);

        assertEquals(mirrorable, featurables.get(Mirrorable.class).iterator().next());
        assertFalse(featurables.get(Transformable.class).iterator().hasNext());

        featurables.remove(object, object.getFeature(Identifiable.class).getId());

        assertFalse(featurables.get(Mirrorable.class).iterator().hasNext());

        featurables.remove(object, object.getFeature(Identifiable.class).getId());

        assertFalse(featurables.get(Mirrorable.class).iterator().hasNext());
    }

    /**
     * Test type with complex object manipulation.
     */
    @Test
    void testFeatureComplex()
    {
        final Featurable complex = new ObjectComplex(services, setup);
        complex.addFeature(new MirrorableModel(services, setup));
        complex.addFeature(new TransformableModel(services, setup));
        featurables.add(complex);

        int i = 0;
        for (final Updatable updatable : featurables.get(Updatable.class))
        {
            assertEquals(complex, updatable);
            i++;
        }
        assertEquals(1, i);
    }

    /**
     * Test type with multiple feature inheritance.
     */
    @Test
    void testFeatureInheritance()
    {
        final Featurable inheritance = new ObjectComplex(services, setup);
        inheritance.addFeature(new FeatureLevel2(services, setup));
        featurables.add(inheritance);

        int i = 0;
        for (final Refreshable refreshable : featurables.get(Refreshable.class))
        {
            assertNotNull(refreshable);
            i++;
        }
        assertEquals(1, i);
    }

    /**
     * Complex object with interface.
     */
    @FeatureInterface
    private static final class ObjectComplex extends FeaturableModel implements Updatable
    {
        private ObjectComplex(Services services, Setup setup)
        {
            super(services, setup);
        }

        @Override
        public void update(double extrp)
        {
            // Mock
        }
    }

    /**
     * Mock feature.
     */
    @FeatureInterface
    private static class FeatureLevel1 extends FeatureModel implements Refreshable
    {
        private FeatureLevel1(Services services, Setup setup)
        {
            super(services, setup);
        }

        @Override
        public void update(double extrp)
        {
            // Mock
        }
    }

    /**
     * Mock feature.
     */
    @FeatureInterface
    private static final class FeatureLevel2 extends FeatureLevel1
    {
        private FeatureLevel2(Services services, Setup setup)
        {
            super(services, setup);
        }
    }
}
