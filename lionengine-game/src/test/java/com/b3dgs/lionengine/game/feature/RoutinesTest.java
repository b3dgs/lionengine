/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Test {@link Routines}.
 */
final class RoutinesTest
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
        config = UtilTransformable.createMedia(RoutinesTest.class);
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
    private final FeaturableModel featurable = new FeaturableModel(services, setup);

    /**
     * Test the routines.
     */
    @Test
    void testRoutines()
    {
        final MyRoutine routine = new MyRoutine(services, setup);
        featurable.addFeature(routine);
        final Routines routines = featurable.addFeature(Routines.class, services, setup);

        assertFalse(routine.update.get());
        assertFalse(routine.render.get());

        routines.update(1.0);

        assertTrue(routine.update.get());
        assertFalse(routine.render.get());

        routines.render(null);

        assertTrue(routine.update.get());
        assertTrue(routine.render.get());
    }

    /**
     * Routine mock.
     */
    @FeatureInterface
    private class MyRoutine extends FeatureModel implements Routine
    {
        private MyRoutine(Services services, Setup setup)
        {
            super(services, setup);
        }

        private final AtomicBoolean update = new AtomicBoolean();
        private final AtomicBoolean render = new AtomicBoolean();

        @Override
        public void update(double extrp)
        {
            Routine.super.update(extrp);
            update.set(true);
        }

        @Override
        public void render(Graphic g)
        {
            Routine.super.render(g);
            render.set(true);
        }
    }
}
