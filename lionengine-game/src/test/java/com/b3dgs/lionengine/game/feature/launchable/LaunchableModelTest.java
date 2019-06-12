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
package com.b3dgs.lionengine.game.feature.launchable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;

/**
 * Test {@link LaunchableModel}.
 */
public final class LaunchableModelTest
{
    private final Services services = new Services();
    private final Featurable featurable = new FeaturableModel();
    private final Launchable launchable = UtilLaunchable.createLaunchable(services, featurable);
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
    public void testLaunch() throws InterruptedException
    {
        launchable.launch();

        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(0.0, transformable.getY());

        launchable.update(1.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(1.0, transformable.getY());

        UtilTests.pause(11);
        launchable.update(1.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(1.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(2.0, transformable.getY());

        launchable.update(1.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(2.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(3.0, transformable.getY());
    }

    /**
     * Test the launch without vector.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLaunchNoVector() throws InterruptedException
    {
        launchable.setVector(null);
        launchable.launch();
        launchable.update(1.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(0.0, transformable.getY());

        UtilTests.pause(11);
        launchable.update(1.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(0.0, transformable.getY());
    }

    /**
     * Test the launch listener.
     */
    @Test
    public void testListener()
    {
        final AtomicBoolean fired = new AtomicBoolean();
        launchable.addListener(launchable -> fired.set(true));

        assertFalse(fired.get());

        launchable.launch();

        assertTrue(fired.get());
    }

    /**
     * Test the check listener.
     */
    @Test
    public void testCheck()
    {
        final Launchable launchable = new LaunchableModel();
        final Self self = new Self();
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
         */
        Self()
        {
            super();
        }

        @Override
        public void notifyFired(Launchable launchable)
        {
            fired.set(true);
        }
    }
}
