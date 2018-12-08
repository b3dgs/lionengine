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
package com.b3dgs.lionengine.game.feature.body;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;

/**
 * Test {@link BodyModel}.
 */
public final class BodyTest
{
    private final Body body = new BodyModel();
    private final FeaturableModel object = new FeaturableModel();
    private final Transformable transformable = object.addFeatureAndGet(new TransformableModel());

    /**
     * Clean test.
     */
    @AfterEach
    public void clean()
    {
        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the gravity on body.
     */
    @Test
    public void testGravity()
    {
        transformable.teleport(0, 6.0);

        body.prepare(object);
        body.setMass(2.0);
        body.setDesiredFps(50);

        assertEquals(2.0, body.getMass());
        assertEquals(2.0 * Constant.GRAVITY_EARTH, body.getWeight());

        body.setGravity(3.0);
        transformable.moveLocation(1.0, body);

        assertEquals(2.0, body.getMass());
        assertEquals(6.0, body.getWeight());

        body.setGravityMax(8.0);
        transformable.moveLocation(1.0, body);

        assertEquals(6.0, transformable.getOldY());
        assertEquals(6.0, transformable.getY());

        body.update(1.0);
        transformable.moveLocation(1.0, body);

        assertEquals(6.0, transformable.getOldY());
        assertEquals(5.85, transformable.getY());

        body.update(1.0);
        transformable.moveLocation(1.0, body);

        assertEquals(5.85, transformable.getOldY());
        assertEquals(5.55, transformable.getY());
    }

    /**
     * Test the gravity reset.
     */
    @Test
    public void testResetGravity()
    {
        transformable.teleport(0, 6.0);

        body.prepare(object);
        body.setMass(2.0);
        body.setGravity(3.0);
        body.setGravityMax(8.0);
        body.setDesiredFps(50);
        body.update(1.0);
        transformable.moveLocation(1.0, body);

        assertEquals(6.0, transformable.getOldY());
        assertEquals(5.85, transformable.getY());

        body.resetGravity();
        body.update(1.0);
        transformable.moveLocation(1.0, body);

        assertEquals(5.85, transformable.getOldY());
        assertEquals(5.7, transformable.getY());
    }

    /**
     * Test the gravity fps.
     */
    @Test
    public void testFps()
    {
        transformable.teleport(0, 6.0);

        body.prepare(object);
        body.setMass(1.0);
        body.setGravity(1.0);
        body.setDesiredFps(50);
        body.update(1.0);
        transformable.moveLocation(1.0, body);

        assertEquals(6.0, transformable.getOldY());
        assertEquals(5.95, transformable.getY());

        body.update(0.5);
        transformable.moveLocation(1.0, body);

        assertEquals(5.95, transformable.getOldY());
        assertEquals(5.8875, transformable.getY());
    }
}
