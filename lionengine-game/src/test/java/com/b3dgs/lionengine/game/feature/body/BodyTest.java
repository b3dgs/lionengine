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
package com.b3dgs.lionengine.game.feature.body;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;

/**
 * Test {@link BodyModel}.
 */
final class BodyTest
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
        config = UtilSetup.createConfig(BodyTest.class);
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
    private final Body body = new BodyModel(services, setup);
    private final FeaturableModel object = new FeaturableModel(services, setup);
    private final Transformable transformable = object.addFeatureAndGet(new TransformableModel(services, setup));

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
    void testGravity()
    {
        transformable.teleport(0, 6.0);

        body.prepare(object);
        body.setMass(2.0);
        body.setGravity(Constant.GRAVITY_EARTH);

        assertEquals(2.0, body.getMass());
        assertEquals(2.0 * Constant.GRAVITY_EARTH, body.getWeight());

        body.setGravity(3.0);
        transformable.backup();
        transformable.moveLocation(1.0, body);

        assertEquals(2.0, body.getMass());
        assertEquals(6.0, body.getWeight());

        body.setGravityMax(8.0);
        transformable.backup();
        transformable.moveLocation(1.0, body);

        assertEquals(6.0, transformable.getOldY());
        assertEquals(6.0, transformable.getY());

        body.update(1.0);
        transformable.backup();
        transformable.moveLocation(1.0, body);

        assertEquals(6.0, transformable.getOldY());
        assertEquals(3.0, transformable.getY());

        body.update(1.0);
        transformable.backup();
        transformable.moveLocation(1.0, body);

        assertEquals(3.0, transformable.getOldY());
        assertEquals(-3.0, transformable.getY());
    }

    /**
     * Test the gravity reset.
     */
    @Test
    void testResetGravity()
    {
        transformable.teleport(0, 6.0);

        body.prepare(object);
        body.setMass(2.0);
        body.setGravity(3.0);
        body.setGravityMax(8.0);
        body.update(1.0);

        transformable.backup();
        transformable.moveLocation(1.0, body);

        assertEquals(6.0, transformable.getOldY());
        assertEquals(3.0, transformable.getY());

        body.resetGravity();
        body.update(1.0);

        transformable.backup();
        transformable.moveLocation(1.0, body);

        assertEquals(3.0, transformable.getOldY());
        assertEquals(0.0, transformable.getY());
    }

    /**
     * Test the gravity fps.
     */
    @Test
    void testFps()
    {
        transformable.teleport(0, 6.0);

        body.prepare(object);
        body.setMass(1.0);
        body.setGravity(1.0);
        body.setGravityMax(1.0);
        body.update(1.0);

        transformable.backup();
        transformable.moveLocation(1.0, body);

        assertEquals(6.0, transformable.getOldY());
        assertEquals(5.0, transformable.getY());

        body.update(0.5);
        transformable.backup();
        transformable.moveLocation(1.0, body);

        assertEquals(5.0, transformable.getOldY());
        assertEquals(4.0, transformable.getY());
    }
}
