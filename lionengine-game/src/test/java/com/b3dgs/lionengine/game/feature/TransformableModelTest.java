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
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;

/**
 * Test {@link TransformableModel}.
 */
public final class TransformableModelTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
    }

    private final Media media = UtilTransformable.createMedia(Featurable.class);
    private final Services services = new Services();
    private final Setup setup = new Setup(media);
    private final Featurable featurable = new FeaturableModel();
    private final Transformable transformable = new TransformableModel(setup);

    /**
     * Prepare test.
     */
    @BeforeEach
    public void before()
    {
        services.add(new MapTileGame());
        featurable.addFeature(new IdentifiableModel());
        transformable.prepare(featurable);
    }

    /**
     * Clean test.
     */
    @AfterEach
    public void after()
    {
        featurable.getFeature(Identifiable.class).notifyDestroyed();

        assertTrue(media.getFile().delete());
    }

    /**
     * Test the transformable with default size.
     */
    @Test
    public void testDefaultSize()
    {
        final Transformable transformable = new TransformableModel();

        assertEquals(0, transformable.getWidth());
        assertEquals(0, transformable.getHeight());
    }

    /**
     * Test the transformable teleport.
     */
    @Test
    public void testTeleport()
    {
        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(0.0, transformable.getY());

        transformable.teleport(1.0, -1.0);

        assertEquals(1.0, transformable.getOldX());
        assertEquals(-1.0, transformable.getOldY());
        assertEquals(1.0, transformable.getX());
        assertEquals(-1.0, transformable.getY());

        transformable.teleportX(2.0);

        assertEquals(2.0, transformable.getOldX());
        assertEquals(-1.0, transformable.getOldY());
        assertEquals(2.0, transformable.getX());
        assertEquals(-1.0, transformable.getY());

        transformable.teleportY(3.0);

        assertEquals(2.0, transformable.getOldX());
        assertEquals(3.0, transformable.getOldY());
        assertEquals(2.0, transformable.getX());
        assertEquals(3.0, transformable.getY());
    }

    /**
     * Test the transformable location setting.
     */
    @Test
    public void testSetLocation()
    {
        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(0.0, transformable.getY());

        transformable.setLocation(1.0, 1.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(1.0, transformable.getX());
        assertEquals(1.0, transformable.getY());

        transformable.setLocationX(2.0);

        assertEquals(1.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(2.0, transformable.getX());
        assertEquals(1.0, transformable.getY());

        transformable.setLocationY(3.0);

        assertEquals(1.0, transformable.getOldX());
        assertEquals(1.0, transformable.getOldY());
        assertEquals(2.0, transformable.getX());
        assertEquals(3.0, transformable.getY());
    }

    /**
     * Test the transformable size.
     */
    @Test
    public void testSetSize()
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
    public void testMoveLocation()
    {
        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(0.0, transformable.getX());
        assertEquals(0.0, transformable.getY());

        transformable.moveLocation(1.0, 1.0, 2.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(1.0, transformable.getX());
        assertEquals(2.0, transformable.getY());

        transformable.moveLocation(1.0, new Force(-2.0, -3.0), new Force(-1.0, -2.0));

        assertEquals(1.0, transformable.getOldX());
        assertEquals(2.0, transformable.getOldY());
        assertEquals(-2.0, transformable.getX());
        assertEquals(-3.0, transformable.getY());
    }

    /**
     * Test the transformable moving on single axis at a time.
     */
    @Test
    public void testMoveLocationSingleAxis()
    {
        transformable.moveLocationX(1.0, 1.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(1.0, transformable.getX());
        assertEquals(0.0, transformable.getY());

        transformable.moveLocationY(1.0, 1.0);

        assertEquals(0.0, transformable.getOldX());
        assertEquals(0.0, transformable.getOldY());
        assertEquals(1.0, transformable.getX());
        assertEquals(1.0, transformable.getY());
    }

    /**
     * Test the transformable transform function.
     */
    @Test
    public void testTransform()
    {
        transformable.transform(1.0, 2.0, 3, 4);

        assertEquals(1.0, transformable.getOldX());
        assertEquals(2.0, transformable.getOldY());
        assertEquals(1.0, transformable.getX());
        assertEquals(2.0, transformable.getY());

        assertEquals(16, transformable.getOldWidth());
        assertEquals(32, transformable.getOldHeight());
        assertEquals(3, transformable.getWidth());
        assertEquals(4, transformable.getHeight());
    }

    /**
     * Test transform notification.
     */
    @Test
    public void testNotify()
    {
        final AtomicBoolean transformed = new AtomicBoolean();
        transformable.addListener(t -> transformed.set(true));
        transformable.setSize(transformable.getWidth(), transformable.getHeight());

        assertFalse(transformed.get());

        transformable.setSize(1, 0);

        assertTrue(transformed.get());

        transformed.set(false);
        transformable.setSize(0, 1);

        assertTrue(transformed.get());

        transformed.set(false);
        transformable.transform(transformable.getX(),
                                transformable.getY(),
                                transformable.getWidth(),
                                transformable.getHeight());

        assertTrue(transformed.get());

        transformable.transform(1.0, 2.0, 0, 1);

        assertTrue(transformed.get());

        transformed.set(false);
        transformable.moveLocation(1.0, 0.0, 0.0);

        assertFalse(transformed.get());

        transformable.moveLocation(1.0, 1.0, 0.0);

        assertTrue(transformed.get());

        transformed.set(false);
        transformable.moveLocation(1.0, 0.0, 1.0);

        assertTrue(transformed.get());
    }
}
