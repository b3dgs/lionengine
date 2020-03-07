/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.assignable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Utilities dedicated to assignable test.
 */
final class UtilAssignable
{
    /**
     * Create the services.
     * 
     * @param clicked The click flag.
     * @param clickNumber The click number recorded.
     * @return The services.
     */
    public static Services createServices(AtomicBoolean clicked, AtomicInteger clickNumber)
    {
        final Services services = new Services();
        final Camera camera = services.create(Camera.class);
        camera.setView(0, 0, 32, 32, 32);

        final Cursor cursor = services.add(new Cursor()
        {
            @Override
            public boolean hasClickedOnce(int click)
            {
                clickNumber.set(click);
                return clicked.get();
            }
        });
        cursor.setArea(0, 0, 64, 64);
        cursor.setLocation(0, 1);

        return services;
    }

    /**
     * Create the assignable.
     * 
     * @param services The services.
     * @param setup The setup.
     * @return The prepared assignable.
     */
    public static AssignableModel createAssignable(Services services, Setup setup)
    {
        final Featurable featurable = new FeaturableModel(services, setup);
        final AssignableModel assignable = new AssignableModel(services, setup);
        assignable.prepare(featurable);

        return assignable;
    }

    /**
     * Add a default assign.
     * 
     * @param assigned The assigned flag.
     * @return The created assign.
     */
    public static Assign createAssign(AtomicBoolean assigned)
    {
        return () -> assigned.set(true);
    }
}
