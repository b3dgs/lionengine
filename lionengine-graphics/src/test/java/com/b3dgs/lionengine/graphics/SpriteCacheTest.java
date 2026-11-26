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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.testsupport.RecordedCommand;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Test sprites over the image cache: instances share the cached source, its pixels and its texture, follow the
 * release rule of its handles, and draw a modified copy or a reloaded image as their own source (REQ-SPRITE-002,
 * REQ-SPR-001, REQ-RES-004, REQ-RES-005, ADR-0019, ADR-0026, ADR-0055).
 */
final class SpriteCacheTest
{
    /** Key of the hero image: 64 by 32 pixels. */
    private static final ResourceKey HERO = new ResourceKey("sprites/hero.png");
    /** Key of the coin image: 8 by 8 pixels. */
    private static final ResourceKey COIN = new ResourceKey("sprites/coin.png");
    /** Id of the hero sheet. */
    private static final ResourceKey HERO_SHEET = new ResourceKey("sheets/hero");
    /** Opaque red. */
    private static final int RED = 0xFFFF0000;
    /** Opaque blue. */
    private static final int BLUE = 0xFF0000FF;
    /** Instances of the crowd. */
    private static final int CROWD = 1000;

    /**
     * Writes an image of one color.
     *
     * @param width The width.
     * @param height The height.
     * @param argb The color.
     * @return The PNG image.
     */
    private static byte[] png(int width, int height, int argb)
    {
        final int[] pixels = new int[width * height];
        Arrays.fill(pixels, argb);
        return PngWriter.rgba(width, height, pixels).write();
    }

    /** The provider of the images. */
    private FakeResourceProvider provider;
    /** The render backend: texture store and recorder of the presented frames. */
    private RecordingBackend backend;
    /** The image cache. */
    private ImageCache cache;
    /** The command buffer. */
    private CommandBuffer commands;

    /**
     * Creates the cache, the backend and the buffer of each test, with the hero and coin images.
     */
    @BeforeEach
    void prepare()
    {
        provider = new FakeResourceProvider(false);
        provider.put(HERO, png(64, 32, 0));
        provider.put(COIN, png(8, 8, 0));
        backend = new RecordingBackend();
        cache = new ImageCache(new Resources(provider), backend);
        commands = new CommandBuffer(160, 120, 4);
    }

    /**
     * Renders the buffer, returns the presented commands and clears the buffer for the next frame.
     *
     * @return The commands of the frame, by layer then submission order.
     */
    private List<RecordedCommand> present()
    {
        backend.present(commands);
        commands.clear();
        return backend.getLastFrame();
    }

    /**
     * Two scenes loading the same key get two handles of one cached image: their sprites draw the same texture from
     * one decoding. Each scene releases its own handle: the other scene keeps drawing, the sprites over a released
     * handle refuse to draw, and the texture leaves with the last handle; a second release changes nothing.
     */
    @Test
    void testScenesShareSource()
    {
        final Image level = cache.load(HERO);
        final Image menu = cache.load(HERO);
        final Sprite levelHero = new Sprite(level);
        final Sprite levelShadow = new Sprite(level);
        final Sprite menuHero = new Sprite(menu);

        levelHero.render(commands);
        levelShadow.render(commands);
        menuHero.render(commands);
        final List<RecordedCommand> frame = present();

        assertNotSame(level, menu);
        assertEquals(1, cache.size());
        assertEquals(1, provider.getOpened());
        final int texture = frame.get(0).getTexture();
        assertEquals(texture, frame.get(1).getTexture());
        assertEquals(texture, frame.get(2).getTexture());

        level.release();
        menuHero.render(commands);

        assertEquals(texture, present().get(0).getTexture());
        assertTrue(backend.isAlive(texture));
        assertThrows(IllegalStateException.class, () -> levelHero.render(commands));
        assertEquals(1, cache.size());

        menu.release();
        menu.release();

        assertFalse(backend.isAlive(texture));
        assertEquals(0, cache.size());
        assertThrows(IllegalStateException.class, () -> menuHero.render(commands));
        assertEquals(0, commands.size());
    }

