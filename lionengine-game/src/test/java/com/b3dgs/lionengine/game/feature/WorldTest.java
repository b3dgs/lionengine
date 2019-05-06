/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Test {@link WorldGame}.
 */
public final class WorldTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(WorldTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    private final Resolution output = new Resolution(640, 480, 60);
    private final Config config = new Config(output, 16, true);
    private final Services services = new Services();

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        services.add(new Context()
        {
            @Override
            public int getX()
            {
                return 0;
            }

            @Override
            public int getY()
            {
                return 0;
            }

            @Override
            public <T extends InputDevice> T getInputDevice(Class<T> type)
            {
                return null;
            }

            @Override
            public Config getConfig()
            {
                return config;
            }
        });
        services.add(new SourceResolutionProvider()
        {
            @Override
            public int getWidth()
            {
                return output.getWidth();
            }

            @Override
            public int getHeight()
            {
                return output.getHeight();
            }

            @Override
            public int getRate()
            {
                return output.getRate();
            }
        });
    }

    /**
     * Test the world.
     */
    @Test
    public void testWorld()
    {
        final WorldMock world = new WorldMock(services);

        final Media media = Medias.create("test");
        try
        {
            world.saveToFile(media);
        }
        finally
        {
            assertTrue(media.getFile().delete());
        }

        world.update(0);
        world.render(null);
    }

    /**
     * Test the world.
     */
    @Test
    public void testWorldFail()
    {
        final WorldFail world = new WorldFail(services);

        assertThrows(() -> world.saveToFile(null), "Unexpected null argument !");

        final Media media = Medias.create("test");

        assertThrows(() -> world.saveToFile(media), "[test] Error on saving to file !");
        assertTrue(media.getFile().delete());

        assertThrows(() -> world.loadFromFile(Medias.create("type.xml")), "[type.xml] Cannot open the media !");
        assertThrows(() -> world.loadFromFile(null), "Unexpected null argument !");
    }

    /**
     * Test spawn with no transformable.
     */
    @Test
    public void testSpawnNoTransformable()
    {
        final WorldGame world = new WorldMock(services);
        assertThrows(() -> world.spawn(Medias.create("object.xml"), 0, 0),
                     Features.ERROR_FEATURE_NOT_FOUND + Transformable.class.getName());
    }

    /**
     * Test spawn.
     */
    @Test
    public void testSpawn()
    {
        final WorldGame world = new WorldMock(services);
        final Featurable featurable = world.spawn(Medias.create("object_features.xml"),
                                                  Geom.createLocalizable(1.0, 2.0));

        assertNotNull(featurable);

        final Handler handler = services.get(Handler.class);

        assertFalse(handler.get(Transformable.class).iterator().hasNext());

        world.update(1.0);

        assertEquals(1.0, handler.get(Transformable.class).iterator().next().getX());
    }
}
