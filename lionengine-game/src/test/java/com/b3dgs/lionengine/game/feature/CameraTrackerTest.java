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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;

/**
 * Test {@link CameraTracker}.
 */
public final class CameraTrackerTest
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
        config = UtilTransformable.createMedia(CameraTrackerTest.class);
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

    /**
     * Test the tracker feature.
     */
    @Test
    public void testTracker()
    {
        final Camera camera = services.add(new Camera());
        camera.setView(0, 0, 16, 32, 32);

        final CameraTracker tracker = new CameraTracker(services);
        tracker.getFeature(Refreshable.class).update(1.0);

        assertEquals(0.0, camera.getX());
        assertEquals(0.0, camera.getY());

        final Transformable transformable = new TransformableModel(services, setup);
        transformable.teleport(1.0, 2.0);

        tracker.track(transformable);
        tracker.getFeature(Refreshable.class).update(1.0);

        assertEquals(-7.0, camera.getX());
        assertEquals(-14.0, camera.getY());

        final Featurable featurable = new FeaturableModel(services, setup);
        featurable.addFeature(transformable);

        transformable.teleport(2.0, 3.0);

        tracker.track(featurable);
        tracker.getFeature(Refreshable.class).update(1.0);

        assertEquals(-6.0, camera.getX());
        assertEquals(-13.0, camera.getY());

        tracker.setOffset(1, 2);
        tracker.getFeature(Refreshable.class).update(1.0);

        assertEquals(-5.0, camera.getX());
        assertEquals(-11.0, camera.getY());
    }
}
