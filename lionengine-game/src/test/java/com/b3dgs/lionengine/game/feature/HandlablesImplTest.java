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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;

/**
 * Test {@link HandlablesImpl}.
 */
public final class HandlablesImplTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig();
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    /** Handlables test. */
    private final HandlablesImpl featurables = new HandlablesImpl();
    /** Object test. */
    private final FeaturableModel object = new FeaturableModel();

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        object.addFeature(new IdentifiableModel());
    }

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
    public void testId()
    {
        featurables.add(object);

        assertEquals(object, featurables.get(object.getFeature(Identifiable.class).getId()));
        assertEquals(object, featurables.values().iterator().next());
        assertEquals(1, featurables.getIds().size());

        featurables.remove(object, object.getFeature(Identifiable.class).getId());

        assertTrue(featurables.getIds().isEmpty());
        assertFalse(featurables.values().iterator().hasNext());
        assertThrows(() -> featurables.get(object.getFeature(Identifiable.class).getId()), "Featurable not found: 2");
    }

    /**
     * Test feature manipulation.
     */
    @Test
    public void testFeature()
    {
        final Mirrorable mirrorable = new MirrorableModel();
        object.addFeatureAndGet(mirrorable);
        featurables.add(object);

        assertEquals(mirrorable, featurables.get(Mirrorable.class).iterator().next());
        assertFalse(featurables.get(Transformable.class).iterator().hasNext());

        featurables.remove(object, object.getFeature(Identifiable.class).getId());

        assertFalse(featurables.get(Mirrorable.class).iterator().hasNext());
    }

    /**
     * Test type with complex object manipulation.
     */
    @Test
    public void testFeatureComplex()
    {
        final Setup setup = new Setup(config);
        final Featurable complex = new ObjectComplex();
        complex.addFeature(new MirrorableModel());
        complex.addFeature(new TransformableModel(setup));
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
    public void testFeatureInheritance()
    {
        final Featurable inheritance = new ObjectComplex();
        inheritance.addFeature(new FeatureLevel2());
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
    private static final class ObjectComplex extends FeaturableModel implements Updatable
    {
        /**
         * Create object.
         */
        public ObjectComplex()
        {
            super();

            addFeature(new IdentifiableModel());
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
    private static class FeatureLevel1 extends FeatureModel implements Refreshable
    {
        @Override
        public void update(double extrp)
        {
            // Mock
        }
    }

    /**
     * Mock feature.
     */
    private static final class FeatureLevel2 extends FeatureLevel1
    {
        // Mock
    }
}
