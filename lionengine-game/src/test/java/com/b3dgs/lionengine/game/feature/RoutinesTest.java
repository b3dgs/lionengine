/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Test {@link Routines}.
 */
public final class RoutinesTest
{
    private final FeaturableModel featurable = new FeaturableModel();

    /**
     * Test the routines.
     */
    @Test
    public void testRoutines()
    {
        final MyRoutine routine = new MyRoutine();
        featurable.addFeature(routine);
        final Routines routines = featurable.addFeatureAndGet(new Routines());

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
