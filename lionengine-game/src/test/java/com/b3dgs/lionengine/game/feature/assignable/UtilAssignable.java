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
package com.b3dgs.lionengine.game.feature.assignable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.identifiable.IdentifiableModel;

/**
 * Utilities dedicated to assignable test.
 */
public class UtilAssignable
{
    /**
     * Create the services.
     * 
     * @param clicked The click flag.
     * @param clickNumber The click number recorded.
     * @return The services.
     */
    public static Services createServices(final AtomicBoolean clicked, final AtomicInteger clickNumber)
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
     * @return The prepared assignable.
     */
    public static AssignableModel createAssignable(Services services)
    {
        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new IdentifiableModel());

        final AssignableModel assignable = new AssignableModel();
        assignable.prepare(featurable, services);

        featurable.prepareFeatures(services);

        return assignable;
    }

    /**
     * Add a default assign.
     * 
     * @param assigned The assigned flag.
     * @return The created assign.
     */
    public static Assign createAssign(final AtomicBoolean assigned)
    {
        return new Assign()
        {
            @Override
            public void assign()
            {
                assigned.set(true);
            }
        };
    }
}
