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
package com.b3dgs.lionengine.game.feature.launchable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.UtilTransformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;

/**
 * Test {@link LaunchableModel}.
 */
final class LaunchableModelTest
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
        config = UtilTransformable.createMedia(LaunchableModelTest.class);
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
    private final Launchable launchable = UtilLaunchable.createLaunchable(services, setup, featurable);
    private final Transformable transformable = featurable.getFeature(Transformable.class);

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        services.add(new MapTileGame());
    }

    /**
     * Clean test.
     */
    @AfterEach
    public void clean()
    {
        featurable.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the launch.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    void testLaunch() throws InterruptedException
    {
        launchable.launch();

        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(0.0, transformable.getY());
        assertEquals(0.0, launchable.getDirection().getDirectionHorizontal());
        assertEquals(1.0, launchable.getDirection().getDirectionVertical());

        launchable.update(1.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(1.0, transformable.getY());
        assertEquals(0.0, launchable.getDirection().getDirectionHorizontal());
        assertEquals(1.0, launchable.getDirection().getDirectionVertical());

        UtilTests.pause(11);
        launchable.update(1.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(1.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(2.0, transformable.getY());
        assertEquals(0.0, launchable.getDirection().getDirectionHorizontal());
        assertEquals(1.0, launchable.getDirection().getDirectionVertical());

        launchable.update(1.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(2.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(3.0, transformable.getY());
        assertEquals(0.0, launchable.getDirection().getDirectionHorizontal());
        assertEquals(1.0, launchable.getDirection().getDirectionVertical());
    }

    /**
     * Test the launch without vector.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    void testLaunchNoVector() throws InterruptedException
    {
        launchable.setVector(null);
        launchable.launch();
        launchable.update(1.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(0.0, transformable.getY());
        assertNull(launchable.getDirection());

        UtilTests.pause(11);
        launchable.update(1.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(0.0, transformable.getY());
        assertNull(launchable.getDirection());
    }

    /**
     * Test the launch listener.
     */
    @Test
    void testListener()
    {
        final AtomicBoolean fired = new AtomicBoolean();
        final LaunchableListener listener = launchable -> fired.set(true);
        launchable.addListener(listener);

        assertFalse(fired.get());

        launchable.launch();

        assertTrue(fired.get());

        launchable.removeListener(listener);
        fired.set(false);

        launchable.launch();

        assertFalse(fired.get());
    }

    /**
     * Test the check listener.
     */
    @Test
    void testCheck()
    {
        final Launchable launchable = new LaunchableModel(services, setup);
        final Self self = new Self(services, setup);
        launchable.checkListener(self);
        launchable.launch();

        assertTrue(self.fired.get());
    }

    /**
     * Self listener.
     */
    private static final class Self extends FeatureModel implements LaunchableListener
    {
        final AtomicBoolean fired = new AtomicBoolean();

        /**
         * Create self.
         * 
         * @param services The services reference.
         * @param setup The setup reference.
         */
        Self(Services services, Setup setup)
        {
            super(services, setup);
        }

        @Override
        public void notifyFired(Launchable launchable)
        {
            fired.set(true);
        }
    }
}
