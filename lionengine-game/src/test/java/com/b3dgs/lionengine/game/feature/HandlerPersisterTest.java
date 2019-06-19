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

import java.io.IOException;
import java.util.Iterator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Test {@link HandlerPersister}.
 */
public final class HandlerPersisterTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void setUp()
    {
        Medias.setLoadFromJar(HandlerPersisterTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test without map.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testWithoutMap() throws IOException
    {
        final Services services = new Services();
        final Factory factory = services.add(new Factory(services));
        final Handler handler = services.add(new Handler(services));
        final Featurable featurable = factory.create(Medias.create("object_features.xml"));
        featurable.getFeature(Transformable.class).teleport(1, 2);
        handler.add(featurable);
        handler.add(factory.create(Medias.create("object_identifiable.xml")));
        handler.update(1.0);

        final HandlerPersister persister = new HandlerPersister(services);
        final Media media = Medias.create("persister.data");
        try (FileWriting writing = new FileWriting(media))
        {
            persister.save(writing);
        }

        final Services services2 = new Services();
        services2.add(new Factory(services2));
        final Handler handler2 = services2.add(new Handler(services2));
        final HandlerPersister persister2 = new HandlerPersister(services2);
        try (FileReading reading = new FileReading(media))
        {
            persister2.load(reading);
        }
        handler2.update(1.0);

        final Transformable transformable = handler2.get(Transformable.class).iterator().next();
        final Iterator<Featurable> iterable = handler2.values().iterator();
        iterable.next();

        assertTrue(iterable.hasNext());
        assertEquals(1.0, transformable.getX());
        assertEquals(2.0, transformable.getY());
    }

    /**
     * Test with map.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testWithMap() throws IOException
    {
        final Services services = new Services();
        services.add(new Camera());
        final Factory factory = services.add(new Factory(services));
        final Handler handler = services.add(new Handler(services));
        final MapTile map = services.add(new MapTileGame());
        map.create(16, 16, 5, 5);

        final Featurable featurable = factory.create(Medias.create("object_features.xml"));
        featurable.getFeature(Transformable.class).teleport(16, 32);
        handler.add(featurable);

        handler.update(1.0);

        final HandlerPersister persister = new HandlerPersister(services);
        final Media media = Medias.create("persister.data");
        try (FileWriting writing = new FileWriting(media))
        {
            persister.save(writing);
        }

        final Services services2 = new Services();
        services2.add(new Factory(services2));
        services2.add(map);
        final Handler handler2 = services2.add(new Handler(services2));
        final HandlerPersister persister2 = new HandlerPersister(services2);
        try (FileReading reading = new FileReading(media))
        {
            persister2.load(reading);
        }
        handler2.update(1.0);

        final Featurable featurable3 = handler2.values().iterator().next();
        final Transformable transformable = featurable3.getFeature(Transformable.class);

        assertEquals(16.0, transformable.getX());
        assertEquals(32.0, transformable.getY());
    }
}
