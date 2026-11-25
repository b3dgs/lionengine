/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphics;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Test {@link SheetGroup}: unique ids, duplicate and unknown ids, shared images, reference counting and release
 * (REQ-SHEET-002, REQ-RT-005, ADR-0026, ADR-0055).
 */
final class SheetGroupTest
{
    /** Key of the hero image, 64 by 32 pixels. */
    private static final ResourceKey HERO_IMAGE = new ResourceKey("sprites/hero.png");
    /** Key of the tiles image, 32 by 32 pixels. */
    private static final ResourceKey TILES_IMAGE = new ResourceKey("tiles/forest.png");
    /** Id of the hero sheet. */
    private static final ResourceKey HERO = new ResourceKey("sheets/hero.json");
    /** Id of the tiles sheet. */
    private static final ResourceKey TILES = new ResourceKey("tiles/forest.tsj");
    /** Grid of 16 by 16 pixels. */
    private static final SheetGrid GRID = new SheetGrid(16, 16);

    /** Provider of the images. */
    private FakeResourceProvider provider;
    /** Texture store. */
    private RecordingBackend backend;
    /** Image cache. */
    private ImageCache cache;
    /** Group under test. */
    private SheetGroup group;

    /**
     * Creates the cache and an empty group for each test.
     */
    @BeforeEach
    void prepare()
    {
        provider = new FakeResourceProvider(false);
        provider.put(HERO_IMAGE, PngWriter.rgba(64, 32, new int[64 * 32]).write());
        provider.put(TILES_IMAGE, PngWriter.rgba(32, 32, new int[32 * 32]).write());
        backend = new RecordingBackend();
        cache = new ImageCache(new Resources(provider), backend);
        group = new SheetGroup(cache);
    }

    /**
     * Sheets are added under their ids and retrieved by them; their images come from the cache.
     */
    @Test
    void testAddAndGet()
    {
        final SpriteSheet hero = group.add(HERO, HERO_IMAGE, GRID);
        final SpriteSheet tiles = group.add(TILES, TILES_IMAGE, new SheetGrid(8, 8));

        assertSame(hero, group.get(HERO));
        assertSame(tiles, group.get(new ResourceKey("tiles/forest.tsj")));
        assertTrue(group.has(HERO));
        assertFalse(group.has(new ResourceKey("sheets/slime.json")));
        assertEquals(2, group.size());
        assertEquals(8, hero.getCellCount());
        assertEquals(16, tiles.getCellCount());
        assertEquals(HERO_IMAGE, hero.getImage().getKey());
        assertTrue(cache.has(HERO_IMAGE, ImageCache.DECODED));
        assertEquals(2, cache.size());
    }