    /**
     * Any number of sprites share one handle: a crowd draws one texture, and the memory of the image is counted once.
     */
    @Test
    void testCrowd()
    {
        final Image coin = cache.load(COIN);
        final Sprite[] crowd = new Sprite[CROWD];
        for (int i = 0; i < CROWD; i++)
        {
            crowd[i] = new Sprite(coin);
            crowd[i].setPosition(i % 160, i / 160);
            crowd[i].render(commands);
        }
        final List<RecordedCommand> frame = present();

        assertEquals(CROWD, frame.size());
        for (final RecordedCommand command : frame)
        {
            assertEquals(coin.getTexture(), command.getTexture());
        }
        assertEquals(1, cache.size());
        assertEquals(8L * 8L * 4L, cache.getUsedBytes());
        coin.release();
        assertEquals(0L, cache.getUsedBytes());
    }

    /**
     * A sheet over an image already loaded is a cache hit: a sprite over a cell of the sheet and a sprite over the
     * whole image draw the same texture; the group and the handle are released independently.
     */
    @Test
    void testSheetSharesImage()
    {
        final Image image = cache.load(HERO);
        final SheetGroup group = new SheetGroup(cache);
        final Sprite whole = new Sprite(image);
        final Sprite cell = new Sprite(group.add(HERO_SHEET, HERO, new SheetGrid(16, 16)));
        cell.setFrame(5);

        whole.render(commands);
        cell.render(commands);
        final List<RecordedCommand> frame = present();

        assertEquals(1, cache.size());
        assertEquals(frame.get(0).getTexture(), frame.get(1).getTexture());
        assertEquals(64, frame.get(0).getSourceWidth());
        assertEquals(16, frame.get(1).getSourceX());

        group.release();
        whole.render(commands);

        assertEquals(1, present().size());
        assertThrows(IllegalStateException.class, () -> cell.render(commands));
        image.release();
        assertEquals(0, cache.size());
    }

    /**
     * Different keys are different sources, each with its own texture.
     */
    @Test
    void testMiss()
    {
        final Image hero = cache.load(HERO);
        final Image coin = cache.load(COIN);

        new Sprite(hero).render(commands);
        new Sprite(coin).render(commands);
        final List<RecordedCommand> frame = present();

        assertEquals(2, cache.size());
        assertNotEquals(frame.get(0).getTexture(), frame.get(1).getTexture());
        hero.release();
        coin.release();
    }

    /**
     * Modified pixels are a copy owned by the caller; added to the cache under a discriminator, the copy is a source
     * of its own, with its texture, drawn by its own sprites, while the cached image and its sprites never change
     * (REQ-SPR-001).
     */
    @Test
    void testModifiedCopy()
    {
        final Image coin = cache.load(COIN);
        final PixelBuffer pixels = coin.copyToBuffer();
        pixels.setPixel(0, 0, RED);
        final Image damaged = cache.add(COIN, "damaged", pixels);
        pixels.setPixel(1, 0, RED);
        final Sprite plain = new Sprite(coin);
        final Sprite broken = new Sprite(damaged);

        plain.render(commands);
        broken.render(commands);
        final List<RecordedCommand> frame = present();
        final int plainTexture = frame.get(0).getTexture();
        final int brokenTexture = frame.get(1).getTexture();

        assertNotEquals(plainTexture, brokenTexture);
        assertEquals(0, backend.getPixels(plainTexture)[0]);
        assertEquals(RED, backend.getPixels(brokenTexture)[0]);
        assertEquals(0, backend.getPixels(brokenTexture)[1]);
        assertEquals(0, coin.getPixel(0, 0));
        assertEquals(2, cache.size());

        damaged.release();
        plain.render(commands);

        assertEquals(plainTexture, present().get(0).getTexture());
        assertFalse(backend.isAlive(brokenTexture));
        coin.release();
    }

    /**
     * A development reload gives the cached image a new texture: every sprite over it draws the new pixels from the
     * next frame, without being created again, and the previous texture is released (ADR-0055).
     */
    @Test
    void testReload()
    {
        final Image level = cache.load(COIN);
        final Image menu = cache.load(COIN);
        final Sprite first = new Sprite(level);
        final Sprite second = new Sprite(menu);
        first.render(commands);
        final int before = present().get(0).getTexture();

        provider.put(COIN, png(8, 8, BLUE));
        cache.reload(COIN);
        first.render(commands);
        second.render(commands);
        final List<RecordedCommand> frame = present();
        final int after = frame.get(0).getTexture();

        assertNotEquals(before, after);
        assertEquals(after, frame.get(1).getTexture());
        assertFalse(backend.isAlive(before));
        assertEquals(BLUE, backend.getPixels(after)[0]);
        assertEquals(8, first.getWidth());
        level.release();
        menu.release();
    }
}
