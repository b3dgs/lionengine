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
package com.b3dgs.lionengine.examples.graphics;

import com.b3dgs.lionengine.graphics.Image;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.graphics.PixelBuffer;
import com.b3dgs.lionengine.graphics.Sprite;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Shares one image between many sprites and two users, and draws a modified copy beside it (REQ-SPRITE-002,
 * REQ-SPR-001).
 *
 * <p>
 * The level loads the coin image once and creates a sprite per coin over the same handle: every coin draws the same
 * texture, decoded once, and the sprites own nothing. The heads-up display loads the same key on its own: the cache
 * returns a second handle on the cached image, so its icon draws the same texture too. A gold coin is a modified copy:
 * the level copies the pixels of the coin, changes them, and adds the copy to the cache under a discriminator; the
 * copy is a source of its own, with its texture, and the cached coin never changes. When the level leaves, it releases
 * its handles: the display keeps drawing its icon, and the coin image leaves the cache with the display's handle.
 * </p>
 *
 * @since 11.0.0
 */
public final class SpriteSharingExample
{
    /** Key of the coin image, in the game data: 8 by 8 pixels. */
    public static final ResourceKey COIN = new ResourceKey("sprites/coin.png");
    /** Discriminator of the gold coin, the modified copy of the coin. */
    public static final String GOLD = "gold";
    /** Number of coins of the level. */
    public static final int COINS = 20;
    /** Color painted on the gold coin, packed {@code 0xAARRGGBB}. */
    public static final int GOLD_COLOR = 0xFFFFD700;
    /** Layer of the display, above the level. */
    public static final int DISPLAY_LAYER = 10;
    /** Logical frame width in pixels. */
    public static final int WIDTH = 160;
    /** Logical frame height in pixels. */
    public static final int HEIGHT = 120;

    /** Distance between two coins in pixels. */
    private static final int COIN_STEP = 8;
    /** Vertical position of the coins in pixels. */
    private static final double COIN_Y = 100.0;
    /** Position of the gold coin in pixels. */
    private static final double GOLD_POSITION = 60.0;
    /** Position of the display icon in pixels. */
    private static final double ICON_POSITION = 6.0;

    /**
     * Private constructor.
     */
    private SpriteSharingExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Draws a frame of the level with the display, releases the level, then draws a frame of the display alone.
     *
     * @param resources The resources holding {@link #COIN}.
     * @param backend The render backend, which presents both frames.
     * @return The number of cached images and of bytes in use while the level runs, after it left and at the end.
     */
    public static Result run(Resources resources, RenderBackend backend)
    {
        final ImageCache cache = new ImageCache(resources, backend.getTextureStore());
        final CommandBuffer commands = new CommandBuffer(WIDTH, HEIGHT, COINS);

        final Image coin = cache.load(COIN);
        final Sprite[] coins = new Sprite[COINS];
        for (int i = 0; i < COINS; i++)
        {
            coins[i] = new Sprite(coin);
            coins[i].setPosition((double) i * COIN_STEP, COIN_Y);
        }
        final Image gold = cache.add(COIN, GOLD, paint(coin));
        final Sprite goldCoin = new Sprite(gold);
        goldCoin.setPosition(GOLD_POSITION, GOLD_POSITION);

        final Image displayCoin = cache.load(COIN);
        final Sprite icon = new Sprite(displayCoin);
        icon.setPosition(ICON_POSITION, ICON_POSITION);
        icon.setLayer(DISPLAY_LAYER);

        for (final Sprite sprite : coins)
        {
            sprite.render(commands);
        }
        goldCoin.render(commands);
        icon.render(commands);
        backend.present(commands);
        final int cachedInLevel = cache.size();
        final long bytesInLevel = cache.getUsedBytes();

        coin.release();
        gold.release();
        final int cachedAfterLevel = cache.size();
        commands.clear();
        icon.render(commands);
        backend.present(commands);

        displayCoin.release();
        return new Result(cachedInLevel, bytesInLevel, cachedAfterLevel, cache.size());
    }

    /**
     * Copies the pixels of an image and paints the copy with the gold color; the image does not change.
     *
     * @param image The image to copy.
     * @return The painted copy, owned by the caller.
     */
    private static PixelBuffer paint(Image image)
    {
        final PixelBuffer pixels = image.copyToBuffer();
        for (int y = 0; y < pixels.getHeight(); y++)
        {
            for (int x = 0; x < pixels.getWidth(); x++)
            {
                pixels.setPixel(x, y, GOLD_COLOR);
            }
        }
        return pixels;
    }

    /**
     * Result of the example.
     *
     * @param cachedInLevel The number of cached images while the level runs: the coin and the gold coin.
     * @param bytesInLevel The bytes of pixels in use while the level runs, each image counted once.
     * @param cachedAfterLevel The number of cached images once the level left, the display keeping the coin.
     * @param cachedAtEnd The number of cached images once every handle was released.
     * @since 11.0.0
     */
    public record Result(int cachedInLevel, long bytesInLevel, int cachedAfterLevel, int cachedAtEnd)
    {
        // Value type of the example.
    }
}