    /**
     * An id already in the group is refused with the id in the message, before anything is read; the first sheet
     * stays.
     */
    @Test
    void testDuplicateId()
    {
        final SpriteSheet hero = group.add(HERO, HERO_IMAGE, GRID);

        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                                                             () -> group.add(HERO, TILES_IMAGE, GRID));
        assertEquals("Sheet id already in the group: sheets/hero.json", exception.getMessage());
        assertSame(hero, group.get(HERO));
        assertEquals(1, provider.getOpened());
        assertEquals(1, cache.size());
    }

    /**
     * An id not in the group is refused with the id in the message.
     */
    @Test
    void testUnknownId()
    {
        group.add(HERO, HERO_IMAGE, GRID);

        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> group.get(TILES));
        assertEquals("Sheet id not in the group: tiles/forest.tsj", exception.getMessage());
    }

    /**
     * Several sheets over one image, in one group or in two, share its pixels and texture, decoded once.
     */
    @Test
    void testSharedImage()
    {
        final SheetGroup other = new SheetGroup(cache);
        final SpriteSheet small = group.add(HERO, HERO_IMAGE, GRID);
        final SpriteSheet large = group.add(new ResourceKey("sheets/hero-large.json"),
                                            HERO_IMAGE,
                                            new SheetGrid(32, 32));
        final SpriteSheet again = other.add(HERO, HERO_IMAGE, GRID);

        assertEquals(small.getImage().getTexture(), large.getImage().getTexture());
        assertEquals(small.getImage().getTexture(), again.getImage().getTexture());
        assertEquals(1, provider.getOpened());
        assertEquals(1, cache.size());

        group.release();
        assertEquals(1, cache.size());
        other.release();
        assertEquals(0, cache.size());
    }

    /**
     * Releasing the group releases the image of every sheet together; a second release has no effect; a released
     * handle refuses every other call.
     */
    @Test
    void testRelease()
    {
        final SpriteSheet hero = group.add(HERO, HERO_IMAGE, GRID);
        group.add(TILES, TILES_IMAGE, GRID);
        final int texture = hero.getImage().getTexture();

        group.release();

        assertTrue(group.isReleased());
        assertEquals(0, cache.size());
        assertFalse(backend.isAlive(texture));
        assertTrue(hero.getImage().isReleased());
        assertEquals(8, hero.getCellCount());
        assertDoesNotThrow(group::release);
        assertThrows(IllegalStateException.class, () -> group.add(HERO, HERO_IMAGE, GRID));
        assertThrows(IllegalStateException.class, () -> group.get(HERO));
        assertThrows(IllegalStateException.class, () -> group.has(HERO));
        assertThrows(IllegalStateException.class, group::size);
        assertThrows(IllegalStateException.class, group::acquire);
    }

    /**
     * Each handle is released by its acquirer: the images stay until the last handle of the group is released, and
     * releasing one handle twice never releases another one (ADR-0028: a tile map keeps its tileset).
     */
    @Test
    void testHandles()
    {
        group.add(TILES, TILES_IMAGE, GRID);
        final SheetGroup map = group.acquire();

        assertSame(group.get(TILES), map.get(TILES));
        map.add(HERO, HERO_IMAGE, GRID);
        assertTrue(group.has(HERO));

        map.release();
        map.release();
        assertTrue(map.isReleased());
        assertFalse(group.isReleased());
        assertEquals(2, cache.size());
        assertEquals(2, group.size());

        group.release();
        assertEquals(0, cache.size());
    }

    /**
     * A sheet that cannot be added leaves nothing in the group and nothing in the cache: a missing image, an invalid
     * image, a grid outside the image, an image above the limit of the cache.
     */
    @Test
    void testAddFailure()
    {
        final ResourceKey absent = new ResourceKey("sprites/absent.png");
        final ResourceKey invalid = new ResourceKey("sprites/invalid.png");
        provider.put(invalid, new byte[]
        {
            1, 2, 3
        });
        final SheetGrid large = new SheetGrid(16, 16, 5, 0, 0, 0);
        final SheetGroup limited = new SheetGroup(new ImageCache(new Resources(provider), backend, 1024L));

        assertThrows(ResourceException.class, () -> group.add(HERO, absent, GRID));
        assertThrows(ImageDecodingException.class, () -> group.add(HERO, invalid, GRID));
        final IllegalArgumentException outside = assertThrows(IllegalArgumentException.class,
                                                              () -> group.add(HERO, HERO_IMAGE, large));
        assertTrue(outside.getMessage().contains(HERO.path()), outside.getMessage());
        assertThrows(IllegalStateException.class, () -> limited.add(HERO, HERO_IMAGE, GRID));

        assertEquals(0, group.size());
        assertEquals(0, cache.size());
        assertEquals(0, limited.size());
        assertEquals(0, provider.getUnclosed());
        group.add(HERO, HERO_IMAGE, GRID);
        assertEquals(1, group.size());
    }

    /**
     * {@code null} arguments are refused.
     */
    @Test
    void testNullArguments()
    {
        assertThrows(IllegalArgumentException.class, () -> new SheetGroup(null));
        assertThrows(IllegalArgumentException.class, () -> group.add(null, HERO_IMAGE, GRID));
        assertThrows(IllegalArgumentException.class, () -> group.add(HERO, null, GRID));
        assertThrows(IllegalArgumentException.class, () -> group.add(HERO, HERO_IMAGE, null));
        assertThrows(IllegalArgumentException.class, () -> group.get(null));
        assertThrows(IllegalArgumentException.class, () -> group.has(null));
        assertEquals(0, cache.size());
    }

    /**
     * Another thread than the owner of the cache cannot use the group, and changes nothing (ADR-0018, ADR-0055).
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    void testOtherThread() throws InterruptedException
    {
        group.add(HERO, HERO_IMAGE, GRID);

        assertRefused(() -> group.add(TILES, TILES_IMAGE, GRID));
        assertRefused(() -> group.get(HERO));
        assertRefused(() -> group.has(HERO));
        assertRefused(group::size);
        assertRefused(group::acquire);
        assertRefused(group::release);

        assertFalse(group.isReleased());
        assertEquals(1, group.size());
        assertEquals(1, cache.size());
        group.release();
        assertEquals(0, cache.size());
    }

    /**
     * Runs an action on another thread and checks that it throws an {@link IllegalStateException} of the thread rule.
     *
     * @param action The action.
     * @throws InterruptedException If interrupted.
     */
    private static void assertRefused(Runnable action) throws InterruptedException
    {
        final IllegalStateException[] failure = new IllegalStateException[1];
        final Thread thread = new Thread(() ->
        {
            try
            {
                action.run();
            }
            catch (final IllegalStateException exception)
            {
                failure[0] = exception;
            }
        }, "worker");
        thread.start();
        thread.join();

        assertInstanceOf(IllegalStateException.class, failure[0]);
        assertTrue(failure[0].getMessage().startsWith("ImageCache used on the thread worker"),
                   failure[0].getMessage());
    }
}
