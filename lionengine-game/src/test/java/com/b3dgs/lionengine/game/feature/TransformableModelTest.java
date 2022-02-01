/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Force;

/**
 * Test {@link TransformableModel}.
 */
final class TransformableModelTest
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
        config = UtilTransformable.createMedia(TransformableModelTest.class);
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
    private final Featurable featurable = new FeaturableModel(services, setup);
    private final TransformableModel transformable = new TransformableModel(services, setup);

    /**
     * Prepare test.
     */
    @BeforeEach
    public void before()
    {
        transformable.prepare(featurable);
    }

    /**
     * Clean test.
     */
    @AfterEach
    public void after()
    {
        featurable.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the transformable with default size.
     */
    @Test
    void testDefaultSize()
    {
        final Media media = UtilSetup.createConfig(TransformableModelTest.class);
        final Transformable transformable = new TransformableModel(services, new Setup(media));

        assertEquals(0, transformable.getWidth());
        assertEquals(0, transformable.getHeight());
        assertTrue(media.getFile().delete());
    }

    /**
     * Test the transformable teleport.
     */
    @Test
    void testTeleport()
    {
        assertLocalization(0.0, 0.0, 0.0, 0.0);

        transformable.teleport(1.0, -1.0);

        assertLocalization(1.0, -1.0, 1.0, -1.0);

        transformable.teleportX(2.0);

        assertLocalization(2.0, -1.0, 2.0, -1.0);

        transformable.teleportY(3.0);

        assertLocalization(2.0, 3.0, 2.0, 3.0);
    }

    /**
     * Test the transformable location setting.
     */
    @Test
    void testSetLocation()
    {
        assertLocalization(0.0, 0.0, 0.0, 0.0);

        transformable.backup();
        transformable.setLocation(1.0, 1.0);

        assertLocalization(0.0, 0.0, 1.0, 1.0);

        transformable.backup();
        transformable.setLocationX(2.0);

        assertLocalization(1.0, 1.0, 2.0, 1.0);

        transformable.setLocationY(3.0);

        assertLocalization(1.0, 1.0, 2.0, 3.0);
    }

    /**
     * Test the transformable size.
     */
    @Test
    void testSetSize()
    {
        assertEquals(16, transformable.getOldWidth());
        assertEquals(32, transformable.getOldHeight());
        assertEquals(16, transformable.getWidth());
        assertEquals(32, transformable.getHeight());

        transformable.setSize(64, 48);

        assertEquals(16, transformable.getOldWidth());
        assertEquals(32, transformable.getOldHeight());
        assertEquals(64, transformable.getWidth());
        assertEquals(48, transformable.getHeight());
    }

    /**
     * Test the transformable moving.
     */
    @Test
    void testMoveLocation()
    {
        assertLocalization(0.0, 0.0, 0.0, 0.0);

        transformable.backup();
        transformable.moveLocation(1.0, 1.0, 2.0);

        assertLocalization(0.0, 0.0, 1.0, 2.0);

        transformable.backup();
        transformable.moveLocation(1.0, new Force(-2.0, -3.0), new Force(-1.0, -2.0));

        assertLocalization(1.0, 2.0, -2.0, -3.0);
    }

    /**
     * Test the transformable moving on single axis at a time.
     */
    @Test
    void testMoveLocationSingleAxis()
    {
        transformable.moveLocationX(1.0, 1.0);

        assertLocalization(0.0, 0.0, 1.0, 0.0);

        transformable.moveLocationY(1.0, 1.0);

        assertLocalization(0.0, 0.0, 1.0, 1.0);
    }

    /**
     * Test the transformable transform function.
     */
    @Test
    void testTransform()
    {
        transformable.transform(1.0, 2.0, 3, 4);

        assertLocalization(1.0, 2.0, 1.0, 2.0);

        assertEquals(16, transformable.getOldWidth());
        assertEquals(32, transformable.getOldHeight());
        assertEquals(3, transformable.getWidth());
        assertEquals(4, transformable.getHeight());
    }

    /**
     * Test transform notification.
     */
    @Test
    void testNotify()
    {
        final AtomicBoolean transformed = new AtomicBoolean();
        final TransformableListener listener = t -> transformed.set(true);
        transformable.addListener(listener);
        transformable.setSize(transformable.getWidth(), transformable.getHeight());

        assertFalse(transformed.get());

        transformable.setSize(1, transformable.getHeight());

        assertTrue(transformed.get());

        transformed.set(false);
        transformable.setSize(transformable.getWidth(), 1);

        assertTrue(transformed.get());

        transformed.set(false);
        transformable.setSize(2, 2);

        assertTrue(transformed.get());

        transformable.transform(transformable.getX(),
                                transformable.getY(),
                                transformable.getWidth(),
                                transformable.getHeight());

        assertTrue(transformed.get());

        transformed.set(false);
        transformable.setSize(2, 2);

        transformable.moveLocation(1.0, 0.0, 0.0);

        assertFalse(transformed.get());

        transformable.moveLocationX(1.0, 1.0);

        assertTrue(transformed.get());

        transformed.set(false);

        transformable.moveLocation(1.0, 0.0, 0.0);
        transformable.moveLocationY(1.0, 1.0);

        assertTrue(transformed.get());

        transformed.set(false);

        transformable.moveLocation(1.0, 0.0, 0.0);
        transformable.moveLocation(1.0, 1.0, 1.0);

        assertTrue(transformed.get());

        transformed.set(false);
        transformable.removeListener(listener);
        transformable.transform(1.0, 2.0, 3, 4);

        assertFalse(transformed.get());
    }

    /**
     * Test recycle call.
     */
    @Test
    void testRecycle()
    {
        transformable.teleport(1.0, 1.0);
        transformable.recycle();

        assertLocalization(0.0, 0.0, 0.0, 0.0);
    }

    private void assertLocalization(double oldX, double oldY, double x, double y)
    {
        assertEquals(oldX, transformable.getOldX());
        assertEquals(oldY, transformable.getOldY());
        assertEquals(x, transformable.getX());
        assertEquals(y, transformable.getY());
    }
}
